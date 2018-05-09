package common;

import baseItems.BaseMain;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
	
	// this gets an item from a json object. if there is no item found it returns "".
	public static String GetNonRequiredItem(JSONObject jo,  String item) throws JSONException
	{
		try
		{
			jo.getString(item);				
		}
		catch (Exception e) 
		{
			return "";
		}	
		
		return jo.getString(item);
	}	
	
	
	// New Methods added by Ana
	
	public static List<WebElement> getSizesOfPages() {

		return driver.findElements(By.xpath("//span/label/input[@name='pageSize']"));    
		
	}

	
	// Clicks on the page number at the bottom - pagination bar
	public static void clickPageNumber(int page) throws InterruptedException {
		
		driver.findElement(By.xpath("//ul[@class='pagination']/li/a[contains(text()," + "\"" + page + "\"" + ")]")).click();
		Thread.sleep(3000);
	}
	
	
	// ENABLED/DISABLED values are converted to true/false
	public static boolean convertToBoolean(String isEnabled) {
		
		if (isEnabled.equals("ENABLED")) {
	
			return true;
			
		} else if (isEnabled.equals("DISABLED")) {
			
			return false;
			
		}
		return false; 
	
	}
	
	
	public static void selectSizeOfList(int size) {
		
		System.out.println("Size to select: " + size);
		
		String xpath1 = "//span/label";
		String xpath2 = "/input[@name='pageSize']";
		
		List<WebElement> sizeRadioButtons = driver.findElements(By.xpath(xpath1 + "/input"));
		
		for (int i = 0; i < sizeRadioButtons.size(); i++) {
			
			WebElement radioButtonSize = sizeRadioButtons.get(i); 
						
			if (radioButtonSize.getAttribute("value").equals(Integer.toString(size))) {
				
				By by = By.xpath(xpath1 + "[" + (i+1) + "]" + xpath2 + "/..");
				
				driver.findElement(By.xpath(xpath1 + "[" + (i+1) + "]" + xpath2 + "/..")).click();
				//driver.findElement(By.xpath(xpath + "[" + (i+1) + "]/..")).click();
				
				// Wait for size to be selected 
				WebDriverWait wait = new WebDriverWait(driver, 3);
				
				try {
					// Sometimes the class value ends with '.. focus active' 
					wait.until(ExpectedConditions.attributeToBe(by, "class", "btn btn-secondary btn-sm focus active"));
					
					try {
						// .. sometimes value ends with '.. active focus'
						wait.until(ExpectedConditions.attributeToBe(by, "class", "btn btn-secondary btn-sm active focus"));
						
						try {
							// .. sometimes value ends with '.. active' (no 'focus')
							wait.until(ExpectedConditions.attributeToBe(by, "class", "btn btn-secondary btn-sm active"));
							
						} catch (TimeoutException e) { }
							
					} catch (TimeoutException e) { }
					
				} catch (TimeoutException e) { }
				
				break;
				
			}
								
		}
		
	}
	
	
	public static void clickArrowSorting(String sortBy, String sortDirection) throws InterruptedException {
		
		WebElement arrow = driver.findElement(By.xpath("//div[@class='table-responsive']/table/thead/tr/th/span[text()='" + sortBy + "']/following-sibling::span"));
		
		// If sortDirection is ASC and the arrow is not already ascending, click arrow once
		if (sortDirection.equals("ASC") && !arrow.getAttribute("class").endsWith("sort-asc")) { 
			
			arrow.click();
			
		} 
		// If sortDirection is DESC and the arrow is ascending, click arrow once
		else if (sortDirection.equals("DESC") && arrow.getAttribute("class").endsWith("sort-asc")) { 
			
			arrow.click();
			
		}
		// If sortDirection is DESC and the arrow is NOT ascending (2 arrows displayed), click arrow twice 
		else if (sortDirection.equals("DESC") && !arrow.getAttribute("class").endsWith("sort-asc")) { 
			
			arrow.click();
			arrow.click();
			
		} 
		// If sortDirection is DESC and the arrow is already descending no click is needed  
		else if (sortDirection.equals("DESC") && arrow.getAttribute("class").endsWith("sort-desc")) { 
			// No clicks - already descending			
		}
		
		Thread.sleep(2000);
		
	}

	
	public static String getTotalCountItems() {
		
		return driver.findElement(By.xpath("//div/jhi-item-count")).getText(); 
		
	}


	public static void clickFilterDropdown(String dropdown) {
		
		String xpath = "//button[@id='sortMenu'][text()='" + dropdown + "']";
		driver.findElement(By.xpath(xpath)).click();
		
	}
	
	public static void enterSearchCriteria(String dropdown, String searchCriteria) throws Exception {
		
		String xpathSearchField = "//button[@id='sortMenu'][text()='" + dropdown + "']/following-sibling::div/div/input";
		String xpathValueToSelect = xpathSearchField + "/../following-sibling::div/button/span/span[1]";  
		
		String xpathEnabledDropdown = "//button[@id='sortMenu'][text()='" + dropdown + "']/following-sibling::div/div/button/span/span[text()='" + searchCriteria + "']";
		
		if (dropdown.equals("Show Enabled and Disabled")) {
			
			WaitForElementVisible(By.xpath(xpathEnabledDropdown), 5);
			
			driver.findElement(By.xpath(xpathEnabledDropdown)).click();
			
		} else if (!dropdown.equals("Show Enabled and Disabled")) {
		
			WaitForElementVisible(By.xpath(xpathSearchField), 5);
			
			driver.findElement(By.xpath(xpathSearchField)).sendKeys(searchCriteria);
			
			WaitForElementPresent(By.xpath(xpathValueToSelect), 3);
			
			driver.findElement(By.xpath(xpathValueToSelect)).click();  // + "[text()='" + searchCriteria + "']")).click();   // *** FAILS HERE FOR DEPLOYMENT
			// //jhi-deployment-selector/form/div/div/div/div[4]/button/span/span[1]
		} 
		
		// Give time to the list to be refreshed after it's filtered.
		Thread.sleep(2000);
		
	}
	
	
	// Set filter 'Tenants' back to "All Tenants" value
	// Set filter 'Applications' back to "All Applications" value
	// Set filter 'Deployments' back to "All Deployments" value
	// Set filter 'Enabled/Disabled' back to "Show Enabled and Disabled" value
	public static void resetFilters() throws InterruptedException {
		
		String xpathTenant = "//jhi-tenant-selector/form/div/div/button[@id='sortMenu']";
		String xpathAllTenants = "//jhi-tenant-selector/form/div/div/div/button/span[text()='All Tenants']";
		
		String xpathApp = "//jhi-application-selector/form/div/div/button[@id='sortMenu']";
		String xpathAllApp = "//jhi-application-selector/form/div/div/div/button/span[text()='All Applications']";
		
		String xpathDep = "//jhi-deployment-selector/form/div/div/button[@id='sortMenu']";
		String xpathAllDep = "//jhi-deployment-selector/form/div/div/div/button/span[text()='All Deployments']";
		
		String xpathEnabled = "//jhi-value-selector/div/div/button[@id='sortMenu']";
		String xpathAllEnabled = "//jhi-value-selector/div/div/div/button/span[text()='Show Enabled and Disabled']";
		
		
		// Set filter 'Tenants' back to "All Tenants" value
		try {
			
			if (!driver.findElement(By.xpath(xpathTenant)).getText().equals("All Tenants")) {
				
				driver.findElement(By.xpath(xpathTenant)).click();
				driver.findElement(By.xpath(xpathAllTenants)).click();
				System.out.println(".. Tenant filter reseted .. ");
			
			} else {

				System.out.println(".. Tenant filter DOES NOT need to be reseted .. ");
			}
			
		} catch (NoSuchElementException e) { }
		
		// Set filter 'Applications' back to "All Applications" value
		try {
			
			if (!driver.findElement(By.xpath(xpathApp)).getText().equals("All Applications")) {
				
				driver.findElement(By.xpath(xpathApp)).click();
				driver.findElement(By.xpath(xpathAllApp)).click();
				System.out.println(".. Application filter reseted .. ");
			
			} else {
				
				System.out.println(".. Application filter DOES NOT need to be reseted .. ");
			}
			
		} catch (NoSuchElementException e) { }
		
		// Set filter 'Deployments' back to "All Deployments" value
		try {
		
			if (!driver.findElement(By.xpath(xpathDep)).getText().equals("All Deployments")) {
			
				driver.findElement(By.xpath(xpathDep)).click();
				driver.findElement(By.xpath(xpathAllDep)).click();
				System.out.println(".. Deployment filter reseted .. ");
				
			} else {

				System.out.println(".. Deployment filter DOES NOT need to be reseted .. ");
			}
			
		} catch (NoSuchElementException e) { }
		
		// Set filter 'Enabled/Disabled' back to "Show Enabled and Disabled" value
		try {
		
			if (!driver.findElement(By.xpath(xpathEnabled)).getText().equals("Show Enabled and Disabled")) {
				
				driver.findElement(By.xpath(xpathEnabled)).click();
				driver.findElement(By.xpath(xpathAllEnabled)).click();
				System.out.println(".. Enabled filter reseted .. ");
			
			} else {

				System.out.println(".. Enabled filter DOES NOT need to be reseted .. ");
			}
						
		} catch (NoSuchElementException e) { }
		
			
		Thread.sleep(2000); 
		
	}
}
