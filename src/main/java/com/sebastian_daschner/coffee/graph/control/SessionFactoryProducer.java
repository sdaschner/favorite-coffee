package com.sebastian_daschner.coffee.graph.control;

import org.neo4j.driver.Driver;
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver;
import org.neo4j.ogm.session.SessionFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class SessionFactoryProducer {

    public static final String[] PACKAGES = {
            "com.sebastian_daschner.coffee.beans.entity",
            "com.sebastian_daschner.coffee.user.entity",
    };

    @Produces
    SessionFactory produceSessionFactory(Driver driver) {
        return new SessionFactory(new BoltDriver(driver), PACKAGES);
    }

    void disposeSessionFactory(@Disposes SessionFactory sessionFactory) {
        sessionFactory.close();
    }

}
