import ForOrder.Order;
import ForOrder.OrderAPI;
import ForUser.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class UserOrdersTest {
    NewUser newUser; // пользователь для регистрации
    OldUser oldUser; // пользователь для авторизации
    String accessToken; // токен
    Response responseInfo; // ответ при получении информации
    String success; //поле каждого ответа о корректности
    Response responseCreate;  // ответ при создании пользователя
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
    @DisplayName("Получение заказов авторизованного пользователя.")
    public void checkUserOrdersLogin() {
        UserAPI.loginUser(oldUser);
        Order order = new Order(ingredients);
        Response responseOrder = OrderAPI.createOrder(order, accessToken);
        responseInfo = OrderAPI.informationOrders(accessToken);
        // Проверяем код ответа и поле ответа "success"
        success = responseInfo.then().statusCode(SC_OK).extract().body().as(Login.class).getSuccess();
        assertEquals("true", success);
        // Проверяем, что у пользователя есть нужный заказ
        String stringOrder = responseOrder.getBody().asString();
        JsonPath jsonPath = new JsonPath(stringOrder);
        int expected = jsonPath.getInt("order.number");
        String stringInfo = responseInfo.getBody().asString();
        JsonPath jsonPathToo = new JsonPath(stringInfo);
        int actual = jsonPathToo.getInt("orders[0].number");
        assertEquals(expected, actual);
        // Проверяем, что вернулся 1 заказ
        int ordersSize = jsonPathToo.getList("orders").size();
        assertEquals(1, ordersSize);
    }

    @Test
    @DisplayName("Получение заказов не авторизованного пользователя.")
    public void checkUserOrdersLogout() {
        responseInfo = OrderAPI.informationOrders("");
        // Проверяем код ответа и поле ответа "success"
        success = responseInfo.then().statusCode(SC_UNAUTHORIZED).extract().body().as(Mistake.class).getSuccess();
        assertEquals("false", success);
    }
}