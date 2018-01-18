package common;

import baseItems.BaseMain;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.testng.Assert;

public class CommonMethods extends BaseMain 
{
	public static String token = "";	
	
	// Call into API and get json array back.  
	public static JSONArray GetJsonArrayWithUrl(String token, String url, String apiType) throws IOException, JSONException
	{
		ShowText("Token for request = " + token);
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		// optional default is GET
		con.setRequestMethod("GET");
		
		//add authorization header
		con.setRequestProperty("Authorization", "Bearer " + token);
		
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();
		
		System.out.println(response.toString().split(apiType)[1]);		
		
		// create json array by removing meta data   
		JSONArray jasonArray = new JSONArray(response.toString().split(apiType)[1]);		
		return jasonArray;
	}	
	
	// Call into API and get metadata string back.  
	public static String GetMetaDataWithUrl(String token, String url, String apiType) throws IOException, JSONException
	{
		ShowText("Token for request = " + token);
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		// optional default is GET
		con.setRequestMethod("GET");
		
		//add authorization header
		con.setRequestProperty("Authorization", "Bearer " + token);
		
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();

		return response.toString().split(apiType)[0]; // return metada string.
	}	
	
	// Select am item from the Platform dropdown list. The item to select is passed as a parameter
    public static void selectItemPlatformDropdown(String itemName)
    {
           
           WaitForElementClickable(By.cssSelector("#platform-menu"), 5, "");
           driver.findElement(By.cssSelector("#platform-menu")).click();
           
           String optionXpath = "//ul/li/a[@class='dropdown-item']/span[text()='" + itemName + "']";
                               
           WaitForElementClickable(By.xpath(optionXpath), 5, "Option for " + itemName + " not found");
           driver.findElement(By.xpath(optionXpath)).click();
    
    }
    
    // Verify the title displayed. The text to verify is passed as a parameter
    public static void verifyTitle(String itemName) {
           
           String title = driver.findElement(By.xpath("//div/h2/span")).getText();
           
           Assert.assertEquals(title, itemName);
           
           System.out.println("Title: " + title);
    }
    
	// call into API and a token as a string
    public static String GetTokenFromPost() throws IOException, JSONException
	{
		// POST -- https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/		
		String url = "http://dc1testrmapp03.prod.tangoe.com:4070/api/authenticate";
		String retToken = "";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
        // Setting basic post request
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");	
		con.setRequestProperty("Content-Type","application/json");

		String postJsonData = "{\"password\":\"admin\",\"rememberMe\":true,\"username\":\"admin\"}";
	
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(postJsonData);
		wr.flush();
		wr.close();
		 		
		System.out.println(con.getResponseCode());
		
		int responseCode = con.getResponseCode();
		System.out.println("nSending 'POST' request to URL : " + url);
		System.out.println("Post Data : " + postJsonData);
		System.out.println("Response Code : " + responseCode);
		 
		BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
		String output;
		StringBuffer response = new StringBuffer();
		 
		while ((output = in.readLine()) != null) 
		{
			response.append(output);
		}
		in.close();
		  
		//printing result from response
		System.out.println(response.toString());
		retToken = response.toString().split(":")[1].replace("}", "");
		System.out.println("Latest Token " + retToken);
		
		// NOTE: couldn't put token id into a string. had to get if from json array into a json object. 
		// to get the response in a json array, need to add leading/trailing "[" and "]"
		JSONArray tokenArray = new JSONArray("[" + response.toString() + "]");
		System.out.println(tokenArray.length());
		JSONObject jo = tokenArray.getJSONObject(0);
		return jo.getString("id_token");
	}    	
	
	
	
	
	
}
