package com.plusls.ommc.util;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MixinConfig {

    /**
     * Package containing all mixins. This package will be monitored by the
     * transformer so that we can explode if some dummy tries to reference a
     * mixin class directly.
     */
    @SerializedName("package")
    public String mixinPackage;

    /**
     * Mixin classes to load, mixinPackage will be prepended
     */
    @SerializedName("mixins")
    public List<String> mixinClasses;

    /**
     * Mixin classes to load ONLY on client, mixinPackage will be prepended
     */
    @SerializedName("client")
    public List<String> mixinClassesClient;

    /**
     * Mixin classes to load ONLY on dedicated server, mixinPackage will be
     * prepended
     */
    @SerializedName("server")
    public List<String> mixinClassesServer;

}
