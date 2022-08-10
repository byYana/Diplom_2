import ForOrder.Order;
import ForOrder.OrderAPI;
import ForUser.*;
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
    NewUser newUser; // пользователь для регистрации
    OldUser oldUser; // пользователь для авторизации
    String accessToken; // токен
    Response responseOrder; //ответ при заказе
    Response responseCreate;  // ответ при создании пользователя
    Response responseLogin; // ответ при авторизации
    String success; //поле каждого ответа о корректности
    Order order; // заказ
    List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa75", "61c0c5a71d1f82001bdaaa75", "61c0c5a71d1f82001bdaaa6e", "61c0c5a71d1f82001bdaaa6c"); // список ингредиентов

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        responseCreate = UserAPI.createUser(newUser);
        accessToken = responseCreate.then().statusCode(SC_OK).extract().body().as(Login.class).getAccessToken();
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
    }

    @After
    public void doAfter() {
        if (accessToken != null) {
            UserAPI.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа авторизованного пользователя с ингредиентами.")
    public void checkOrderLoginWithIngredients() {
        responseLogin = UserAPI.loginUser(oldUser);
        order = new Order(ingredients);
        responseOrder = OrderAPI.createOrder(order, accessToken);
        // Проверяем код ответа и поле ответа "success"
        success = responseOrder.then().statusCode(SC_OK).extract().body().as(Login.class).getSuccess();
        assertEquals("true", success);
    }


    @Test
    @DisplayName("Создание заказа не авторизованного пользователя с ингредиентами.")
    public void checkOrderLogout() {
        /*В документации не указано, что должно приходить в таком случае.
        Аналогичный запрос отправила в Postman, где так же вернулся 200 код и true.
        В самом приложении нельзя добавить заказ без авторизации.*/
        order = new Order(ingredients);
        responseOrder = OrderAPI.createOrder(order, accessToken);
        // Проверяем код ответа и поле ответа "success"
        success = responseOrder.then().statusCode(SC_OK).extract().body().as(Login.class).getSuccess();
        assertEquals("true", success);
    }

    @Test
    @DisplayName("Создание заказа авторизованного пользователя без ингредиентами.")
    public void checkOrderWithoutIngredients() {
        responseLogin = UserAPI.loginUser(oldUser);
        order = new Order(null);
        responseOrder = OrderAPI.createOrder(order, accessToken);
        // Проверяем код ответа и поле ответа "success"
        success = responseOrder.then().statusCode(SC_BAD_REQUEST).extract().body().as(Mistake.class).getSuccess();
        assertEquals("false", success);
    }

    @Test
    @DisplayName("Создание авторизованного пользователя с неизвестным id ингредиентом.")
    public void checkOrderWithDefectIngredients() {
        responseLogin = UserAPI.loginUser(oldUser);
        order = new Order(List.of("61c0c5a71d1f82001bda"));
        responseOrder = OrderAPI.createOrder(order, accessToken);
        // Проверяем код ответа
        responseOrder.then().statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}