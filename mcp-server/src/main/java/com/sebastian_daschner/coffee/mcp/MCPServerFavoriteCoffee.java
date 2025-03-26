package com.sebastian_daschner.coffee.mcp;

import com.sebastian_daschner.coffee.mcp.entity.BeanRatingRequest;
import com.sebastian_daschner.coffee.mcp.entity.CoffeeBean;
import com.sebastian_daschner.coffee.mcp.entity.CoffeeBeanRating;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@ApplicationScoped
public class MCPServerFavoriteCoffee {

    @Inject
    @ConfigProperty(name = "server.url")
    String baseUrl;

    @RestClient
    FavoriteCoffeeClient client;

    @Startup
    void init() {
        Log.info("Starting Favorite Coffee MCP server, application URL: " + baseUrl);
    }

    @Tool(description = "Lists all coffee beans")
    public List<CoffeeBean> list_coffee_beans(@ToolArg(description = "The coffee flavor to filter by. The flavors are: Spicy, Chocolaty, Fruity, Flowery, Buttery, Earthy, Winey, Nutty", required = false) String flavor) {
        return client.beans(flavor);
    }

    @Tool(description = "Lists all coffee bean names that the user has rated")
    public List<CoffeeBeanRating> list_rated_coffee_beans() {
        return client.ratedBeans();
    }

    @Tool(description = "Lists all coffee bean recommendations. These are beans that haven't been rated by the user and likely match their taste due to what they rated positively before")
    public List<CoffeeBean> list_recommended_coffee_beans() {
        return client.recommendedBeans();
    }

    @Tool(description = "Rates a bean in stars of 1 to 3 (1 means 1 star, i.e. not good or not my taste, 2 means 2 stars i.e. average or OK, 3 means 3 stars, i.e. very good or exactly my taste)")
    public String rate_bean(@ToolArg(description = "The UUID of the coffee bean to rate") String uuid, @ToolArg(description = "The rating of 1 to 3") int rating) {
        client.rateBean(UUID.fromString(uuid), new BeanRatingRequest(rating));
        return "OK";
    }

}
