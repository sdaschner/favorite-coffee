package com.sebastian_daschner.coffee.beans.entity;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Origin {

    @Id
    public String country;

    @Override
    public String toString() {
        return "Origin{" +
               "country='" + country + '\'' +
               '}';
    }
}
