package com.plusls.ommc.mixin.feature.fallbackLanguageList;

import com.plusls.ommc.config.Configs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;

@Mixin(LanguageManager.class)
public class MixinLanguageManager {

    @Final
    @Shadow
    private static LanguageInfo DEFAULT_LANGUAGE;

    @Shadow
    private Map<String, LanguageInfo> languages;


    @Redirect(method = "onResourceManagerReload", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean addFallbackLanguage(List<LanguageInfo> list, Object e) {
        LanguageInfo en_us = this.languages.getOrDefault("en_us", DEFAULT_LANGUAGE);
        boolean ret = false;
        List<String> fallbackLanguageList = Configs.Lists.FALLBACK_LANGUAGE_LIST.getStrings();
        for (int i = fallbackLanguageList.size() - 1; i >= 0; --i) {
            LanguageInfo languageDefinition = this.languages.getOrDefault(fallbackLanguageList.get(i), DEFAULT_LANGUAGE);
            if (languageDefinition != e && languageDefinition != en_us) {
                ret |= list.add(languageDefinition);
            }
        }

        if (e != en_us) {
            ret |= list.add((LanguageInfo) e);
        }
        return ret;
    }
}
