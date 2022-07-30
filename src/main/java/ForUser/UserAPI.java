package ForUser;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserAPI {
    private static final String URL = " https://stellarburgers.nomoreparties.site/";
    private static final String HANDLE = "api/auth";

    @Step("Создание пользователя.")
    public static Response createUser(NewUser user) {
        return given().contentType(ContentType.JSON)
                .and().body(user).when().post(URL + HANDLE + "/register");
    }

    @Step("Удаление пользователя.")
    public static void deleteUser(String token) {
        given().header("Authorization", token)
                .when().delete(URL + HANDLE + "/user").then().statusCode(202).and().body("success", equalTo(true));
    }

    @Step("Регистрация пользователя.")
    public static Response loginUser(OldUser user) {
        return given().header("Content-type", "application/json")
                .and().body(user).when().post(URL + HANDLE + "/login");
    }

    @Step("Выход из системы.")
    public static Response logoutUser(RefreshToken refreshToken) {
        return given().header("Content-type", "application/json")
                .and().body(refreshToken).when().post(URL + HANDLE + "/logout");
    }

    @Step("Изменение информации о пользователе.")
    public static Response changeInformation(String token, NewUser user) {
        return given().header("Authorization", token)
                .and().body(user).when().patch(URL + HANDLE + "/user");
    }
}
