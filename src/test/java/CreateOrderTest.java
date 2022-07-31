import ForOrder.Order;
import ForOrder.OrderAPI;
import ForUser.Login;
import ForUser.NewUser;
import ForUser.OldUser;
import ForUser.UserAPI;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class CreateOrderTest {
    NewUser newUser;
    OldUser oldUser;
    String accessToken;
    Response responseOrder;
    Response loginCreate;
    List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa75", "61c0c5a71d1f82001bdaaa75", "61c0c5a71d1f82001bdaaa6e", "61c0c5a71d1f82001bdaaa6c");

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        loginCreate = UserAPI.createUser(newUser);
        accessToken = loginCreate.then().extract().body().as(Login.class).getAccessToken();
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
    }

    @After
    public void doAfter() {
        UserAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание заказа авторизованного пользователя с ингредиентами.")
    public void checkOrderLoginWithIngredients() {
        accessToken = UserAPI.loginUser(oldUser).then().extract().body().as(Login.class).getAccessToken();
        Order order = new Order(ingredients);
        responseOrder = OrderAPI.createOrder(order);
        responseOrder.then().statusCode(SC_OK);
        assertEquals("true", responseOrder.then().extract().body().as(Login.class).getSuccess());

    }

    @Test
    @DisplayName("Создание заказа не авторизованного пользователя с ингредиентами.")
    public void checkOrderLogout() {
        /*В документации не указано, что должно приходить в таком случае.
        Аналогичный запрос отправила в Postman, где так же вернулся 200 код и true.
        В самом приложении нельзя добавить заказ без авторизации.*/
        Order order = new Order(ingredients);
        responseOrder = OrderAPI.createOrder(order);
        responseOrder.then().statusCode(SC_OK);
        assertEquals("true", responseOrder.then().extract().body().as(Login.class).getSuccess());
    }

    @Test
    @DisplayName("Создание заказа авторизованного пользователя без ингредиентами.")
    public void checkOrderWithoutIngredients() {
        UserAPI.loginUser(oldUser);
        Order order = new Order(null);
        responseOrder = OrderAPI.createOrder(order);
        responseOrder.then().statusCode(SC_BAD_REQUEST);
        assertEquals("false", responseOrder.then().extract().body().as(Login.class).getSuccess());
    }

    @Test
    @DisplayName("Создание авторизованного пользователя с неизвестным id ингредиентом.")
    public void checkOrderWithDefectIngredients() {
        UserAPI.loginUser(oldUser);
        Order order = new Order(Arrays.asList("61c0c5a71d1f82001bda"));
        responseOrder = OrderAPI.createOrder(order);
        responseOrder.then().statusCode(SC_INTERNAL_SERVER_ERROR);
        assertEquals("false", responseOrder.then().extract().body().as(Login.class).getSuccess());
    }
}