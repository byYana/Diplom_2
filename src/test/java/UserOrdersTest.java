import ForOrder.OrderAPI;
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

public class UserOrdersTest {                           //Получение заказов конкретного пользователя:
    NewUser newUser;
    OldUser oldUser;
    String accessToken;

    @Before
    public void doBefore() {
        newUser = NewUser.getRandomUser();
        Response responseUser = UserAPI.createUser(newUser);
        accessToken = responseUser.jsonPath().getString("accessToken");
        oldUser = new OldUser(newUser.getEmail(), newUser.getPassword());
    }

    @After
    public void doAfter() {
        UserAPI.deleteUser(accessToken);
    }

    @Test
    public void checkUserOrdersLogin() {                 // - авторизованный пользователь,
        UserAPI.loginUser(oldUser);
        Response responseInformation = OrderAPI.informationOrders(accessToken);
        responseInformation.then().assertThat().statusCode(SC_OK);
        responseInformation.then().assertThat().body("success", equalTo(true));
    }

    @Test
    public void checkUserOrdersLogout() {                // - неавторизованный пользователь.
        Response responseInformation = OrderAPI.informationOrders("");
        responseInformation.then().assertThat().statusCode(SC_UNAUTHORIZED);
        responseInformation.then().assertThat().body("success", equalTo(false));
    }
}
