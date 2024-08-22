package org.sculk.entity;


import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/*
 *   ____             _ _
 *  / ___|  ___ _   _| | | __
 *  \___ \ / __| | | | | |/ /
 *   ___) | (__| |_| | |   <
 *  |____/ \___|\__,_|_|_|\_\
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author: SculkTeams
 * @link: http://www.sculkmp.org/
 */
public class AttributeMap {

    private final Map<String, Attribute> attributeMap = new HashMap<>();

    public void add(Attribute attribute) {
        attributeMap.put(attribute.getId(), attribute);
    }

    public Attribute get(String id) {
        return attributeMap.getOrDefault(id, null);
    }

    public Map<String, Attribute> getAll() {
        return attributeMap;
    }

    public Map<String, Attribute> needSend() {
        return attributeMap.entrySet().stream()
                .filter(stringAttributeEntry -> stringAttributeEntry.getValue().isSyncable() && stringAttributeEntry.getValue().isDesynchronized())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
