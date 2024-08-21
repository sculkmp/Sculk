package org.sculk.plugin;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import lombok.Getter;
import org.sculk.Server;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class PluginManager {
    public static final Yaml yamlLoader;

    static {
        Representer representer = new Representer(new DumperOptions());
        representer.getPropertyUtils().setSkipMissingProperties(true);
        yamlLoader = new Yaml(new CustomClassLoaderConstructor(PluginManager.class.getClassLoader(), new LoaderOptions()), representer);
    }

    @Getter
    private final Server server;
    private final PluginLoader pluginLoader;

    protected final Object2ObjectMap<String, PluginClassLoader> pluginClassLoaders = new Object2ObjectArrayMap<>();
    private final Object2ObjectMap<String, Plugin> pluginMap = new Object2ObjectArrayMap<>();
    private final Object2ObjectMap<String, Class<?>> cachedClasses = new Object2ObjectArrayMap<>();

    private final List<Pair<PluginData, Path>> pluginsToLoad = new ObjectArrayList<>();

    public PluginManager(Server server) {
        this.server = server;
        this.pluginLoader = new PluginLoader(this);
        try {
            this.loadPluginsInside(Paths.get(server.getDataPath() + "/plugins/"));
        } catch (IOException e) {
            server.getLogger().error("Error while filtering plugin files: " + e);
        }
    }

    private void loadPluginsInside(Path folderPath) throws IOException {
        Comparator<PluginData> comparator = (o1, o2) -> {
            if (o2.getName().equals(o1.getName())) {
                return 0;
            }
            if (o2.getDepends() == null) {
                return 1;
            }
            return o2.getDepends().contains(o1.getName()) ? -1 : 1;
        };

        Map<PluginData, Path> plugins = new TreeMap<>(comparator);
        try (Stream<Path> stream = Files.walk(folderPath)) {
            stream.filter(Files::isRegularFile).filter(PluginLoader::isJarFile).forEach(jarPath -> {
                PluginData config = this.loadPluginConfig(jarPath);
                if (config != null) {
                    plugins.put(config, jarPath);
                }
            });
        }
        plugins.forEach(this::registerClassLoader);
    }

    private PluginData loadPluginConfig(Path path) {
        if (!Files.isRegularFile(path) || !PluginLoader.isJarFile(path)) {
            server.getLogger().warn("Cannot load plugin: Provided file is no jar file: " + path.getFileName());
            return null;
        }

        File pluginFile = path.toFile();
        if (!pluginFile.exists()) {
            return null;
        }
        return this.pluginLoader.loadPluginData(pluginFile);
    }

    private PluginClassLoader registerClassLoader(PluginData config, Path path) {
        if (this.getPluginByName(config.getName()) != null) {
            server.getLogger().warn("Plugin is already loaded: " + config.getName());
            return null;
        }

        PluginClassLoader classLoader = this.pluginLoader.loadClassLoader(config, path.toFile());
        if (classLoader != null) {
            this.pluginClassLoaders.put(config.getName(), classLoader);
            this.pluginsToLoad.add(ObjectObjectImmutablePair.of(config, path));
            server.getLogger().debug("Loaded class loader from " + path.getFileName());
        }
        return classLoader;
    }

    public void loadAllPlugins() {
        for (Pair<PluginData, Path> pair : this.pluginsToLoad) {
            this.loadPlugin(pair.key(), pair.value());
        }
        this.pluginsToLoad.clear();
    }

    public Plugin loadPlugin(PluginData config, Path path) {
        File pluginFile = path.toFile();
        if (this.getPluginByName(config.getName()) != null) {
            server.getLogger().warn("Plugin is already loaded: " + config.getName());
            return null;
        }

        PluginClassLoader classLoader = this.pluginClassLoaders.get(config.getName());
        if (classLoader == null) {
            classLoader = this.registerClassLoader(config, path);
        }

        if (classLoader == null) {
            return null;
        }

        Plugin plugin = this.pluginLoader.loadPluginJAR(config, pluginFile, classLoader);
        if (plugin == null) {
            return null;
        }

        try {
            plugin.onLoad();
        } catch (Exception e) {
            server.getLogger().error("Failed to load plugin " + config.getName() + ": " + e);
            return null;
        }

        server.getLogger().info("Loaded plugin " + config.getName() + " successfully!");
        this.pluginMap.put(config.getName(), plugin);
        return plugin;
    }

    public void enableAllPlugins() {
        LinkedList<Plugin> failed = new LinkedList<>();

        for (Plugin plugin : this.pluginMap.values()) {
            if (!this.enablePlugin(plugin, null)) {
                failed.add(plugin);
            }
        }

        if (failed.isEmpty()) {
            return;
        }

        server.getLogger().warn("§cFailed to load plugins: §e" + String.join(", ", failed.stream()
                .map(Plugin::getName)
                .toList()));
    }

    public boolean enablePlugin(Plugin plugin, String parent) {
        if (plugin.isEnabled()) {
            return true;
        }

        if (plugin.getDescription().getDepends() != null && !this.checkDependencies(plugin, parent)) {
            return false;
        }

        try {
            plugin.setEnabled(true);
        } catch (Exception e) {
            server.getLogger().error(e.getMessage());
            return false;
        }
        return true;
    }

    private boolean checkDependencies(Plugin plugin, String parent) {
        String pluginName = plugin.getName();
        if (plugin.getDescription().getDepends() != null) {
            for (String depend : plugin.getDescription().getDepends()) {
                if (depend.equals(parent)) {
                    server.getLogger().warn("§cCannot enable plugin " + pluginName + ", circular dependency " + parent + "!");
                    return false;
                }

                Plugin dependPlugin = this.getPluginByName(depend);
                if (dependPlugin == null) {
                    server.getLogger().warn("§cCannot enable plugin " + pluginName + ", missing dependency " + depend + "!");
                    return false;
                }

                if (!dependPlugin.isEnabled() && !this.enablePlugin(dependPlugin, pluginName)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void disableAllPlugins() {
        for (Plugin plugin : this.pluginMap.values()) {
            server.getLogger().info("Disabling plugin " + plugin.getName() + "!");
            try {
                plugin.setEnabled(false);
            } catch (Exception e) {
                server.getLogger().error(e.getMessage());
            }
        }
    }

    public Class<?> getClassFromCache(String className) {
        Class<?> clazz = this.cachedClasses.get(className);
        if (clazz != null) {
            return clazz;
        }

        for (PluginClassLoader loader : this.pluginClassLoaders.values()) {
            try {
                clazz = loader.findClass(className, false);
                if (clazz != null) {
                    this.cachedClasses.put(className, clazz); // Cache the found class
                    return clazz;
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }
        return null;
    }

    protected void cacheClass(String className, Class<?> clazz) {
        this.cachedClasses.putIfAbsent(className, clazz);
    }

    public Map<String, Plugin> getPluginMap() {
        return Collections.unmodifiableMap(this.pluginMap);
    }

    public Collection<Plugin> getPlugins() {
        return Collections.unmodifiableCollection(this.pluginMap.values());
    }

    public Collection<PluginClassLoader> getPluginClassLoaders() {
        return Collections.unmodifiableCollection(this.pluginClassLoaders.values());
    }

    public Plugin getPluginByName(String pluginName) {
        return this.pluginMap.getOrDefault(pluginName, null);
    }

}