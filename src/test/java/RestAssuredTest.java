import static io.restassured.RestAssured.*;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.*;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.*;


public class RestAssuredTest {
	Response response;
	String data;
//Run	
	@BeforeTest
	public void Setup() {
		RestAssured.baseURI="http://localhost:3000";
	}
	
	
	@BeforeClass
	@Description("Simple Validation of GET response")
	public void SimpleValidation() {
		given()
        .get("/About") // Replace with actual API endpoint
	       .then()
	       .statusCode(200);		 
		
	}
	
	@Test
	@Description("Test Case 1 : Validate API GET response using Extract Response")
	public void TestCase1() {
		response = given()
           .get("/Domains") // Replace with actual API endpoint
	       .then()
	       .extract().response();
        response.then().statusCode(200);	 
        
	}
	@Test
	@Description("Test Case 2 : Validate API POST response")
	public void TestCase2() {
		 String payload = """
		        {
		        "id":"4",
		 		"CompanyID": 104,
		 		 "CompanyName": "TechPay",
		 		 "Active": true
		        }
		        """;
		response = given()
				.header("Content-Type", "application/json")
		        .header("Accept", "application/json")
		        .body(payload)
            .when()
                .post("/Company")
            .then()
                .log().all()  			 // 🔥 logs full response
                
                .extract().response();
		data=response.jsonPath().getString("id");
		Reporter.log("ID log-"+data,true);
			
    }
	
	@Test
	@Description("Test Case 3 : Validate API PUT response")
	public void TestCase3() {
		String payload="""
				{
		 		"CompanyID": 105,
		 		 "CompanyName": "Techi"
		        }
				""";
		response = given()
		.body(payload)
		.when()
		.put("/Company/"+data)
		.then()
		.statusLine("HTTP/1.1 200 OK")
		.log().all()
		.extract().response();
	}
	@Test
	@Description("Test Case 4 : Validate API Patch response")
	public void TestCase4() {
		String payload="{\"id\":4}";
		response=given()
			.body(payload)
		.when()
			.patch("/Company/"+data)
		.then()
			.time(lessThan(100L))	//import static org.hamcrest.Matchers.lessThan;
			.time(greaterThan(5L)) //import static org.hamcrest.Matchers.*;
			.extract().response();

	}
	@Test
	@Description("Test Case 5 : Validate API Delete response")
	public void TestCase5() {
		response=given()
		.when()
			.delete("/Company/"+data)
		.then()
			.statusCode(200)
			.extract().response();		
	}

	
	
	@AfterMethod
	public void Result(ITestResult result) {
		attachResponse(response);
		if(result.getStatus()==ITestResult.FAILURE)
			Reporter.log(result.getName().toString()+" : Failed",true);
		else
			Reporter.log(result.getName().toString()+ " : Passed",true);
			response=null;
	}
	
	private void attachResponse(Response response) {
//		Allure.addAttachment("Screenshot", response.asPrettyString());
		Allure.attachment(response.asString(), response.asPrettyString());
	}
	
	
}
