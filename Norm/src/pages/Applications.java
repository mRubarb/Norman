package pages;


import java.util.ArrayList;
import java.util.List;

import baseItems.BaseMain;
import classes.ApplicationClass;
import classes.TenantAppForAppSearch;
import common.CommonMethods;

import org.testng.Assert;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Applications extends BaseMain
{
	public static String AppTableCss = ".table.table-striped>tbody>tr";
	public static int expectedNumberOfColumns = 5;
	
	public static String key;
	public static String name;
	public static String description;
	public static String enabledString;
	public static boolean enabled;
	public static String applicationsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/applications";
	public static String tenantsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/tenants";	

	public static String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTUxODgxOTAzOH0.HHgZ-BwWT1jrvo8yPLJsOA5YEP3utNDdBolJPHpbY4wcpOaBWGiXKS2ivbg-8pLvuGOb2ri6MTk3W-NqlsIEcg";
	
	public static int maxItemsPerPage = 50;
	public static int[] pageSizes = {5, 10, 20, 50}; // selectable page sizes in console. 
	
	public static List<ApplicationClass> listOfExpectedApps = new ArrayList<ApplicationClass>();
	public static List<ApplicationClass> listOfActualApps = new ArrayList<ApplicationClass>();	
	public static List<TenantAppForAppSearch> listOfTenantWithAppExpected = new ArrayList<TenantAppForAppSearch>();
	
	/*
    static enum PageSelections
    {
    	five,
    	ten,
    	twenty,
    	fifty
    }	
	*/
	
	public static enum ActionForApplications
	{
		Show,
		Store
	}	
		
	public static void GoToApplications()
	{
		CommonMethods.selectItemPlatformDropdown("Applications");
	}
	
	// this gets all items in the collection from the API and all items from the applications UI and compares them.
	public static void VerifyFullList() throws IOException, JSONException, InterruptedException
	{
		String url = applicationsURL + "?pageSize=300"; // get all the applications from API.
		String apiType = "\"applications\":";
		String metadata = "";
		int totalCount = 0;
		
		metadata = CommonMethods.GetMetaDataWithUrl(token, url, apiType);

		// take out totalCount 
		totalCount = Integer.parseInt(metadata.split(":")[2].split(",")[0]);
		
		JSONArray jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);

		Assert.assertTrue(jArray.length() == totalCount); // verify collection count matches metadata. 
		
		// store all the applications from API onto expected list.
		for(int x = 0; x < jArray.length(); x++)
		{
			// the list page has a maximum number of rows. 
			if(x == (maxItemsPerPage - 1))
			{
				break;
			}			
			
			JSONObject jo = jArray.getJSONObject(x);
			//System.out.println(jo.getString("key"));		
			//System.out.println(jo.getString("name"));
			//System.out.println(jo.getString("description"));
			//System.out.println(jo.getBoolean("enabled"));
			
			key = jo.getString("key");
			name = jo.getString("name");
			description = jo.getString("description");
			enabled = jo.getBoolean("enabled");
			
			listOfExpectedApps.add(new ApplicationClass(key, name, description, enabled));
		}		
		
		// set page size to max.
		WaitForElementClickable(By.xpath("(//span/label)[4]"), 5, "");
		driver.findElement(By.xpath("(//span/label)[4]")).click(); 		
		Thread.sleep(1000);
		
		// store the application in UI to listOfActualApps
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		// ShowApplicationsActualAndExpectedCollection();
		
		// verify actual and expected are equal.
		VerifyApplicationsCollectionsExpectedAndActual();
	}	
	
	// this compares what the API returns to what is in the UI for each page size selection that can be tested.
	public static void VerifyPageSettings() throws IOException, JSONException, InterruptedException
	{
		String url = applicationsURL + "?pageSize=300"; // this will get full list of applications from API.
		String apiType = "\"applications\":";
		String metadata = "";
		int totalCount = 0;
		JSONArray jArray;
		
		// get meta data. 
		metadata = CommonMethods.GetMetaDataWithUrl(token, url, apiType);

		// take out totalCount of applications in list. 
		totalCount = Integer.parseInt(metadata.split(":")[2].split(",")[0]);
		
		//  get all of the applications from API call and verify count is equal to meta data totalCount.
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);

		Assert.assertTrue(jArray.length() == totalCount); // verify collection count matches meta data. 
		
		// loop through the possible page selections.
		for(int j = 0; j < pageSizes.length; j++)
		{
			if(pageSizes[j] > totalCount) // make sure there enough applications in collection to get requested page size. if not, break from for loop.
			{
				break;
			}
			ShowText("Verify Page Size " +  pageSizes[j] + " **************** .\r\n");
			SetUiPageSizeSelector(j + 1); // select index of page size 5 selector.
			
			// call info API with page size and store results from API call. store results from UI applications list.  
			ApiRequestWithPageSize(url, apiType, pageSizes[j]);  
			// compare the stored results.
			VerifyApplicationsCollectionsExpectedAndActual();
		}
	}		

	public static void VerifyPaging() throws IOException, JSONException
	{
		VerifyCurrentPaging(token, 3);
		VerifyCurrentPaging(token, 4);		
		VerifyCurrentPaging(token, 5);
	}
	
	public static void VerifySorting() throws IOException, JSONException
	{
		// get totalCount from metadata.
		String url = ""; // applicationsURL + "?pageSize=300";
		String apiType = "\"applications\":";
		String sortDirection = "ASC";
		String sortBy = "";
		JSONArray jArray;

		sortBy = "IS_ENABLED";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy; 
		VerifySortingHelper(url, token, apiType);


		sortBy = "KEY";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy;
		VerifySortingHelper(url, token, apiType);
		
		sortBy = "NAME";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy;
		VerifySortingHelper(url, token, apiType);
		sortDirection = "DESC";

		sortBy = "IS_ENABLED";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy; 
		VerifySortingHelper(url, token, apiType);

		sortBy = "KEY";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy;
		VerifySortingHelper(url, token, apiType);
		
		sortBy = "NAME";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy;
		VerifySortingHelper(url, token, apiType);
	}

	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	// * go through the tenants list and call 'getAppsForTenant' for each tenant.  
	// * find a tenant with one application associated to it and store that tenant/application 
	//   as an object and put the object onto a list.
	// * find the tenant with the most applications associated to it and store that tenant/applications 
	//   as an object and put the object onto a list. 
	// /////////////////////////////////////////////////////////////////////////////////////////////////// 
	public static void VerifyFilteringByTenant() throws IOException, JSONException
	{
		String url = ""; 
		String apiType = "\"tenants\":";
		int indexForTenantWithMostApps = 0;
		JSONObject jo;
		JSONArray jArray;
		JSONArray jArrayAppsFromTenantCall;
		boolean savedTenantWithOneApp = false;
		
		//CommonMethods.showResponse = false;
		
		// create a URL that will get a list of all the tenants.
		url = tenantsURL + "?pageSize=300"; 
		
		// get the list of tenants. 
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);

		// loop through tenants list. find a tenant with one associated application. store the tenant name and the application in a 
		// 'TenantAppForAppSearch' object and add the object to the 'listOfTenantWithApp' list. 
		System.out.println(jArray.length());
		for(int x = 0; x < jArray.length(); x++)
		{
			jo = jArray.getJSONObject(x);
			System.out.println(" Tenant Name found -------------");
			System.out.println(jo.getString("key"));		
			System.out.println("");
			
			// build a URL that will call 'getAppsForTenant' passing in the current tenant key. 
			url = tenantsURL + "/" + jo.getString("key") + "/applications";
			apiType = "\"applications\":";
			
			// call 'getAppsForTenant' passing in the current tenant key. receive back a list of application objects (can also be empty).
			jArrayAppsFromTenantCall = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
			
			// if only find one application returned from getAppsForTenant call, store the tenant name and the application properties 
			// into a 'TenantAppForAppSearch' object. after that, don't store any more tenant names with one application. 
			if((jArrayAppsFromTenantCall.length() == 1) && !savedTenantWithOneApp) 
			{
				BuildTenantAppForAppSearchObject(jo.getString("key"), jArrayAppsFromTenantCall);
				savedTenantWithOneApp = true;
			}
			
			// store the jArray (list of tenants) index of the tenant that has the most associated applications (jArrayAppsFromTenantCall).
			if(jArrayAppsFromTenantCall.length() > 1)
			{
				if(indexForTenantWithMostApps < jArrayAppsFromTenantCall.length())
				{
					indexForTenantWithMostApps = x;
				}
			}
		}
		
		// get tenant with most associated applications.
		jo = jArray.getJSONObject(indexForTenantWithMostApps);
		
		// build a URL that will call 'getAppsForTenant' using the tenant key from above. 
		url = tenantsURL + "/" + jo.getString("key") + "/applications";
		apiType = "\"applications\":";
		
		// call 'getAppsForTenant' passing in the tenant key. 
		jArrayAppsFromTenantCall = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		// store the tenant and applications as an object and store the object onto a list.
		BuildTenantAppForAppSearchObject(jo.getString("key"), jArrayAppsFromTenantCall);
		
		// show tenant with one application and tenant with the most applications.
		for(TenantAppForAppSearch tenApp : listOfTenantWithAppExpected)
		{
			tenApp.Show();
		}
		
	}	
	
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	// 															HELPERS 
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	// receive a list of application(s) and a tenant name. create a 'TenantAppForAppSearch' object to hold the tenant name and all of 
	// the applications that go with it.
	public static void BuildTenantAppForAppSearchObject(String tenantName, JSONArray jArrayOfApps) throws JSONException
	{
		TenantAppForAppSearch tempObj = new TenantAppForAppSearch(tenantName); // create object that will hold tenant name and its associated applications.
		System.out.println("Tenant name in 'BuildTenantAppForAppSearchObject' is " + tenantName);
		
		// loop through the applications in array of applications. get each application and add it to the 'TenantAppForAppSearch' object's list of applications.
		for(int y = 0; y < jArrayOfApps.length(); y++)
		{
			JSONObject joObjectApplication = jArrayOfApps.getJSONObject(y); // get application. 
			tempObj.AddAppToList(new ApplicationClass(joObjectApplication.getString("key"), joObjectApplication.getString("name"), 
					joObjectApplication.getString("description"), joObjectApplication.getBoolean("enabled")));
		}
		listOfTenantWithAppExpected.add(tempObj);
	}	
	
	
	
	public static void VerifySortingHelper(String url, String token, String apiType) throws IOException, JSONException
	{
		JSONArray jArray;
		
		//  get all of the applications from API call and verify count is equal to metadata totalCount.
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);

		AddJsonArrayToExpectedList(jArray);
		// get apps from console and add to actual list// TODO: 
		ShowApplicationsActualAndExpectedCollection();
		listOfExpectedApps.clear();
	}	
	
	// go through rows and columns and show items or add items to the list of apps.
	public static void ShowActualApplicationsOrStore(ActionForApplications action)
	{
		int numberOfRows = driver.findElements(By.cssSelector(".table.table-striped>tbody>tr")).size(); // get number of rows.
		
		for(int x = 1; x <= numberOfRows; x++)
		{
			int numberOfColumns = driver.findElements(By.cssSelector(AppTableCss + ":nth-of-type(1)>td")).size(); 
			
			Assert.assertTrue(numberOfColumns == expectedNumberOfColumns); // verify number of columns is correct.
			
			
			if (action.equals(ActionForApplications.Show))
			{
				ShowText("----------------------------");
				for(int y = 1; y <= numberOfColumns; y++)
				{
					ShowText(driver.findElement(By.cssSelector(AppTableCss + ":nth-of-type(" + x + ")>td:nth-of-type(" + y + ")")).getText());					
				}	
			}
			else
			{
				// get items in this TD block
				List<WebElement> eleList = driver.findElements(By.cssSelector(AppTableCss + ":nth-of-type(" + x + ")>td"));
				
				// store items for app
				key = eleList.get(0).getText();
				name = eleList.get(1).getText();
				description = eleList.get(2).getText();
				enabledString = eleList.get(3).getText();
				//ShowText(enabledString);
				//enabled = Boolean.parseBoolean(enabledString);
				if(enabledString.equals("ENABLED"))
				{
					enabled = true;
				}
				else
				{
					enabled = false;
				}
				
				
				
				
				
				// listOfExpectedApps.add(new ApplicationClass(key, name, description, enabled));
				listOfActualApps.add(new ApplicationClass(key, name, description, enabled));
			}
		}
	}

	public static void AddJsonArrayToExpectedList(JSONArray jArray) throws JSONException
	{
		// store all the applications from API call onto expected list.
		for(int y = 0; y < jArray.length(); y++)
		{
			JSONObject jo = jArray.getJSONObject(y);
			
			key = jo.getString("key");
			name = jo.getString("name");
			description = jo.getString("description");
			enabled = jo.getBoolean("enabled");
			listOfExpectedApps.add(new ApplicationClass(key, name, description, enabled));				
		}		
	}	
	
	public static void VerifyCurrentPaging(String token, int pageSize) throws IOException , JSONException
	{
		String url = applicationsURL + "?pageSize=300";
		String apiType = "\"applications\":";
		String metadata = "";
		int totalCount = 0;
		int totalPages = 0;
		int numberOfItemsInPartialPage = 0;
		JSONArray jArray;
		
		// get metadata
		metadata = CommonMethods.GetMetaDataWithUrl(token, url, apiType);
		
		// take out totalCount 
		totalCount = Integer.parseInt(metadata.split(":")[2].split(",")[0]);
		
		// get the expected total number of pages.
		totalPages = GetTotalPages(totalCount, pageSize);
		
		// if there will be a last page with less than 'pageSize' items, calculate how many items there should be. 
		if((totalCount % pageSize) > 0)
		{
			numberOfItemsInPartialPage = totalCount % pageSize;			
		}
		
		// call each page through the API and get each page shown in the console. for each of the pages compare actual (from console) to expected (from API). 
		for(int x = 0; x < totalPages; x++)
		{
			// get current page from API call and store page info into expected list.
			url = applicationsURL + "?pageSize=" + pageSize + "&page=" + String.valueOf(x+1);
			jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
			AddJsonArrayToExpectedList(jArray);
			
			// set page in console and get items in page onto actual list. // TODO
			
			// ShowApplicationsActualAndExpectedCollection(); 
			//listOfExpectedApps.clear();
			
			// redundant check -- if the last page will have less than 'pageSize' number of items, verify the correct number of 
			if(x == (totalPages - 1) && numberOfItemsInPartialPage != 0)
			{
				ShowText("Verify length.");
				ShowInt(jArray.length());
			}
			

			// compare list // TODO:
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
		Assert.assertTrue(listOfActualApps.size() == listOfExpectedApps.size(), 
				          "Error in size checks. List of actual size = " + listOfActualApps.size() + " List of expected size = " + listOfExpectedApps.size()); 
		
		// lists should be same order.
		for(ApplicationClass appClass :  listOfActualApps)
		{
			Assert.assertTrue(appClass.m_Key.equals(listOfExpectedApps.get(x).m_Key));
			Assert.assertTrue(appClass.m_Name.equals(listOfExpectedApps.get(x).m_Name));
			Assert.assertTrue(appClass.m_Description.equals(listOfExpectedApps.get(x).m_Description));
			Assert.assertTrue(appClass.m_Enabled == listOfExpectedApps.get(x).m_Enabled);
			
			x++;
		}

		// this is for when the list objects are not in the same order. 
		/*
		for(ApplicationClass appClass :  listOfActualApps)
		{
			
			for(int x = 0; x < listOfExpectedApps.size(); x++)
			{
				if(appClass.m_Key.equals(listOfExpectedApps.get(x).m_Key))
				{
					ShowText(appClass.m_Key + " " + listOfExpectedApps.get(x).m_Key);
					Assert.assertTrue(appClass.m_Name.equals(listOfExpectedApps.get(x).m_Name));
					Assert.assertTrue(appClass.m_Description.equals(listOfExpectedApps.get(x).m_Description));
					Assert.assertTrue(appClass.m_Enabled == listOfExpectedApps.get(x).m_Enabled);
					bFoundInLoop = true;
				}
			}
			
			if(!bFoundInLoop)
			{
				ShowText("Fail in method 'VerifyApplicationsCollectionsExpectedAndActual'");
				message = "Fail in method 'VerifyApplicationsCollectionsExpectedAndActual'. ";
				Assert.fail(message + "Application class with key: "  +  appClass.m_Key + " not found");
			}
			bFoundInLoop = false;
		}
		*/
	}	
	
	public static void ShowApplicationsActualAndExpectedCollection()
	{
		ShowText(" ********** Actual Apps **********");
		for(ApplicationClass appClass : listOfActualApps)
		{
			appClass.ShowApp();
		}

		ShowText(" ********** Expected Apps **********");
		for(ApplicationClass appClass : listOfExpectedApps)
		{
			appClass.ShowApp();
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
	
	public static void ApiRequestWithPageSize(String url, String apiType, int pageSize) throws IOException, JSONException, InterruptedException
	{
		listOfActualApps.clear();
		listOfExpectedApps.clear();
		JSONArray jArray;

		ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		// update URL for page setting and get application list from API.
		url = applicationsURL + "?pageSize=" +  String.valueOf(pageSize);
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
						
		AddJsonArrayToExpectedList(jArray);
		//ShowApplicationsActualAndExpectedCollection();
	}
	
	
}
