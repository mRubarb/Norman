package pages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.portable.ValueOutputStream;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import baseItems.BaseMain;
import classes.ApplicationClass;
import classes.RouteClass;
import common.CommonMethods;
import pages.Applications.ActionForApplications;

public class Routes extends BaseMain 
{
	public static String key = "";
	public static String tenantKey = "";
	public static String tenantName = "";	
	public static String appKey = "";	
	public static String appName = "";	
	public static String deployKey = "";
	public static String deployVersion = "";	
	public static String tenantId = "";
	public static String description = "";
	public static boolean enabled = false;	
	public static String disabledReason = "";	
	public static boolean allowServiceCalls = false;
	public static String host = "";
	public static String path = "";
	
	public static String apiType = "\"routes\":";
	public static String RoutesTableCss = ".table.table-striped>tbody>tr";
	public static String RoutesURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/routes";
	public static String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTUyMzY1MjUwOH0.zptwBdrk3cDixUCMuhFShkFdGDSBC_LYyKrvBIevnpwMFNVX6DFRO-40EZhbzhGrni41rS4eP8VWzPcQjNWdPA";

	public static int maxItemsPerPage = 50;
	
	public static int expectedNumberOfColumns = 8;
	
	public static List<RouteClass> listOfExpectedRoutes = new ArrayList<RouteClass>();
	public static List<RouteClass> listOfActualRoutes = new ArrayList<RouteClass>();	

	public static int[] pageSizes = {5, 10, 20, 50}; // selectable page sizes in console.	
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// this gets all items in the collection from the API and all items from the applications UI and compares them.
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void VerifyFullList() throws IOException, JSONException, InterruptedException
	{
		String url = RoutesURL + "?pageSize=300&includeTenant=true&&includeDeployment=true&includeApplication=true"; // get all the routes from the API.
		String metadata = "";
		int totalCount = 0;
		
		metadata = CommonMethods.GetMetaDataWithUrl(token, url, apiType); // metadata

		// take out totalCount 
		totalCount = Integer.parseInt(metadata.split(":")[2].split(",")[0]);
		
		// this will have the collection of routes in a JSON array.
		JSONArray jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);

		Assert.assertTrue(jArray.length() == totalCount); // verify collection count matches metadata. 
		
		// store all the routes from API onto expected list.
		for(int x = 0; x < jArray.length(); x++)
		{
			// the list page has a maximum number of rows. 
			if(x == (maxItemsPerPage - 1))
			{
				break;
			}			
			
			JSONObject jo = jArray.getJSONObject(x); // get current json object from the list  
			GetAndAddRouteFromApiToExpectedList(jo); // add all the fields into a route object.
		}		
		
		System.out.println("Size from API = " +  listOfExpectedRoutes.size() + " rows.");
		
		// set page size to max.
		WaitForElementClickable(By.xpath("(//span/label)[4]"), 5, "");
		driver.findElement(By.xpath("(//span/label)[4]")).click(); 		
		Thread.sleep(1000);
		
		// store the routes in UI to listOfActualApps
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		// ShowApplicationsActualAndExpectedCollection(); 
		
