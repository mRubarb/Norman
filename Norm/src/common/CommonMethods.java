package common;

import baseItems.BaseMain;
import classes.ApplicationClass;
import classes.Deployment;
import classes.RouteClass;
import classes.Tenant;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	// ALLOW/BLOCK values are converted to true/false - this is for routes
	public static boolean convertToBoolean(String value) {
		
		if (value.equals("ENABLED") || value.equals("ALLOW")) {
	
			return true;
			
		} else if (value.equals("DISABLED") || value.equals("BLOCK")) {
			
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
	
	
	// Puts the JSON response obtained from the API GET /tenants into a list.
	public static List<Tenant> putJsonArrayTenantsIntoList(JSONArray jsonArrayTenants) throws JSONException {
		
		List<Tenant> actualTenantsList = new ArrayList<>();
		
		// Put the deployments in the json array into a list of tenants
		for (int i = 0; i < jsonArrayTenants.length(); i++) {
			
			JSONObject jo = jsonArrayTenants.getJSONObject(i);
			Tenant tenant = new Tenant();
			
			/*			
			System.out.print(i+1 + "  Key: " + jo.getString("key"));
			System.out.print(", Name: " + jo.getString("name"));
			System.out.print(", Default Tenant ID: " + GetNonRequiredItem(jo, "defaultTenantID")
			System.out.println(", Enabled: " + jo.getBoolean("enabled"));
			*/
			
			tenant.setKey(jo.getString("key"));
			tenant.setName(jo.getString("name"));
			tenant.setDefaultTenantID(CommonMethods.GetNonRequiredItem(jo, "defaultTenantID"));
			tenant.setEnabled(jo.getBoolean("enabled"));
		
			actualTenantsList.add(tenant);
			
		}
		
		return actualTenantsList;
			
	}
	
	
	// Puts the JSON response obtained from the API GET /applications into a list.
	public static List<ApplicationClass> putJsonArrayAppsIntoList(JSONArray jsonArrayApps) throws JSONException {
		
		List<ApplicationClass> applicationList = new ArrayList<>();
		
		// Put the deployments in the json array into a list of applications
		for (int i = 0; i < jsonArrayApps.length(); i++) {
			
			JSONObject jo = jsonArrayApps.getJSONObject(i);
			ApplicationClass app = new ApplicationClass(jo.getString("key"), jo.getString("name"), "", jo.getBoolean("enabled"), "", "");
			
			/*			
			System.out.print(i+1 + "  Key: " + jo.getString("key"));
			System.out.print(", Name: " + jo.getString("name"));
			System.out.print(", Default Tenant ID: " + GetNonRequiredItem(jo, "defaultTenantID")
			System.out.println(", Enabled: " + jo.getBoolean("enabled"));
			*/
			
			applicationList.add(app);
			
		}
		
		return applicationList;
			
	}
	
	
	// Puts the JSON response obtained from the API GET /deployments into a list.
	public static List<Deployment> putJsonArrayDeploymentsIntoList(JSONArray jsonArrayDeps) throws JSONException {
		
		List<Deployment> deploymentList = new ArrayList<>();
		
		// Put the deployments in the json array into a list of deployments
		for (int i = 0; i < jsonArrayDeps.length(); i++) {
			
			JSONObject jo = jsonArrayDeps.getJSONObject(i);
			Deployment dep = new Deployment(jo.getString("key"), jo.getString("applicationKey"), jo.getString("version"), "", jo.getBoolean("enabled"));
			
			/*			
			System.out.print(i+1 + "  Key: " + jo.getString("key"));
			System.out.print(", Version: " + jo.getString("version"));
			System.out.println(", Enabled: " + jo.getBoolean("enabled"));
			*/
			
			deploymentList.add(dep);
			
		}
		
		return deploymentList;
			
	}
	
	
	// Puts the JSON response obtained from the API GET /routes into a list.
	public static List<RouteClass> putJsonArrayRoutesIntoList(JSONArray jsonArrayRoutes) throws JSONException {
		
		List<RouteClass> routeList = new ArrayList<>();
		
		// Put the routes in the json array into a list of routes
		for (int i = 0; i < jsonArrayRoutes.length(); i++) {
			
			JSONObject jo = jsonArrayRoutes.getJSONObject(i);
			RouteClass route = new RouteClass(jo.getString("key"), jo.getString("tenantKey"), "", 
					jo.getString("applicationKey"), "", jo.getString("deploymentKey"), "", jo.getString("tenantID"), "", 
					jo.getBoolean("enabled"), "", jo.getBoolean("allowServiceCalls"), 
					jo.getString("host"), jo.getString("path"));
			
			if (jo.getBoolean("enabled") == false) route.m_disabledReason = jo.getString("disabledReason"); 
						
			/*			
			System.out.print(i+1 + "  Key: " + jo.getString("key"));
			System.out.println(", Enabled: " + jo.getBoolean("enabled"));
			*/
			
			routeList.add(route);
			
		}
		
		return routeList;
	}


	// Verify the data of the Routes listed in the Routes tab (Details pages that have Routes tab are: Deployments, Tenants, Applications)
	public static void verifyRouteDataTabInDetailsPage(String key, String detailsPage) throws Exception {
		
		/*
		 * 1. Click 'Routes' tab
		 *  	* Wait for tab to be selected
		 * 2. Get a list with the routes listed in the UI
		 * 3. Run request for GET /routes?deploymentKey=deploymentKeyValue to get a list with the routes in the API
		 * 4. Compare the routes listed on each list - they should be the same 
		 * 
		 */
		
			
		// 1. Click 'Routes' tab
		String xpathRouteTab = "//li/a[@id='route_tab']/div[text()='Routes ']";
		driver.findElement(By.xpath(xpathRouteTab)).click();
			
		// * Wait for tab to be selected
		WebDriverWait wait = new WebDriverWait(driver, 4);
		wait.until(ExpectedConditions.attributeToBe(By.id("route_tab"), "aria-expanded", "true"));
		WaitForElementVisible(By.xpath("//table/thead/tr/th[@jhisortby='KEY']"), 4);
		
		// set max size of list
		int sizeOfList = 50;
		CommonMethods.selectSizeOfList(sizeOfList);
		
		
		// 2. Get a list with the routes listed in the UI
		int routeCount = Integer.parseInt(driver.findElement(By.xpath("//li[@class='nav-item']/a/div[text()='Routes ']/span")).getText());
		List<RouteClass> routesInTab = new ArrayList<>();
		List<String> routesKeysInTab = new ArrayList<>();
				
		for (int i = 1; i <= routeCount; i++) {
		
			String routeKey = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[1]/a")).getText();
			String hostAndPath = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[2]/div[1]")).getText(); // host and path are in the same line in the tab
			String tenantID = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[2]/div[2]")).getText(); 
			
			String applicationKey = "";
			String deploymentKey = "";
			String tenantKey = ""; 
			
			if (detailsPage.equals("tenant")) {
				
				applicationKey = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[3]/div/a")).getText();
				deploymentKey = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[4]/div/a")).getText();
				
			} else if (detailsPage.equals("application")) {
				
				tenantKey = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[3]/div/a")).getText();
				deploymentKey = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[4]/div/a")).getText();
				
			} else if (detailsPage.equals("deployment")) {
				
				tenantKey = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[3]/div/a")).getText();
				applicationKey = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[4]/div/a")).getText();
				
			}
			 
			boolean enabled = convertToBoolean(driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[5]/div/span")).getText());
			String disabledReason = "";
			if (enabled == false) disabledReason = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[5]/div[2]")).getText();
			boolean allowServiceCalls = convertToBoolean(driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[6]/div/span")).getText()); 
			/*
			 *  RouteClass(String key, String tennantKey, String tennantName, String appKey, String appName, String deployKey, String deployVersion, String tenantId, String description, boolean enabled, String disabledReason,
		    boolean  allowServiceCalls, String host, String path)*/
			
			RouteClass route = new RouteClass(routeKey, tenantKey, "", applicationKey, "", deploymentKey, "", tenantID, "", 
					enabled, disabledReason, allowServiceCalls, hostAndPath, "");
			routesInTab.add(route);
			routesKeysInTab.add(routeKey);
			
			// route.ShowRoute();
			
			// System.out.println("key: " + routeKey);
			
		}
		
		
		// 3. Run request for GET /routes?deploymentKey=deploymentKeyValue to get a list with the routes in the API
		String token = CommonMethods.GetTokenFromPost();
		
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/routes?" + detailsPage + "Key=" + key + "&pageSize=" + sizeOfList;
		//                                E.g.: "platformservice/api/v1/routes?deploymentKey=deploymentKey&pageSize=" + sizeOfList;
		 
		String apiType = "\"" + "routes" + "\"" + ":";
		
		JSONArray jsonArrayRoutes = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		List<RouteClass> routesFromAPI = CommonMethods.putJsonArrayRoutesIntoList(jsonArrayRoutes);	
		List<String> routeKeysFromAPI = new ArrayList<>();
		
		HashMap<String, RouteClass> hashmapRoutesAPI = new HashMap<>();
		
		for (int i = 0; i < routesFromAPI.size(); i++) {
			
			routeKeysFromAPI.add(routesFromAPI.get(i).m_Key);
			hashmapRoutesAPI.put(routesFromAPI.get(i).m_Key, routesFromAPI.get(i));
			
		}
		
		
		// 4. Compare the routes listed on each list - they should be the same 
		Collections.sort(routesKeysInTab);
		Collections.sort(routeKeysFromAPI);
		
		for (int i = 0; i < routeCount; i++) {
			
			Assert.assertEquals(routeKeysFromAPI.get(i), routesKeysInTab.get(i));
			
			String rKey = routesInTab.get(i).m_Key;
			
			Assert.assertEquals(routesInTab.get(i).m_host, hashmapRoutesAPI.get(rKey).m_host + hashmapRoutesAPI.get(rKey).m_path);
			
			Assert.assertEquals(routesInTab.get(i).m_tenantId, hashmapRoutesAPI.get(rKey).m_tenantId);
			
			if (detailsPage.equals("tenant")) {
				
				Assert.assertEquals(routesInTab.get(i).m_appKey, hashmapRoutesAPI.get(rKey).m_appKey);
				Assert.assertEquals(routesInTab.get(i).m_deployKey, hashmapRoutesAPI.get(rKey).m_deployKey);
				
			} else if (detailsPage.equals("application")) {
				
				Assert.assertEquals(routesInTab.get(i).m_tennantKey, hashmapRoutesAPI.get(rKey).m_tennantKey);
				Assert.assertEquals(routesInTab.get(i).m_deployKey, hashmapRoutesAPI.get(rKey).m_deployKey);
				
			} else if (detailsPage.equals("deployment")) {
				
				Assert.assertEquals(routesInTab.get(i).m_tennantKey, hashmapRoutesAPI.get(rKey).m_tennantKey);
				Assert.assertEquals(routesInTab.get(i).m_appKey, hashmapRoutesAPI.get(rKey).m_appKey);
				
			}
			
			Assert.assertEquals(routesInTab.get(i).m_enabled, hashmapRoutesAPI.get(rKey).m_enabled);
			
			if (routesInTab.get(i).m_enabled == false)  
				Assert.assertEquals(routesInTab.get(i).m_disabledReason, hashmapRoutesAPI.get(rKey).m_disabledReason);
			
			Assert.assertEquals(routesInTab.get(i).m_allowServiceCalls, hashmapRoutesAPI.get(rKey).m_allowServiceCalls);
			
		}
		
	}
	
	
	// Verify the data of the Tenants listed in the Tenants tab (Details pages that have Tenants tab are: Deployments, Applications)
	public static void verifyTenantDataTabInDetailsPage(String key, String detailsPage) throws Exception {
		
		/*
		 * 1. Click 'Tenants' tab
		 *  	* Wait for tab to be selected
		 * 2. Get a list with the tenants listed in the UI
		 * 3. Run request for GET /tenants?tenantKey=tenantKeyValue to get a list with the tenants in the API
		 * 4. Compare the tenants listed on each list - they should be the same 
		 * 
		 */
		
		
		// 1. Click 'Tenants' tab
		String xpathTenantTab = "//li/a[@id='tenant_tab']/div[text()='Tenants ']";
		driver.findElement(By.xpath(xpathTenantTab)).click();
		
		// * Wait for tab to be selected
		WebDriverWait wait = new WebDriverWait(driver, 4);
		wait.until(ExpectedConditions.attributeToBe(By.id("tenant_tab"), "aria-expanded", "true"));
		WaitForElementVisible(By.xpath("//table/thead/tr/th[@jhisortby='KEY']"), 4);
		
		
		// 2. Get a list with the tenants listed in the UI
		int tenantCount = Integer.parseInt(driver.findElement(By.xpath("//li[@class='nav-item']/a/div[text()='Tenants ']/span")).getText());
		List<Tenant> tenantsInTab = new ArrayList<>();
		List<String> tenantsKeysInTab = new ArrayList<>();
				
		for (int i = 1; i <= tenantCount; i++) {
		
			String tenantKey = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[1]/a")).getText();
			String tenantName = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[2]")).getText();
			boolean tenantEnabled = Boolean.parseBoolean(driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[4]/span")).getText());
			
			Tenant tenant = new Tenant();
			tenant.setKey(tenantKey);
			tenant.setName(tenantName);
			tenant.setEnabled(tenantEnabled);
			tenantsInTab.add(tenant);
			tenantsKeysInTab.add(tenantKey);
			
			// System.out.println("key from tab: " + tenantKey);
			
		}
		
			
		// 3. Run request for GET /tenants?tenantKey=tenantKeyValue
		String token = CommonMethods.GetTokenFromPost();
		
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/tenants?" + detailsPage + "Key=" + key;
		//                                E.g.: "platformservice/api/v1/tenants?deploymentKey=deploymentKey;
		String apiType = "\"" + "tenants" + "\"" + ":";
		
		JSONArray jsonArrayTenants = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		List<Tenant> tenantsFromAPI = CommonMethods.putJsonArrayTenantsIntoList(jsonArrayTenants);	
		List<String> tenantKeysFromAPI = new ArrayList<>();
		
		HashMap<String, Tenant> hashmapTenantsAPI = new HashMap<>();
				
		for (int i = 0; i < tenantsFromAPI.size(); i++) {
			
			tenantKeysFromAPI.add(tenantsFromAPI.get(i).getKey());
			hashmapTenantsAPI.put(tenantsFromAPI.get(i).getKey(), tenantsFromAPI.get(i));
			
		}
		

		// 4. Compare the tenants listed on each list - they should be the same 
		Collections.sort(tenantsKeysInTab);
		Collections.sort(tenantKeysFromAPI);
		
		for (int i = 0; i < tenantCount; i++) {
			
			Assert.assertEquals(tenantKeysFromAPI.get(i), tenantsKeysInTab.get(i));
			
			String tKey = tenantsInTab.get(i).getKey();
			Assert.assertEquals(tenantsInTab.get(i).getName(), hashmapTenantsAPI.get(tKey).getName());
			Assert.assertEquals(tenantsInTab.get(i).isEnabled(), hashmapTenantsAPI.get(tKey).isEnabled());
			
		}
		
	}
	
	
	// Verify the data of the Deployments listed in the Deployments tab (Details pages that have Deployments tab are: Tenants, Applications)
	public static void verifyDeploymentDataTabInDetailsPage(String key, String detailsPage) throws Exception {
		
		/*
		 * 1. Click 'Deployments' tab
		 *  	* Wait for tab to be selected
		 * 2. Get a list with the deployments listed in the UI
		 * 3. Run request for GET /deployments?tenantKey=tenantKeyValue to get a list with the deployments in the API
		 * 4. Compare the deployments listed on each list - they should be the same 
		 * 
		 */

		
		// 1. Click 'Deployments' tab
		String xpathDepTab = "//li/a[@id='deployment_tab']/div[text()='Deployments ']";
		driver.findElement(By.xpath(xpathDepTab)).click();
		
		// * Wait for tab to be selected
		WebDriverWait wait = new WebDriverWait(driver, 4);
		wait.until(ExpectedConditions.attributeToBe(By.id("deployment_tab"), "aria-expanded", "true"));
		WaitForElementVisible(By.xpath("//table/thead/tr/th[@jhisortby='KEY']"), 4);
		
		
		// 2. Get a list with the deployments listed in the UI
		int deploymentCount = Integer.parseInt(driver.findElement(By.xpath("//li[@class='nav-item']/a/div[text()='Deployments ']/span")).getText());
		List<Deployment> deploymentsInTab = new ArrayList<>();
		List<String> deploymentsKeysInTab = new ArrayList<>();
				
		for (int i = 1; i <= deploymentCount; i++) {
		
			String depKey = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[1]/a")).getText();
			String appKey = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[2]/div[1]/a")).getText();
			String depVersion = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[3]")).getText();
			String depEnabled = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[5]/span")).getText();
			
			Deployment dep = new Deployment(depKey, appKey, depVersion, "", CommonMethods.convertToBoolean(depEnabled));
			deploymentsInTab.add(dep);
			deploymentsKeysInTab.add(depKey);
			
		}
		
		
		// 3. Run request for GET /deployments?tenantKey=tenantKeyValue to get a list with the deployments in the API
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/deployments?" + detailsPage + "Key=" + key;
		String apiType = "\"" + "deployments" + "\"" + ":";
		
		JSONArray jsonArrayDeployments = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		List<Deployment> deploymentsFromAPI = CommonMethods.putJsonArrayDeploymentsIntoList(jsonArrayDeployments);	
		List<String> deploymentKeysFromAPI = new ArrayList<>();
		
		HashMap<String, Deployment> hashmapDeploymentsAPI = new HashMap<>();
		
		for (int i = 0; i < deploymentsFromAPI.size(); i++) {
			
			deploymentKeysFromAPI.add(deploymentsFromAPI.get(i).getKey());
			hashmapDeploymentsAPI.put(deploymentsFromAPI.get(i).getKey(), deploymentsFromAPI.get(i));
			
		}
		
		
		// 4. Compare the deployments listed on each list - they should be the same
		Collections.sort(deploymentsKeysInTab);
		Collections.sort(deploymentKeysFromAPI);
		
		for (int i = 0; i < deploymentCount; i++) {
			
			Assert.assertEquals(deploymentKeysFromAPI.get(i), deploymentsKeysInTab.get(i));
			
			String dKey = deploymentsInTab.get(i).getKey();
			
			Assert.assertEquals(deploymentsInTab.get(i).getApplicationKey(), hashmapDeploymentsAPI.get(dKey).getApplicationKey());
			Assert.assertEquals(deploymentsInTab.get(i).getVersion(), hashmapDeploymentsAPI.get(dKey).getVersion());
			Assert.assertEquals(deploymentsInTab.get(i).isEnabled(), hashmapDeploymentsAPI.get(dKey).isEnabled());
						
		}
		
	}
	
	
	// Verify the data of the Applications listed in the Applications tab (Details pages that have Applications tab are: Tenants, Deployments)
	public static void verifyApplicationDataTabInDetailsPage(String key, String detailsPage) throws Exception {
		
		/*
		 * 1. Click 'Applications' tab
		 *  	* Wait for tab to be selected
		 * 2. Get a list with the apps listed in the UI
		 * 3. Run request for GET /applications?tenantKey=tenantKeyValue to get a list with the apps in the API
		 * 4. Compare the apps listed on each list - they should be the same 
		 * 
		 */
		
		
		// 1. Click 'Applications' tab
		String xpathAppTab = "//li/a[@id='tenant_tab']/div[text()='Applications ']";  // tenant_tab -- the 'id' is incorrectly named in the DOM - it should application_tab
		driver.findElement(By.xpath(xpathAppTab)).click();
		
		// * Wait for tab to be selected
		WebDriverWait wait = new WebDriverWait(driver, 4);
		wait.until(ExpectedConditions.attributeToBe(By.id("tenant_tab"), "aria-expanded", "true"));
		WaitForElementVisible(By.xpath("//table/thead/tr/th[@jhisortby='KEY']"), 4);
		
		
		// 2. Get a list with the apps listed in the UI
		int applicationCount = Integer.parseInt(driver.findElement(By.xpath("//li[@class='nav-item']/a/div[text()='Applications ']/span")).getText());
		List<ApplicationClass> applicationsInTab = new ArrayList<>();
		List<String> applicationsKeysInTab = new ArrayList<>();
				
		for (int i = 1; i <= applicationCount; i++) {
		
			String appKey = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[1]/a")).getText();
			String appName = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[2]")).getText();
			String appEnabled = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[5]/span")).getText();
			
			ApplicationClass app = new ApplicationClass(appKey, appName, "", CommonMethods.convertToBoolean(appEnabled), "", "");
			applicationsInTab.add(app);
			applicationsKeysInTab.add(appKey);
			
			// System.out.println("key: " + appKey);
			
		}
		
			
		// 3. Run request for GET /applications?tenantKey=tenantKeyValue
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/applications?" + detailsPage + "Key=" + key;
		String apiType = "\"" + "applications" + "\"" + ":";
		
		JSONArray jsonArrayApplications = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		List<ApplicationClass> applicationsFromAPI = CommonMethods.putJsonArrayAppsIntoList(jsonArrayApplications);	
		List<String> applicationKeysFromAPI = new ArrayList<>();
		
		HashMap<String, ApplicationClass> hashmapApplicationsAPI = new HashMap<>();
		
		for (int i = 0; i < applicationsFromAPI.size(); i++) {
			
			applicationKeysFromAPI.add(applicationsFromAPI.get(i).m_Key);
			hashmapApplicationsAPI.put(applicationsFromAPI.get(i).m_Key, applicationsFromAPI.get(i));
			
		}
		

		// 4. Compare the apps listed on each list - they should be the same 
		Collections.sort(applicationsKeysInTab);
		Collections.sort(applicationKeysFromAPI);
		
		for (int i = 0; i < applicationCount; i++) {
			
			Assert.assertEquals(applicationKeysFromAPI.get(i), applicationsKeysInTab.get(i));
			
			String appKey = applicationsInTab.get(i).m_Key;
			
			Assert.assertEquals(applicationsInTab.get(i).m_Name, hashmapApplicationsAPI.get(appKey).m_Name);
			Assert.assertEquals(applicationsInTab.get(i).m_Enabled, hashmapApplicationsAPI.get(appKey).m_Enabled);
		
		}
		
	}
	
	
	// Open element details page. It works for Tenants and Deployments
	public static void openElementDetailsPage(String key, String elementType) throws Exception {
		
		int pageSize = 10;
		
		// make sure that the View button clicked belongs to the tenant that is going to be viewed
		
		String indexViewButton = "";
		
		if (elementType.equals("tenant")) indexViewButton = "5";
		if (elementType.equals("deployment") || elementType.equals("application")) indexViewButton = "6";
		
		
		for (int i = 1; i <= pageSize; i++) {
		
			String xpathButtonView = "//table/tbody/tr[" + i + "]/td[" + indexViewButton + "]/div/button/span[text()='View']";
			
			driver.findElement(By.xpath(xpathButtonView)).click();	
			
			//WaitForElementPresent(By.xpath("//jhi-" + elementType + "-detail/div/h2/div[text()='Tenant Detail ']"), 3);
			
			String xpathKey = "//jhi-" + elementType + "-detail/div/div/div[@class='row']/dt[text()='Key:']/following-sibling::dd";
			
			WaitForElementPresent(By.xpath(xpathKey), 3);
			
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
			
}
