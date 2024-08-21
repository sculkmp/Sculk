package org.sculk.plugin;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PluginData {

    public String name;
    public String version;
    public String author;
    public String main;
    public List<String> depends;

}