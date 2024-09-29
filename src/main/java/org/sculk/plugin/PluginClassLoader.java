package org.sculk.plugin;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {

    private final PluginManager pluginManager;
    private final Object2ObjectOpenHashMap<String, Class<?>> classes = new Object2ObjectOpenHashMap<>();

    public PluginClassLoader(PluginManager pluginManager, ClassLoader parent, File file) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);
        this.pluginManager = pluginManager;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return this.findClass(name, true);
    }

    protected Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        Class<?> result = this.classes.get(name);
        if (result != null) {
            return result;
        }

        if (checkGlobal) {
            result = this.pluginManager.getClassFromCache(name);
        }

        if (result == null && (result = super.findClass(name)) != null) {
            this.pluginManager.cacheClass(name, result);
        }

        this.classes.put(name, result);
        return result;
    }

}