package com.APITestSample;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.reusable.JsonPayload;
import com.reusable.ReusableMethods;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class APITest1 {
	
	String placeId = "";
	String name = "Sachin";
	String address = "29, side layout, cohen 09";
	String baseURI = "https://rahulshettyacademy.com";
	
	@Test()
	public void addAPI()
	{
		String response = given().baseUri(baseURI).log().all().queryParam("key", "qaclick123").header("Content-Type","application/json")
		.body(JsonPayload.payload("Gunjan Kumar", "70 Summer walk, USA"))
		.when().post("/maps/api/place/add/json").then().log().all().assertThat().statusCode(200).body("scope", equalTo("APP"))
		.header("Server", "Apache/2.4.52 (Ubuntu)").extract().response().asString();
		
		JsonPath js = ReusableMethods.rawToJson(response);
		 placeId = js.getString("place_id");
		 System.out.println("Place Id: "+placeId);
	}
	
	@Test(dependsOnMethods = {"addAPI"})
	public void updateAPI()
	{
		given().baseUri(baseURI).log().all().queryParam("key", "qaclick123").header("Content-Type","application/json")
		.body("{\n"
				+ "\"place_id\":\""+placeId+"\",\n"
				+ "\"address\":\""+address+"\",\n"
				+ "\"key\":\"qaclick123\"\n"
				+ "}\n"
				+ "")
		.when().put("/maps/api/place/update/json").then().log().all().assertThat().statusCode(200).body("msg", equalTo("Address successfully updated"));
	}
	
	@Test(dependsOnMethods = {"updateAPI"})
	public void getAPI()
	{
		String response = given().baseUri(baseURI).log().all().queryParam("key", "qaclick123").queryParam("place_id", placeId)
		.when().get("/maps/api/place/get/json").then().assertThat().log().all().statusCode(200).extract().response().asString();
		
		JsonPath js = ReusableMethods.rawToJson(response);
		String actualAddress = js.getString("address");
		Assert.assertEquals(actualAddress, address, "Invalid address found");
	}

}
