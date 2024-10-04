package org.sculk.permission;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
@Getter
public class Permission {
    final String name;
    @Setter
    String description = "";
    List<Permission> children;

    public Permission(String name) {
        this.name = name;
    }

    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addChildren(Permission children){
        this.children.add(children);
    }

    public void removeChildren(Permission children){
        this.children.remove(children);
    }
}
