package pages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.parsers.CachingParserPool.ShadowedGrammarPool;
import org.apache.xerces.parsers.DTDConfiguration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.seleniumhq.jetty9.server.handler.ContextHandler.Availability;
import org.testng.Assert;

import com.google.gson.JsonArray;

import baseItems.BaseMain;
import classes.ApplicationClass;
import classes.RouteClass;
import common.CommonMethods;
import common.CommonMethods_AppsRoutes;
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
	public static final String saveButtonPoUp_Locator = "//button[@class='btn btn-primary']";	 // TODO: same for routes.
	public static final String addButton_Locator = "//button[@class='btn btn-primary ml-auto p-2']"; // TODO: same for routes.	
	public static final String uiCancel_Locator = "(//button[@class='btn btn-secondary'])[2]"; // TODO: same for routes.
	public static final String approveDeleteInPoup_Locator = "//input[@class='ng-untouched ng-pristine ng-valid']"; // TODO: same for routes.
	public static final String selectDeleteInPoup_Locator = "//button[@class='btn btn-danger']"; // TODO: same for routes.
	
	// this is the real route in the QA environment - don't touch.
	public static final String tenantDontUse = "CTTI";
	public static final String appDontUse = "RVM";
	public static final String deployDontUse = "RVM_DC1QA_CLUSTER_1";
	
	// used to see if text box is in error or not.
	public static final String uiTextBoxInError = "form-control ng-untouched ng-pristine ng-invalid";
	public static final String uiTextBoxNoError = "form-control ng-touched ng-dirty ng-valid";	
	
	// temporary test route 
	public static final String automationSubDomain= "seleniumSubDomain";
	public static final String automationDomain= "seleniumDomain.com";
	public static final String automationPath= "/seleniumPath";
	public static final String automationfullPath= automationSubDomain + "." + automationDomain + automationPath;
	public static final String automationTenantID = "selenium123";
	public static final String automationDescription = "selenium Description";	
	public static final String automationRouteKey = "SELENIUM_TENANT:SELEN_APP:1246924192";
	public static final String automationTenant = "SELENIUM_TENANT";
	public static final String automationApp = "SELEN_APP";
	public static final String automationDeploy = "SELEN_DEPLOYMENT";
	public static final String automationDisabledReason = "UPGRADING";	
	public static final boolean automationAllowServiceCalls = false;
	public static final boolean automationEnabled = false;	
	
	// for validating auto-populated items in add route pop-up.
	public static String tempHost = "";
	public static String tempPath = "";
	public static String tempTenantID = "";
													 	
	public static String apiType = "\"routes\":";
	public static String RoutesTableCss = ".table.table-striped>tbody>tr";
	public static String RoutesURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/routes";
	public static final String tenantsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/tenants";
	public static String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTUyNjMwODEwM30.0GU24pu7F8itDwHp8prFWlMwstsF53ISCxDQmteDEHCEWwXEt6V50AhnQTCLN7o6q-GQBlCQulTyeM6yn_C3bg";
	public static final int maxItemsPerPage = 50;
	
	public static int expectedNumberOfColumns = 8;
	
	public static List<RouteClass> listOfExpectedRoutes = new ArrayList<RouteClass>();
	public static List<RouteClass> listOfActualRoutes = new ArrayList<RouteClass>();
	public static List<String> listOfTenantItems = new ArrayList<String>();	

	public static int[] pageSizes = {5, 10, 20, 50}; // selectable page sizes in console.	
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// this gets all items in the collection from the API and all items from the applications UI and compares them.
	// this does this for one page of maxItemsPerPage or less items.
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void VerifyFullList() throws Exception
	{
		String url = RoutesURL + "?pageSize=" + maxItemsPerPage + "&includeTenant=true&&includeDeployment=true&includeApplication=true"; // get all the routes from the API, up to 50.
		String metadata = "";
		int totalCount = 0;
		
		metadata = CommonMethods.GetMetaDataWithUrl(token, url, apiType); // metadata

		// take out totalCount 
		totalCount = Integer.parseInt(metadata.split(":")[2].split(",")[0]);
		
		if(totalCount > maxItemsPerPage)
		{
			totalCount = maxItemsPerPage;
		}

		// this will have the collection of routes in a JSON array.
		JSONArray jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);

		Assert.assertTrue(jArray.length() == totalCount); // verify collection count matches metadata. 

		// store all the routes from API onto expected list.
		for(int x = 0; x < jArray.length(); x++)
		{
			JSONObject jo = jArray.getJSONObject(x); // get current json object from the list  
			GetAndAddRouteFromApiToExpectedList(jo); // add all the fields into a route object.
		}		
		
		System.out.println("Size from API = " +  listOfExpectedRoutes.size() + " rows.");
		
		CommonMethods_AppsRoutes.SetPageSizeToMax();
		
		// store the routes in UI to listOfActualApps
		ShowActualRoutesOrStore(ActionForApplications.Store);
		//ShowActualRoutesOrStore(ActionForApplications.Show);		
		//ShowRoutesActualAndExpectedCollection(); 
		
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
			
			CommonMethods_AppsRoutes.SetUiPageSizeSelector(pageSizeSelectorIndex);
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

	public static void ValidatePrePopulatedItemsAndEdits() throws Exception // bladd
	{
		boolean startListCompare = false;
		
		ShowCurrentTest("Routes: AddRoutePrePopulatedItems");

		// store application list info.
		CommonMethods_AppsRoutes.SetPageSizeToMax();
		Applications.listOfActualApps.clear();
		Applications.GoToApplications();
		Applications.SetPageSizeToMax();
		Applications.ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		// create a list that holds tenant key and tenantID. get information from API. 
		CreateTenantInformation();
		
		// go to routes list.
		GoToRoutes();

		// select add route and wait for title
		ClickItem(addButton_Locator, 3); // add
		WaitForElementVisible(By.xpath("//strong[text()='Add Route']"), 3); // title

		// select tenant and application that will populate tenantID and URL.
		SetTenantPulldown(GetTenantID(true));
		SetAppPulldown(AppHostPathRequest(true));
		Thread.sleep(500);
		
		// verify host, path, and tenantID.
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("domain"))).getAttribute("value"),  tempHost, "");
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("path"))).getAttribute("value"),  tempPath, "");
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).getAttribute("value"),  tempTenantID, "");		

		// select tenant and application that will not populate tenantID and URL.
		SetTenantPulldown(GetTenantID(false));
		SetAppPulldown(AppHostPathRequest(false));
		Thread.sleep(500);
		
		// verify host, path, and tenantID are empty
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("domain"))).getAttribute("value"),  "", "");
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("path"))).getAttribute("value"),  "", "");
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).getAttribute("value"),  "", "");		
	
		ClickItem("(.//*[@id='sortMenu'])[5]", 3);
		Thread.sleep(500);
		

		
		List<WebElement> listOfTenantsInPulldown = driver.findElements(By.cssSelector((".dropdown-menu")));
		
		for(WebElement ele: listOfTenantsInPulldown)
		{
			/*
			if(ele.getText().equals("Select Tenant"))
			{
				startListCompare = true;
				ShowText("True");
			}
			*/
			if(startListCompare)
			{
				ShowText(ele.getText());				
			}
			
			if(ele.getText().length() > 0)
			{
				ShowText(ele.getText());
				startListCompare = true;
			}
				
				
		}
		

	
	}
	
	
	public static void AddRoute() throws Exception 
	{
		int indexLocator = 0;
		boolean foundAddedRoute = false;
		
		ShowCurrentTest("Routes: AddRoute");
		
		// delete test route if it exists.
		DeleteRouteByKeyFromRow(automationRouteKey);

		// select add and wait for title
		ClickItem(addButton_Locator, 3); // add
		WaitForElementVisible(By.xpath("//strong[text()='Add Route']"), 3); // title
		
		// select correct pull-downs.
		PopulatePulldownsForAddRoute();
		
		// fill in fields and save.
		driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).sendKeys(automationSubDomain);
		driver.findElement(By.xpath(GetXpathForTextBox("domain"))).sendKeys(automationDomain);
		driver.findElement(By.xpath(GetXpathForTextBox("path"))).sendKeys(automationPath);
		driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).sendKeys(automationTenantID);
		driver.findElement(By.xpath("//textarea[@formcontrolname='description']")).sendKeys(automationDescription);		
		driver.findElement(By.xpath("(//input[@formcontrolname='allowServiceCalls'])[2]")).click();
		driver.findElement(By.xpath("(//input[@formcontrolname='enabled'])[2]")).click();		
		driver.findElement(By.xpath("(//input[@formcontrolname='enabled'])[2]")).click();
		//driver.findElement(By.xpath("//select[@formcontrolname='disabledReason']")).click();
		new Select(driver.findElement(By.xpath("//select[@formcontrolname='disabledReason']"))).selectByVisibleText("Upgrading");
		
		driver.findElement(By.xpath(saveButtonPoUp_Locator)).click();
		WaitForElementNotVisibleNoThrow(By.xpath(saveButtonPoUp_Locator) ,3); 
		
		// store routes from UI
		listOfActualRoutes.clear();
		ShowActualRoutesOrStore(ActionForApplications.Store);
		
		// see if added route exist by searching for it.
		for(RouteClass rtClass: listOfActualRoutes)
		{
			if((rtClass.m_host + rtClass.m_path).equals(automationfullPath.toLowerCase()))
			{
				foundAddedRoute = true;
				break;
			}
			indexLocator++;
		}

		if(!foundAddedRoute) // error if new route not found.
		{
			Assert.fail("Route that was added is not found in 'AddRoute'.");
		}
		
		// verify route is in list.
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_Key, automationRouteKey);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_tenantId, automationTenantID);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_tennantKey, automationTenant);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_appKey, automationApp);		
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_deployKey, automationDeploy);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_allowServiceCalls, automationAllowServiceCalls);		
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_enabled, automationEnabled);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_disabledReason, automationDisabledReason);
	}
	
	public static void AddValidations() throws Exception 
	{
		String existingRoutePath = "";
		String badPathError = "Path must only contain letters, numbers, periods, dashes and underscores and must start with a forward slash";
		String tooManyCharsPathError = "The subdomain.domain combination must not exceed 256 characters";
		String tenantPulldownMessage = "Select Tenant";
		String applicationPulldownMessage = "Select Application";		
		String deploymentPulldownMessage = "Select Deployment";
		String errMessagesPulldown = "Failed expected message in pulldown.";
		String subdomainNoSpecialChars = "Subdomain must only contain letters, numbers, periods, dashes and underscores";
		String domainNoSpecialChars = "Domain must only contain letters, numbers, periods, dashes and underscores";
		String specialCharErrNotFound = "Special character error not found.";		
		String[] validationMessagesAddRoute = new String[]{"Must select a tenant", "Must select an application", "Must select a deployment", "required", "required", "required", "Tenant ID is required"};
		
		String greaterThan256Chars = "256bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbmmmmmmmm"
				+ "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
				+ "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbnm";
		
		int cntr = 0;
		
		ShowCurrentTest("Routes: AddValidations");

		CommonMethods_AppsRoutes.SetPageSizeToMax();

		// delete test route if it exists.
		DeleteRouteByKeyFromRow(automationRouteKey);

		// ///////////////////////////////////////////////////////////////////////////////////
		// first get a list of all routes in UI, considering there may be a second page.
		// need this to make sure the route being added doesn't exist.
		// NOTE: 5/8/18 - QA environment was made small. two pages won't happen.
		// /////////////////////////////////////////////////////////////////////////////////// 
		//LoadActualList(numberOfRows, ActionForApplications.Store);
		
		ShowActualRoutesOrStore(ActionForApplications.Store);
		//ShowActualRoutesOrStore(ActionForApplications.Show);
		
		existingRoutePath = listOfActualRoutes.get(0).m_host + listOfActualRoutes.get(0).m_path;
		
		ShowText(existingRoutePath);
		
		// make sure test application is not on the list.
		//DeleteAppByKeyFromRow(testAppKey); 
		
		// select add, wait for title, and select cancel, and verify back in the applications list.
		ClickItem(addButton_Locator, 3); // add
		WaitForElementVisible(By.xpath("//strong[text()='Add Route']"), 3); // title
		ClickItem(uiCancel_Locator, 3); // cancel
	
		if(!WaitForElementNotVisibleNoThrow(By.xpath(uiCancel_Locator), 3)) // verify UI closed 
		{
			Assert.fail("Add route UI is still showing. It should have closed after cancel.");
		}
		
		// select add 
		ClickItem(addButton_Locator, 3); // add
		WaitForElementVisible(By.xpath("//strong[text()='Add Route']"), 3); // title

		// verify text boxes are highlighted.
		VerifyAllTextBoxConditions(false);
		
		// verify messages in pull-downs.
		Assert.assertEquals(driver.findElement(By.xpath("(//button[@id='sortMenu'])[5]")).getText(), tenantPulldownMessage, errMessagesPulldown);
		Assert.assertEquals(driver.findElement(By.xpath("(//button[@id='sortMenu'])[6]")).getText(), applicationPulldownMessage, errMessagesPulldown);
		Assert.assertEquals(driver.findElement(By.xpath("(//button[@id='sortMenu'])[7]")).getText(), deploymentPulldownMessage, errMessagesPulldown);		
		
		ShowAllValidationMessages();
		
		// get all the validation messages and verify them.
		List<WebElement> listOfErrorMessages =  driver.findElements(By.cssSelector(".form-text.text-danger>small")); // store validation messages.
		
		// verify all validation messages.
		for(WebElement ele : listOfErrorMessages) 
		{
			//ShowText(ele.getText()); //ShowText(failMessagesAddRoute[cntr]);
			Assert.assertEquals(ele.getText(), validationMessagesAddRoute[cntr], "Incorrect validation message found.");
			//ShowText(driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).getAttribute("class"));
			cntr++;
		}

		// fill in required text.
		driver.findElement(By.xpath(GetXpathForTextBox("domain"))).sendKeys(automationDomain);
		driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).sendKeys(automationSubDomain);
		driver.findElement(By.xpath(GetXpathForTextBox("path"))).sendKeys(automationPath);		
		driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).sendKeys(automationTenantID);
		
		// verify the text boxes aren't in validation error.
		VerifyAllTextBoxConditions(true);
		
		// put text into path that has no leading "/" and verify validation error.
		driver.findElement(By.xpath(GetXpathForTextBox("path"))).clear();
		driver.findElement(By.xpath(GetXpathForTextBox("path"))).sendKeys("test");
		WaitForElementVisible(By.xpath("(//div/small)[4]"), 2);
		Assert.assertEquals(driver.findElement(By.xpath("(//div/small)[4]")).getText(), badPathError);
		
		// sub-domain too many characters.
		driver.findElement(By.xpath(GetXpathForTextBox("path"))).clear();
		driver.findElement(By.xpath(GetXpathForTextBox("path"))).sendKeys(automationPath); 
		driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).sendKeys(greaterThan256Chars);
		WaitForElementVisible(By.xpath("(//div/small)[4]"), 2);
		Assert.assertEquals(driver.findElement(By.xpath("(//div/small)[4]")).getText(), tooManyCharsPathError);
		
		// domain too many characters.
		driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).clear();
		driver.findElement(By.xpath(GetXpathForTextBox("domain"))).sendKeys(greaterThan256Chars);
		WaitForElementVisible(By.xpath("(//div/small)[4]"), 2);
		Assert.assertEquals(driver.findElement(By.xpath("(//div/small)[4]")).getText(), tooManyCharsPathError);

		// //////////////////////////////////////////////////////////////////////////////////////////////////////
		// verify the max allowed characters to enter in sub-domain, domain, and path is 256
		// //////////////////////////////////////////////////////////////////////////////////////////////////////	
		
		CancelOpenAddRouteUI(true); // need to start at open.
		
		driver.findElement(By.xpath(GetXpathForTextBox("path"))).sendKeys("/" + greaterThan256Chars);
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("path"))).getAttribute("value").length(), 256);
		
		driver.findElement(By.xpath(GetXpathForTextBox("path"))).clear();
		driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).sendKeys(greaterThan256Chars);
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).getAttribute("value").length(), 256);
		
		CancelOpenAddRouteUI(true); // need to start at open.
		
		driver.findElement(By.xpath(GetXpathForTextBox("domain"))).sendKeys(greaterThan256Chars);
		driver.findElement(By.xpath(GetXpathForTextBox("domain"))).sendKeys(greaterThan256Chars);
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("domain"))).getAttribute("value").length(), 256);		

		// //////////////////////////////////////////////////////////////////////////////////////////////////////
		// verify validation error for "*" character in sub-domain and domain fields. 
		// //////////////////////////////////////////////////////////////////////////////////////////////////////
		ClearRequiredFields();
		driver.findElement(By.xpath(GetXpathForTextBox("path"))).sendKeys(automationPath);
		driver.findElement(By.xpath(GetXpathForTextBox("domain"))).sendKeys("*"); // special char		
		driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).sendKeys("*"); // special char

		WaitForElementVisible(By.xpath("(//div/small)[1]"), 5);
		WaitForElementVisible(By.xpath("(//div/small)[2]"), 5);
		Assert.assertEquals(driver.findElement(By.xpath("(//div/small)[1]")).getText(), subdomainNoSpecialChars, specialCharErrNotFound);
		Assert.assertEquals(driver.findElement(By.xpath("(//div/small)[2]")).getText(), domainNoSpecialChars, specialCharErrNotFound);
		
		// verify existing route can't be added.
		ClearRequiredFields();
		
		CancelOpenAddRouteUI(false);
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

	public static String AppHostPathRequest(boolean hasHostAndPath)
	{
		String retString = "";
		boolean foundRequest = false;
		
		Assert.assertTrue(Applications.listOfActualApps.size() > 0); // application list should be created.
		
		for(ApplicationClass appClass : Applications.listOfActualApps)
		{
			if(hasHostAndPath)
			{
				if(!appClass.m_defaultHost.equals("") && !appClass.m_defaultPath.equals(""))
				{
					retString = appClass.m_Key;
					tempHost = appClass.m_defaultHost;
					tempPath = appClass.m_defaultPath;					
					foundRequest = true;
					break;
				}
			}
			if(!hasHostAndPath)
			{
				if(appClass.m_defaultHost.equals("") && appClass.m_defaultPath.equals(""))
				{
					retString = appClass.m_Key;
					foundRequest = true;
					break;
				}
			}
		}
		
		if(!foundRequest && hasHostAndPath)
		{
			Assert.fail("Did not find app with a host and path in 'AppHostPathRequest'.");
		}
		
		if(!foundRequest && !hasHostAndPath)
		{
			Assert.fail("Did not find app with missing host and path in 'AppHostPathRequest'.");
		}
		
		return retString;
	}
	
	
	// find a tenant key that has a tenant Id or doesn't have a tenant Id. each tenant element in 'listOfTenantItems' has this format - tenantKey:tenantID.
	// if the  tenantID is missing for the tenant key, string character "null" is what the tenantID is. 
	public static String GetTenantID(boolean hasTenantId)
	{
		String retStr = "";
		
		boolean foundRequest = false;
		
		for(String str : listOfTenantItems)
		{
			if(hasTenantId)
			{
				if(!str.split(":")[1].equals("null"))
				{
					foundRequest = true;
					retStr = str.split(":")[0];
					tempTenantID = str.split(":")[1];
					break;
				}
			}
			else
			{
				if(str.split(":")[1].equals("null"))
				{
					foundRequest = true;
					retStr = str.split(":")[0];
					break;
				}
			}
		}

		if(!foundRequest && hasTenantId)
		{
			Assert.fail("Did not find tenant with a tenantID in 'GetTenantID'.");
		}
		
		if(!foundRequest && !hasTenantId)
		{
			Assert.fail("Did not find tenant with a missing tenantID in 'GetTenantID'.");
		}

		return retStr;
	}
	
	
	// store all tenant keys and their corresponding tenantID/name.
	public static void CreateTenantInformation() throws Exception
	{
		JSONArray jArray;
		
		String url = tenantsURL + "?pageSize=";
		String apiType = "\"tenants\":"; 
		
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		// store some tenant info from the API onto listOfTenantItems list.
		for(int x = 0; x < jArray.length(); x++)
		{
			JSONObject jo = jArray.getJSONObject(x); // get current json object from the list  

			if(CommonMethods.GetNonRequiredItem(jo, "defaultTenantID").equals(""))
			{
				listOfTenantItems.add(jo.getString("key") + ":" + "null" + ":" + jo.getString("name"));
			}
			else
			{
				listOfTenantItems.add(jo.getString("key") + ":" + jo.getString("defaultTenantID") + ":" + jo.getString("name"));				
			}
		}		

		// for(String str : listOfTenantItems){ShowText(str.split(":")[0] + ":" + str.split(":")[1]);}		
	}
	
	
	// see if a route key exists in the actual list from UI. 
	public static int FindIndexExistingRoute(String routeKey)
	{
		boolean foundAdd = false;
		int indexCntr = 1;

		for(RouteClass rtClass : listOfActualRoutes)
		{
			if(rtClass.m_Key.equals(routeKey))
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
	
	public static void DeleteRouteByKeyFromRow(String routeKey) throws Exception
	{
		int indexCntr = 0;

		// clear actual list and get max page size. 
		listOfActualRoutes.clear();
		CommonMethods_AppsRoutes.SetPageSizeToMax();

		listOfActualRoutes.clear();
		
		// store the actual applications, see if test application exists. 
		ShowActualRoutesOrStore(ActionForApplications.Store);

		indexCntr = FindIndexExistingRoute(routeKey);
		
		if(indexCntr != -1) // test application exists, delete it and then verify it has been deleted.
		{
			CommonMethods_AppsRoutes.SelectDeleteByRowInList(indexCntr); // select delete application list row with appKey.
			
			// this is important. it verifies the correct route is being deleted. it verifies the correct key is present.
			WaitForElementVisible(By.xpath("//dd[text()='" + automationRouteKey + "']"), 3);
			Assert.assertEquals(driver.findElement(By.xpath("//dd[text()='" + automationRouteKey + "']")).getText(), automationRouteKey, "");
			
			ClickItem(approveDeleteInPoup_Locator ,3); // approve 			
			
			ClickItem(selectDeleteInPoup_Locator, 3); // delete.             			
			WaitForElementNotVisibleNoThrow(By.xpath(selectDeleteInPoup_Locator), 4);
			
			listOfActualRoutes.clear();

			// store the actual applications.
			ShowActualRoutesOrStore(ActionForApplications.Store);

			Assert.assertTrue(FindIndexExistingRoute(appKey) == -1, "Error -- test route was supposed to be deleted."); 
		}		
		listOfActualRoutes.clear();
	}

	public static void PopulatePulldownsForAddRoute() throws Exception
	{
		// ///////////
		// tenant 
		// ///////////
		ClickItem("(.//*[@id='sortMenu'])[5]", 3);
		
		// send search text.		
		WaitForElementClickable(By.xpath("(//input[@placeholder='Search...'])[4]"), 3, "");
		driver.findElement(By.xpath("(//input[@placeholder='Search...'])[4]")).sendKeys(automationTenant);
		
		//ClickItem("(//span[text()='" + automationTenant  + "']/../..)[2]", 3);
		ClickItem("(//div[@class='dropdown-menu'])[5]/div[2]", 2); // this seems more consistent with different selections.

		// ///////////
		// application
		// ///////////
		ClickItem("(.//*[@id='sortMenu'])[6]", 3);
		
		// send search text.		
		WaitForElementClickable(By.xpath("(//input[@placeholder='Search...'])[5]"), 3, "");
		driver.findElement(By.xpath("(//input[@placeholder='Search...'])[5]")).sendKeys(automationApp);

		//ClickItem("(//span[text()='" + automationApp + "']/../..)[2]", 2); // didn't always work with other text.
		ClickItem("(//div[@class='dropdown-menu'])[6]/div[2]", 2); // this seems more consistent with different selections.
		
		// ///////////
		// deploy
		// ///////////
		ClickItem("(.//*[@id='sortMenu'])[7]", 3);
		
		// send search text.		
		WaitForElementClickable(By.xpath("(//input[@placeholder='Search...'])[6]"), 3, "");
		driver.findElement(By.xpath("(//input[@placeholder='Search...'])[6]")).sendKeys(automationDeploy);

		//ClickItem("(//span[text()='" + automationDeploy + " "  +  "']/../..)[2]", 2);
		ClickItem("(//div[@class='dropdown-menu'])[7]/div[2]", 2); // this seems more consistent with different selections.
	}
	
	public static void SetTenantPulldown(String tenantName)
	{
		// tenant
		ClickItem("(.//*[@id='sortMenu'])[5]", 3);
		
		// send search text.		
		WaitForElementClickable(By.xpath("(//input[@placeholder='Search...'])[4]"), 3, "");
		driver.findElement(By.xpath("(//input[@placeholder='Search...'])[4]")).sendKeys(tenantName);
		
		ClickItem("(//span[text()='" + tenantName  + "']/../..)[2]", 3);
	}
	
	public static void SetAppPulldown(String appName)
	{
		// application
		ClickItem("(.//*[@id='sortMenu'])[6]", 3);
		
		// send search text.		
		WaitForElementClickable(By.xpath("(//input[@placeholder='Search...'])[5]"), 3, "");
		driver.findElement(By.xpath("(//input[@placeholder='Search...'])[5]")).sendKeys(appName);

		ClickItem("(//div[@class='dropdown-menu'])[6]/div[2]", 2);
	}
	
	public static void ClearRequiredFields()
	{
		driver.findElement(By.xpath(GetXpathForTextBox("path"))).clear();		
		driver.findElement(By.xpath(GetXpathForTextBox("domain"))).clear();
		driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).clear();
		driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).clear();		
	}
	
	
	public static void CancelOpenAddRouteUI(boolean reOpen) throws Exception
	{
		ClickItem(uiCancel_Locator, 3); // cancel
		
		if(!WaitForElementNotVisibleNoThrow(By.xpath(uiCancel_Locator), 3)) // verify UI closed 
		{
			Assert.fail("Add route UI is still showing. It should have closed after cancel.");
		}

		if(reOpen)
		{
			ClickItem(addButton_Locator, 3); // add
			WaitForElementVisible(By.xpath("//strong[text()='Add Route']"), 3); // title
		}
	}
	
	//click pull-downs and text boxes to get all validation messages to show up.
	public static void ShowAllValidationMessages()
	{
		ClickItem("//button[text()='Select Tenant']", 3);
		ClickItem("//button[@class='dropdown-item active']/span[text()='Select Tenant']", 3);		
		ClickItem("//button[text()='Select Application']", 3);
		ClickItem("//button[@class='dropdown-item active']/span[text()='Select Application']", 3);		
		ClickItem("//button[text()='Select Deployment']", 3);
		ClickItem("//button[@class='dropdown-item active']/span[text()='Select Deployment']", 3);	
		ClickItem(GetXpathForTextBox("subdomain"), 3);		
		ClickItem(GetXpathForTextBox("domain"), 3);
		ClickItem(GetXpathForTextBox("path"), 3);	
		ClickItem(GetXpathForTextBox("tenantID"), 3);
		ClickItem("//textarea[@formcontrolname='description']", 3);
	}
	
	
	// this verifies the four text boxes in the add route UI have or don't have the red validation-error indicator.
	public static void VerifyAllTextBoxConditions(boolean shouldHaveNoError)
	{
		if(!shouldHaveNoError)
		{
			String errMessage = "Failed verifiication test for text boxes.";
			Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).getAttribute("class"), uiTextBoxInError, errMessage);
			Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("domain"))).getAttribute("class"), uiTextBoxInError, errMessage);		
			Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("path"))).getAttribute("class"), uiTextBoxInError, errMessage);
			Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).getAttribute("class"), uiTextBoxInError, errMessage);			
		}
		else
		{
			String errMessage = "Failed verifiication test for text boxes.";
			Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).getAttribute("class"), uiTextBoxNoError, errMessage);
			Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("domain"))).getAttribute("class"), uiTextBoxNoError, errMessage);		
			Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("path"))).getAttribute("class"), uiTextBoxNoError, errMessage);
			Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).getAttribute("class"), uiTextBoxNoError, errMessage);						
		}
	}
	
	public static String GetXpathForTextBox(String item) // TODO: use in routes also
	{
		return "//input[@formcontrolname='" + item + "']";
	}
	
	public static void ClickItem(String xpath, int timeOut) // TODO: this is common
	{
		WaitForElementClickable(By.xpath(xpath), timeOut, "");
		driver.findElement(By.xpath(xpath)).click();
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
	
	
	public static void ShowCurrentTest(String currentTest) // TODO: duplicate
	{
		ShowText("******************************* Running test name " + currentTest + " **************************************************");
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
			ShowActualRoutesOrStore(ActionForApplications.Store);

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
	// go through rows and columns and show items or add items to the list of route objects.
	// /////////////////////////////////////////////////////////////////////////////////////////
	public static void ShowActualRoutesOrStore(ActionForApplications action)
	{
		int numberOfRows = driver.findElements(By.cssSelector(".table.table-striped>tbody>tr")).size(); // get number of rows.
		int numberOfColumns = 0;
		String tempString = "";
		
		System.out.println("Num Rows In UI. = " + numberOfRows);
		
		LoadActualList(numberOfRows, action);
	}	
	
	// this loads the items in the current page in the UI onto the actual list.
	public static void LoadActualList(int numberOfRows, ActionForApplications action)
	{
		String tempString = "";
		int numberOfColumns = 0;
		
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
	
	
	
	public static void ShowRoutesActualAndExpectedCollection()
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
