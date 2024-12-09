package org.sculk.player.text;

import java.util.HashMap;

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
public class ScoreBuilder implements IJsonText {


    private String name;
    private String objective;

    @Override
    public String getName() {
        return "score";
    }

    @Override
    public Object build() {
        HashMap<String, HashMap<String, String>> score = new HashMap<>();
        HashMap<String, String> data = new HashMap<>();
        data.put("name", this.name);
        data.put("objective", this.objective);
        score.put("score", data);
        return score;
    }

    @Override
    public String toString() {
        return "";
    }

    public ScoreBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ScoreBuilder setObjective(String objective) {
        this.objective = objective;
        return this;
    }
}