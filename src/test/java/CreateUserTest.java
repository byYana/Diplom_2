import ForUser.NewUser;
import ForUser.UserAPI;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class CreateUserTest {
    NewUser user;
    NewUser userDooble;

    @Before
    public void doBefore() {
        user = NewUser.getRandomUser();
        userDooble = user;
    }

    @Test
    public void checkCodeCreateUser() {              //проверяем код ответа
        Response response = UserAPI.createUser(user);
        String accessToken = response.jsonPath().getString("accessToken");
        assertEquals(SC_OK, response.statusCode());
        assertEquals(SC_ACCEPTED, UserAPI.deleteUser(accessToken).statusCode());
    }

    @Test
    public void checkSuccessCreateUser() {           //проверяем тело ответа по значению "success"
        Response response = UserAPI.createUser(user);
        assertEquals(true, response.jsonPath().getJsonObject("success"));
        String accessToken = response.jsonPath().getString("accessToken");
        assertEquals(SC_ACCEPTED, UserAPI.deleteUser(accessToken).statusCode());
    }

    @Test
    public void checkCodeDoobleCreateUser() {        //проверяем код ответа
        Response response = UserAPI.createUser(user);
        Response responseDooble = UserAPI.createUser(user);
        assertEquals(SC_FORBIDDEN, responseDooble.statusCode());
        String accessToken = response.jsonPath().getString("accessToken");
        assertEquals(SC_ACCEPTED, UserAPI.deleteUser(accessToken).statusCode());
    }

    @Test
    public void checkSuccessDoobleCreateUser() {     //проверяем тело ответа по значению "success"
        Response response = UserAPI.createUser(user);
        Response responseDooble = UserAPI.createUser(userDooble);
        assertEquals("false", responseDooble.jsonPath().getString("success"));
        String accessToken = response.jsonPath().getString("accessToken");
        assertEquals(SC_ACCEPTED, UserAPI.deleteUser(accessToken).statusCode());
    }

    @Test
    public void checkCodeDefectEmail() {             //проверяем код ответа
        user.setEmail(null);
        Response response = UserAPI.createUser(user);
        assertEquals(SC_FORBIDDEN, response.statusCode());
    }

    @Test
    public void checkSuccessDefectEmail() {         //проверяем тело ответа по значению "success"
        user.setEmail(null);
        Response response = UserAPI.createUser(user);
        assertEquals("false", response.jsonPath().getString("success"));
    }

    @Test
    public void checkCodeDefectPassword() {         //проверяем код ответа
        user.setPassword(null);
        Response response = UserAPI.createUser(user);
        assertEquals(SC_FORBIDDEN, response.statusCode());
    }

    @Test
    public void checkSuccessDefectPassword() {      //проверяем тело ответа по значению "success"
        user.setPassword(null);
        Response response = UserAPI.createUser(user);
        assertEquals(false, response.jsonPath().getJsonObject("success"));

    }

    @Test
    public void checkCodeDefectName() {              //проверяем код ответа
        user.setName(null);
        Response response = UserAPI.createUser(user);
        assertEquals(SC_FORBIDDEN, response.statusCode());
    }

    @Test
    public void checkSuccessDefectName() {          //проверяем тело ответа по значению "success"
        user.setName(null);
        Response response = UserAPI.createUser(user);
        assertEquals(false, response.jsonPath().getJsonObject("success"));
    }
}
