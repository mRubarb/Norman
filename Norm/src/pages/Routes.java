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
			GetAndAddRouteFromApiToExpectedList(jo); // add all the fields in the object.
		}		
		
		System.out.println("Size from API = " +  listOfExpectedRoutes.size() + " rows.");
		
		// set page size to max.
		WaitForElementClickable(By.xpath("(//span/label)[4]"), 5, "");
		driver.findElement(By.xpath("(//span/label)[4]")).click(); 		
		Thread.sleep(1000);
		
		// store the application in UI to listOfActualApps
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		ShowApplicationsActualAndExpectedCollection(); 
		
		// verify actual and expected are equal.
		VerifyApplicationsCollectionsExpectedAndActual();
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
	
	
	// add expected items found in json object jo passed in to variables in this class.
	// make a route class and add it to the 'listOfExpectedRoutes' list 
	public static void GetAndAddRouteFromApiToExpectedList(JSONObject jo) throws JSONException
	{
		key = jo.getString("key");  

		// tenant items.
		JSONObject myJobj =  jo.getJSONObject("tenant");
		tenantKey = (String) myJobj.get("key");
		tenantName = (String) myJobj.get("name");
		//ShowText(tenantKey);
		//ShowText(tenantName);
		
		// application items.
		myJobj =  jo.getJSONObject("application");
		appKey = (String) myJobj.get("key");
		appName = (String) myJobj.get("name");
		//ShowText(appKey);
		//ShowText(appName);

		// deployment items.
		myJobj =  jo.getJSONObject("deployment");
		deployKey = (String) myJobj.get("key");
		deployVersion = (String) myJobj.get("version");
		//ShowText(deployKey);
		//ShowText(deployVersion);
		
		
		//appKey = jo.getString("applicationKey");
		//deployKey = jo.getString("deploymentKey"); 
		enabled = jo.getBoolean("enabled");		
		tenantId = jo.getString("tenantID");
		host = jo.getString("host");
		path = jo.getString("path");
		allowServiceCalls = jo.getBoolean("allowServiceCalls");
		disabledReason = CommonMethods.GetNonRequiredItem(jo, "disabledReason"); 
		description = CommonMethods.GetNonRequiredItem(jo, "description");
		
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
	
	
	// go through rows and columns and show items or add items to the list of apps.
	public static void ShowActualApplicationsOrStore(ActionForApplications action)
	{
		int numberOfRows = driver.findElements(By.cssSelector(".table.table-striped>tbody>tr")).size(); // get number of rows.
		int numberOfColumns = 0;
		String tempString = "";
		String tempStringTwo = "";
		
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
				//ShowText("...");
				
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
				//enabled = Boolean.valueOf(eleList.get(5).getText().split("\n")[0]);

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
				
				tempStringTwo = eleList.get(6).getText(); // .split("\n")[0];
				
				/*
				ShowText("...");				
				ShowText(key);
				ShowText(host);
				ShowText(path);
				ShowText(tenantId);
				ShowText(tenantKey);
				ShowText(tenantName);
				ShowText(appKey);				
				ShowText(appName);
				ShowText(deployKey);				
				ShowText(deployVersion);
				ShowText(String.valueOf(enabled));				
				ShowText(disabledReason);
				ShowText(tempStringTwo);
				*/			
				// listOfExpectedApps.add(new ApplicationClass(key, name, description, enabled));
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
	public static void VerifyApplicationsCollectionsExpectedAndActual()
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
			//ShowText(appClass.m_Key);
			//ShowText(listOfExpectedApps.get(x).m_Key);			
			Assert.assertTrue(routeClass.m_Key.equals(listOfExpectedRoutes.get(x).m_Key));
			Assert.assertTrue(routeClass.m_tennantKey .equals(listOfExpectedRoutes.get(x).m_tennantKey));
			Assert.assertTrue(routeClass.m_tennantName.equals(listOfExpectedRoutes.get(x).m_tennantName));
			Assert.assertTrue(routeClass.m_appKey.equals(listOfExpectedRoutes.get(x).m_appKey));
			Assert.assertTrue(routeClass.m_appname.equals(listOfExpectedRoutes.get(x).m_appname)); 	
			Assert.assertTrue(routeClass.m_deployKey.equals(listOfExpectedRoutes.get(x).m_deployKey));			
			Assert.assertTrue(routeClass.m_deployVersion.equals(listOfExpectedRoutes.get(x).m_deployVersion));		
			
			Assert.assertTrue(routeClass.m_tenantId.equals(listOfExpectedRoutes.get(x).m_tenantId));			
			//Assert.assertTrue(routeClass.m_description.equals(listOfExpectedRoutes.get(x).m_description)); // no description shown in the UI.		
			
			Assert.assertTrue(routeClass.m_path .equals(listOfExpectedRoutes.get(x).m_path ));
			x++;
		}
	}
	
	
}
