package com.github.superslowjelly.jndidetector.configuration;

import com.github.superslowjelly.jndidetector.JNDIDetector;

public class ConfigManager {

    public ConfigManager() {
        this.initConfigLoader();
    }

    public ConfigLoader<Config> configLoader;

    private void initConfigLoader() { this.configLoader = new ConfigLoader<>(Config.class, "JNDIDetector.conf", JNDIDetector.getConfigDir()); }

    public ConfigLoader<Config> getConfigLoader() { return this.configLoader; }

    public Config getConfig() { return this.getConfigLoader().get(); }

    public void reload() { this.getConfigLoader().reload(); }
}
