package com.plusls.ommc.compat.sodium;

import com.plusls.ommc.ModInfo;
import com.plusls.ommc.compat.CustomDepPredicate;
import com.plusls.ommc.util.YarnUtil;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class SodiumDependencyPredicate {

    static public class FluidRendererCheckLavaSpritesPredicate implements CustomDepPredicate {
        @Override
        public boolean test(ClassNode classNode) {
            for (int i = 0; i < classNode.fields.size(); ++i) {
                if (classNode.fields.get(i).name.equals("lavaSprites")) {
                    return true;
                }
            }
            return false;
        }
    }

    static public class FluidRendererPredicate implements CustomDepPredicate {
        private static String methodDesc = "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;";

        static {
            if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
                methodDesc = YarnUtil.obfuscateString(methodDesc);
            }
        }

        @Override
        public boolean test(ClassNode classNode) {
            for (MethodNode method : classNode.methods) {
                if (method.name.equals("render") && method.desc.contains(methodDesc)) {
                    return true;
                }
            }
            return false;
        }
    }

    static public class FluidRendererOldPredicate implements CustomDepPredicate {
        @Override
        public boolean test(ClassNode classNode) {
            return !new FluidRendererPredicate().test(classNode);
        }
    }

    static public class BlockRendererPredicate implements CustomDepPredicate {
        private static String methodDesc = "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;";

        static {
            if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
                methodDesc = YarnUtil.obfuscateString(methodDesc);
            }
        }

        @Override
        public boolean test(ClassNode classNode) {
            for (MethodNode method : classNode.methods) {
                ModInfo.LOGGER.error("methodDesc: {} method.desc: {}", methodDesc, method.desc);
                if (method.name.equals("renderModel") && method.desc.contains(methodDesc)) {
                    ModInfo.LOGGER.error("methodDesc: {} method.desc: {} ret: true", methodDesc, method.desc);
                    return true;
                }
            }
            ModInfo.LOGGER.error("{} ret false", classNode.name);
            return false;
        }
    }

    static public class BlockRendererOldPredicate implements CustomDepPredicate {
        @Override
        public boolean test(ClassNode classNode) {
            return !new BlockRendererPredicate().test(classNode);
        }
    }
}
