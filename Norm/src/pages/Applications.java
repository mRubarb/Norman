package pages;


import java.util.ArrayList;
import java.util.List;

import baseItems.BaseMain;
import classes.ApplicationClass;
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
	public static int expectedNumberOfColumns = 4;
	
	public static String key;
	public static String name;
	public static String description;
	public static String enabledString;
	public static boolean enabled;
	public static String applicationsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/applications";
	public static String tenantsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/tenants";	

	public static String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTUxODgxOTAzOH0.HHgZ-BwWT1jrvo8yPLJsOA5YEP3utNDdBolJPHpbY4wcpOaBWGiXKS2ivbg-8pLvuGOb2ri6MTk3W-NqlsIEcg";
	
	public static List<ApplicationClass> listOfExpectedApps = new ArrayList<ApplicationClass>();
	public static List<ApplicationClass> listOfActualApps = new ArrayList<ApplicationClass>();	
	
	public static enum ActionForApplications
	{
		Show,
		Store
	}	
		
	public static void GoToApplications()
	{
		CommonMethods.selectItemPlatformDropdown("Applications");
	}
	
	// this gets all items in the collection from the API and all items from the applications UI.
	public static void VerifyFullList() throws IOException, JSONException
	{
		// get all the applications from API.
		String url = applicationsURL + "?pageSize=300";
		String apiType = "\"applications\":";
		String metadata = "";
		int totalCount = 0;
		
		metadata = CommonMethods.GetMetaDataWithUrl(token, url, apiType);

		// take out totalCount 
		totalCount = Integer.parseInt(metadata.split(":")[2].split(",")[0]);
		
		JSONArray jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		//System.out.println("Json array length = " + jArray.length());

		Assert.assertTrue(jArray.length() == totalCount); // verify collection count matches metadata. 
		
		// store all the applications from API onto expected list.
		for(int x = 0; x < jArray.length(); x++)
		{
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
		
		ShowApplicationsActualAndExpectedCollection();
		
		// DEBUG
		//ShowText(" ********** Expected Apps **********");
		//for(ApplicationClass appClass : listOfExpectedApps){appClass.ShowApp();}
		
		// setup UI to show all applications. TODO:
		
		// store the application in UI to listOfActualApps
		//ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		// verify actual and expected are equal.
		//VerifyApplicationsCollectionsExpectedAndActual();
	}	
	
	public static void VerifyPullDownForRows() throws IOException, JSONException
	{
		// get totalCount from metadata.
		String url = applicationsURL + "?pageSize=300";
		String apiType = "\"applications\":";
		String metadata = "";
		int totalCount = 0;
		int maxRowsToTest = 3;
		JSONArray jArray;
		
		metadata = CommonMethods.GetMetaDataWithUrl(token, url, apiType);

		// take out totalCount 
		totalCount = Integer.parseInt(metadata.split(":")[2].split(",")[0]);
		
		//  get all of the applications from API call and verify count is equal to metadata totalCount.
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);

		Assert.assertTrue(jArray.length() == totalCount); // verify collection count matches metadata. 
		
		for(int x = 0; x < maxRowsToTest; x++)
		{
			listOfActualApps.clear();
			listOfExpectedApps.clear();
			
			// set UI pull down to x + 1 and store the application in UI to listOfActualApps // TODO:
			ShowActualApplicationsOrStore(ActionForApplications.Store);
			
			// update URL for next pull down setting.
			url = applicationsURL + "?pageSize=" + String.valueOf(x + 1);
			
			jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
			
			AddJsonArrayToExpectedList(jArray);
			ShowApplicationsActualAndExpectedCollection();
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

	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	// 															HELPERS 
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

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
				enabled = Boolean.parseBoolean(enabledString);
				
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
			
			ShowApplicationsActualAndExpectedCollection(); 
			listOfExpectedApps.clear();
			
			// redundant check -- if the last page will have less than 'pageSize' number of items, verify the correct number of 
			if(x == (totalPages - 1) && numberOfItemsInPartialPage != 0)
			{
				ShowText("Verify length.");
				ShowInt(jArray.length());
			}
			
			// set page in console and get items in page onto actual list. // TODO
			// compare list // TODO:
		}
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
	
	
	
}
