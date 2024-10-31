import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.json.JSONArray;
import org.json.JSONObject;

public class TaskRestAssured {

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://api.restful-api.dev"; // Set base URI
    }

    @Test
    public void getAllObjects() {
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.request(Method.GET, "/objects");

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNotNull(response.getBody().asString());

        // Parse response body
        JSONArray jsonArray = new JSONArray(response.asString());
        Assert.assertTrue(jsonArray.length() > 0, "Response body should not be empty.");

        // Validate specific object details (id = 1)
        JSONObject firstObject = jsonArray.getJSONObject(0);
        JSONObject data = firstObject.getJSONObject("data");

        // Check type of response
        Assert.assertTrue(firstObject.get("id") instanceof String, "id   is not an Integer");
        Assert.assertTrue(firstObject.get("name") instanceof String, "name is not an Integer");
        Assert.assertTrue(data.length() > 0, "Response body should not be empty.");
        Assert.assertTrue(data.get("color") instanceof String, "color is not an Integer");
        Assert.assertTrue(data.get("capacity") instanceof String, "capacity is not an Integer");

        Assert.assertEquals(firstObject.getString("id"), "1");
        Assert.assertEquals(firstObject.getString("name"), "Google Pixel 6 Pro");
        Assert.assertEquals(data.getString("color"), "Cloudy White");
        Assert.assertEquals(data.getString("capacity"), "128 GB");
    }

    @Test
    public void getObjectById() {
        RequestSpecification httpRequest = RestAssured.given().param("id", 7).log().all();
        Response response = httpRequest.request(Method.GET, "/objects");
        System.out.println("Response : " + response.asPrettyString());

        // Asssert status Code 200
        Assert.assertEquals(response.getStatusCode(), 200);

        // Assert response body not null
        Assert.assertNotNull(response.getBody().asString());

        JSONArray jsonArray = new JSONArray(response.asString());
        JSONObject jsonData = jsonArray.getJSONObject(0);
        JSONObject data = jsonData.getJSONObject("data");
        Object priceObject = data.getDouble("price");

        // Check response Type data
        Assert.assertTrue(jsonData.get("id") instanceof String, "id is not an Integer");
        Assert.assertTrue(jsonData.get("name") instanceof String, "name is not an Integer");
        Assert.assertTrue(data.length() > 0, "Response body should not be empty.");
        Assert.assertTrue(data.get("year") instanceof Integer, "year is not an Integer");
        Assert.assertTrue(priceObject instanceof Double, "price is not an Integer");
        Assert.assertTrue(data.get("CPU model") instanceof String, "CPU model is not an Integer");
        Assert.assertTrue(data.get("Hard disk size") instanceof String, "Hard disk size is not an Integer");

        // Validate Data
        Assert.assertEquals(jsonData.get("id"), "7");
        Assert.assertEquals(jsonData.get("name"), "Apple MacBook Pro 16");
        Assert.assertEquals(data.get("year"), 2019);
        Assert.assertEquals(data.getDouble("price"), 1849.99);
        Assert.assertEquals(data.get("CPU model"), "Intel Core i9");
        Assert.assertEquals(data.get("Hard disk size"), "1 TB");
    }

    @Test
    public void postObject() {
        RequestSpecification httpRequest = RestAssured.given()
                .header("Content-Type", "application/json") // Set Content-Type to application/json
                .log().all(); // Log the entire request for debugging
        // Body request
        JSONObject payload = new JSONObject();
        payload.put("name", "Advan Notebook");

        JSONObject dataPayload = new JSONObject();
        dataPayload.put("year", 2019);
        dataPayload.put("price", 2000.19);
        dataPayload.put("CPU model", "AMD Ryzen 5");
        dataPayload.put("Hard disk size", "1 TB");
        payload.put("data", dataPayload);

        httpRequest.body(payload.toString());
        Response response = httpRequest.post("/objects");
        System.out.println("Response: " + response.asPrettyString());

        // Assert status code 200
        Assert.assertEquals(response.getStatusCode(), 200);

        // Assert response body not null
        Assert.assertNotNull(response.getBody().asString());

        // Parsing JSON response
        JSONObject jsonResponse = new JSONObject(response.asString());
        JSONObject data = jsonResponse.getJSONObject("data");
        Object priceObject = data.getDouble("price");

        // Assertion for response type data
        Assert.assertTrue(jsonResponse.get("id") instanceof String, "id is not a String");
        Assert.assertTrue(jsonResponse.get("name") instanceof String, "name is not a String");
        Assert.assertTrue(jsonResponse.get("createdAt") instanceof String, "createdAt is not a String");
        Assert.assertTrue(data.get("year") instanceof Integer, "year is not an Integer");
        Assert.assertTrue(priceObject instanceof Double, "price is not an Integer");
        Assert.assertTrue(data.get("CPU model") instanceof String, "CPU model is not a String");
        Assert.assertTrue(data.get("Hard disk size") instanceof String, "Hard disk size is not a String");

        // Validate data values
        Assert.assertEquals(jsonResponse.get("name"), "Advan Notebook");
        Assert.assertEquals(data.get("year"), 2019);
        Assert.assertEquals(data.getDouble("price"), 2000.19);
        Assert.assertEquals(data.get("CPU model"), "AMD Ryzen 5");
        Assert.assertEquals(data.get("Hard disk size"), "1 TB");

        // Delete Data Test
        RestAssured.given().log().all().delete("/objects/" + jsonResponse.get("id"));
        
        //Check Deleted Data
        Response responseGetById = RestAssured.given().log().all().get("/objects/"+jsonResponse.get("id"));
        JsonPath jsonPathByid = responseGetById.jsonPath();
        Assert.assertEquals(jsonPathByid.get("error"), "Oject with id="+jsonResponse.get("id")+" was not found.");
    }

}
