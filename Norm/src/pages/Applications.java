package pages;


import java.util.ArrayList;
import java.util.List;

import baseItems.BaseMain;
import classes.ApplicationClass;
import classes.TenantAppForAppSearch;
import common.CommonMethods;
import common.CommonMethods_AppsRoutes;

import org.testng.Assert;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import javax.swing.JOptionPane;

public class Applications extends BaseMain
{
	public static String AppTableCss = ".table.table-striped>tbody>tr";
	public static int expectedNumberOfColumns = 6;
	
	public static String key = "";
	public static String name = "";
	public static String description = "";
	public static String enabledString = "";
	public static boolean enabled = false;
	public static String defaultHost = "";
	public static String defaultPath = "";
	public static String defaultHostPath = "";
	public static String tenantWithNoApp = "";
	public static final String extraText = "NEW";
	public static final String approveDeleteInPoup_Locator = "//input[@class='ng-untouched ng-pristine ng-valid']";
	public static final String selectDeleteInPoup_Locator = "//button[@class='btn btn-danger']";	
	public static final String addButton_Locator = "//button[@class='btn btn-primary ml-auto p-2']"; // TODO: same for routes.
	public static final String uiCancel_Locator = "(//button[@class='btn btn-secondary'])[2]"; // TODO: same for routes.	
	
	public static final String applicationsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/applications";
	public static final String tenantsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/tenants";	
	public static final String deploymentsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/deployments";	
	public static final String routesURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/routes";
	
	public static String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTUyNjMwODEwM30.0GU24pu7F8itDwHp8prFWlMwstsF53ISCxDQmteDEHCEWwXEt6V50AhnQTCLN7o6q-GQBlCQulTyeM6yn_C3bg";
	
	public static final String testAppKey = "1234567890";
	public static final String testAppName = "ZZZ_ZEBRA_XYZ";
	public static final String testDefaultHost = "automationzz.com";
	public static final String testDefaultPath = "/automationzz";
	public static final String testDescription = "automation description";
	public static boolean enabledValueAfterToggle = false;
	
	public static int maxItemsPerPage = 50;
	public static int[] pageSizes = {5, 10, 20, 50}; // selectable page sizes in console. 
	
	public static List<ApplicationClass> listOfExpectedApps = new ArrayList<ApplicationClass>();
	public static List<ApplicationClass> listOfActualApps = new ArrayList<ApplicationClass>();	
	public static List<TenantAppForAppSearch> listOfTenantWithAppExpected = new ArrayList<TenantAppForAppSearch>();
	
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
	public static void VerifyFullList() throws Exception
	{
		int pageSize = 50;
		String url = applicationsURL + "?pageSize=" + pageSize; // get all the applications from API.
		String apiType = "\"applications\":";
		String metadata = "";
		int totalCount = 0;
		
		ShowCurrentTest("VerifyFullList");
		
		metadata = CommonMethods.GetMetaDataWithUrl(token, url, apiType);

		// take out totalCount 
		totalCount = Integer.parseInt(metadata.split(":")[2].split(",")[0]);
		
		if(totalCount > 50)
		{
			totalCount = 50;
		}
		
		JSONArray jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);

		Assert.assertTrue(jArray.length() == totalCount); // verify collection count matches metadata. 
		
		AddJsonArrayToExpectedList(jArray);
		
		System.out.println("Size from UI = " +     listOfExpectedApps.size() + " rows.");
		
		// set maximum page size.
		SetPageSizeToMax();

		// store the application in UI to listOfActualApps
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		// verify actual and expected are equal.
		VerifyApplicationsCollectionsExpectedAndActual();
		
		ClearActualExpectedLists();
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
			CommonMethods_AppsRoutes.SetUiPageSizeSelector(j + 1); // select index of page size 5 selector.
			
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

