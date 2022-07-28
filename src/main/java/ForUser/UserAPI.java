package ForUser;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserAPI {
    private static final String URL = " https://stellarburgers.nomoreparties.site/";
    private static final String HANDLE = "api/auth";

    @Step("Создание пользователя.")
    public static Response createUser(NewUser user) {
        return given().contentType(ContentType.JSON)
                .and().body(user).when().post(URL + HANDLE + "/register");
    }

    @Step("Удаление пользователя.")
    public static Response deleteUser(String token) {
        return given().header("Authorization", token)
                .when().delete(URL + HANDLE + "/user");
    }

    @Step("Регистрация пользователя.")
    public static Response loginUser(OldUser user) {
        return given().contentType(ContentType.JSON)
                .and().body(user).when().post(URL + HANDLE + "/login");
    }

    @Step("Выход из системы.")
    public static Response logoutUser(String refreshToken) {
        return given().contentType(ContentType.JSON)
                .and().body(refreshToken).when().post(URL + HANDLE + "/logout");
    }

    @Step("Изменение информации о пользователе.")
    public static Response changeInformation(String token, NewUser user) {
        return given().header("Authorization", token)
                .and().body(user).when().patch(URL + HANDLE + "/user");
    }
}
