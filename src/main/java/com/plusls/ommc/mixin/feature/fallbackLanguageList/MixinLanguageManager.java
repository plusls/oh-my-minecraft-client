package com.plusls.ommc.mixin.feature.fallbackLanguageList;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;

@Mixin(LanguageManager.class)
public class MixinLanguageManager {

    @Final
    @Shadow
    private static LanguageDefinition ENGLISH_US;

    @Shadow
    private Map<String, LanguageDefinition> languageDefs;



    @Redirect(method = "reload", at=@At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean addFallbackLanguage(List<LanguageDefinition> list, Object e) {
        LanguageDefinition en_us = this.languageDefs.getOrDefault("en_us", ENGLISH_US);
        boolean ret = false;
        List<String> fallbackLanguageList = Configs.Lists.FALLBACK_LANGUAGE_LIST.getStrings();
        for (int i = fallbackLanguageList.size() - 1; i >= 0; --i) {
            LanguageDefinition languageDefinition = this.languageDefs.getOrDefault(fallbackLanguageList.get(i), ENGLISH_US);
            if (languageDefinition != e && languageDefinition != en_us) {
                ret |= list.add(languageDefinition);
            }
        }

        if (e != en_us) {
            ret |= list.add((LanguageDefinition) e);
        }
        return ret;
    }
}
