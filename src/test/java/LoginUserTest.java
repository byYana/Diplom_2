import ForUser.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class LoginUserTest {
    OldUser oldUser; // пользователь для авторизации
    NewUser newUser;  // пользователь для регистрации
    OldUser defectUser; // пользователь с неверными данными
    String accessToken; // токен
    Response responseLogin;  // ответ при авторизации
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
        if (accessToken != null) {
            UserAPI.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем.")
    public void checkLoginUser() {
        responseLogin = UserAPI.loginUser(oldUser);
        success = responseLogin.then().statusCode(SC_OK).extract().body().as(Login.class).getSuccess();
        assertEquals("true", success);
    }

    @Test
    @DisplayName("Логин с неверным почтой.")
    public void checkLoginDefectEmail() {
        defectUser = new OldUser(newUser.getEmail(), newUser.getPassword());
        defectUser.setRandomEmail();
        responseLogin = UserAPI.loginUser(defectUser);
        // Проверяем код ответа и поле ответа "success"
        success = responseLogin.then().statusCode(SC_UNAUTHORIZED).extract().body().as(Mistake.class).getSuccess();
        assertEquals("false", success);
    }

    @Test
    @DisplayName("Логин с неверным паролем.")
    public void checkLoginDefectPassword() {
        defectUser = new OldUser(newUser.getEmail(), newUser.getPassword());
        defectUser.setRandomPassword();
        responseLogin = UserAPI.loginUser(defectUser);
        // Проверяем код ответа и поле ответа "success"
        success = responseLogin.then().statusCode(SC_UNAUTHORIZED).extract().body().as(Mistake.class).getSuccess();
        assertEquals("false", success);
    }
}
