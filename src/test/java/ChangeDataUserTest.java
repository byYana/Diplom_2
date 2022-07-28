import ForUser.NewUser;
import ForUser.OldUser;
import ForUser.UserAPI;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;

public class ChangeDataUserTest {
    OldUser oldUser;
    NewUser newUser;
    Response response;
    String accessToken;
    Response responseCreate;

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        responseCreate = UserAPI.createUser(newUser);
        accessToken = responseCreate.jsonPath().getString("accessToken");
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
    }

    @After
    public void doAfter() {
        assertEquals(SC_ACCEPTED,UserAPI.deleteUser(accessToken).statusCode());
    }

    @Test
    public void checkCodeChangeName() {             //проверяем код ответа
        UserAPI.loginUser(oldUser);
        newUser.setRandomName();
        response = UserAPI.changeInformation(accessToken, newUser);
        assertEquals(SC_OK, response.statusCode());
    }

    @Test
    public void checkSuccessChangeName() {           //проверяем тело ответа по значению "success"
        UserAPI.loginUser(oldUser);
        newUser.setRandomName();
        response = UserAPI.changeInformation(accessToken, newUser);
        assertEquals("true", response.jsonPath().getString("success"));
    }

    @Test
    public void checkCodeChangeEmail() {             //проверяем код ответа
        UserAPI.loginUser(oldUser);
        newUser.setRandomEmail();
        response = UserAPI.changeInformation(accessToken, newUser);
        assertEquals(SC_OK, response.statusCode());
    }

    @Test
    public void checkSuccessChangeEmail() {           //проверяем тело ответа по значению "success"
        UserAPI.loginUser(oldUser);
        newUser.setRandomEmail();
        response = UserAPI.changeInformation(accessToken, newUser);
        assertEquals("true", response.jsonPath().getString("success"));
    }

    @Test
    public void checkCodeChangePassword() {             //проверяем код ответа
        UserAPI.loginUser(oldUser);
        newUser.setRandomPassword();
        response = UserAPI.changeInformation(accessToken, newUser);
        assertEquals(SC_OK, response.statusCode());
    }

    @Test
    public void checkSuccessChangePassword() {           //проверяем тело ответа по значению "success"
        UserAPI.loginUser(oldUser);
        newUser.setRandomPassword();
        response = UserAPI.changeInformation(accessToken, newUser);
        assertEquals("true", response.jsonPath().getString("success"));
    }
}

