import ForUser.NewUser;
import ForUser.UserAPI;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;

public class CreateUserTest {
    NewUser user;

    @Before
    public void doBefore() {
        user = NewUser.getRandomUser();
    }

    @Test
    public void checkCodeCreateUser() {              //проверяем код ответа
        Response response = UserAPI.createUser(user);
        assertEquals(SC_OK, response.statusCode());
        UserAPI.deleteUser(response.jsonPath().getString("accessToken"));
    }

    @Test
    public void checkSuccessCreateUser() {           //проверяем тело ответа по значению "success"
        Response response = UserAPI.createUser(user);
        assertEquals("true", response.jsonPath().getString("success"));
        UserAPI.deleteUser(response.jsonPath().getString("accessToken"));
    }

    @Test
    public void checkCodeDoobleCreateUser() {        //проверяем код ответа
        Response response = UserAPI.createUser(user);
        Response responseDooble = UserAPI.createUser(user);
        assertEquals(SC_FORBIDDEN, responseDooble.statusCode());
        UserAPI.deleteUser(response.jsonPath().getString("accessToken"));
    }

    @Test
    public void checkSuccessDoobleCreateUser() {     //проверяем тело ответа по значению "success"
        Response response = UserAPI.createUser(user);
        Response responseDooble = UserAPI.createUser(user);
        assertEquals("false", responseDooble.jsonPath().getString("success"));
        UserAPI.deleteUser(response.jsonPath().getString("accessToken"));
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
        assertEquals("false", response.jsonPath().getString("success"));

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
        assertEquals("false", response.jsonPath().getString("success"));
    }
}
