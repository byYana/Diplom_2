import ForUser.NewUser;
import ForUser.OldUser;
import ForUser.UserAPI;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class LoginUserTest {
    OldUser oldUser;
    NewUser newUser;
    String refreshToken;
    String accessToken;
    Response responseCreate;

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        responseCreate = UserAPI.createUser(newUser);
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
        accessToken = responseCreate.jsonPath().getString("accessToken");
        refreshToken = responseCreate.jsonPath().getJsonObject("refreshToken");
        UserAPI.logoutUser(refreshToken);
    }

    @After
    public void doAfter() {
        //UserAPI.deleteUser(response.jsonPath().getString("accessToken"));
        assertEquals(SC_ACCEPTED, UserAPI.deleteUser(accessToken).statusCode());
    }

    @Test
    public void checkCodeLoginUser() {              //проверяем код ответа
        Response responseLogin = UserAPI.loginUser(oldUser);
        assertEquals(SC_OK, responseLogin.statusCode());
    }

    @Test
    public void checkSuccessLoginUser() {              //проверяем тело ответа по значению "success"
        Response responseLogin = UserAPI.loginUser(oldUser);
        assertEquals(true, responseLogin.jsonPath().getJsonObject("success"));
    }

    @Test
    public void checkCodeLoginDefectEmail() {              //проверяем код ответа
        oldUser.setRandomEmail();
        Response responseLogin = UserAPI.loginUser(oldUser);
        assertEquals(SC_UNAUTHORIZED, responseLogin.statusCode());
    }

    @Test
    public void checkSuccessLoginDefectEmail() {              //проверяем тело ответа по значению "success"
        oldUser.setRandomEmail();
        Response responseLogin = UserAPI.loginUser(oldUser);
        assertEquals(false, responseLogin.jsonPath().getJsonObject("success"));
    }

    @Test
    public void checkCodeLoginDefectPassword() {              //проверяем код ответа
        oldUser.setRandomPassword();
        Response responseLogin = UserAPI.loginUser(oldUser);
        assertEquals(SC_UNAUTHORIZED, responseLogin.statusCode());
    }

    @Test
    public void checkSuccessLoginDefectPassword() {              //проверяем тело ответа по значению "success"
        oldUser.setRandomPassword();
        Response responseLogin = UserAPI.loginUser(oldUser);
        assertEquals(false, responseLogin.jsonPath().getJsonObject("success"));
    }

}
