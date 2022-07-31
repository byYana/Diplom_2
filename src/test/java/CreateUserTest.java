import ForUser.Login;
import ForUser.NewUser;
import ForUser.UserAPI;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;


public class CreateUserTest {
    NewUser user;
    Response loginResponse;
    String accessToken;

    @Before
    public void doBefore() {
        user = NewUser.getRandomUser();
    }

    @Test
    @DisplayName("Создание уникального пользователя.")
    public void checkCreateUser() {
        loginResponse = UserAPI.createUser(user);
        loginResponse.then().statusCode(SC_OK);
        accessToken = loginResponse.then().extract().body().as(Login.class).getAccessToken();
        assertEquals("true", loginResponse.then().extract().body().as(Login.class).getSuccess());
        UserAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован.")
    public void checkDoobleCreateUser() {
        loginResponse = UserAPI.createUser(user);
        accessToken = loginResponse.then().extract().body().as(Login.class).getAccessToken();
        loginResponse = UserAPI.createUser(user);
        loginResponse.then().statusCode(SC_FORBIDDEN);
        assertEquals("false", loginResponse.then().extract().body().as(Login.class).getSuccess());
        UserAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание пользователя без почты.")
    public void checkDefectEmail() {
        user.setEmail(null);
        loginResponse = UserAPI.createUser(user);
        loginResponse.then().statusCode(SC_FORBIDDEN);
        assertEquals("false", loginResponse.then().extract().body().as(Login.class).getSuccess());
    }

    @Test
    @DisplayName("Создание пользователя без пароля.")
    public void checkDefectPassword() {
        user.setPassword(null);
        loginResponse = UserAPI.createUser(user);
        loginResponse.then().statusCode(SC_FORBIDDEN);
        assertEquals("false", loginResponse.then().extract().body().as(Login.class).getSuccess());
    }

    @Test
    @DisplayName("Создание пользователя без имени.")
    public void checkDefectName() {
        user.setName(null);
        loginResponse = UserAPI.createUser(user);
        loginResponse.then().statusCode(SC_FORBIDDEN);
        assertEquals("false", loginResponse.then().extract().body().as(Login.class).getSuccess());
    }
}
