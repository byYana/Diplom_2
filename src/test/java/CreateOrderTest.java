import ForOrder.Order;
import ForOrder.OrderAPI;
import ForUser.NewUser;
import ForUser.OldUser;
import ForUser.RefreshToken;
import ForUser.UserAPI;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.core.Is.is;

public class CreateOrderTest {                //Создание заказа:
    NewUser newUser;
    Response responseCreate;
    OldUser oldUser;
    String accessToken;
    RefreshToken refreshToken;
    List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa75", "61c0c5a71d1f82001bdaaa75", "61c0c5a71d1f82001bdaaa6e", "61c0c5a71d1f82001bdaaa6c");

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        responseCreate = UserAPI.createUser(newUser);
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
        accessToken = responseCreate.jsonPath().getString("accessToken");
        refreshToken = new RefreshToken(responseCreate.jsonPath().getString("refreshToken"));
    }

    @After
    public void doAfter() {
        UserAPI.deleteUser(accessToken);
    }

    @Test
    public void checkOrderLoginWithIngredients() {              // - с авторизацией, с ингредиентами,
        UserAPI.loginUser(oldUser);
        Order order = new Order(ingredients);
        Response responseOrder = OrderAPI.createOrder(order);
        responseOrder.then().statusCode(SC_OK);
        responseOrder.then().assertThat().body("success", is(true));
    }

    @Test
    public void checkOrderLogout() {                            // - без авторизации,
        /*В документации не указано, что должно приходить в таком случае.
        Аналогичный запрос отправила в Postman, где так же вернулся 200 код и true.
        В самом приложении нельзя добавить заказ без авторизации.*/
        Order order = new Order(ingredients);
        Response responseOrder = OrderAPI.createOrder(order);
        responseOrder.then().statusCode(SC_OK);
        responseOrder.then().assertThat().body("success", is(true));
    }

    @Test
    public void checkOrderWithoutIngredients() {                // - без ингредиентов,
        UserAPI.loginUser(oldUser);
        Order order = new Order(null);
        Response responseOrder = OrderAPI.createOrder(order);
        responseOrder.then().statusCode(SC_BAD_REQUEST);
        responseOrder.then().assertThat().body("success", is(false));
    }

    @Test
    public void checkOrderWithDefectIngredients() {            // - с неверным хешем ингредиентов.
        UserAPI.loginUser(oldUser);
        Order order = new Order(Arrays.asList("61c0c5a71d1f82001bda"));
        Response responseOrder = OrderAPI.createOrder(order);
        responseOrder.then().statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}