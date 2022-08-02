import ForUser.Login;
import ForUser.Mistake;
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
    NewUser user; // пользователь для регистрации
    String accessToken;  // токен
    String success;  //поле каждого ответа о корректности
    Response responseCreate;  // ответ при создании пользователя

    @Before
    public void doBefore() {
        user = NewUser.getRandomUser();
    }

    @Test
    @DisplayName("Создание уникального пользователя.")
    public void checkCreateUser() {
        responseCreate = UserAPI.createUser(user);
        accessToken = responseCreate.then().statusCode(SC_OK).extract().body().as(Login.class).getAccessToken();
        success = responseCreate.then().statusCode(SC_OK).extract().body().as(Login.class).getSuccess();
        assertEquals("true", success);
        UserAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован.")
    public void checkDoobleCreateUser() {
        responseCreate = UserAPI.createUser(user);
        accessToken = responseCreate.then().statusCode(SC_OK).extract().body().as(Login.class).getAccessToken();
        responseCreate = UserAPI.createUser(user);
        success = responseCreate.then().statusCode(SC_FORBIDDEN).extract().body().as(Mistake.class).getSuccess();
        assertEquals("false", success);
        UserAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание пользователя без почты.")
    public void checkDefectEmail() {
        user.setEmail(null);
        responseCreate = UserAPI.createUser(user);
        success = responseCreate.then().statusCode(SC_FORBIDDEN).extract().body().as(Mistake.class).getSuccess();
        assertEquals("false", success);
    }

    @Test
    @DisplayName("Создание пользователя без пароля.")
    public void checkDefectPassword() {
        user.setPassword(null);
        responseCreate = UserAPI.createUser(user);
        success = responseCreate.then().statusCode(SC_FORBIDDEN).extract().body().as(Mistake.class).getSuccess();
        assertEquals("false", success);
    }

    @Test
    @DisplayName("Создание пользователя без имени.")
    public void checkDefectName() {
        user.setName(null);
        responseCreate = UserAPI.createUser(user);
        success = responseCreate.then().statusCode(SC_FORBIDDEN).extract().body().as(Mistake.class).getSuccess();
        assertEquals("false", success);
    }
}
