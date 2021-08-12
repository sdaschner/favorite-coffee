package com.sebastian_daschner.coffee.beans.boundary;

import com.sebastian_daschner.coffee.beans.entity.*;
import org.neo4j.ogm.cypher.query.SortOrder;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class CoffeeBeans {

    @Inject
    SessionFactory sessionFactory;

    public CoffeeBean getCoffeeBean(UUID uuid) {
        Session session = sessionFactory.openSession();
        return session.load(CoffeeBean.class, uuid);
    }

    public List<CoffeeBean> getCoffeeBeans() {
        return getCoffeeBeans(null);
    }

    public List<CoffeeBean> getCoffeeBeans(SortCriteria sortCriteria) {
        Session session = sessionFactory.openSession();

        if (sortCriteria == SortCriteria.RATING)
            return coffeeBeansSortByRating(session);

        if (sortCriteria == SortCriteria.RECOMMENDATION)
            return coffeeBeansSortByRecommendation(session);

        return new ArrayList<>(session.loadAll(CoffeeBean.class, new SortOrder("name"), 1));
    }

    private List<CoffeeBean> coffeeBeansSortByRating(Session session) {
        Iterable<CoffeeBean> result = session.query(CoffeeBean.class, """
                        MATCH (bean:CoffeeBean)
                        OPTIONAL MATCH (bean)-[isFrom:IS_FROM]-(origin:Origin)
                        OPTIONAL MATCH (bean)-[tastes:TASTES]-(flavor:Flavor)
                        OPTIONAL MATCH (user:User)-[r:RATED]-(bean)
                        WITH bean, isFrom, origin, collect(tastes) as tastes, collect(flavor) as flavors, r, user, coalesce(r.rating, 0) as rating
                        RETURN bean, isFrom, origin, tastes, flavors, r, user
                        ORDER by rating DESC, bean.name ASC;
                        """,
                Map.of());
        return resultList(result);
    }

    private List<CoffeeBean> coffeeBeansSortByRecommendation(Session session) {
        Iterable<CoffeeBean> result = session.query(CoffeeBean.class, """
                        MATCH (flavor:Flavor)
                        OPTIONAL MATCH (:User)-[rated:RATED]-(:CoffeeBean)-[tastes:TASTES]-(flavor)
                        WITH coalesce(rated.rating, 2) - 2 as rating, coalesce(tastes.percentage, 1.0) as percentage, flavor
                        WITH flavor, sum(rating * percentage) as flavorWeight
                        MATCH (bean:CoffeeBean)-[tastes:TASTES]-(flavor)
                        OPTIONAL MATCH (bean)-[isFrom:IS_FROM]-(origin:Origin)
                        OPTIONAL MATCH (user:User)-[r:RATED]-(bean)
                        WITH bean, isFrom, origin, collect(tastes) as tastes, collect(flavor) as flavors, r, user, sum(flavorWeight * tastes.percentage) as weight
                        RETURN bean, isFrom, origin, tastes, flavors, r, user
                        ORDER BY weight DESC, bean.name ASC;
                        """,
                Map.of());
        return resultList(result);
    }

    public List<CoffeeBean> getCoffeeBeansSpecificFlavor(String flavor) {
        Session session = sessionFactory.openSession();

        Iterable<CoffeeBean> result = session.query(CoffeeBean.class, """
                        MATCH (b:CoffeeBean)-[:TASTES]->(:Flavor {name: $flavor})
                        MATCH (b)-[isFrom:IS_FROM]->(country)
                        MATCH (b)-[tastes:TASTES]->(flavor)
                        RETURN b, collect(isFrom), collect(country), collect(tastes), collect(flavor)
                        ORDER by b.name;
                        """,
                Map.of("flavor", flavor));

        return resultList(result);
    }

    public List<CoffeeBean> getCoffeeBeansWithUnexpectedFlavors() {
        Session session = sessionFactory.openSession();

        Iterable<CoffeeBean> result = session.query(CoffeeBean.class, """
                        MATCH (flavor)<-[:TASTES]-(b:CoffeeBean)-[isFrom:IS_FROM]->(country)
                        WHERE NOT (country)-[:IS_KNOWN_FOR]->(flavor)
                        WITH b, country, isFrom
                        MATCH (b)-[tastes:TASTES]->(flavor)
                        RETURN b, collect(isFrom), collect(country), collect(tastes), collect(flavor)
                        ORDER by b.name;
                        """,
                Map.of());

        return resultList(result);
    }

    private List<CoffeeBean> resultList(Iterable<CoffeeBean> result) {
        ArrayList<CoffeeBean> coffeeBeans = new ArrayList<>();
        result.forEach(coffeeBeans::add);
        return coffeeBeans;
    }

    public void createBean(String name, String origin, Map<String, Double> flavors) {
        Session session = sessionFactory.openSession();
        runInTransaction(() -> {

            CoffeeBean bean = new CoffeeBean();
            bean.name = name;

            verifyNameNotExists(name, session);

            setOrigin(origin, session, bean);
            addFlavorProfiles(flavors, session, bean);

            verifyFlavorPercentages(bean);

            session.save(bean);

        }, session);
    }

    public void updateBeanFlavors(UUID uuid, Map<String, Double> flavors) {
        Session session = sessionFactory.openSession();

        runInTransaction(() -> {
            CoffeeBean bean = session.load(CoffeeBean.class, uuid);
            if (bean == null)
                throw new IllegalArgumentException("Could not find bean with id " + uuid);

            bean.flavorProfiles.clear();
            addFlavorProfiles(flavors, session, bean);
            verifyFlavorPercentages(bean);

            session.save(bean);
        }, session);
    }

    public void deleteBean(UUID uuid) {
        Session session = sessionFactory.openSession();
        runInTransaction(() -> session.delete(session.load(CoffeeBean.class, uuid)), session);
    }

    private void setOrigin(String name, Session session, CoffeeBean bean) {
        Origin origin = session.load(Origin.class, name);
        if (origin == null)
            throw new IllegalArgumentException("Origin with name " + name + " not found");

        bean.origin = origin;
    }

    private void addFlavorProfiles(Map<String, Double> flavors, Session session, CoffeeBean bean) {
        flavors.forEach((f, p) -> {
            Flavor flavor = session.load(Flavor.class, f);
            if (flavor == null)
                throw new IllegalArgumentException("Flavor with " + f + " not found");

            FlavorProfile profile = new FlavorProfile(flavor, p);
            profile.bean = bean;
            bean.flavorProfiles.add(profile);
        });
    }

    private void verifyNameNotExists(String name, Session session) {
        CoffeeBean existing = session.load(CoffeeBean.class, name);
        if (existing != null)
            throw new IllegalArgumentException("Bean with name already exists");
    }

    private void verifyFlavorPercentages(CoffeeBean bean) {
        double sum = bean.flavorProfiles.stream()
                .mapToDouble(f -> f.percentage)
                .sum();
        if (Math.abs(1 - sum) >= 0.001)
            throw new IllegalArgumentException("Flavor percentages don't add up to 100% (1.0)");
    }

    public void rateBean(UUID uuid, int rating) {
        if (rating < 1 || rating > 3)
            throw new IllegalArgumentException("Can only rate a bean as 1, 2, or 3 stars");

        Session session = sessionFactory.openSession();

        runInTransaction(() -> {
            CoffeeBean bean = session.load(CoffeeBean.class, uuid);
            User user = session.load(User.class, "User");
            if (bean == null)
                throw new IllegalArgumentException("Could not find bean with id " + uuid);
            if (user == null)
                throw new IllegalArgumentException("Could not find user");

            if (bean.userRating == null)
                bean.userRating = new UserRating(user, bean);
            bean.userRating.rating = rating;

            session.save(bean, 1);
        }, session);
    }

    private void runInTransaction(Runnable runnable, Session session) {
        Transaction transaction = session.beginTransaction();
        try {
            runnable.run();
            transaction.commit();
            transaction.close();
        } catch (RuntimeException e) {
            System.err.println("Could not execute transaction: " + e);
            transaction.rollback();
            throw e;
        }
    }
}
