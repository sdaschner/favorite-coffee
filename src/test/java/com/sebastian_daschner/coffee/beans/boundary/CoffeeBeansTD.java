package com.sebastian_daschner.coffee.beans.boundary;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver;
import org.neo4j.ogm.session.SessionFactory;

public class CoffeeBeansTD extends CoffeeBeans {

    public CoffeeBeansTD() {
        String databaseUri = "bolt://localhost:7687";
        String username = "neo4j";
        String password = "test";

        Driver driver = GraphDatabase.driver(databaseUri, AuthTokens.basic(username, password));
        sessionFactory = new SessionFactory(new BoltDriver(driver), "com.sebastian_daschner.coffee.beans.entity", "com.sebastian_daschner.coffee.user.entity");
    }

}

