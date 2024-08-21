package org.sculk.plugin;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.sculk.Server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Getter
public abstract class Plugin {
    protected boolean enabled = false;
    private PluginData description;
    private Server server;
    private Logger logger;
    private File pluginFile;
    private File dataFolder;
    private boolean initialized = false;

    protected final void init(PluginData description, Server server, File pluginFile) {
        Preconditions.checkArgument(!this.initialized, "Plugin has been already initialized!");

        this.initialized = true;
        this.description = description;
        this.server = server;
        this.logger = server.getLogger();

        this.pluginFile = pluginFile;
        this.dataFolder = new File(Server.getInstance().getDataPath() + "/plugins/" + description.getName().toLowerCase() + "/");

        if (this.dataFolder.mkdirs()) {
            this.logger.info("Created plugin data folder");
        }
    }

    public void onLoad() {
    }

    public abstract void onEnable();

    public void onDisable() {
    }

    public InputStream getResourceFile(String filename) {
        try(JarFile jar = new JarFile(this.pluginFile)) {
            JarEntry entry = jar.getJarEntry(filename);
            return jar.getInputStream(entry);
        } catch (IOException e) {
            return null;
        }
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
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
    }

    public String getName() {
        return this.description.getName();
    }
}