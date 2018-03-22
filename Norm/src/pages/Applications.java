package pages;


import java.util.ArrayList;
import java.util.List;

import baseItems.BaseMain;
import classes.ApplicationClass;
import classes.TenantAppForAppSearch;
import common.CommonMethods;

import org.testng.Assert;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Applications extends BaseMain
{
	public static String AppTableCss = ".table.table-striped>tbody>tr";
	public static int expectedNumberOfColumns = 6;
	
	public static String key;
	public static String name;
	public static String description;
	public static String enabledString;
	public static boolean enabled;
	public static String defaultHost;	
	public static String defaultPath;	
	public static String defaultHostPath;
	
	public static String applicationsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/applications";
	public static String tenantsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/tenants";	

	public static String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTUyMzY1MjUwOH0.zptwBdrk3cDixUCMuhFShkFdGDSBC_LYyKrvBIevnpwMFNVX6DFRO-40EZhbzhGrni41rS4eP8VWzPcQjNWdPA";
	
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
	
	// this gets all items in the collection from the API and all items from the applications UI and compare them.
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
			
			JSONObject jo = jArray.getJSONObject(x); // get current json object from the list  
			GetAndAddAppFromApiToExpectedList(jo); // add all the fields in the object.
		}		
		
		System.out.println("Size from UI = " +     listOfExpectedApps.size() + " rows.");
		
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
			PassDataAndStoreApiRequest(apiType, pageSizes[j], 0, "", ""); // NEEDS WORK -- was changed --- WONT'T work in this method   
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
	
	// verify sorting on all columns that can be sorted.
	public static void VerifySortingFullPage() throws Exception
	{
		String url = ""; 
		String apiType = "\"applications\":";
		String sortDirection = "";
		String sortBy = "";
		JSONArray jArray;

		SetPageSizeToMax();
	
		ClickSorting("//span[text()='Enabled']");
		
		sortDirection = "ASC";
		sortBy = "IS_ENABLED";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy; 
		VerifySortingHelper(url, token, apiType);
		
		ClickSorting("//span[text()='Enabled']");
		
		sortDirection = "DESC";
		sortBy = "IS_ENABLED";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy; 
		VerifySortingHelper(url, token, apiType);
		
		ClickSorting("//span[text()='Key']");
		
		sortDirection = "ASC";
		sortBy = "KEY";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy; 
		VerifySortingHelper(url, token, apiType);
		
		ClickSorting("//span[text()='Key']");
		
		sortDirection = "DESC";
		sortBy = "KEY";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy; 
		VerifySortingHelper(url, token, apiType);

		
		ClickSorting("//span[text()='Name']");
		
		sortDirection = "ASC";
		sortBy = "NAME";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy; 
		VerifySortingHelper(url, token, apiType);
		
		ClickSorting("//span[text()='Name']");
		
		sortDirection = "DESC";
		sortBy = "NAME";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy; 
		VerifySortingHelper(url, token, apiType);
		
		ClickSorting("//span[text()='Host']");

		sortDirection = "ASC";
		sortBy = "DEFAULT_HOST";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy; 
		VerifySortingHelper(url, token, apiType);
		
		ClickSorting("//span[text()='Host']");
		
		sortDirection = "DESC";
		sortBy = "DEFAULT_HOST";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy; 
		VerifySortingHelper(url, token, apiType);

		ClickSorting("//span[text()='Path']");
		
		sortDirection = "ASC";
		sortBy = "DEFAULT_PATH";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy; 
		VerifySortingHelper(url, token, apiType);
		
		ClickSorting("//span[text()='Path']");
		
		sortDirection = "DESC";
		sortBy = "DEFAULT_PATH";
		url = applicationsURL + "?pageSize=300" + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy; 
		VerifySortingHelper(url, token, apiType);
	}


	// bladd
	public static void VerifySortingMultiplePages() throws Exception
	{
		// get all the applications from API.
		String url = applicationsURL + "?pageSize=300";
		String apiType = "\"applications\":";
		String metadata = "";
		String sortDirection = "";
		String sortBy = "";
		int totalCount = 0;
		int numberOfPages = 0;
		int pageSize = 0;
		int pageSizeSelectorIndex = 1;
		
		metadata = CommonMethods.GetMetaDataWithUrl(token, url, apiType);

		// take out totalCount 
		totalCount = Integer.parseInt(metadata.split(":")[2].split(",")[0]);
		
		ShowInt(totalCount);

		//SetUiPageSizeSelector(1);
		for(int tempInt : pageSizes)
		{
			// System.out.println(tempInt);
			SetUiPageSizeSelector(pageSizeSelectorIndex);
			pageSize = tempInt;
			numberOfPages = GetTotalPages(totalCount, pageSize);
			System.out.println("Expected number of pages for page size = " +  pageSize + " = " + numberOfPages);
			
			// click a sort item.
			ClickSorting("//span[text()='Enabled']");
			
			// define sorting
			sortDirection = "ASC";
			sortBy = "IS_ENABLED";
			
			VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);
			
			// click a sort item.
			ClickSorting("//span[text()='Enabled']");
			
			// define sorting
			sortDirection = "DESC";
			sortBy = "IS_ENABLED";
			
			VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);

			pageSizeSelectorIndex++;
			
			
		}
		
		
		//pageSize = 5;
		//numberOfPages = GetTotalPages(totalCount, pageSize);
		
		//System.out.println("Expected number of pages for page size = " +  pageSize + " = " + numberOfPages);

		// /////////////////////////////////////
		// set page size to 5.
		// /////////////////////////////////////
		//SetUiPageSizeSelector(1);

		
		
		/*
		// click a sort item.
		ClickSorting("//span[text()='Enabled']");
		
		// define sorting
		sortDirection = "ASC";
		sortBy = "IS_ENABLED";
		
		VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);

		// click a sort item.
		ClickSorting("//span[text()='Enabled']");
		
		// define sorting
		sortDirection = "DESC";
		sortBy = "IS_ENABLED";
		
		VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);

		
		// click a sort item.
		ClickSorting("//span[text()='Key']");
		
		// define sorting
		sortDirection = "ASC";
		sortBy = "KEY";
		
		VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);

		// click a sort item.
		ClickSorting("//span[text()='Key']");
		
		// define sorting
		sortDirection = "DESC";
		sortBy = "KEY";
		
		VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);


		// click a sort item.
		ClickSorting("//span[text()='Name']");
		
		// define sorting
		sortDirection = "ASC";
		sortBy = "NAME";
		
		VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);

		// click a sort item.
		ClickSorting("//span[text()='Name']");
		
		// define sorting
		sortDirection = "DESC";
		sortBy = "NAME";
		
		VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);

		
		// click a sort item.
		ClickSorting("//span[text()='Host']");
		
		// define sorting
		sortDirection = "ASC";
		sortBy = "DEFAULT_HOST";
		
		VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);

		// click a sort item.
		ClickSorting("//span[text()='Host']");
		
		// define sorting
		sortDirection = "DESC";
		sortBy = "DEFAULT_HOST";
		
		VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);
		
		// click a sort item.
		ClickSorting("//span[text()='Path']");
		
		// define sorting
		sortDirection = "ASC";
		sortBy = "DEFAULT_PATH";
		
		VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);

		// click a sort item.
		ClickSorting("//span[text()='Path']");
		
		// define sorting
		sortDirection = "DESC";
		sortBy = "DEFAULT_PATH";
		
		VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);		
		*/
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
			// bladdxx
			//JSONObject joObjectApplication = jArrayOfApps.getJSONObject(y); // get application. 
			//tempObj.AddAppToList(new ApplicationClass(joObjectApplication.getString("key"), joObjectApplication.getString("name"), 
			//		joObjectApplication.getString("description"), joObjectApplication.getBoolean("enabled")));
		}
		listOfTenantWithAppExpected.add(tempObj);
	}	
	
	
	
	public static void VerifySortingHelper(String url, String token, String apiType) throws IOException, JSONException
	{
		JSONArray jArray;
		
		//  get all of the applications from API call and verify count is equal to metadata totalCount.
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);

		AddJsonArrayToExpectedList(jArray);
		
		// store the applications in UI to listOfActualApps
		ShowActualApplicationsOrStore(ActionForApplications.Store);

		// ShowApplicationsActualAndExpectedCollection();
		
		VerifyApplicationsCollectionsExpectedAndActual();
		
		
		listOfExpectedApps.clear();
		listOfActualApps.clear();		
		
		
	}	
	
	// go through rows and columns and show items or add items to the list of apps.
	public static void ShowActualApplicationsOrStore(ActionForApplications action)
	{
		int numberOfRows = driver.findElements(By.cssSelector(".table.table-striped>tbody>tr")).size(); // get number of rows.
		
		System.out.println("Num Rows In UI. = " + numberOfRows);
		 
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
				
				// store items for app - bladdxxx below changed.
				key = eleList.get(0).getText();
				name = eleList.get(1).getText();
				description = eleList.get(3).getText();
				enabledString = eleList.get(4).getText();
				defaultHostPath = eleList.get(2).getText(); // - bladdxxx
				if(!defaultHostPath.equals(""))
				{
					defaultHost = defaultHostPath.split("/")[0];
					// defaultPath = "/" + defaultHostPath.split("/")[1];
					defaultPath = GetDefaultPath(defaultHostPath);
				}
				else
				{
					defaultHost = "";
					defaultPath = "";
				}
				
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
				listOfActualApps.add(new ApplicationClass(key, name, description, enabled, defaultHost, defaultPath));
			}
		}
	}

	public static void AddJsonArrayToExpectedList(JSONArray jArray) throws JSONException
	{
		// store all the applications from API call onto expected list.
		for(int y = 0; y < jArray.length(); y++)
		{
			JSONObject jo = jArray.getJSONObject(y);
			
			GetAndAddAppFromApiToExpectedList(jo);
			
			//key = jo.getString("key");
			//name = jo.getString("name");
			//description = jo.getString("description");
			//enabled = jo.getBoolean("enabled");
			//listOfExpectedApps.add(new ApplicationClass(key, name, description, enabled, defaultHost, defaultPath));
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
			//ShowText(appClass.m_Key);
			//ShowText(listOfExpectedApps.get(x).m_Key);			
			Assert.assertTrue(appClass.m_Key.equals(listOfExpectedApps.get(x).m_Key));
			Assert.assertTrue(appClass.m_Name.equals(listOfExpectedApps.get(x).m_Name));
			Assert.assertTrue(appClass.m_Description.equals(listOfExpectedApps.get(x).m_Description));
			Assert.assertTrue(appClass.m_Enabled == listOfExpectedApps.get(x).m_Enabled);
			Assert.assertTrue(appClass.m_defaultHost.equals(listOfExpectedApps.get(x).m_defaultHost)); 			
			Assert.assertTrue(appClass.m_defaultPath.equals(listOfExpectedApps.get(x).m_defaultPath)); 			
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
	
	// add sortBy, sortDirection
	public static void PassDataAndStoreApiRequest(String apiType, int pageSize, int pageNumber, String sortDirection, String sortBy) throws IOException, JSONException, InterruptedException
	{
		listOfActualApps.clear();
		listOfExpectedApps.clear();
		JSONArray jArray;
		String url = "";

		// NOTE !!!!!!!!!!!!!!!!!!!!!!
		// ShowActualApplicationsOrStore(ActionForApplications.Store); // why id this here? removed 2/7/17 - will make something else fail
		
		// update URL for page setting and get application list from API.
		url = applicationsURL + "?pageSize=" +  String.valueOf(pageSize) + "&page=" + String.valueOf(pageNumber + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy);
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
						
		AddJsonArrayToExpectedList(jArray);
		//ShowApplicationsActualAndExpectedCollection();
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

	// add expected items found in in json object jo.
	public static void GetAndAddAppFromApiToExpectedList(JSONObject jo) throws JSONException
	{
		key = jo.getString("key");  
		name = jo.getString("name");
		description = CommonMethods.GetNonRequiredItem(jo, "description"); 
		enabled = jo.getBoolean("enabled");
		defaultHost = CommonMethods.GetNonRequiredItem(jo, "defaultHost");
		defaultPath = CommonMethods.GetNonRequiredItem(jo, "defaultPath");			
		
		listOfExpectedApps.add(new ApplicationClass(key, name, description, enabled, defaultHost, defaultPath));		
	}	
	
	public static void SetPageSizeToMax() throws Exception
	{
		// set page size to max.
		WaitForElementClickable(By.xpath("(//span/label)[4]"), 5, "");
		driver.findElement(By.xpath("(//span/label)[4]")).click(); 		
		Thread.sleep(1000);
	}

	public static void ShowPopup(String message)		
	{
		JOptionPane.showMessageDialog(null, message);
	}
	
	// this clicks a column selection to set sorting mode.
	public static void ClickSorting(String item) throws Exception		
	{
		WaitForElementClickable(By.xpath(item), 2, "");
		driver.findElement(By.xpath(item)).click();
		Thread.sleep(2000);
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
			VerifyApplicationsCollectionsExpectedAndActual();
		}
	}	
}
