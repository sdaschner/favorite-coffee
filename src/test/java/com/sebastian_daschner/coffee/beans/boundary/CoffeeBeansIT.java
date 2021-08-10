package com.sebastian_daschner.coffee.beans.boundary;

import com.sebastian_daschner.coffee.beans.entity.CoffeeBean;
import com.sebastian_daschner.coffee.beans.entity.Flavor;
import com.sebastian_daschner.coffee.beans.entity.FlavorProfile;
import com.sebastian_daschner.coffee.beans.entity.SortCriteria;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.session.Session;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoffeeBeansIT {

    private final CoffeeBeans coffeeBeans = new CoffeeBeansTD();

    @Test
    void testGetCoffeeBeans() {
        List<CoffeeBean> beans = coffeeBeans.getCoffeeBeans();
        assertThat(beans).hasSize(96);

        CoffeeBean bean = beans.get(0);
        assertThat(bean.name).isEqualTo("18 Rabbit Black Honey Process");
        assertThat(bean.origin.country).isEqualTo("Honduras");
        assertThat(bean.flavorProfiles).containsExactlyInAnyOrder(
                new FlavorProfile(new Flavor("Buttery"), 0.25),
                new FlavorProfile(new Flavor("Fruity"), 0.25),
                new FlavorProfile(new Flavor("Chocolaty"), 0.25),
                new FlavorProfile(new Flavor("Flowery"), 0.25));
    }

    @Test
    void testGetRecommendedCoffeeBeans() {
        List<CoffeeBean> beans = coffeeBeans.getCoffeeBeans(SortCriteria.RECOMMENDATION);
        assertThat(beans).hasSize(96);

        assertThat(beans).extracting("name").startsWith("Bela Vista Yellow Bourbon Honey");

        UUID uuid = beans.stream()
                .filter(b -> b.name.equals("Geisha Washed"))
                .map(b -> b.uuid)
                .findAny().orElse(null);

        coffeeBeans.rateBean(uuid, 3);

        beans = coffeeBeans.getCoffeeBeans(SortCriteria.RECOMMENDATION);
        assertThat(beans).extracting("name").startsWith("Geisha Washed");
    }

    @AfterEach
    @BeforeEach
    void clearRatings() {
        Session session = coffeeBeans.sessionFactory.openSession();
        session.query("MATCH (:User)-[r:RATED]-() DELETE r;", Map.of());
    }

}