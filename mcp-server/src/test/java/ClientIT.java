import com.sebastian_daschner.coffee.mcp.MCPServerFavoriteCoffee;
import com.sebastian_daschner.coffee.mcp.entity.CoffeeBean;
import com.sebastian_daschner.coffee.mcp.entity.CoffeeBeanRating;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class ClientIT {

    @Inject
    MCPServerFavoriteCoffee mcpServer;

    @Test
    void list_coffee_beans() {
        List<CoffeeBean> coffeeBeans = mcpServer.list_coffee_beans(null);
        assertThat(coffeeBeans.size()).isGreaterThan(80);
    }

    @Test
    void list_fruity_coffee_beans() {
        List<CoffeeBean> coffeeBeans = mcpServer.list_coffee_beans("Fruity");
        assertThat(coffeeBeans.size()).isGreaterThan(60);
    }

    @Test
    void rate_and_list_rated_coffee_beans() {
        List<CoffeeBeanRating> ratings = mcpServer.list_rated_coffee_beans();
        int prevSize = ratings.size();

        CoffeeBean bean = mcpServer.list_recommended_coffee_beans().get(0);
        mcpServer.rate_bean(bean.uuid, 3);

        ratings = mcpServer.list_rated_coffee_beans();
        int newSize = ratings.size();
        assertThat(newSize).isEqualTo(prevSize + 1);
    }

}
