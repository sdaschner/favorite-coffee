package com.sebastian_daschner.coffee.beans.boundary;

import com.sebastian_daschner.coffee.beans.entity.CoffeeBean;
import com.sebastian_daschner.coffee.beans.entity.FlavorProfile;
import com.sebastian_daschner.coffee.beans.entity.SortCriteria;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class IndexController {

    @Location("index.html")
    Template index;

    @Inject
    CoffeeBeans coffeeBeans;

    @GET
    public TemplateInstance index(@QueryParam("sortBy") SortCriteria sortCriteria) {
        List<CoffeeBean> beans = coffeeBeans.getCoffeeBeans(sortCriteria);

        SortCriteria sortBy = sortCriteria == null ? SortCriteria.NAME : sortCriteria;
        return index.data("beans", beans)
                .data("sortBy", sortBy);
    }

    @TemplateExtension
    static String percentageToString(FlavorProfile profile) {
        return String.format("%,.1f%%", profile.percentage * 100d);
    }

}
