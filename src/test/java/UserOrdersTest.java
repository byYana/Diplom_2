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

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class UserOrdersTest {
    OldUser oldUser;
    String accessToken;
    Response responseInformation;

    @Before
    public void doBefore() {
        NewUser newUser = NewUser.getRandomUser();
        Response responseCreate = UserAPI.createUser(newUser);
        accessToken = responseCreate.then().extract().body().as(Login.class).getAccessToken();
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
    }

    @After
    public void doAfter() {
        UserAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя.")
    public void checkUserOrdersLogin() {
       // accessToken = UserAPI.loginUser(oldUser).then().extract().body().as(Login.class).getAccessToken();
        responseInformation = OrderAPI.informationOrders(accessToken);
        responseInformation.then().statusCode(SC_OK);
        assertEquals("true", responseInformation.then().extract().body().as(Login.class).getSuccess());
    }

    @Test
    @DisplayName("Получение заказов не авторизованного пользователя.")
    public void checkUserOrdersLogout() {
        responseInformation = OrderAPI.informationOrders("");
        responseInformation.then().statusCode(SC_UNAUTHORIZED);
        assertEquals("false", responseInformation.then().extract().body().as(Login.class).getSuccess());
    }
}