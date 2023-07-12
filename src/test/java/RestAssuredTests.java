import Bodies.*;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import Objects.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RestAssuredTests {
    Date date = new Date();
    SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = dmyFormat.format(date);
    BookingDates bookingdates = new BookingDates().builder()
            .checkin(formattedDate)
            .checkout(formattedDate)
            .build();
    private RequestSpecification specForPutPatchDelete;

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI = "http://restful-booker.herokuapp.com";
        AuthBody tokenBody = new AuthBody().builder()
                .username("admin")
                .password("password123")
                .build();

        Response responseToken = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(tokenBody)
                .post("https://restful-booker.herokuapp.com/auth");

        String token = responseToken.as(AuthResponse.class).getToken();
        specForPutPatchDelete = new RequestSpecBuilder()
                .addHeader("Cookie", "token=" + token)
                .build();


        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();
    }

    @Test
    public void createBookingTest() {
        CreateBookingRequestBody requestBody = new CreateBookingRequestBody().builder()
                .firstname("Valerii")
                .lastname("Morhental")
                .totalprice(444)
                .depositpaid(true)
                .bookingdates(bookingdates)
                .additionalneeds("Additional need")
                .build();

        Response response = RestAssured.given()
                .body(requestBody)
                .post("/booking");

        String responseMessage = response.as(CreateBookingRequestResponse.class).getBooking().getAdditionalneeds();
        Assert.assertEquals("Additional need", responseMessage, "request was unsuccessful");
    }

    @Test
    public void getAllBookingsTest() {
        Response response = RestAssured.get("/booking");
        response.then().statusCode(200);
    }

    @Test
    public void patchTotalPriceInRandomBookingTest() {


        PatchTotalPriceRequestBody patchBody = new PatchTotalPriceRequestBody().builder()
                .totalprice(9990)
                .build();

        Response response = RestAssured.get("/booking");
        Integer bookingId = response.jsonPath().get("find{it.bookingid}.bookingid");

        Response responsePatch = RestAssured.given()
                .spec(specForPutPatchDelete)
                .body(patchBody)
                .patch("/booking/{id}", bookingId);
        Number updatedTotalPrice = responsePatch.as(PatchBookingResponseBody.class).getTotalprice();
        Assert.assertEquals(9990, updatedTotalPrice, "Total price was not changed");
    }

    @Test
    public void putBookingTest() {
        Response response = RestAssured.get("/booking");
        Integer bookingId = response.jsonPath().get("find{it.bookingid}.bookingid");

        Response getBookingByID = RestAssured.get("/booking/{id}", bookingId);
        String lastname = getBookingByID.as(GetBookingByIDResponseBody.class).getLastname();
        Number totalprice = getBookingByID.as(GetBookingByIDResponseBody.class).getTotalprice();
        boolean depositpaid = getBookingByID.as(GetBookingByIDResponseBody.class).isDepositpaid();
        BookingDates bookingdates = getBookingByID.as(GetBookingByIDResponseBody.class).getBookingdates();

        PutBookingRequestBody putBookingRequestBody = new PutBookingRequestBody().builder()
                .firstname("SLAVKA")
                .lastname(lastname)
                .totalprice(totalprice)
                .depositpaid(depositpaid)
                .bookingdates(bookingdates)
                .additionalneeds("NEW NEEDS")
                .build();

        Response putBookingResponse = RestAssured.given()
                .spec(specForPutPatchDelete)
                .body(putBookingRequestBody)
                .put("/booking/{id}", bookingId);
        String additionalNeedsUpdated = putBookingResponse.as(GetBookingByIDResponseBody.class).getAdditionalneeds();
        Assert.assertEquals(additionalNeedsUpdated, "NEW NEEDS", "updated did not happen");

    }

    @Test
    public void deleteBookingTest() {
        Response response = RestAssured.get("/booking");
        int bookingId = response.jsonPath().get("find{it.bookingid}.bookingid");

        Response deleteBooking = RestAssured.given()
                .spec(specForPutPatchDelete)
                .delete("/booking/{id}", bookingId);

        deleteBooking.then().statusCode(201);

    }


}
