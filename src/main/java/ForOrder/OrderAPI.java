package ForOrder;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderAPI {
    private static final String URL = " https://stellarburgers.nomoreparties.site/";
    private static final String HANDLE = "api/orders";

    @Step("Создание заказа.")
    public static Response createOrder(Order order) {   //4
        return given().contentType(ContentType.JSON)
                .and().body(order).when().post(URL + HANDLE);
    }

    @Step("Получаем заказы.")
    public static Response informationOrders(String token) {
        return given().header("Authorization", token)
                .get(URL + HANDLE);
    }
}
