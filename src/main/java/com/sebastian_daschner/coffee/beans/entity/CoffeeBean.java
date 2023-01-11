package com.sebastian_daschner.coffee.beans.entity;

import com.sebastian_daschner.coffee.user.control.UserRatingTypeAdapter;
import com.sebastian_daschner.coffee.user.entity.UserRating;
import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.id.UuidStrategy;
import org.neo4j.ogm.typeconversion.UuidStringConverter;

import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.neo4j.ogm.annotation.Relationship.Direction.INCOMING;

@NodeEntity
public class CoffeeBean {

    @Id
    @GeneratedValue(strategy = UuidStrategy.class)
    @Convert(UuidStringConverter.class)
    @JsonbTransient
    public UUID uuid;

    @Required
    public String name;

    @Relationship("IS_FROM")
    public Origin origin;

    @Relationship("TASTES")
    public Set<FlavorProfile> flavorProfiles = new HashSet<>();

    @Relationship(value = "RATED", direction = INCOMING)
    @JsonbTypeAdapter(UserRatingTypeAdapter.class)
    public UserRating userRating;

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