		// verify actual and expected are equal.
		VerifyRouteCollectionsExpectedAndActual();
	}		
	
	// //////////////////////////////////////////////////////////////
	// this is for sorting all page sizes for all sortable columns.
	// //////////////////////////////////////////////////////////////	
	public static void VerifySortingMultiplePages() throws Exception
	{
		// get all the applications from API.
		String url = RoutesURL + "?pageSize=300";
		String metadata = "";
		int totalCount = 0;
		int numberOfPages = 0;
		int pageSize = 0;
		int pageSizeSelectorIndex = 1;

		// get total count.
		metadata = CommonMethods.GetMetaDataWithUrl(token, url, apiType);
		totalCount = Integer.parseInt(metadata.split(":")[2].split(",")[0]); // take out totalCount
		ShowInt(totalCount);

		// this goes through each page size in the array 'pageSizes' that holds the page sizes.
		// for each page size 
		for(int tempInt : pageSizes)
		{
			System.out.println("Verify page size: " + tempInt);
			
			SetUiPageSizeSelector(pageSizeSelectorIndex);
			pageSize = tempInt;
			numberOfPages = GetTotalPages(totalCount, pageSize);
			System.out.println("Expected number of pages for page size = " +  pageSize + " is " + numberOfPages);

			// click a sort item and verify.
			ClickSorting("//span[text()='Key']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "KEY");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Key']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "KEY");

			// click a sort item and verify.
			ClickSorting("//span[text()='Host']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "HOST");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Host']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "HOST");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Path']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "PATH");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Path']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "PATH");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Tenant ID']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "TENANT_ID");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Tenant ID']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "TENANT_ID");

			
			// click a sort item and verify.
			ClickSorting("//span[text()='Tenant Key']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "TENANT_KEY");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Tenant Key']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "TENANT_KEY");			
			
			// click a sort item and verify.
			ClickSorting("(//span[text()='Name'])[1]");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "TENANT_NAME");
			
			// click a sort item and verify.
			ClickSorting("(//span[text()='Name'])[1]");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "TENANT_NAME");			

			// click a sort item and verify.
			ClickSorting("(//span[text()='Application Key'])[1]");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "APPLICATION_KEY");
			
			// click a sort item and verify.
			ClickSorting("(//span[text()='Application Key'])[1]");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "APPLICATION_KEY");

			// click a sort item and verify.
			ClickSorting("(//span[text()='Name'])[2]");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "APPLICATION_NAME");
			
			// click a sort item and verify.
			ClickSorting("(//span[text()='Name'])[2]");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "APPLICATION_NAME");									


			// click a sort item and verify.
			ClickSorting("//span[text()='Deployment Key']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "DEPLOYMENT_KEY");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Deployment Key']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "DEPLOYMENT_KEY");									

			// click a sort item and verify.
			ClickSorting("//span[text()='Version']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "DEPLOYMENT_VERSION");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Version']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "DEPLOYMENT_VERSION");									
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Enabled']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "IS_ENABLED");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Enabled']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "IS_ENABLED");									

			// click a sort item and verify.
			ClickSorting("//span[text()='Reason']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "DISABLED_REASON");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Reason']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "DISABLED_REASON");									
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Service Calls']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "ALLOW_SERVICE_CALLS");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Service Calls']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "ALLOW_SERVICE_CALLS");									
			
			pageSizeSelectorIndex++;
		}	
	}

			
	public static void GoToRoutes()
	{
		CommonMethods.selectItemPlatformDropdown("Routes");
	}	
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	// 															HELPERS 
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	// this clicks a column selection to set sorting mode.
	public static void ClickSorting(String item) throws Exception		
	{
		WaitForElementClickable(By.xpath(item), 2, "");
		driver.findElement(By.xpath(item)).click();
		Thread.sleep(2000);
	}
		
	// this sets page to 5, 10, 20, or 50.
	static public void SetUiPageSizeSelector(int index) throws InterruptedException
	{
		if(index > 4 || index < 1)
		{
			Assert.fail("Bad index number sent to 'SetPageSize' method in Applicatiins page class.");
		}
		
		// set page size to max.
		WaitForElementClickable(By.xpath("(//span/label)[" + index + "]"), 5, "");
		driver.findElement(By.xpath("(//span/label)[" + index + "]")).click(); 		
		Thread.sleep(1000);
	}	
	
	public static void VerifyPagesSorting(int numberOfPages, String apiType, int pageSize, String sortDirection, String sortBy) throws Exception		
	{
		for(int x = 0; x < numberOfPages; x++) // go through each page.
		{
			// click a page per x index.
			WaitForElementClickable(By.cssSelector(".pagination>li:nth-of-type(" + (x + 3) + ")>a"), 3, "");
			driver.findElement(By.cssSelector(".pagination>li:nth-of-type(" + (x + 3) + ")>a")).click();
			Thread.sleep(1000);

			// this stores API info for page x + 1  into listOfExpectedApps 
			PassDataAndStoreApiRequest(apiType, pageSize, x + 1, sortDirection, sortBy);
			
			// store the application info in UI to listOfActualApps
			ShowActualApplicationsOrStore(ActionForApplications.Store);

			// verify actual and expected are equal.
			VerifyRouteCollectionsExpectedAndActual();
		}
	}		
	
	// add sortBy, sortDirection
	public static void PassDataAndStoreApiRequest(String apiType, int pageSize, int pageNumber, String sortDirection, String sortBy) throws IOException, JSONException, InterruptedException
	{
		listOfActualRoutes.clear();
		listOfExpectedRoutes.clear();
		JSONArray jArray;
		String url = "";
		
		// update URL for page setting and get application list from API.
		url = RoutesURL + "?pageSize=" +  String.valueOf(pageSize) + "&page=" + String.valueOf(pageNumber + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy + "&includeTenant=true&includeDeployment=true&includeApplication=true");
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
						
		AddJsonArrayToExpectedList(jArray);
		//ShowApplicationsActualAndExpectedCollection();
	}	
	
	public static void AddJsonArrayToExpectedList(JSONArray jArray) throws JSONException
	{
		// store all the applications from API call onto expected list.
		for(int y = 0; y < jArray.length(); y++)
		{
			JSONObject jo = jArray.getJSONObject(y);
			
			GetAndAddRouteFromApiToExpectedList(jo);
			
			//key = jo.getString("key");
			//name = jo.getString("name");
			//description = jo.getString("description");
			//enabled = jo.getBoolean("enabled");
			//listOfExpectedApps.add(new ApplicationClass(key, name, description, enabled, defaultHost, defaultPath));
		}		
	}	
	
	// add expected items found in json object, jo, passed into this method into local variables in this class.
	// make a route class from the variabless and add it to the 'listOfExpectedRoutes' list 
	public static void GetAndAddRouteFromApiToExpectedList(JSONObject jo) throws JSONException
	{
		key = jo.getString("key");  

		// tenant items.
		JSONObject myJobj =  jo.getJSONObject("tenant");
		tenantKey = (String) myJobj.get("key");
		tenantName = (String) myJobj.get("name");
		
		// application items.
		myJobj =  jo.getJSONObject("application");
		appKey = (String) myJobj.get("key");
		appName = (String) myJobj.get("name");

		// deployment items.
		myJobj =  jo.getJSONObject("deployment");
		deployKey = (String) myJobj.get("key");
		deployVersion = (String) myJobj.get("version");
		
		enabled = jo.getBoolean("enabled");		
		tenantId = jo.getString("tenantID");
		host = jo.getString("host");
		path = jo.getString("path");
		allowServiceCalls = jo.getBoolean("allowServiceCalls");
		disabledReason = CommonMethods.GetNonRequiredItem(jo, "disabledReason"); 
		description = CommonMethods.GetNonRequiredItem(jo, "description");
		
		// add to expected list
		listOfExpectedRoutes.add(new RouteClass(key, tenantKey, tenantName, appKey, appName, deployKey, deployVersion, tenantId, description, enabled, disabledReason, allowServiceCalls, host, path));
	}		
	
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
		
		System.out.println(response.toString());
		
		System.out.println(response.toString().split(apiType)[1]);		
		
		// create json array by removing meta data   
		JSONArray jasonArray = new JSONArray(response.toString().split(apiType)[1]);		
		return jasonArray;
	}		

	// return the default path from a combination of host and path.  
	public static String GetDefaultPath(String hostPath)
	{
		int numSlashes = StringUtils.countMatches(hostPath, "/");
		
		// ShowText(hostPath.split("/")[0]);
		String path = "";
		
		for(int x = 1; x != numSlashes + 1; x++)
		{
			path += "/" + hostPath.split("/")[x];
		}
		
		return path;
	}	
	
	// /////////////////////////////////////////////////////////////////////////////////////////
	// go through rows and columns and show items or add items to the list of route objects
	// /////////////////////////////////////////////////////////////////////////////////////////
	public static void ShowActualApplicationsOrStore(ActionForApplications action)
	{
		int numberOfRows = driver.findElements(By.cssSelector(".table.table-striped>tbody>tr")).size(); // get number of rows.
		int numberOfColumns = 0;
		String tempString = "";
		
		System.out.println("Num Rows In UI. = " + numberOfRows);
		

		for(int x = 1; x <= numberOfRows; x++)
		{
			numberOfColumns = driver.findElements(By.cssSelector(RoutesTableCss + ":nth-of-type(1)>td")).size(); 
			
			Assert.assertTrue(numberOfColumns == expectedNumberOfColumns); // verify number of columns is correct.
			
			if (action.equals(ActionForApplications.Show))
			{
				ShowText("----------------------------");
				for(int y = 1; y <= numberOfColumns; y++)
				{
					ShowText(driver.findElement(By.cssSelector(RoutesTableCss + ":nth-of-type(" + x + ")>td:nth-of-type(" + y + ")")).getText());	
				}	
			}
			else
			{
				// get items in this TD block
				List<WebElement> eleList = driver.findElements(By.cssSelector(RoutesTableCss + ":nth-of-type(" + x + ")>td"));
				
				//System.out.println("------ " + eleList.size());
				
				key = eleList.get(0).getText();
				
				tempString = eleList.get(1).getText().split("\n")[0]; // host and path combined.
 
				host = tempString.split("/")[0]; // assign host, item before the first "/".
				path = GetDefaultPath(tempString); // assign path, everything after first "/".

				tenantId = eleList.get(1).getText().split("\n")[1];
				tenantKey = eleList.get(2).getText().split("\n")[0];
				tenantName = eleList.get(2).getText().split("\n")[1];
				appKey = eleList.get(3).getText().split("\n")[0];
				appName = eleList.get(3).getText().split("\n")[1];				
				deployKey = eleList.get(4).getText().split("\n")[0];
				deployVersion = eleList.get(4).getText().split("\n")[1];				

				tempString = eleList.get(5).getText().split("\n")[0];
				if(tempString.equals("DISABLED"))
				{
					disabledReason = eleList.get(5).getText().split("\n")[1];		
					enabled = false;
				}
				else
				{
					disabledReason = "";
					enabled = true;
				}
				
				tempString = eleList.get(6).getText(); // .split("\n")[0];
				if(tempString.equals("ALLOW"))
				{
					allowServiceCalls = true;
				}
				else
				{
					allowServiceCalls = false;					
				}
				
				// add current route to actual list.
				listOfActualRoutes.add(new RouteClass(key, tenantKey, tenantName, appKey, appName, deployKey, deployVersion, tenantId, description, enabled, disabledReason, allowServiceCalls, host, path));
			}			
		}
	}	
	
	public static void ShowApplicationsActualAndExpectedCollection()
	{
		ShowText(" ********** Actual Apps **********");
		for(RouteClass routeClass : listOfActualRoutes)
		{
			routeClass.ShowRoute();
		}

		ShowText(" ********** Expected Apps **********");
		for(RouteClass routeClass : listOfExpectedRoutes)
		{
			routeClass.ShowRoute();
		}
	}
	
	// compare the list of apps from the API call to the list of apps from the UI.
	// both lists should be in the same order.
	public static void VerifyRouteCollectionsExpectedAndActual()
	{
		boolean bFoundInLoop = false;
		String message = "";
		int x = 0;
		
		// lists should be same size.
		Assert.assertTrue(listOfActualRoutes.size() == listOfExpectedRoutes.size(), 
				          "Error in size checks. List of actual size = " + listOfActualRoutes.size() + " List of expected size = " + listOfExpectedRoutes.size()); 
		
		// lists should be same order.
		for(RouteClass routeClass :  listOfActualRoutes)
		{
			Assert.assertTrue(routeClass.m_Key.equals(listOfExpectedRoutes.get(x).m_Key));
			Assert.assertTrue(routeClass.m_tennantKey .equals(listOfExpectedRoutes.get(x).m_tennantKey));
			Assert.assertTrue(routeClass.m_tennantName.equals(listOfExpectedRoutes.get(x).m_tennantName));
			Assert.assertTrue(routeClass.m_appKey.equals(listOfExpectedRoutes.get(x).m_appKey));
			Assert.assertTrue(routeClass.m_appname.equals(listOfExpectedRoutes.get(x).m_appname)); 	
			Assert.assertTrue(routeClass.m_deployKey.equals(listOfExpectedRoutes.get(x).m_deployKey));			
			Assert.assertTrue(routeClass.m_deployVersion.equals(listOfExpectedRoutes.get(x).m_deployVersion));		
			Assert.assertTrue(routeClass.m_tenantId.equals(listOfExpectedRoutes.get(x).m_tenantId));			
			//Assert.assertTrue(routeClass.m_description.equals(listOfExpectedRoutes.get(x).m_description)); // no description shown in the UI.		
			Assert.assertTrue(routeClass.m_tenantId.equals(listOfExpectedRoutes.get(x).m_tenantId));			
			Assert.assertTrue(routeClass.m_enabled == listOfExpectedRoutes.get(x).m_enabled);
			Assert.assertTrue(routeClass.m_disabledReason.equals(listOfExpectedRoutes.get(x).m_disabledReason));			
			Assert.assertTrue(routeClass.m_host.equals(listOfExpectedRoutes.get(x).m_host));			
			Assert.assertTrue(routeClass.m_path .equals(listOfExpectedRoutes.get(x).m_path ));
			Assert.assertTrue(routeClass.m_allowServiceCalls == listOfExpectedRoutes.get(x).m_allowServiceCalls);
			x++;
		}
	}
	
	static public int GetTotalPages(int totalCount, int pageSize)
	{
		int totalPages = 0;
		
		totalPages = totalCount/pageSize;
		
		if(totalCount % pageSize > 0)
		{
			totalPages++;
		}
		return totalPages;
	}		
	
	
}
