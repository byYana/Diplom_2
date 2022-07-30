import ForUser.NewUser;
import ForUser.OldUser;
import ForUser.RefreshToken;
import ForUser.UserAPI;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;

public class LoginUserTest {                //~~Логин пользователя:
    OldUser oldUser;
    NewUser newUser;
    String accessToken;
    Response responseCreate;
    RefreshToken refreshToken;

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        responseCreate = UserAPI.createUser(newUser);
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
        accessToken = responseCreate.jsonPath().getString("accessToken");
        refreshToken = new RefreshToken(responseCreate.jsonPath().getString("refreshToken"));
    }

    @After
    public void doAfter() {
        UserAPI.deleteUser(accessToken);
    }

    @Test
    public void checkLoginUser() {              // ~~- логин под существующим пользователем,~~
        Response responseLogin = UserAPI.loginUser(oldUser);
        accessToken = responseLogin.jsonPath().getString("accessToken");
        responseLogin.then().statusCode(SC_OK);
        responseLogin.then().assertThat().body("success", equalTo(true));
    }


    @Test
    public void checkLoginDefectEmail() {       // ~~- логин с неверным логином.~~~~
        oldUser.setRandomEmail();
        Response responseLogin = UserAPI.loginUser(oldUser);
        responseLogin.then().statusCode(SC_UNAUTHORIZED);
        responseLogin.then().assertThat().body("success", is(false));
    }

    @Test
    public void checkLoginDefectPassword() {   // ~~- логин с неверным паролем.~~~~
        oldUser.setRandomPassword();
        Response responseLogin = UserAPI.loginUser(oldUser);
        responseLogin.then().statusCode(SC_UNAUTHORIZED);
        responseLogin.then().assertThat().body("success", is(false));
    }
}
