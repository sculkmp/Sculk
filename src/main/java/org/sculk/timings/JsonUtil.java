package org.sculk.timings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

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
public class JsonUtil {

    private static final JsonMapper MAPPER = new JsonMapper();

    public static ArrayNode toArray(Object... objects) {
        ArrayNode array = MAPPER.createArrayNode();
        for (Object object : objects) {
            array.addPOJO(object);
        }
        return array;
    }

    public static JsonNode toObject(Object object) {
        return MAPPER.valueToTree(object);
    }

    public static <E> JsonNode mapToObject(Iterable<E> collection, Function<E, JSONPair> mapper) {
        Map object = new LinkedHashMap();
        for (E e : collection) {
            JSONPair pair = mapper.apply(e);
            if (pair != null) {
                object.put(pair.key, pair.value);
            }
        }
        return MAPPER.valueToTree(object);
    }

    public static <E> ArrayNode mapToArray(E[] elements, Function<E, Object> mapper) {
        ArrayList array = new ArrayList();
        Collections.addAll(array, elements);
        return mapToArray(array, mapper);
    }

    public static <E> ArrayNode mapToArray(Iterable<E> collection, Function<E, Object> mapper) {
        ArrayNode node = MAPPER.createArrayNode();
        for (E e : collection) {
            Object obj = mapper.apply(e);
            if (obj != null) {
                node.addPOJO(obj);
            }
        }
        return node;
    }

    public static class JSONPair {
        public final String key;
        public final Object value;

        public JSONPair(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public JSONPair(int key, Object value) {
            this.key = String.valueOf(key);
            this.value = value;
        }
    }

}
