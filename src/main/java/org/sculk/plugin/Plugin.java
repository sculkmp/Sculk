package org.sculk.plugin;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.Logger;
import org.sculk.Server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class Plugin {
    protected boolean enabled = false;
    private PluginData description;
    private Server server;
    private Logger logger;
    private File pluginFile;
    private File dataFolder;
    private boolean initialized = false;

    public Plugin() {
    }

    protected final void init(PluginData description, Server server, File pluginFile) {
        Preconditions.checkArgument(!this.initialized, "Plugin has been already initialized!");
        this.initialized = true;
        this.description = description;
        this.server = server;
        this.logger = server.getLogger();

        this.pluginFile = pluginFile;
        this.dataFolder = new File(Server.getInstance().getDataPath() + "/plugins/" + description.getName().toLowerCase() + "/");
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
    }

    public void onLoad() {
    }

    public abstract void onEnable();

    public void onDisable() {
    }

    public InputStream getResourceFile(String filename) {
        try {
            JarFile pluginJar = new JarFile(this.pluginFile);
            JarEntry entry = pluginJar.getJarEntry(filename);
            return pluginJar.getInputStream(entry);
        } catch (IOException e) {
        }
        return null;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        try {
            if (enabled) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        } catch (Exception e) {
            this.logger.error("Error while enabling/disabling plugin " + this.getName() + ": " + e);
        }
    }

    public PluginData getDescription() {
        return this.description;
    }

    public String getName() {
        return this.description.getName();
    }

    public Server getServer() {
        return this.server;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public File getDataFolder() {
        return this.dataFolder;
    }
}