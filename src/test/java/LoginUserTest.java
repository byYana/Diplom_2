import ForUser.NewUser;
import ForUser.OldUser;
import ForUser.RefreshToken;
import ForUser.UserAPI;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;

public class LoginUserTest {
    OldUser oldUser;
    NewUser newUser;
    Response response;
    RefreshToken refreshToken;

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        response = UserAPI.createUser(newUser);
        oldUser= new OldUser(newUser.getEmail(),newUser.getPassword());
    }

    @Test
    public void checkCodeLoginUser() {              //проверяем код ответа
        refreshToken = new RefreshToken(response.jsonPath().getString("refreshToken"));
        UserAPI.LogoutUser(refreshToken);
        Response responseLogin = UserAPI.LoginUser(oldUser);
        assertEquals(SC_OK, responseLogin.statusCode());
        UserAPI.deleteUser(response.jsonPath().getString("accessToken"));
    }
    @Test
    public void checkSuccessLoginUser() {              //проверяем тело ответа по значению "success"
        refreshToken = new RefreshToken(response.jsonPath().getString("refreshToken"));
        UserAPI.LogoutUser(refreshToken);
        Response responseLogin = UserAPI.LoginUser(oldUser);
        assertEquals("true", responseLogin.jsonPath().getString("success"));
        UserAPI.deleteUser(response.jsonPath().getString("accessToken"));
    }

}
