package com.sebastian_daschner.coffee.user.boundary;

import com.sebastian_daschner.coffee.beans.boundary.CoffeeBeans;
import com.sebastian_daschner.coffee.beans.entity.CoffeeBean;
import com.sebastian_daschner.coffee.beans.entity.CoffeeBeanRating;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("profile")
@Produces(MediaType.TEXT_HTML)
public class UserPageController {

    @Location("profile.html")
    Template profile;

    @Inject
    CoffeeBeans coffeeBeans;

    @GET
    public TemplateInstance userPage() {
        List<CoffeeBeanRating> beanRatings = coffeeBeans.getCoffeeBeanRatings();
        List<CoffeeBean> recommendedBeans = coffeeBeans.getRecommendedBeans();
        List<CoffeeBean> untestedBeans = coffeeBeans.getUntestedCoffeeBeans();

        return profile.data("beanRatings", beanRatings)
                .data("recommendedBeans", recommendedBeans)
                .data("untestedBeans", untestedBeans);
    }

}
