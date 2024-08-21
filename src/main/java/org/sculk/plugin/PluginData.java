package org.sculk.plugin;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class PluginData{

    public String name;
    public String version;
    public String author;
    public String main;
    public String api;
    public List<String> apis;
    public List<String> depends;

    public String getAuthor() {
        return this.author;
    }

    public String getMain() {
        return this.main;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public List<String> getDepends() {
        return this.depends;
    }

    public List<String> getApi() {
        if(apis == null) apis = new ArrayList<>();
        if(api != null && !apis.contains(api)) apis.add(api);

        return apis;
    }
}