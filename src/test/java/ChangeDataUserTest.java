import ForUser.NewUser;
import ForUser.OldUser;
import ForUser.UserAPI;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class ChangeDataUserTest {               //Изменение данных пользователя: все Ok
    OldUser oldUser;
    NewUser newUser;
    Response response;
    String accessToken;
    Response responseCreate;
    String message = "You should be authorised";

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        responseCreate = UserAPI.createUser(newUser);
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
        accessToken = responseCreate.jsonPath().getString("accessToken");
    }

    @After
    public void doAfter() {
        UserAPI.deleteUser(accessToken);

    }

    @Test
    public void checkChangeName() {           // ~~- с авторизацией, меняем имя~~
        Response responseLogin = UserAPI.loginUser(oldUser);
        newUser.setRandomName();
        response = UserAPI.changeInformation(accessToken, newUser);
        assertEquals(SC_OK, response.statusCode());
        responseLogin.then().assertThat().body("success", equalTo(true));
    }

    @Test
    public void checkChangeEmail() {           // ~~- с авторизацией, меняем почту~~
        Response responseLogin = UserAPI.loginUser(oldUser);
        newUser.setRandomEmail();
        response = UserAPI.changeInformation(accessToken, newUser);
        assertEquals(SC_OK, response.statusCode());
        responseLogin.then().assertThat().body("success", equalTo(true));
    }

    @Test
    public void checkChangePassword() {        // ~~- с авторизацией, меняем пароль~~
        Response responseLogin = UserAPI.loginUser(oldUser);
        newUser.setRandomPassword();
        response = UserAPI.changeInformation(accessToken, newUser);
        assertEquals(SC_OK, response.statusCode());
        responseLogin.then().assertThat().body("success", equalTo(true));
    }

    @Test
    public void checkDefectChangeName() {        // - без авторизации, меняем имя
        newUser.setRandomName();
        response = UserAPI.changeInformation("", newUser);
        assertEquals(SC_UNAUTHORIZED, response.statusCode());
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo(message));
    }

    @Test
    public void checkDefectChangeEmail() {       // - без авторизации, меняем почту
        newUser.setRandomEmail();
        response = UserAPI.changeInformation("", newUser);
        assertEquals(SC_UNAUTHORIZED, response.statusCode());
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo(message));
    }

    @Test
    public void checkDefectChangePassword() {    // - без авторизации, меняем пароль
        newUser.setRandomPassword();
        response = UserAPI.changeInformation("", newUser);
        assertEquals(SC_UNAUTHORIZED, response.statusCode());
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo(message));
    }
}

