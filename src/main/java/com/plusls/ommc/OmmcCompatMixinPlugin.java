package com.plusls.ommc;

import com.plusls.ommc.compat.CustomDepPredicate;
import com.plusls.ommc.compat.Dependencies;
import com.plusls.ommc.compat.NeedObfuscate;
import com.plusls.ommc.util.YarnUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.util.version.VersionPredicateParser;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class OmmcCompatMixinPlugin extends OmmcMixinPlugin {

    private final List<String> obfuscatedMixinList = new ArrayList<>();
    static private Path tempDirectory;

    static private Method oldMatchesMethod;

    static {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            try {
                tempDirectory = createTempDirectory();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Cannot create temp directory.");
            }
        }
        try {
            oldMatchesMethod = Class.forName("net.fabricmc.loader.util.version.VersionPredicateParser").getMethod("matches", Version.class, String.class);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        }

    }
    // private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ClassNode loadClassNode(String className) {
        ClassNode classNode;
        try {
            classNode = MixinService.getService().getBytecodeProvider().getClassNode(className);
        } catch (ClassNotFoundException | IOException e) {
            throw new IllegalStateException(String.format("load ClassNode: %s fail.", className));
        }
        return classNode;
    }

    public static Path createTempDirectory() throws IOException {
        final Path tmp = Files.createTempDirectory(String.format("%s-", ModInfo.MOD_ID));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                FileUtils.forceDelete(tmp.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        return tmp;
    }

    private static boolean getRemap(AnnotationNode annotationNode, boolean defaultValue) {
        Boolean ret = Annotations.getValue(annotationNode, "remap");
        if (ret == null) {
            return defaultValue;
        }
        return ret;
    }

    private final static List<String> NAME_LIST = Arrays.asList("method", "target");

    private static void obfuscateAnnotation(AnnotationNode annotationNode, boolean defaultRemap) {
        boolean remap = getRemap(annotationNode, defaultRemap);
        for (int i = 0; i < annotationNode.values.size(); i += 2) {
            if (annotationNode.values.get(i + 1) instanceof AnnotationNode) {
                AnnotationNode subAnnotationNode = (AnnotationNode) annotationNode.values.get(i + 1);
                obfuscateAnnotation(subAnnotationNode, remap);
            } else if (annotationNode.values.get(i + 1) instanceof ArrayList && ((ArrayList) annotationNode.values.get(i + 1)).size() > 0 && ((ArrayList) annotationNode.values.get(i + 1)).get(0) instanceof AnnotationNode) {
                ArrayList<AnnotationNode> subAnnotationNodeList = (ArrayList) annotationNode.values.get(i + 1);
                for (AnnotationNode subAnnotationNode : subAnnotationNodeList) {
                    obfuscateAnnotation(subAnnotationNode, remap);
                }
            } else if (!defaultRemap) {
                String name = (String) annotationNode.values.get(i);
                if (NAME_LIST.contains(name)) {
                    if (annotationNode.values.get(i + 1) instanceof String) {
                        String str = (String) annotationNode.values.get(i + 1);
                        annotationNode.values.set(i + 1, YarnUtil.obfuscateString(str));
                    } else if (annotationNode.values.get(i + 1) instanceof ArrayList && ((ArrayList) annotationNode.values.get(i + 1)).size() > 0 && ((ArrayList) annotationNode.values.get(i + 1)).get(0) instanceof String) {
                        ArrayList<String> strList = (ArrayList) annotationNode.values.get(i + 1);
                        for (int j = 0; j < strList.size(); ++j) {
                            strList.set(j, YarnUtil.obfuscateString(strList.get(j)));
                        }
                    }
                }
            }
        }
    }

    private static void obfuscateAnnotation(ClassNode classNode, Path outputDirectory) throws IOException {
        String fullClassName = classNode.name;
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        boolean classRemap = getRemap(Annotations.getInvisible(classNode, Mixin.class), true);

        for (MethodNode method : classNode.methods) {
            if (method.visibleAnnotations != null) {
                for (AnnotationNode annotationNode : method.visibleAnnotations) {
                    obfuscateAnnotation(annotationNode, classRemap);
                }
            }
        }
        classNode.accept(classWriter);

        int packageNameIdx = fullClassName.lastIndexOf('/');
        String packageName, className;
        if (packageNameIdx == -1) {
            packageName = "";
            className = fullClassName;
        } else {
            packageName = fullClassName.substring(0, packageNameIdx);
            className = fullClassName.substring(packageNameIdx + 1);
        }
        classNode.invisibleAnnotations.remove(Annotations.getInvisible(classNode, NeedObfuscate.class));
        Files.createDirectories(Paths.get(outputDirectory.toString(), packageName));
        Files.write(Paths.get(outputDirectory.toString(), packageName, className + "Obfuscated.class"), classWriter.toByteArray());
    }

    @Override
    public void onLoad(String mixinPackage) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {


            Object urlLoader = Thread.currentThread().getContextClassLoader();
            Class<?> knotClassLoader;
            try {
                if (oldMatchesMethod == null) {
                    knotClassLoader = Class.forName("net.fabricmc.loader.impl.launch.knot.KnotClassLoader");
                } else {
                    knotClassLoader = Class.forName("net.fabricmc.loader.launch.knot.KnotClassLoader");
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new IllegalStateException("Cannot load class: net.fabricmc.loader.launch.knot.KnotClassLoader");
            }

            try {
                Method method = knotClassLoader.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(urlLoader, tempDirectory.toUri().toURL());
            } catch (MalformedURLException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
                throw new IllegalStateException("Cannot add custom class path to KnotClassLoader");
            }
        }
    }

    private void obfuscateClass(String classFullName) {
        ClassNode classNode = loadClassNode(classFullName);
        if (Annotations.getInvisible(classNode, NeedObfuscate.class) != null) {
            try {
                obfuscateAnnotation(classNode, tempDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("obfuscateAnnotation fail!");
            }
            obfuscatedMixinList.add(String.format("%sObfuscated", classFullName.substring(classFullName.lastIndexOf('.') + 1)));
        }
    }


    private boolean myMatches(Version version, String s) {
        try {
            if (oldMatchesMethod != null) {
                return (boolean) oldMatchesMethod.invoke(null, version, s);
            }
            return VersionPredicateParser.parse(s).test(version);
        } catch (VersionParsingException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkDependency(String targerClassName, AnnotationNode dependency) {
        String modId = Annotations.getValue(dependency, "modId");
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(modId);
        if (modContainerOptional.isPresent()) {
            ModContainer modContainer = modContainerOptional.get();
            List<String> versionList = Annotations.getValue(dependency, "version");
            for (String version : versionList) {
                if (!myMatches(modContainer.getMetadata().getVersion(), version)) {
                    return false;
                }
            }
            ClassNode targetClassNode;
            try {
                targetClassNode = loadClassNode(targerClassName);
            } catch (IllegalStateException ignored) {
                ModInfo.LOGGER.warn("Can't load class: {} when check dependency", targerClassName);
                return false;
            }
            List<Type> predicateList = Annotations.getValue(dependency, "predicate");
            if (predicateList != null) {
                for (Type predicateType : predicateList) {
                    try {
                        CustomDepPredicate predicate = Class.forName(predicateType.getClassName()).asSubclass(CustomDepPredicate.class).getDeclaredConstructor().newInstance();
                        if (!predicate.test(targetClassNode)) {
                            return false;
                        }

                    } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                        e.printStackTrace();
                        ModInfo.LOGGER.warn("fuckyou");
                        throw new IllegalStateException("get CustomDepPredicate fail!");
                    }

                }
            }
            return true;
        }
        return false;
    }

    public boolean checkDependencies(ClassNode mixinClassNode, String targetClassName) {
        AnnotationNode dependencies = Annotations.getInvisible(mixinClassNode, Dependencies.class);
        if (Annotations.getInvisible(mixinClassNode, Dependencies.class) != null) {
            List<AnnotationNode> dependencyArray = Annotations.getValue(dependencies, "dependencyList");
            for (AnnotationNode dependency : dependencyArray) {
                if (!checkDependency(targetClassName, dependency)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        ClassNode mixinClassNode = loadClassNode(mixinClassName);
        if (!checkDependencies(mixinClassNode, targetClassName)) {
            return false;
        }

        if (tempDirectory != null && Annotations.getInvisible(mixinClassNode, NeedObfuscate.class) != null) {
            obfuscateClass(mixinClassName);
            return false;
        }
        return true;
    }

    @Override
    public List<String> getMixins() {
        return obfuscatedMixinList;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }
}
