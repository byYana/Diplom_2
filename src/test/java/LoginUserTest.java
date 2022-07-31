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

public class LoginUserTest {
    OldUser oldUser;
    NewUser newUser;
    String accessToken;
    Response loginCreate;
    Response response;

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        loginCreate = UserAPI.createUser(newUser);
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
        accessToken = loginCreate.then().extract().body().as(Login.class).getAccessToken();
    }

    @After
    public void doAfter() {
        UserAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Логин под существующим пользователем.")
    public void checkLoginUser() {
        response = UserAPI.loginUser(oldUser);
        response.then().statusCode(SC_OK);
        accessToken = response.then().extract().body().as(Login.class).getAccessToken();
        assertEquals("true", response.then().extract().body().as(Login.class).getSuccess());
    }


    @Test
    @DisplayName("Логин с неверным почтой.")
    public void checkLoginDefectEmail() {
        oldUser.setRandomEmail();
        response = UserAPI.loginUser(oldUser);
        response.then().statusCode(SC_UNAUTHORIZED);
        assertEquals("false", response.then().extract().body().as(Login.class).getSuccess());
    }

    @Test
    @DisplayName("Логин с неверным паролем.")
    public void checkLoginDefectPassword() {
        oldUser.setRandomPassword();
        response = UserAPI.loginUser(oldUser);
        response.then().statusCode(SC_UNAUTHORIZED);
        assertEquals("false", response.then().extract().body().as(Login.class).getSuccess());
    }
}
