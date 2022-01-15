package com.plusls.ommc;

import com.plusls.ommc.compat.CustomDepPredicate;
import com.plusls.ommc.compat.Dependencies;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class OmmcCompatMixinPlugin extends OmmcMixinPlugin {


    private static ClassNode loadClassNode(String className) {
        ClassNode classNode;
        try {
            classNode = MixinService.getService().getBytecodeProvider().getClassNode(className);
        } catch (ClassNotFoundException | IOException e) {
            throw new IllegalStateException(String.format("load ClassNode: %s fail.", className));
        }
        return classNode;
    }


    private static boolean checkDependency(String targetClassName, AnnotationNode dependency) {
        String modId = Annotations.getValue(dependency, "modId");
        List<String> versionList = Annotations.getValue(dependency, "version");

        for (String version : versionList) {
            if (!checkDependency(modId, version)) {
                return false;
            }
        }

        ClassNode targetClassNode = loadClassNode(targetClassName);
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

    private static boolean checkDependencies(ClassNode mixinClassNode, String targetClassName) {
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
        return checkDependencies(mixinClassNode, targetClassName);
    }


}