	// create app that has only key and name and verify.
	public static void AddScenariosOne() throws Exception 
	{
		int indexCntr = 0;
		
		ShowCurrentTest("AddScenariosOne");
		
		SetPageSizeToMax();
		
		// make sure app to add does not exist.
		DeleteAppByKeyFromRow(testAppKey);
		
		// select add, wait for title, fill in key and name, and hit return. 
		ClickItem(addButton_Locator, 3); // add
		WaitForElementVisible(By.xpath("//strong[text()='Add Application']"), 3); // title 

		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("key"))).sendKeys(testAppKey);
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("name"))).sendKeys(testAppName);		

		ClickItem(CommonMethods_AppsRoutes.saveButtonPoUp_Locator, 3); // save		
		
		WaitForElementNotVisibleNoThrow(By.xpath(CommonMethods_AppsRoutes.saveButtonPoUp_Locator), 3);
		
		// wait for view button after page save.
		//WaitForElementVisible(By.xpath("(//button[@class='btn btn-info btn-sm'])[6]"), 4);
		
		// verify application was added.
		listOfActualApps.clear();
		
		// store the actual applications 
		ShowActualApplicationsOrStore(ActionForApplications.Store);

		// verify add.
		Assert.assertTrue(FindIndexExistingApp(testAppKey) > 0, "Error in 'AddScenarios' test. Test app was supposed to be present.");
		
		// get index so data can be verified. verify index is not -1.
		indexCntr = FindIndexExistingApp(testAppKey);
		Assert.assertTrue(indexCntr > 0, "Error in 'AddScenarios' test. Test app was supposed to be present.");
		
		// verify data in added app.
		VerifyKeyNameOnly(indexCntr - 1);
		
		ClearActualExpectedLists();
	}

	// edit existing - verify edit. this uses a test application.
	public static void EditApplicationDeleteItemsFromRowSelect() throws Exception 
	{
		String deployUrl = deploymentsURL + "?pageSize=300&applicationKey=";
		String routeUrl = routesURL + "?pageSize=300&applicationKey=";		
		String apiTypeDeploy = "\"deployments\":";
		String apiTypeRoute = "\"routes\":";
		String appDeleteHasAssociations = "";		
		// String appDeleteHasNoAssociations = "";
		final String commonCaution = "Are you sure you want to delete this application?Note: This cannot be undone.";
		JSONArray jArray;
		int rowIndex = 0;
		int rowIndexToUseForAssoiciatedApp = 0;
		int rowIndexToUseForNonAssoiciatedApp = 0;
		int numDeployements = 0;
		int tempNumDeployements = 0;		
		int numRoutes = 0;
		boolean haveStoredAppWithNoDeployments = false;
		String[] textArray;
		
		ShowCurrentTest("EditApplicationDeleteItemsFromRowSelect.");

		SetPageSizeToMax();

		ClearActualExpectedLists();
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		// /////////////////////////////////////////////////////////////////////////////////////////////////////
		// go through each application object. 
		// /////////////////////////////////////////////////////////////////////////////////////////////////////
		// this will find the application with the most routes and an application with no deployments.
		for(ApplicationClass app : listOfActualApps)
		{
			jArray = CommonMethods.GetJsonArrayWithUrl(token, deployUrl + app.m_Key, apiTypeDeploy); // call deployments and filter on the application.
			if(jArray.length() > 0)
			{
				tempNumDeployements = jArray.length(); 
				jArray = CommonMethods.GetJsonArrayWithUrl(token, routeUrl + app.m_Key, apiTypeRoute); // call routes and filter on the application.
				if(jArray.length() > 0)
				{
					if(jArray.length() > numRoutes) // this row has more routes than the max routes found so far. store number of deployments and routes.
					{
						numRoutes = jArray.length(); 
						numDeployements = tempNumDeployements;
						rowIndexToUseForAssoiciatedApp = rowIndex;
						appDeleteHasAssociations = app.m_Key;
					}
				}
			}
			else // application has no associated deployments. store the first one found.
			{
				if(!haveStoredAppWithNoDeployments)
				{
					appDeleteHasAssociations = app.m_Key;
					rowIndexToUseForNonAssoiciatedApp = rowIndex;
					haveStoredAppWithNoDeployments = true;
				}
			}
			rowIndex++;
		}
		
		ShowText(appDeleteHasAssociations);
		System.out.println("index to use associated = " + rowIndexToUseForAssoiciatedApp);
		System.out.println("index to use non associated = " + rowIndexToUseForNonAssoiciatedApp);		
		System.out.println("deploys = " + numDeployements);
		System.out.println("routes = " + numRoutes);
		
		// need to change page sizes to be able to select a delete in row,
		CommonMethods_AppsRoutes.SetUiPageSizeSelector(1);
		SetPageSizeToMax();
		
		// /////////////////////////////////////////////////////////////////////////////
		// select delete row that will show the pop-up with no deployments or routes.
		// /////////////////////////////////////////////////////////////////////////////

		SelectDeleteRowInAppList(rowIndexToUseForNonAssoiciatedApp + 1); // select pop-up that has no deployments/routes		
		
		// verify caution that is always shown.
		Assert.assertEquals(driver.findElement(By.cssSelector(".col>p")).getText().replace("\n", ""), commonCaution, "");
		
		// verify caution that is always shown with check box.
		textArray =  driver.findElement(By.xpath("(//small)[1]")).getText().split("\n");
		Assert.assertTrue(textArray.length == 1);
		Assert.assertEquals(textArray[0], "  I understand the consequences of deleting this application");

		VerifyKeyAndNameInDeletePopup(rowIndexToUseForNonAssoiciatedApp); // verify correct key and name are shown.

		// select cancel.
		ClickItem("//button[@class='btn btn-secondary']", 3);

		// /////////////////////////////////////////////////////////////////////////////////
		// now select delete row that will show the pop-up with deployments and routes. 
		// /////////////////////////////////////////////////////////////////////////////////
		
		SelectDeleteRowInAppList(rowIndexToUseForAssoiciatedApp + 1); // select pop-up that has deployments/routes
		
		// verify caution that is always shown.
		Assert.assertEquals(driver.findElement(By.cssSelector(".col>p")).getText().replace("\n", ""), commonCaution, "");
		
		// this gets all of the text in the pink box found in the UI.
		textArray =  driver.findElement(By.xpath("(//small)[1]")).getText().split("\n");
		
		// verify text unique to application with deployment(s) and route(s).
		Assert.assertTrue(textArray.length == 3);		

		// verify test in pink area of UI.
		Assert.assertEquals(textArray[0], "Deleting this application will also delete:", "");
		Assert.assertEquals(textArray[1], "The " + numDeployements + " associated deployments");		
		Assert.assertEquals(textArray[2], "The " + numRoutes + " associated routes", "");		
		
		VerifyKeyAndNameInDeletePopup(rowIndexToUseForAssoiciatedApp); // verify correct key and name are shown.

		// //////////////////////////////////////////
		// verify enabled/disable for delete button.   
		// //////////////////////////////////////////
		Assert.assertFalse(driver.findElement (By.xpath(selectDeleteInPoup_Locator)).isEnabled()); // delete button not enabled
		
		ClickItem(approveDeleteInPoup_Locator , 3); // select check box.
		Thread.sleep(250);

		Assert.assertTrue(driver.findElement (By.xpath(selectDeleteInPoup_Locator)).isEnabled());  // delete button enabled
	}
	
	// edit existing - verify edit. this uses a test application.
	public static void EditApplicationFromRowEdit() throws Exception
	{
		int rowIndex = 0;
		
		ShowCurrentTest("EditApplicationFromRowEdit");
		
		// SetPageSizeToMax();
		
		// CommonMethods.selectSizeOfList(10);
		CommonMethods_AppsRoutes.SetUiPageSizeSelector(2);
		
		// store off what's in UI.
		ClearActualExpectedLists();
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		// need to use a certain automation test application so won't affect someone else's application. 
		// if the test application is not there create it.
		if(FindIndexExistingApp(testAppKey) == -1)
		{
			AddScenariosTwo(false); // sending false says to add test application but not verify data.
		}

		// store what's in UI after adding test app. 
		ClearActualExpectedLists();
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		// automation test application should be on list.
		rowIndex = FindIndexExistingApp(testAppKey);
		Assert.assertTrue(rowIndex != -1); // make sure found application. // TODO: intermittent fail here.
		
		ClickRowViewForEdit(rowIndex); // click to edit test application.

		// verify expected values.
		VerifyEnabledValues(listOfActualApps.get(rowIndex - 1).m_Enabled); // enabled
		VerifyKey(listOfActualApps.get(rowIndex - 1).m_Key); // key
		VerifyKnownValues(rowIndex - 1); // all others
		
		// clear everything and change state of enabled/disabled.
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("name"))).clear();
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("defaultHost"))).clear();
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("defaultPath"))).clear();
		driver.findElement(By.xpath("//textarea[@formcontrolname='description']")).clear(); // description.				
		
		// enter new values and save.
		ToggleEnabledDisabled(rowIndex); // toggle enabled
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("name"))).sendKeys(testAppName + extraText); // name
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("defaultHost"))).sendKeys(testDefaultHost + extraText); // host 
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("defaultPath"))).sendKeys(testDefaultPath + extraText); // path
		driver.findElement(By.xpath("//textarea[@formcontrolname='description']")).sendKeys(testDescription + extraText); // description
		
		ClickItem(CommonMethods_AppsRoutes.saveButtonPoUp_Locator, 3); // save  
		Thread.sleep(1000);
		
		// test app should be near top so only use 10 rows. 
		CommonMethods.selectSizeOfList(10);

		// store what's in UI (application list) after modifying test application. 
		ClearActualExpectedLists();
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		// verify the edits made.
		VerifyKnownUpdatedValuesFromList(rowIndex - 1);
		
		// delete test application.
		DeleteAppByKeyFromRow(testAppKey);
		
		// after deleting application, make sure it's gone.
		// store what's in application list.
		ClearActualExpectedLists();
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		Assert.assertTrue(FindIndexExistingApp(testAppKey) == -1, "Check that makes sure test app is not in apps list failed.");
		
		ClearActualExpectedLists();
	}
	
	// edit existing - verify reset. this uses any application that has all data populated.
	public static void EditApplicationReset() throws Exception
	{
		int rowIndex = 1;
		boolean expectedEnableDisableState = false;
		
		ShowCurrentTest("EditApplicationReset");
		
		SetPageSizeToMax();
		
		// find row index of an application that has all the fields populated and select 'Edit' on it.
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		for(ApplicationClass app : listOfActualApps)
		{
			if(app.m_Key != "" && app.m_Name != "" && app.m_defaultHost != "" && app.m_defaultPath != "" && app.m_Description != "")
			{
				break;
			}
			rowIndex++;	
		}
		
		driver.findElement(By.xpath("(//button[@class='btn btn-primary btn-sm'])[" + rowIndex + "]")).click();
		WaitForElementVisible(By.xpath("//strong[text()='Edit Application']"), 3); // title
		
		// verify button states - save, cancel, reset.
		Assert.assertFalse(driver.findElement(By.xpath("(//button[@class='btn btn-secondary'])[1]")).isEnabled());
		Assert.assertTrue(driver.findElement(By.xpath(uiCancel_Locator)).isEnabled());
		Assert.assertFalse(driver.findElement(By.xpath(CommonMethods_AppsRoutes.saveButtonPoUp_Locator)).isEnabled());
		
		// change data for everything in form.
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("name"))).sendKeys(testAppName + "bad");
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("defaultHost"))).sendKeys(testDefaultHost + "bad");
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("defaultPath"))).sendKeys(testDefaultPath + "bad");
		driver.findElement(By.xpath("//textarea[@formcontrolname='description']")).sendKeys(testDescription + "bad"); // description		
		
		ToggleEnabledDisabled(rowIndex);
		
		// select reset button
		ClickItem("(//button[@class='btn btn-secondary'])[1]", 2);
		
		ShowText(driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("name"))).getAttribute("value"));

		// verify original values are back after reset.
		VerifyKnownValues(rowIndex - 1);
		expectedEnableDisableState =  listOfActualApps.get(rowIndex - 1).m_Enabled; // get expected enable/disable selection.	
		
		VerifyEnabledValues(expectedEnableDisableState);
		
		// close pop-up UI.
		ClickItem(uiCancel_Locator, 3);
	}
	
	// create test application that has key, name, host, path, and enabled set to non-default (false).
	public static void AddScenariosTwo(boolean doVerification) throws Exception 
	{
		int indexCntr = 0;
		
		if(doVerification) // this method can also just add/create the application and not verify it. 
		{
			ShowCurrentTest("AddScenariosTwo");			
		}

		SetPageSizeToMax();
		
		// make sure application to add does not exist.
		DeleteAppByKeyFromRow(testAppKey);
		
		// select add, wait for title, fill in key and name, and hit return. 
		ClickItem(addButton_Locator, 3); // add
		WaitForElementVisible(By.xpath("//strong[text()='Add Application']"), 3); // title 

		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("key"))).sendKeys(testAppKey);
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("name"))).sendKeys(testAppName);
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("defaultHost"))).sendKeys(testDefaultHost);
		driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("defaultPath"))).sendKeys(testDefaultPath);
		
		driver.findElement(By.xpath("//textarea[@formcontrolname='description']")).sendKeys(testDescription); // description		
		ClickItem("//input[@value='false']", 3); // click false
		ClickItem(CommonMethods_AppsRoutes.saveButtonPoUp_Locator, 3); // save		
		
		WaitForElementNotVisibleNoThrow(By.xpath(CommonMethods_AppsRoutes.saveButtonPoUp_Locator), 3);
		
		
		// verify application was added.
		listOfActualApps.clear();
		
		if(doVerification)
		{
			// store the actual applications 
			ShowActualApplicationsOrStore(ActionForApplications.Store);

			// verify add.
			Assert.assertTrue(FindIndexExistingApp(testAppKey) > 0, "Error in 'AddScenarios' test. Test app was supposed to be present.");
			
			// get index so data can be verified. verify index is not -1.
			indexCntr = FindIndexExistingApp(testAppKey);
			Assert.assertTrue(indexCntr > 0, "Error in 'AddScenarios' test. Test app was supposed to be present.");
			
			VerifyAllAppItems(indexCntr - 1);
		}

		/*
		// store the actual applications 
		ShowActualApplicationsOrStore(ActionForApplications.Store);

		// verify add.
		Assert.assertTrue(FindIndexExistingApp(testAppKey) > 0, "Error in 'AddScenarios' test. Test app was supposed to be present.");
		
		// get index so data can be verified. verify index is not -1.
		indexCntr = FindIndexExistingApp(testAppKey);
		Assert.assertTrue(indexCntr > 0, "Error in 'AddScenarios' test. Test app was supposed to be present.");
		
		// verify data in added app.
		// VerifyKeyNameOnly(indexCntr - 1);
		
		VerifyAllAppItems(indexCntr - 1);
		*/
		
		ClearActualExpectedLists();
	}
	
	public static void AddValidations() throws Exception
	{
		String keyTooLong = "2222222222222222222222222222222222222";
		String nameTooLong = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
		String validKey = "123456789";
		String defaultPathError = "Default Path must only contain letters, numbers, periods, dashes and underscores and must start with a forward slash";
		String defaultHostError = "Default Host must only contain letters, numbers, periods, dashes, and underscores";
		String keyInUse =  "Key is already in use";
		String  badCharacterString = "nn>?";
		int maxKeySize = 10;
		int maxNameSize = 50;
		
		ShowCurrentTest("AddValidations");
		
		// make sure test application is not on the list.
		DeleteAppByKeyFromRow(testAppKey);
		
		// store the actual applications. this will be used in validation error when adding existing app key.
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		// select add, wait for title, and select cancel, and verify back in the applications list.
		ClickItem(addButton_Locator, 3); // add
		WaitForElementVisible(By.xpath("//strong[text()='Add Application']"), 3); // title 
		ClickItem(uiCancel_Locator, 3); // cancel 
		CommonMethods.verifyTitle("Applications"); // back in applications list.

		// select add and save, verify 'required' under key
		ClickItem(addButton_Locator, 3); // add
		WaitForElementVisible(By.xpath("//strong[text()='Add Application']"), 3);
		ClickItem("//button[@class='btn btn-primary']/span[text()='Save']", 3); // save		
		WaitForElementVisible(By.xpath("//small[text()='required']"), 3); // verify		
		
		// click name, non-required field, and verify 'required' under name.
		ClickItem("//input[@formcontrolname = 'name']", 3); // 
		ClickItem("//input[@formcontrolname = 'defaultPath']", 3); //
		WaitForElementVisible(By.xpath("(//small[text()='required'])[2]"), 2); // verify
		
		// enter key with > 10 characters and verify only 10 are allowed.
		driver.findElement(By.xpath("//input[@formcontrolname='key']")).sendKeys(keyTooLong);
		Assert.assertTrue(driver.findElement(By.xpath("//input[@formcontrolname='key']")).getAttribute("value").length() == maxKeySize, "Text length in key text box is too long in 'AddValidations' method.");
		
		// enter a valid key and then enter name > 50 characters.
		driver.findElement(By.xpath("//input[@formcontrolname='key']")).sendKeys(validKey);
		driver.findElement(By.xpath("//input[@formcontrolname='name']")).sendKeys(nameTooLong);		
		Assert.assertTrue(driver.findElement(By.xpath("//input[@formcontrolname='name']")).getAttribute("value").length() == maxNameSize, "Text length in name text box is too long in 'AddValidations' method.");		
		
		// enter a host and enter a path with no "/" at beginning, click the description text box, and verify error in default path.
		driver.findElement(By.xpath("//input[@formcontrolname='defaultHost']")).sendKeys("goodhost");
		driver.findElement(By.xpath("//input[@formcontrolname='defaultPath']")).sendKeys("pathNoSlash");
		ClickItem("//textarea[@formcontrolname='description']", 3); 
		Assert.assertEquals(driver.findElement(By.xpath("//div/small")).getText(), defaultPathError, "Failed to find correct error message for Default Path in 'AddValidations' method.");
		
		// enter bad characters into default host  and verify error in default host.
		driver.findElement(By.xpath("//input[@formcontrolname='defaultHost']")).sendKeys(badCharacterString);
		Assert.assertEquals(driver.findElement(By.xpath("(//div/small)[1]")).getText(), defaultHostError, "Failed to find correct error message for Default Host in 'AddValidations' method.");		
		//ShowText(driver.findElement(By.xpath("(//div/small)[1]")).getText());
		
		// clear default path, enter slash and bad characters, and verify error in default path.
		driver.findElement(By.xpath("//input[@formcontrolname='defaultPath']")).clear();
		driver.findElement(By.xpath("//input[@formcontrolname='defaultPath']")).sendKeys("/" + badCharacterString);		
		Assert.assertEquals(driver.findElement(By.xpath("(//div/small)[2]")).getText(), defaultPathError, "Failed to find correct error message for Default Host in 'AddValidations' method.");
		
		// select reset button, enter existing key into the key field, select default host, and verify message. 
		driver.findElement(By.xpath("(//button[@class='btn btn-secondary'])[1]")).click();
		driver.findElement(By.xpath("//input[@formcontrolname='key']")).sendKeys(listOfActualApps.get(0).m_Key);
		driver.findElement(By.xpath("//input[@formcontrolname='defaultHost']")).click();
		Thread.sleep(500);
		
		Assert.assertEquals(driver.findElement(By.xpath("//div/small")).getText(), keyInUse, "Failed to find correct error message for Key in 'AddValidations' method.");
		ShowText(driver.findElement(By.xpath("//div/small")).getText());
		
		// close add UI
		ClickItem(uiCancel_Locator, 3); // cancel
		
		ClearActualExpectedLists();
	}

	// this is for sorting all page sizes for all sortable columns.
	public static void VerifySortingMultiplePages() throws Exception
	{
		// get all the applications from API.
		String url = applicationsURL + "?pageSize=300";
		String apiType = "\"applications\":";
		String metadata = "";
		int totalCount = 0;
		int numberOfPages = 0;
		int pageSize = 0;
		int pageSizeSelectorIndex = 1;
		
		// get total count.
		metadata = CommonMethods.GetMetaDataWithUrl(token, url, apiType);
		totalCount = Integer.parseInt(metadata.split(":")[2].split(",")[0]); // take out totalCount
		//ShowInt(totalCount);

		// this goes through each page size in the array 'pageSizes' that holds the page sizes.
		// for each page size 
		for(int tempInt : pageSizes)
		{
			System.out.println("Verify page size: " + tempInt);
			
			CommonMethods_AppsRoutes.SetUiPageSizeSelector(pageSizeSelectorIndex);
			pageSize = tempInt;
			numberOfPages = GetTotalPages(totalCount, pageSize);
			System.out.println("Expected number of pages for page size = " +  pageSize + " is " + numberOfPages);

			// click a sort item and verify.
			ClickSorting("//span[text()='Enabled']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "IS_ENABLED");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Enabled']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "IS_ENABLED");

			// click a sort item and verify.
			ClickSorting("//span[text()='Key']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "KEY");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Key']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "KEY");			
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Name']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "NAME");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Name']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "NAME");			
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Host']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "DEFAULT_HOST");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Host']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "DEFAULT_HOST");						
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Path']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "ASC", "DEFAULT_PATH");
			
			// click a sort item and verify.
			ClickSorting("//span[text()='Path']");
			VerifyPagesSorting(numberOfPages, apiType, pageSize, "DESC", "DEFAULT_PATH");									
			
			pageSizeSelectorIndex++;
		}
	}	
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// * go through the tenants list and then call application filtering on the tenant key to find 
	//   associated application(s) for each tenant.  
	// * find a tenant with one application associated to it and store that tenant/application 
	//   as an object 'TenantAppForAppSearch' and put the object onto a list 'listOfTenantWithAppExpected'.
	// * find the tenant with the most applications associated to it and store that tenant/applications 
	//   as object 'TenantAppForAppSearch' and put the object onto a list 'listOfTenantWithAppExpected'.
	// * filter on each of the tenants listed above in tenants pull-down and verify results (expected/actual).
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	public static void VerifyFilteringByTenant() throws IOException, JSONException, Exception 
	{
		String url = ""; 
		String apiType = "\"tenants\":";
		JSONArray jArray;

		List<ApplicationClass> appExpectedlList;
		
		ShowCurrentTest("VerifyFilteringByTenant");
		
		// create a URL that will get a list of all the tenants.
		url = tenantsURL + "?pageSize=300"; 
		
		// get the list of tenants. 
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);

		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// this will store a tenant with one application and a tenant with the most applications as described in method description above.  
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		BuildListOfTenantsForFilterTests(jArray);

		// ///////////////////////////////////////////////////////////////
		// this tests filtering using the tenant with one application.       
		// ///////////////////////////////////////////////////////////////

		if(listOfTenantWithAppExpected.get(0).m_TenantName != "")
		{
			ShowText("Tenant filter with one application = " + listOfTenantWithAppExpected.get(0).m_TenantName);
			
			// select the tenant with one application in tenants pull-down.  
			// the '0' passed in indicates to select the tenant with one application.   
			ClickAndSelectTenantInPulldown(listOfTenantWithAppExpected.get(0).m_TenantName);
			
			// listOfTenantWithAppExpected.get(0).m_TenantName // DEBUG show tenant being filtered on.
			
			// store the result of the search onto list of actual applications.
			ShowActualApplicationsOrStore(ActionForApplications.Store);
			
			// get the application list found in the first tenant object and make sure it only holds one application.
			appExpectedlList =   listOfTenantWithAppExpected.get(0).GetApplicationList();

			Assert.assertTrue(appExpectedlList.size() == 1, "Application should only have one application in 'VerifyFilteringByTenant'.");
			
			listOfExpectedApps.add(appExpectedlList.get(0)); // add application to expected list.
			
			VerifyApplicationsCollectionsExpectedAndActual(); // verify

			ClearActualExpectedLists();
		}
		else
		{
			ShowText("NOTE: not testing tenant filter with the one applications because there is no tenant with one application." );
		}
		
		// /////////////////////////////////////////////////////////////////////
		// this tests filtering using the tenant with the most applications.       
		// /////////////////////////////////////////////////////////////////////

		if(listOfTenantWithAppExpected.get(1).m_TenantName != "")
		{
			ShowText("Tenant filter with the most applications = " + listOfTenantWithAppExpected.get(1).m_TenantName);
			
			// select the tenant with the most applications in tenants pull-down.  
			// the '1' passed in indicates to select the tenant with the most applications.   
			ClickAndSelectTenantInPulldown(listOfTenantWithAppExpected.get(1).m_TenantName);
			
			// get the list of expected applications found in the second tenant object.
			appExpectedlList =  listOfTenantWithAppExpected.get(1).GetApplicationList();
			
			// add the expected applications to the expected list.
			for(ApplicationClass aplClass: appExpectedlList)
			{
				listOfExpectedApps.add(aplClass);
			}

			// store the result of the filter in UI into list of actual applications.
			ShowActualApplicationsOrStore(ActionForApplications.Store);
			
			VerifyApplicationsCollectionsExpectedAndActual();
			
			ClearActualExpectedLists();
		}
		else
		{
			ShowText("NOTE: not testing tenant filter with the most applications because there is no tenant with more than one application." );
		}
		
		// //////////////////////////////////////////////////////////////////////////////////////
		// select the tenant that has no associated applications and verify the UI message.  
		// //////////////////////////////////////////////////////////////////////////////////////		
		ShowText("Testing tenant filter with the no applications." );
		
		ClickAndSelectTenantInPulldown(tenantWithNoApp);
		
		VerifyNoApplicationsPresent();
		
		ClearActualExpectedLists();

		ResetTenantPulldown();
		
	}	

	// verify results from enabled and disabled filter using full list.
	public static void VerifyFilteringByEnabledDisabled() throws Exception  
	{
		String url = applicationsURL + "?pageSize=300&enabled=true"; // get all the enabled applications from API.
		String apiType = "\"applications\":";
		
		ShowCurrentTest("VerifyFilteringByEnabledDisabled");
		
		SetPageSizeToMax();
		
		// set to filter enabled
		ClickItem("(//button[@id='sortMenu'])[2]", 3);
		ClickItem("(//button[@class='dropdown-item active'])[2]/following::div[1]", 3);
		
		VerifyEnabledDisabled(url, apiType);
		
		// set back to enabled (this is needed because locators change when click enabled) and then set to filter disabled.
		ClickItem("(//button[@id='sortMenu'])[2]", 3);
		ClickItem("//div[@class='dropdown show']/div/button", 3);
		
		// set to disabled.
		ClickItem("(//button[@id='sortMenu'])[2]", 3);
		ClickItem("(//button[@class='dropdown-item active'])[2]/following::div[2]", 3);
		
		url = applicationsURL + "?pageSize=300&enabled=false"; // get all the disabled applications from API.

		VerifyEnabledDisabled(url, apiType);
		ClearActualExpectedLists();
		ResetEnabledPulldown();
	}
	
	public static void VerifyFilteringEnabledDisabledWithTenant() throws Exception
	{
		String url = ""; 
		String apiType = "\"tenants\":";
		JSONArray jArray;
				
		ShowCurrentTest("VerifyFilteringEnabledDisabledWithTenant");
		
		ClearActualExpectedLists();
		
		// create a URL that will get a list of all the tenants.
		url = tenantsURL + "?pageSize=300"; 
		
		// get the list of tenants. 
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
	
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// this will store a tenant with one application and a tenant with the most applications as described in method description above.  
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		BuildListOfTenantsForFilterTests(jArray);

		ShowText("Tenat for filter is " + listOfTenantWithAppExpected.get(1).m_TenantName);
		
		// call API and get expected for enabled true and tenant with the most applications. 
		url = applicationsURL + "?pageSize=300&enabled=true&tenantKey=" + listOfTenantWithAppExpected.get(1).m_TenantName; 
		apiType = "\"applications\":";
		
		// get the list of expected applications.
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		// put the expected applications on the expected list.
		AddJsonArrayToExpectedList(jArray);
		
		// set to enabled
		ClickItem("(//button[@id='sortMenu'])[2]", 3);
		ClickItem("(//button[@class='dropdown-item active'])[2]/following::div[1]", 3);
		
		// select tenant pull-down.
		ClickAndSelectTenantInPulldown(listOfTenantWithAppExpected.get(1).m_TenantName);
		
		// store UI data.
		ShowActualApplicationsOrStore(ActionForApplications.Store);
				
		// first see if there will be an empty list. if so, look for empty page message. first make sanity check on actual and expected sizes. lists should be same size.
		Assert.assertTrue(listOfActualApps.size() == listOfExpectedApps.size(), "Error in size checks. List of actual size = " + listOfActualApps.size() + " List of expected size = " + listOfExpectedApps.size()); 
		if(listOfActualApps.size() == 0)
		{
			VerifyNoApplicationsPresent();
		}
		else
		{
			VerifyApplicationsCollectionsExpectedAndActual();			
		}

		ClearActualExpectedLists();
		
		// set back to enabled (this is needed because locators change when click enabled) and then set to filter disabled.
		ClickItem("(//button[@id='sortMenu'])[2]", 3);
		ClickItem("//div[@class='dropdown show']/div/button", 3);
		
		// set to disabled.
		ClickItem("(//button[@id='sortMenu'])[2]", 3);
		ClickItem("(//button[@class='dropdown-item active'])[2]/following::div[2]", 3);
		
		// call API and get expected for enabled true and tenant with the most applications. 
		url = applicationsURL + "?pageSize=300&enabled=false&tenantKey=" + listOfTenantWithAppExpected.get(1).m_TenantName; 
		apiType = "\"applications\":";
		
		// get the list of expected applications.
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		// put the expected applications on the expected list.
		AddJsonArrayToExpectedList(jArray);
		
		// store UI data.
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		// first see if there will be an empty list. if so, look for empty page message. first make sanity check on actual and expected sizes. lists should be same size.
		Assert.assertTrue(listOfActualApps.size() == listOfExpectedApps.size(), "Error in size checks. List of actual size = " + listOfActualApps.size() + " List of expected size = " + listOfExpectedApps.size()); 
		if(listOfActualApps.size() == 0)
		{
			VerifyNoApplicationsPresent();
		}
		else
		{
			VerifyApplicationsCollectionsExpectedAndActual();			
		}
		
		ResetEnabledPulldown();
		ResetTenantPulldown();
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	// 															HELPERS 
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	// verify key and name shown matches actual key and name found in applications list. 
	public static void VerifyKeyAndNameInDeletePopup(int index)
	{
		Assert.assertEquals(listOfActualApps.get(index).m_Key, driver.findElement(By.xpath("(//dd[@class='col-sm-11'])[1]")).getText(),  // key
				            "Fail in method VerifyKeyAndNameInDeletePopup. Keys don't match.");

		Assert.assertEquals(listOfActualApps.get(index).m_Name, driver.findElement(By.xpath("(//dd[@class='col-sm-11'])[2]")).getText(), // name
	            "Fail in method VerifyKeyAndNameInDeletePopup. Names don't match.");		
	}
	
	// this will select the delete button in a specified applications list row. 
	public static void SelectDeleteRowInAppList(int rowToSelect)
	{
		String xpath =  "(//button[@class='btn btn-danger btn-sm'])[" + rowToSelect + "]";
		ClickItem(xpath, 3);
	}
	
	
	public static void VerifyEnabledValues(boolean expectedEnableDisableState)
	{
		if(expectedEnableDisableState == true)
		{
			Assert.assertTrue(driver.findElement(By.xpath("//input[@value='true']")).isSelected());
			Assert.assertFalse(driver.findElement(By.xpath("//input[@value='false']")).isSelected());			
		}
		else
		{
			Assert.assertTrue(driver.findElement(By.xpath("//input[@value='false']")).isSelected());
			Assert.assertFalse(driver.findElement(By.xpath("//input[@value='true']")).isSelected());			
		}
	}
	
	public static void VerifyKnownValues(int rowIndex)
	{
		Assert.assertEquals(driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("name"))).getAttribute("value"), listOfActualApps.get(rowIndex).m_Name);
		Assert.assertEquals(driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("defaultHost"))).getAttribute("value"), listOfActualApps.get(rowIndex).m_defaultHost);		
		Assert.assertEquals(driver.findElement(By.xpath(CommonMethods_AppsRoutes.GetXpathForTextBox("defaultPath"))).getAttribute("value"), listOfActualApps.get(rowIndex).m_defaultPath);
		Assert.assertEquals(driver.findElement(By.xpath("//textarea[@formcontrolname='description']")).getAttribute("value"), listOfActualApps.get(rowIndex).m_Description);
	}

	// this gets a row index that says what row in the listOfActualApps has the actual info. the expected info is stored in variables. 
	// the 'extraText' variable was used to change the value of existing data in   
	public static void VerifyKnownUpdatedValuesFromList(int rowIndex)
	{
		Assert.assertEquals(listOfActualApps.get(rowIndex).m_Key, testAppKey);
		Assert.assertEquals(listOfActualApps.get(rowIndex).m_Name, testAppName + extraText);		
		Assert.assertEquals(listOfActualApps.get(rowIndex).m_defaultHost, testDefaultHost + extraText.toLowerCase());		
		Assert.assertEquals(listOfActualApps.get(rowIndex).m_defaultPath, testDefaultPath + extraText.toLowerCase());
		Assert.assertEquals(listOfActualApps.get(rowIndex).m_Description, testDescription + extraText);		
		Assert.assertEquals(listOfActualApps.get(rowIndex).m_Enabled, enabledValueAfterToggle);		
	}
	
	
	public static void VerifyKey(String expectedKey)
	{
		Assert.assertEquals((driver.findElement(By.xpath("//dt[text()='Key:']/following-sibling::dd"))).getText(), expectedKey);
	}
	
	public static void ToggleEnabledDisabled(int rowIndex)
	{
		if (listOfActualApps.get(rowIndex - 1).m_Enabled == false)
		{
			ClickItem("//input[@value='true']", 3); // click true
			enabledValueAfterToggle = true;
		}
		else
		{
			ClickItem("//input[@value='false']", 3); // click false
			enabledValueAfterToggle = false;
		}		
	}
	
	public static void ClickRowViewForEdit(int rowIndex) throws Exception
	{
		driver.findElement(By.xpath("(//button[@class='btn btn-primary btn-sm'])[" + rowIndex + "]")).click();
		WaitForElementVisible(By.xpath("//strong[text()='Edit Application']"), 3); // title
	}
	
	// verify 'no applications present' text in empty applications page.
	public static void VerifyNoApplicationsPresent() throws Exception
	{
		WaitForElementVisible(By.xpath("//div[text()='No applications found']"), 3); // UI message.
	}
	
	public static void ResetTenantPulldown()
	{
		// set tenant pull-down to default.
		ClickItem(".//*[@id='sortMenu']", 3);
		ClickItem("//div[@class='dropdown-menu']/button", 3);
	}
	
	public static void ResetEnabledPulldown()
	{
		// set enabled/disabled back to default.
		ClickItem("(.//*[@id='sortMenu'])[2]", 3);
		ClickItem("//span[text()='Show Enabled and Disabled']/..", 3);
	}
	
	public static void VerifyEnabledDisabled(String url, String apiType) throws Exception
	{
		JSONArray jArray;
		
		// get array of enabled applications from the API and  store them.
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		AddJsonArrayToExpectedList(jArray);
		
		// store the actual applications in UI 
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		VerifyApplicationsCollectionsExpectedAndActual();
		
		ClearActualExpectedLists();
	}
	
	public static void DeleteAppByKeyFromRow(String appKey) throws Exception
	{
		int indexCntr = 0;

		// clear actual list and get max page size. 
		listOfActualApps.clear();
		SetPageSizeToMax();

		// store the actual applications, see if test application exists. 
		ShowActualApplicationsOrStore(ActionForApplications.Store);

		indexCntr = FindIndexExistingApp(appKey);

		//ShowInt(indexCntr);
		
		if(indexCntr != -1) // test application exists, delete it and then verify it has been deleted.
		{
			SelectDeleteRowInAppList(indexCntr); // select delete application list row with appKey.
			//ClickItem("//input[@class='ng-untouched ng-pristine ng-valid']",3); // approve delete.
			ClickItem(approveDeleteInPoup_Locator ,3); // approve delete.			
			
			
			// ClickItem("//button[@class='btn btn-danger']", 3); // delete.
			ClickItem(selectDeleteInPoup_Locator, 3); // delete.			
			WaitForElementNotVisibleNoThrow(By.xpath("//button[@class='btn btn-danger']"), 4);
			
			listOfActualApps.clear();

			// store the actual applications.
			ShowActualApplicationsOrStore(ActionForApplications.Store);

			Assert.assertTrue(FindIndexExistingApp(appKey) == -1, "Error in 'AddScenarios' test. Test app was supposed to be deleted.");
		}		
		
		listOfActualApps.clear();
	}
	
	// see if an application key exists in the actual list from UI. 
	public static int FindIndexExistingApp(String appKey)
	{
		boolean foundAdd = false;
		int indexCntr = 1;
	
		for(ApplicationClass appClass : listOfActualApps)
		{
			if(appClass.m_Key.equals(appKey))
			{
				foundAdd = true;
				break;
			}
			indexCntr++;
		}
		
		if(!foundAdd)
		{
			return -1;
		}
		return indexCntr;
	}		
	
	public static void ClickItem(String xpath, int timeOut)
	{
		WaitForElementClickable(By.xpath(xpath), timeOut, "");
		driver.findElement(By.xpath(xpath)).click();
	}
	
	//public static String GetXpathForTextBox(String item) // TODO: use in routes also
	//{
	//	return "//input[@formcontrolname='" + item + "']";
	//}
	
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
					             joObjectApplication.getString("description"), joObjectApplication.getBoolean("enabled"), 
					             CommonMethods.GetNonRequiredItem(joObjectApplication, "defaultHost"), CommonMethods.GetNonRequiredItem(joObjectApplication, "defaultPath")));
		}
		listOfTenantWithAppExpected.add(tempObj);
	}	
	
	// loop through tenants list. find a tenant with one dependent application. store the tenant name and 
	// the application in a 'TenantAppForAppSearch' object and add the object to the 'listOfTenantWithApp' 
	// list. Find the tenant with the most dependent applications and store it's index.
	// find a tenant with no applications and store it into a variable.
	public static int BuildListOfTenantsForFilterTests(JSONArray jArrayTenants) throws Exception 
	{
		String url = ""; 
		String apiType = "\"tenants\":";
		int indexForTenantWithMostApps = 0;
		JSONObject jo;
		JSONArray jArrayAppsFromTenantCall;
		boolean savedTenantWithOneApp = false;		
		boolean savedTenantWithNoApp = false;
		boolean savedTenantWithMoreThanOneApp = false;		
		
		// loop through tenants list. find a tenant with one dependent application. store the tenant name and the application in a 
		// 'TenantAppForAppSearch' object and add the object to the 'listOfTenantWithApp' list. 
		// Find the tenant with the most dependent applications and store it's index.
		for(int x = 0; x < jArrayTenants.length(); x++)
		{
			jo = jArrayTenants.getJSONObject(x);
			System.out.println(jo.getString("key"));		
			
			// build an application URL that will call tenant filter with the current tenant key  
			url = applicationsURL + "?tenantKey=" + jo.getString("key") + "&pageSize=300";
			apiType = "\"applications\":";
			
			// call 'applications' filtering on the current tenant key. receive back a list of application objects (can also be empty).
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
				savedTenantWithMoreThanOneApp = true;
			}

			// store a tenant that has no associated applications.
			if(jArrayAppsFromTenantCall.length() == 0 && !savedTenantWithNoApp)
			{
				tenantWithNoApp = jo.getString("key");
				savedTenantWithNoApp = true;
			}
		}
		
		// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// at this point the tenant with the most applications is known. it is found in the array index 'indexForTenantWithMostApps' 
		// in the 'jArray' array of tenants. use the array index 'indexForTenantWithMostApps' to store the tenant and all the 
		// applications that go with tenant with the most applications onto a 'TenantAppForAppSearch' object and then add the object 
		// to the 'listOfTenantWithAppExpected' list.
		// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		jo = jArrayTenants.getJSONObject(indexForTenantWithMostApps);
		url = applicationsURL + "?tenantKey=" + jo.getString("key") + "&pageSize=300";
		apiType = "\"applications\":";
	
		jArrayAppsFromTenantCall = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
	
		if(savedTenantWithMoreThanOneApp) // if found an application with more than one tenant, setup TenantAppForAppSearch object.
		{
			BuildTenantAppForAppSearchObject(jo.getString("key"), jArrayAppsFromTenantCall);			
		}
		else // if an application was not found, set name in TenantAppForAppSearch object to "".
		{
			BuildTenantAppForAppSearchObject("", jArrayAppsFromTenantCall);
		}
	
		return indexForTenantWithMostApps;			
	}
	
	public static void ClearActualExpectedLists()
	{
		listOfActualApps.clear();
		listOfExpectedApps.clear();
	}
	

	public static void VerifySortingHelper(String url, String token, String apiType) throws IOException, JSONException
	{
		JSONArray jArray;
		
		//  get all of the applications from API call (expected).
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);

		// store expected data. 
		AddJsonArrayToExpectedList(jArray);
		
		// store the applications in UI to listOfActualApps (actual)
		ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		VerifyApplicationsCollectionsExpectedAndActual();
		
		listOfExpectedApps.clear();
		listOfActualApps.clear();		
	}	
	
	// go through rows and columns and show items or add items to the list of apps.
	public static void ShowActualApplicationsOrStore(ActionForApplications action)
	{
		int numberOfRows = driver.findElements(By.cssSelector(".table.table-striped>tbody>tr")).size(); // get number of rows.
		
		System.out.println("Num Rows In UI to load. = " + numberOfRows);
		 
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
				description = eleList.get(3).getText();
				enabledString = eleList.get(4).getText();
				defaultHostPath = eleList.get(2).getText(); 
				if(!defaultHostPath.equals(""))
				{
					defaultHost = defaultHostPath.split("/")[0];
					defaultPath = GetDefaultPath(defaultHostPath);
				}
				else
				{
					defaultHost = "";
					defaultPath = "";
				}
				
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
		ShowText("Page load completed.");
	
	}

	// receive Json array for applications and add it to the expected list.
	public static void AddJsonArrayToExpectedList(JSONArray jArray) throws JSONException
	{
		int loopMax = 0;
		
		// want to limit list sizes to 'maxItemsPerPage' because max page size is 50.
		if(jArray.length() > maxItemsPerPage)
		{
			loopMax = maxItemsPerPage;
		}
		else
		{
			loopMax = jArray.length();
		}
		
		// store all the applications from API call onto expected list.
		for(int y = 0; y < loopMax; y++)
		{
			JSONObject jo = jArray.getJSONObject(y);
			GetAndAddAppFromApiToExpectedList(jo);
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

	// compare the list of applications from the API call to the list of applications from the UI.
	// both lists should be in the same order.
	public static void VerifyApplicationsCollectionsExpectedAndActual()
	{
		//boolean bFoundInLoop = false;
		//String message = "";
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
	
	// add sortBy, sortDirection
	public static void PassDataAndStoreApiRequest(String apiType, int pageSize, int pageNumber, String sortDirection, String sortBy) throws IOException, JSONException 
	{
		listOfActualApps.clear();
		listOfExpectedApps.clear();
		JSONArray jArray;
		String url = "";

		// update URL for page setting and get application list from API.
		url = applicationsURL + "?pageSize=" +  String.valueOf(pageSize) + "&page=" + String.valueOf(pageNumber + "&sortDirection=" + sortDirection + "&sortBy=" + sortBy);
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
						
		// store API list.
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
		// CommonMethods.selectSizeOfList(50);
		CommonMethods_AppsRoutes.SetUiPageSizeSelector(4);
		WaitForElementClickable(By.xpath("(//button[@class='btn btn-info btn-sm'])[5]"), 3, "");
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

	// loop starts at page number one. when loop goes into last page, have to exit. 
	public static void VerifyPagesSorting(int numberOfPages, String apiType, int pageSize, String sortDirection, String sortBy) throws Exception		
	{
		int index = 0;
		System.out.println("Num Pages = " + numberOfPages);
		for(int x = 0; x < numberOfPages; x++) // go through each page.
		{
			// click a page per x index ---- 4/14/18 - this quit working. indexing across page numbers would fail because of changing size of number clicks in DOM. 
			//WaitForElementClickable(By.cssSelector(".pagination>li:nth-of-type(" + (x + 3) + ")>a"), 3, "");
			//driver.findElement(By.cssSelector(".pagination>li:nth-of-type(" + (x + 3) + ")>a")).click();
			
			ShowText("Verify page num " + (x + 1));
			
			Thread.sleep(1000);
			
			// this stores API info for page x + 1  into listOfExpectedApps 
			PassDataAndStoreApiRequest(apiType, pageSize, x + 1, sortDirection, sortBy);
			
			// store the application info in UI to listOfActualApps
			ShowActualApplicationsOrStore(ActionForApplications.Store);

			// verify actual and expected are equal.
			VerifyApplicationsCollectionsExpectedAndActual();
			
			if(x == (numberOfPages - 1)) // last page has been verified - exit.
			{
				break;
			}
			
			// select next page.
			//index = driver.findElements(By.xpath("//ul[@class='pagination']/li")).size();
			//ClickItem("(//ul[@class='pagination']/li)[" + (index - 1) + "]/a", 3);
			SelectNextPage();
		}
		
		// ClickItem("(//ul[@class='pagination']/li)[1]/a", 3); // back to first page.
		SelectFirstPage();
		
		Thread.sleep(1000);
	}	

	// select next page. // TODO: this is common
	public static void SelectNextPage()
	{
		int index = 0;
		index = driver.findElements(By.xpath("//ul[@class='pagination']/li")).size();
		ClickItem("(//ul[@class='pagination']/li)[" + (index - 1) + "]/a", 3);
	}
	
	// select next page. // TODO: this is common
	// select next page.
	public static void SelectFirstPage()
	{
		ClickItem("(//ul[@class='pagination']/li)[1]/a", 3); // back to first page.	
	}
	
	public static void VerifySortingForItemAndDirection(int numberOfPages, String apiType, int pageSize, String sortDirection, String sortBy) throws Exception
	{
		VerifyPagesSorting(numberOfPages, apiType, pageSize, sortDirection, sortBy);	
	}

	public static void ClickAndSelectTenantInPulldown(int tenantListIndex) throws Exception
	{
		// click tenants pull-down
		driver.findElement(By.xpath("(.//*[@id='sortMenu'])[1]")).click();  
		// send search text.		
		WaitForElementClickable(By.xpath("//div[@class='d-flex flex-row']/input"), 3, "");
		driver.findElement(By.xpath("//div[@class='d-flex flex-row']/input")).sendKeys(listOfTenantWithAppExpected.get(tenantListIndex).m_TenantName); 
		// click the result.
		WaitForElementClickable(By.xpath("//button[@class='dropdown-item']"), 3, "");
		driver.findElement(By.xpath("//button[@class='dropdown-item']")).click();
		Thread.sleep(2000);
	}

	// select tenant name passed in from tenant pull-down list in UI
	public static void ClickAndSelectTenantInPulldown(String tenantName) throws Exception
	{
		// click tenants pull-down
		driver.findElement(By.xpath("(.//*[@id='sortMenu'])[1]")).click(); 
		
		// send search text.		
		WaitForElementClickable(By.xpath("//div[@class='d-flex flex-row']/input"), 3, "");
		driver.findElement(By.xpath("//div[@class='d-flex flex-row']/input")).sendKeys(tenantName); 
		
		// click the result.
		WaitForElementClickable(By.xpath("//button[@class='dropdown-item']"), 3, "");
		driver.findElement(By.xpath("//button[@class='dropdown-item']")).click();
		Thread.sleep(2000);
	}
	
	
	// the actual list index should only contain a key and name.  
	public static void VerifyKeyNameOnly(int index) throws Exception
	{
		String errMess = "Fail in verify 'VerifyKeyNameOnly' for ";
		ApplicationClass temp = listOfActualApps.get(index);
		
		Assert.assertEquals(temp.m_Key, testAppKey, errMess + "appKey.");
		Assert.assertEquals(temp.m_Name, testAppName, errMess + "appName.");
		Assert.assertEquals(temp.m_defaultHost, "", errMess + "defaultHost.");		
		Assert.assertEquals(temp.m_defaultPath, "", errMess + "defaultPath.");
		Assert.assertEquals(temp.m_Description, "", errMess + "description.");
		Assert.assertEquals(temp.m_Enabled, true, errMess + "enabled.");		
	}
	
	// the actual list index should contain everything. enabled has been set to non-default (false).
	public static void VerifyAllAppItems(int index) throws Exception
	{
		String errMess = "Fail in verify 'VerifyKeyNameOnly' for ";
		ApplicationClass temp = listOfActualApps.get(index);
		
		Assert.assertEquals(temp.m_Key, testAppKey, errMess + "appKey.");
		Assert.assertEquals(temp.m_Name, testAppName, errMess + "appName.");
		Assert.assertEquals(temp.m_defaultHost, testDefaultHost, errMess + "defaultHost.");		
		Assert.assertEquals(temp.m_defaultPath, testDefaultPath, errMess + "defaultPath.");
		Assert.assertEquals(temp.m_Description, testDescription, errMess + "description.");
		Assert.assertEquals(temp.m_Enabled, false, errMess + "enabled.");		
	}	
	
	
	
	public static void ShowCurrentTest(String currentTest) // TODO: duplicate
	{
		ShowText("******************************* Running test name " + currentTest + " **************************************************");
	}
	
	
	
	
	
}
