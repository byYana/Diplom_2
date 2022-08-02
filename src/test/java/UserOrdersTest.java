import ForOrder.OrderAPI;
import ForUser.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        responseCreate = UserAPI.createUser(newUser);
        accessToken = responseCreate.then().statusCode(SC_OK).extract().body().as(Login.class).getAccessToken();
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
    }

    @After
    public void doAfter() {
        if (accessToken.equals(null)) {
            accessToken = UserAPI.refreshToken(oldUser).then().extract().body().as(Login.class).getAccessToken();
        }
        UserAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя.")
    public void checkUserOrdersLogin() {
        responseInfo = OrderAPI.informationOrders(accessToken);
        success = responseInfo.then().statusCode(SC_OK).extract().body().as(Login.class).getSuccess();
        assertEquals("true", success);
    }

    @Test
    @DisplayName("Получение заказов не авторизованного пользователя.")
    public void checkUserOrdersLogout() {
        responseInfo = OrderAPI.informationOrders("");
        success = responseInfo.then().statusCode(SC_UNAUTHORIZED).extract().body().as(Mistake.class).getSuccess();
        assertEquals("false", success);
    }
}