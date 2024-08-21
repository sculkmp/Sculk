package org.sculk.plugin;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class PluginData {

    public String name;
    public String version;
    public String author;
    public String main;
    public String api;
    public List<String> apis;
    public List<String> depends;

    public List<String> getApi() {
        if(apis == null) apis = new ArrayList<>();
        if(api != null && !apis.contains(api)) apis.add(api);

        return apis;
    }
}