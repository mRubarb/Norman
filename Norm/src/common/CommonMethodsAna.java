package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import baseItems.BaseMain;

public class CommonMethodsAna  extends BaseMain {

	public static int[] pageSizes = {5, 10, 20, 50};
	
	public static List<WebElement> getSizesOfPages() {

		return driver.findElements(By.xpath("//span/label/input[@name='pageSize']"));    
		
	}

	
	// Clicks on the page number at the bottom - pagination bar
	public static void clickPageNumber(int page) throws InterruptedException {
		
		driver.findElement(By.xpath("//ul[@class='pagination']/li/a[contains(text()," + "\"" + page + "\"" + ")]")).click();
		Thread.sleep(3000);
	}
	
	
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
	
	
	//****TO BE MOVED TO COMMON METHODS CLASS***** 
	// Call into API and get single JSON object.  
	public static JSONObject getSingleObject(String token, String url) throws IOException, JSONException
	{
				
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
		
		System.out.println(response.toString());		
		
		// Create json object
		JSONObject jsonObject = new JSONObject(response.toString());		
		return jsonObject;
		
	}	
		
	public static void refreshPage() {
		
		driver.findElement(By.xpath("//button[@title='refresh']")).click();
		
	}
	

	// Open element details page. It works for Tenants.
	// Trying Deployments
	public static void openElementDetailsPage(String key, String elementType) throws InterruptedException {
		
		int pageSize = 10;
		
		// make sure that the View button clicked belongs to the tenant that is going to be viewed
		
		String indexViewButton = "";
		
		if (elementType.equals("tenant")) indexViewButton = "5";
		if (elementType.equals("deployment")) indexViewButton = "6";
		
		
		for (int i = 1; i <= pageSize; i++) {
		
			String xpathButtonView = "//table/tbody/tr[" + i + "]/td[" + indexViewButton + "]/div/button/span[text()='View']";
			
			driver.findElement(By.xpath(xpathButtonView)).click();	
			
			String xpathKey = "//jhi-" + elementType + "-detail/div/div/div[@class='row']/dt[text()='Key:']/following-sibling::dd";
			
			String keyUI = driver.findElement(By.xpath(xpathKey)).getText().split("   ")[0].trim();
			
			System.out.println("Tenant key: " + keyUI);
			
			if (keyUI.equals(key)) {
				
				// System.out.println(elementType + " found");
				break;
			}
			// System.out.println(elementType + " NOT found");
			
			// If the element clicked is not the element that we need then click Back button
			driver.findElement(By.xpath("//button/span[text()=' Back']/..")).click();
			Thread.sleep(2000);
			
		}
	
	}


	
	
}
