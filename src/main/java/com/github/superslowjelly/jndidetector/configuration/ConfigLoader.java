package com.github.superslowjelly.jndidetector.configuration;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.util.MapFactories;

import java.io.File;

public class ConfigLoader<T> {

    private final HoconConfigurationLoader LOADER;
    private CommentedConfigurationNode node;
    private final Class<T> CLASS;
    private final TypeToken<T> TOKEN;
    private T value;

    public ConfigLoader(Class<T> clazz, String name, File configDir) {
        if (!configDir.exists()) configDir.mkdirs();

        File file = new File(configDir, name);
        try {
            if (!file.exists()) file.createNewFile();
        } catch (Exception e) { e.printStackTrace(); }

        this.CLASS = clazz;
        this.TOKEN = TypeToken.of(clazz);
        this.LOADER = HoconConfigurationLoader.builder()
            .setFile(file)
            .build();
        this.value = load(false);
    }

    private T load(boolean set) {
        try {
            this.node = this.LOADER.load(ConfigurationOptions.defaults().setMapFactory(MapFactories.insertionOrdered()).setShouldCopyDefaults(true));
            T value = set ? this.value : this.node.getNode("config").getValue(TOKEN, CLASS.newInstance());
            this.node.getNode("config").setValue(TOKEN, value);
            this.LOADER.save(this.node);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void reload() {
        this.value = this.load(false);
    }

    public T get() {
        return this.value;
    }

    public void save() {
        this.load(true);
    }
}