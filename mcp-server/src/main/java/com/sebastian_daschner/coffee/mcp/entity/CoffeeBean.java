package com.sebastian_daschner.coffee.mcp.entity;

import java.util.HashSet;
import java.util.Set;

public class CoffeeBean {

    public String uuid;

    public String name;

    public Origin origin;

    public Set<FlavorProfile> flavorProfiles = new HashSet<>();

    public Integer userRating;

    @Override
    public String toString() {
        return "CoffeeBean{" +
               "name='" + name + '\'' +
               ", origin=" + origin +
               ", flavorProfiles=" + flavorProfiles +
               ", userRating=" + userRating +
               '}';
    }
}
