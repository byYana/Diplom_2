import ForUser.NewUser;
import ForUser.UserAPI;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;


public class CreateUserTest {                   //~~Создание пользователя:~~
    NewUser user;

    @Before
    public void doBefore() {
        user = NewUser.getRandomUser();
    }

    @Test
    public void checkCreateUser() {             // ~~- создать уникального пользователя;~~
        Response response = UserAPI.createUser(user);
        String accessToken = response.jsonPath().getString("accessToken");
        assertEquals(SC_OK, response.statusCode());
        response.then().assertThat().body("success", equalTo(true));
        UserAPI.deleteUser(accessToken);
    }

    @Test
    public void checkDoobleCreateUser() {        // ~~- создать пользователя, который уже зарегистрирован;~~
        Response response = UserAPI.createUser(user);
        Response responseDooble = UserAPI.createUser(user);
        assertEquals(SC_FORBIDDEN, responseDooble.statusCode());
        responseDooble.then().assertThat().body("success", equalTo(false));
        String accessToken = response.jsonPath().getString("accessToken");
        UserAPI.deleteUser(accessToken);
    }

    @Test
    public void checkDefectEmail() {             // ~~- создать пользователя и не заполнить поле почты.~~
        user.setEmail(null);
        Response response = UserAPI.createUser(user);
        assertEquals(SC_FORBIDDEN, response.statusCode());
        response.then().assertThat().body("success", equalTo(false));
    }

    @Test
    public void checkDefectPassword() {         // ~~- создать пользователя и не заполнить поле пароль.~~
        user.setPassword(null);
        Response response = UserAPI.createUser(user);
        assertEquals(SC_FORBIDDEN, response.statusCode());
        response.then().assertThat().body("success", equalTo(false));
    }

    @Test
    public void checkDefectName() {         // ~~- создать пользователя и не заполнить поле имя.~~
        user.setName(null);
        Response response = UserAPI.createUser(user);
        assertEquals(SC_FORBIDDEN, response.statusCode());
        response.then().assertThat().body("success", equalTo(false));
    }
}
