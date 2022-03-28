package com.sebastian_daschner.coffee.beans.boundary;

import com.sebastian_daschner.coffee.beans.entity.CoffeeBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

@Path("beans")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoffeeBeansResource {

    @Inject
    CoffeeBeans coffeeBeans;

    @GET
    public List<CoffeeBean> beans(@QueryParam("flavor") @DefaultValue("") String flavor) {
        if (!flavor.isBlank())
            return coffeeBeans.getCoffeeBeansSpecificFlavor(flavor);
        return coffeeBeans.getCoffeeBeans();
    }

    @POST
    public void create(@Valid @NotNull JsonObject json) {
        String name = json.getString("name");
        String origin = json.getString("origin");

        Map<String, Double> flavors = json.getJsonArray("flavorProfiles")
                .getValuesAs(JsonObject.class).stream()
                .collect(toMap(j -> j.getString("flavor"), j -> j.getJsonNumber("percentage").doubleValue()));

        coffeeBeans.createBean(name, origin, flavors);
    }

    @GET
    @Path("{uuid}")
    public CoffeeBean bean(@PathParam("uuid") UUID uuid) {
        CoffeeBean bean = coffeeBeans.getCoffeeBean(uuid);
        if (bean == null)
            throw new NotFoundException();
        return bean;
    }

    @PATCH
    @Path("{uuid}")
    public Response updateBean(@PathParam("uuid") UUID uuid, JsonObject json) {

        Map<String, Double> flavors = json.getJsonArray("flavorProfiles")
                .getValuesAs(JsonObject.class).stream()
                .collect(toMap(j -> j.getString("flavor"), j -> j.getJsonNumber("percentage").doubleValue()));

        coffeeBeans.updateBeanFlavors(uuid, flavors);

        return Response.noContent().build();
    }

    @DELETE
    @Path("{uuid}")
    public Response deleteBean(@PathParam("uuid") UUID uuid) {
        coffeeBeans.deleteBean(uuid);
        return Response.noContent().build();
    }

    @PUT
    @Path("{uuid}/ratings")
    public void rateBean(@PathParam("uuid") UUID uuid, JsonObject json) {
        int rating = json.getJsonNumber("rating").intValue();
        coffeeBeans.rateBean(uuid, rating);
    }

}
