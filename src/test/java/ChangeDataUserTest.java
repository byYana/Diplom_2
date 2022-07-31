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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class ChangeDataUserTest {
    OldUser oldUser;
    NewUser newUser;
    String accessToken;
    Response responseCreate;
    Response response;
    String message = "You should be authorised";

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        responseCreate = UserAPI.createUser(newUser);
        accessToken = responseCreate.then().extract().body().as(Login.class).getAccessToken();
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
    }

    @After
    public void doAfter() {
        UserAPI.deleteUser(accessToken);

    }

    @Test
    @DisplayName("Авторизованный пользователь меняет имя.")
    public void checkChangeName() {
        response = UserAPI.loginUser(oldUser);
        newUser.setRandomName();
        accessToken = response.then().extract().body().as(Login.class).getAccessToken();
        response = UserAPI.changeInformation(accessToken, newUser);
        response.then().statusCode(SC_OK);
        assertEquals("true", response.then().extract().body().as(Login.class).getSuccess());
    }

    @Test
    @DisplayName("Авторизованный пользователь меняет почту.")
    public void checkChangeEmail() {
        response = UserAPI.loginUser(oldUser);
        newUser.setRandomEmail();
        accessToken = response.then().extract().body().as(Login.class).getAccessToken();
        response = UserAPI.changeInformation(accessToken, newUser);
        response.then().statusCode(SC_OK);
        assertEquals("true", response.then().extract().body().as(Login.class).getSuccess());
    }

    @Test
    @DisplayName("Авторизованный пользователь меняет пароль.")
    public void checkChangePassword() {
        response = UserAPI.loginUser(oldUser);
        newUser.setRandomPassword();
        accessToken = response.then().extract().body().as(Login.class).getAccessToken();
        response = UserAPI.changeInformation(accessToken, newUser);
        response.then().statusCode(SC_OK);
        assertEquals("true", response.then().extract().body().as(Login.class).getSuccess());

    }

    @Test
    @DisplayName("Не авторизованный пользователь меняет имя.")
    public void checkDefectChangeName() {
        newUser.setRandomName();
        response = UserAPI.changeInformation("", newUser);
        response.then().statusCode(SC_UNAUTHORIZED);
        response.then().assertThat().body("message", equalTo(message));
        assertEquals("false", response.then().extract().body().as(Login.class).getSuccess());
    }

    @Test
    @DisplayName("Не авторизованный пользователь меняет почту.")
    public void checkDefectChangeEmail() {       // - без авторизации, меняем почту
        newUser.setRandomEmail();
        response = UserAPI.changeInformation("", newUser);
        response.then().statusCode(SC_UNAUTHORIZED);
        response.then().assertThat().body("message", equalTo(message));
        assertEquals("false", response.then().extract().body().as(Login.class).getSuccess());
    }

    @Test
    @DisplayName("Не авторизованный пользователь меняет пароль.")
    public void checkDefectChangePassword() {    // - без авторизации, меняем пароль
        newUser.setRandomPassword();
        response = UserAPI.changeInformation("", newUser);
        response.then().statusCode(SC_UNAUTHORIZED);
        response.then().assertThat().body("message", equalTo(message));
        assertEquals("false", response.then().extract().body().as(Login.class).getSuccess());

    }
}

