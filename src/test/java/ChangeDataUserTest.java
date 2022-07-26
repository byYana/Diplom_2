import ForUser.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class ChangeDataUserTest {
    OldUser oldUser; // пользователь для авторизации
    NewUser newUser; // пользователь для регистрации
    String accessToken; // токен
    Response responseCreate; // ответ при создании пользователя
    String message = "You should be authorised"; //сообщение об ошибке
    Response responseLogin; // ответ при авторизации
    Response responseInfo; // ответ при смене данных
    String success; //поле каждого ответа о корректности
    String messageMistake;  // поле об ошибке

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        responseCreate = UserAPI.createUser(newUser);
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
        accessToken = responseCreate.then().statusCode(SC_OK).extract().body().as(Login.class).getAccessToken();
    }

    @After
    public void doAfter() {
        if (accessToken != null) {
            UserAPI.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Авторизованный пользователь меняет имя.")
    public void checkChangeName() {
        responseLogin = UserAPI.loginUser(oldUser);
        newUser.setRandomName();
        responseInfo = UserAPI.changeInformation(accessToken, newUser);
        // Проверяем код ответа и поле ответа "success"
        success = responseInfo.then().statusCode(SC_OK).extract().body().as(Login.class).getSuccess();
        assertEquals("true", success);
    }

    @Test
    @DisplayName("Авторизованный пользователь меняет почту.")
    public void checkChangeEmail() {
        responseLogin = UserAPI.loginUser(oldUser);
        newUser.setRandomEmail();
        responseInfo = UserAPI.changeInformation(accessToken, newUser);
        // Проверяем код ответа и поле ответа "success"
        success = responseInfo.then().statusCode(SC_OK).extract().body().as(Login.class).getSuccess();
        assertEquals("true", success);
    }

    @Test
    @DisplayName("Авторизованный пользователь меняет пароль.")
    public void checkChangePassword() {
        responseLogin = UserAPI.loginUser(oldUser);
        newUser.setRandomPassword();
        responseInfo = UserAPI.changeInformation(accessToken, newUser);
        // Проверяем код ответа и поле ответа "success"
        success = responseInfo.then().statusCode(SC_OK).extract().body().as(Login.class).getSuccess();
        assertEquals("true", success);
    }

    @Test
    @DisplayName("Не авторизованный пользователь меняет имя.")
    public void checkDefectChangeName() {
        newUser.setRandomName();
        responseInfo = UserAPI.changeInformation("", newUser);
        success = responseInfo.then().statusCode(SC_UNAUTHORIZED).extract().body().as(Mistake.class).getSuccess();
        // Проверяем код ответа и поле ответа "success" и "message"
        messageMistake = responseInfo.then().statusCode(SC_UNAUTHORIZED).extract().body().as(Mistake.class).getMessage();
        assertEquals("false", success);
        assertEquals(message, messageMistake);
    }

    @Test
    @DisplayName("Не авторизованный пользователь меняет почту.")
    public void checkDefectChangeEmail() {
        newUser.setRandomEmail();
        responseInfo = UserAPI.changeInformation("", newUser);
        success = responseInfo.then().statusCode(SC_UNAUTHORIZED).extract().body().as(Mistake.class).getSuccess();
        // Проверяем код ответа и поле ответа "success" и "message"
        messageMistake = responseInfo.then().statusCode(SC_UNAUTHORIZED).extract().body().as(Mistake.class).getMessage();
        assertEquals("false", success);
        assertEquals(message, messageMistake);
    }

    @Test
    @DisplayName("Не авторизованный пользователь меняет пароль.")
    public void checkDefectChangePassword() {
        newUser.setRandomPassword();
        responseInfo = UserAPI.changeInformation("", newUser);
        success = responseInfo.then().statusCode(SC_UNAUTHORIZED).extract().body().as(Mistake.class).getSuccess();
        // Проверяем код ответа и поле ответа "success" и "message"
        messageMistake = responseInfo.then().statusCode(SC_UNAUTHORIZED).extract().body().as(Mistake.class).getMessage();
        assertEquals("false", success);
        assertEquals(message, messageMistake);
    }
}

