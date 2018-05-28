package pages;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import com.gargoylesoftware.htmlunit.WaitingRefreshHandler;

import baseItems.BaseMain;
import classes.ApplicationClass;
import classes.RouteClass;
import common.CommonMethods;
import common.CommonMethods_AppsRoutes;
import common.CommonMethods_AppsRoutes.DeployInfo;
import common.CommonMethods_AppsRoutes.TenantInfo;
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
	public static final String pullDown_Locator = ".//*[@id='sortMenu']";
	public static final String uiRadioButton_Locator = "//label[@class='radio-inline']";	
	public static final String uiTitle_Locator = "//div[@class='modal-header']/h4";
	
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
	public static final String automationTenantName = "selenium tenant";	
	public static final String automationApp = "SELEN_APP";
	public static final String automationAppName = "selenium app";
	public static final String automationDeploy = "SELEN_DEPLOYMENT";
	public static final String automationDeployVersion = "18.1";	
	public static final String automationDisabledReason = "UPGRADING";	
	public static final boolean automationAllowServiceCalls = false;
	public static final boolean automationEnabled = false;	
	
	public static final String charSeparatorForTenantItems = "\\^";
	
	// for validating auto-populated items in add route pop-up.
	public static String tempHost = "";
	public static String tempPath = "";
	public static String tempTenantID = "";

	public static String appWithNoDeploys = ""; // used in route add pulldown tests,
	
	public static String apiType = "\"routes\":";
	public static String RoutesTableCss = ".table.table-striped>tbody>tr";
	public static String RoutesURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/routes";
	public static final String tenantsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/tenants";
	public static final String deploymentsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/deployments";	
	public static String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTUyOTg4MDg5NX0.TDNUXzChWcaaTiQESxzL4-wuUolfzT4EpmL5IUmAkLtsh6oXaBArxAlDJNE2Lv-2yXL9zec7jEVHeQ3HXHARaw";	
	
	
	public static final int maxItemsPerPage = 50;
	
	public static int expectedNumberOfColumns = 8;
	public static int maxNumberOfItemsInPulldown = 10;
	public static int indexToSelectForTenant = 1;
	public static int indexToSelectForApp = 1;
	public static int indexToSelectForRoute = 1;	
	public static int indexToSelectForDeployment = 1;	
	
	public static List<RouteClass> listOfExpectedRoutes = new ArrayList<RouteClass>();
	public static List<RouteClass> listOfActualRoutes = new ArrayList<RouteClass>();
	public static List<CommonMethods_AppsRoutes.TenantInfo> listOfTenantItems = new ArrayList<CommonMethods_AppsRoutes.TenantInfo>();
	public static List<DeployInfo> listOfMaxDeploymentItems = new ArrayList<DeployInfo>();

	public static int[] pageSizes = {5, 10, 20, 50}; // selectable page sizes in console.	

	enum DisabledReason
	{
		upgrading, 
		onboarding,
		offboarding,
		maintenance,
		unknown,
		noselect
	}
	
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

	// step 7, 8, and 9 - reset and cancel.
	public static void ValidationAndInitialStates_PartThree() throws Exception // bladd
	{
		// make sure user is on routes page. add test route and open it.
		MakeSureUserOnRoutespage();
		CommonMethods_AppsRoutes.SetPageSizeToMax();
		AddRoute(true);
		SelectEditByRow(indexToSelectForRoute); // select test route edit
		
		// change each item in the test route.
		PopulateRouteAddFieldsChangeData(true);
		
		System.out.println(driver.findElement(By.xpath("//span[text()='Reset']/..")).isEnabled());
		System.out.println(driver.findElement(By.xpath(saveButtonPoUp_Locator)).isEnabled());		
		System.out.println(driver.findElement(By.xpath(uiCancel_Locator)).isEnabled());
		
		driver.findElement(By.xpath("//span[text()='Reset']/..")).click();

		System.out.println(driver.findElement(By.xpath("//span[text()='Reset']/..")).isEnabled());
		System.out.println(driver.findElement(By.xpath(saveButtonPoUp_Locator)).isEnabled());		
		System.out.println(driver.findElement(By.xpath(uiCancel_Locator)).isEnabled());

		
	}
	
	// step 5 and 6
	public static void ValidationAndInitialStates_PartTwo() throws Exception 
	{
		String errDeployWrongNumItemsInList = "Deployment dropdown should only have two items.";
		String errDeployDisabledWarningFailed = "Disabled warning test failed.";
		
		ShowCurrentTest("Routes: ValidationAndInitialStates_PartTwo");		
		
		// ////////////////////////////////////////////////////////////////////////////////////////
		// store row indexes to test tenant and application. add route and store its index.
		// set tenant and application for test route true and verify edit route has no warning
		// indicators in tenant and application fields.
		// ////////////////////////////////////////////////////////////////////////////////////////

		CommonMethods_AppsRoutes.SetPageSizeToMax();

		ShowText("Store row indexes for apps, tenants, and deployments.");
		SetupRowIndexesForTestAppTenantDeployment(); // find row indexes for test application, test tenant, and test deployment.

		GoToRoutes();
		CommonMethods_AppsRoutes.SetPageSizeToMax();

		ShowText("Delete/Add a test route.");
		AddRoute(true); // add the test route. with parameter set true, this stores row index for test route after it is deleted(maybe)/created. 

		ShowText("Verify no warnings.");
		// ////////////////////////////////////////////////////////////////////////////////////////
		// set tenant and application for test route true/true and verify edit route warnings
		// ////////////////////////////////////////////////////////////////////////////////////////

		SetTenantAppEnabledState(true, true);
		
		GoToRoutes();
		CommonMethods_AppsRoutes.SetPageSizeToMax();
		
		SelectEditByRow(indexToSelectForRoute); // select test route edit
		
		// verify there are no tenant and application warnings.
		Assert.assertTrue(driver.findElements(By.cssSelector(".container.no-gutters>dl>dd:nth-of-type(2)>span")).size() == 1); // tenant
		Assert.assertTrue(driver.findElements(By.cssSelector(".container.no-gutters>dl>dd:nth-of-type(3)>span")).size() == 1); // application		
		
		CancelOpenAddRouteUI(false); // close edit route

		ShowText("Verify app warnings.");
		// ////////////////////////////////////////////////////////////////////////////////////////
		// set tenant and application for test route true/false and verify edit route warnings
		// ////////////////////////////////////////////////////////////////////////////////////////
		SetTenantAppEnabledState(true, false);

		GoToRoutes();
		CommonMethods_AppsRoutes.SetPageSizeToMax();

		SelectEditByRow(indexToSelectForRoute); // select test route edit
		
		// verify there is tenant warning and no application warning.
		Assert.assertTrue(driver.findElements(By.cssSelector(".container.no-gutters>dl>dd:nth-of-type(2)>span")).size() == 1); // tenant
		Assert.assertTrue(driver.findElements(By.cssSelector(".container.no-gutters>dl>dd:nth-of-type(3)>span")).size() == 2); // application		
		
		CancelOpenAddRouteUI(false); // close edit route
		
		ShowText("Verify tenant warnings.");
		// ////////////////////////////////////////////////////////////////////////////////////////
		// set tenant and application for test route false/true and verify edit route warnings
		// ////////////////////////////////////////////////////////////////////////////////////////
		SetTenantAppEnabledState(false, true);

		GoToRoutes();
		CommonMethods_AppsRoutes.SetPageSizeToMax();

		SelectEditByRow(indexToSelectForRoute); // select test route edit
		
		// verify there is no tenant warning and is application warning.
		Assert.assertTrue(driver.findElements(By.cssSelector(".container.no-gutters>dl>dd:nth-of-type(2)>span")).size() == 2); // tenant
		Assert.assertTrue(driver.findElements(By.cssSelector(".container.no-gutters>dl>dd:nth-of-type(3)>span")).size() == 1); // application		
		
		CancelOpenAddRouteUI(false); // close edit route
		
		ShowText("Verify app and tenant warnings.");
		// ////////////////////////////////////////////////////////////////////////////////////////
		// set tenant and application for test route false/false and verify edit route warnings
		// ////////////////////////////////////////////////////////////////////////////////////////
		SetTenantAppEnabledState(false, false);

		GoToRoutes();
		CommonMethods_AppsRoutes.SetPageSizeToMax();

		SelectEditByRow(indexToSelectForRoute); // select test route edit
		
		// verify there is a tenant warning and an application warning.
		Assert.assertTrue(driver.findElements(By.cssSelector(".container.no-gutters>dl>dd:nth-of-type(2)>span")).size() == 2); // tenant
		Assert.assertTrue(driver.findElements(By.cssSelector(".container.no-gutters>dl>dd:nth-of-type(3)>span")).size() == 2); // application		
		
		CancelOpenAddRouteUI(false); // close edit route

		// set tenant and application enabled to true/true
		SetTenantAppEnabledState(true, true);

		// //////////////////////////////////////////////
		// verify test deployment enabled and disabled. 
		// //////////////////////////////////////////////
		
		SetDeploymentEnabledState(false); // set deployment disabled
		
		CommonMethods.selectItemPlatformDropdown("Routes");
		WaitForPageLoad();

		CommonMethods_AppsRoutes.SetPageSizeToMax();
		
		SelectEditByRow(indexToSelectForRoute); // select test route edit

		ClickItem("(" + pullDown_Locator + ")[5]", 3);  // select deployment pull-down. no method created or this because this is only done once in edit route. 
		
		// deployment drop-down should only have one item and the search space.
		Assert.assertTrue(driver.findElements(By.xpath("(//div[@class='dropdown-menu'])[5]/div")).size() == 2, errDeployWrongNumItemsInList);
		// verify warning in deployment drop down.
		Assert.assertTrue(driver.findElements(By.xpath("(//button[@class='dropdown-item active'])[5]/span/span")).size() == 3, errDeployDisabledWarningFailed);		

		CancelOpenAddRouteUI(false); // close edit route
		
		SetDeploymentEnabledState(true); // set deployment enabled.
		
		CommonMethods.selectItemPlatformDropdown("Routes");
		WaitForPageLoad();

		CommonMethods_AppsRoutes.SetPageSizeToMax();
		
		SelectEditByRow(indexToSelectForRoute); // select test route edit

		ClickItem("(" + pullDown_Locator + ")[5]", 3);  // select deployment pull-down. no method created or this because this is only done once in edit route. 
		
		// deployment drop-down should only have one item and the search space.
		Assert.assertTrue(driver.findElements(By.xpath("(//div[@class='dropdown-menu'])[5]/div")).size() == 2, errDeployWrongNumItemsInList);
		// verify no warning in deployment drop down.
		Assert.assertTrue(driver.findElements(By.xpath("(//button[@class='dropdown-item active'])[5]/span/span")).size() == 2, errDeployDisabledWarningFailed);		
		
		CancelOpenAddRouteUI(false); // close edit route
	}
	
	// steps 1, 2, 3. step 4 requires hovers.
	public static void ValidationAndInitialStates_PartOne() throws Exception
	{
		int indexToSelect = -1;
		String errMessage = "Verification error in 'ValidationAndInitialStates'.";
		String actualAppKey = "";
		String actualDeployVersion = "";
		String moreThanFiftyChars = "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
		String moreThan1024Chars = "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
		int maxCharsTenantId = 50;
		int maxCharsDescription = 1024;
		
		ShowCurrentTest("Routes: ValidationAndInitialStates_PartOne");		

		// add a route. this will populate the 'listOfActualRoutes' with the test route included. 
		AddRoute(false); // this will delete an existing test route and add it back into a known state.
		
		indexToSelect = TestRouteIsInRoutesList() + 1; // find row index. add one because index is zero based.
		
		SelectEditByRow(indexToSelect);

		WaitForElementVisible(By.xpath("//strong[text()='Edit Route']"), 2);
		
		// in UI - store key, tenant name, application name, and URL into list.
		List<WebElement> eleList = driver.findElements(By.cssSelector(".container.no-gutters>dl>dd"));

		// verify what's showing.
		Assert.assertEquals(eleList.get(0).getText(), automationRouteKey, errMessage);
		Assert.assertEquals(eleList.get(1).getText(), automationTenantName, errMessage);
		Assert.assertEquals(eleList.get(2).getText(), automationAppName, errMessage);
		Assert.assertEquals(eleList.get(3).getText(), automationfullPath.toLowerCase(), errMessage);
	
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).getAttribute("value"), automationTenantID);
		Assert.assertEquals(driver.findElement(By.xpath("//textarea[@formcontrolname='description']")).getAttribute("value"), automationDescription);		
		
		// .container.no-gutters>dl>dd:nth-of-type(2)>span
		
		
		// verify info in deployment pull-down.
		driver.findElement(By.xpath("(" + pullDown_Locator  + ")[5]")).click(); // select

		// verify number items in pull-down and what's in pull-down.
		eleList = driver.findElements(By.xpath("(//div[@class='dropdown-menu'])[5]/div"));
		Assert.assertTrue(eleList.size() == 2, ""); // one item and search box.
		Assert.assertEquals(driver.findElement(By.xpath("(//button[@class='dropdown-item active'])[5]/span/span[1]")).getText(), automationDeploy, "" ); 
		
		// verify deployment drop-down items. 
		actualAppKey = driver.findElement(By.xpath("(//button[@class='dropdown-item active'])[5]/span/span[2]")).getText().replace("[","").replace("]","").split(" ")[0]; // application key
		actualDeployVersion = driver.findElement(By.xpath("(//button[@class='dropdown-item active'])[5]/span/span[2]")).getText().replace("[","").replace("]","").split(" ")[1]; // deploy version.	 	
		
		Assert.assertEquals(actualAppKey, automationApp, errMessage);
		Assert.assertEquals(actualDeployVersion, automationDeployVersion, errMessage);
		
		// verify max characters in tenant ID and description
		driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).sendKeys(moreThanFiftyChars);
		Assert.assertTrue(driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).getAttribute("value").length() == maxCharsTenantId);
		
		driver.findElement(By.xpath("//textarea[@formcontrolname='description']")).sendKeys(moreThan1024Chars);
		Assert.assertTrue(driver.findElement(By.xpath("//textarea[@formcontrolname='description']")).getAttribute("value").length() == maxCharsDescription);
		
		CancelOpenAddRouteUI(false); // close route add UI.
		
	}
	
	public static void ValidatePrePopulatedItemsAndEdits_PartThree() throws Exception 
	{
		int indexLocator = -1;
		ShowCurrentTest("Routes: ValidatePrePopulatedItemsAndEdits_PartThree");
		
		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// finish test for disabled reason values unknown (selected via pull-down), upgrading, maintenance, and on boarding.
		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// /////////////////////////////////////////////////////////
		// disabled reason value unknown (selected via pull-down).
		// /////////////////////////////////////////////////////////
		SetupForAppRouteEnabledAndServiceCall(false, false, DisabledReason.unknown); // using pull-down set reason to unknown
		SaveRoutePopup();

		indexLocator = StoreUI_WithPartialVerification();
		
		// verify items of interest.
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_enabled, false);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_allowServiceCalls, false);		
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_disabledReason, DisabledReason.unknown.toString().toUpperCase());
		
		// //////////////////////////////////
		// disabled reason value upgrading.
		// //////////////////////////////////
		SetupForAppRouteEnabledAndServiceCall(false, false, DisabledReason.upgrading); // using pull-down set reason to upgrading
		SaveRoutePopup();
		
		indexLocator = StoreUI_WithPartialVerification();

		// verify items of interest.
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_enabled, false);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_allowServiceCalls, false);		
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_disabledReason, DisabledReason.upgrading.toString().toUpperCase());
		
		// ////////////////////////////////////
		// disabled reason value maintenance.
		// ////////////////////////////////////
		SetupForAppRouteEnabledAndServiceCall(false, false, DisabledReason.maintenance); // using pull-down set reason to maintenance
		SaveRoutePopup();

		indexLocator = StoreUI_WithPartialVerification();
		
		// verify items of interest.
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_enabled, false);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_allowServiceCalls, false);		
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_disabledReason, DisabledReason.maintenance.toString().toUpperCase());
		
		// ////////////////////////////////////
		// disabled reason value on boarding.
		// ////////////////////////////////////
		SetupForAppRouteEnabledAndServiceCall(false, false, DisabledReason.maintenance); // using pull-down set reason to maintenance
		SaveRoutePopup();

		indexLocator = StoreUI_WithPartialVerification();
		
		// verify items of interest.
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_enabled, false);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_allowServiceCalls, false);		
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_disabledReason, DisabledReason.maintenance.toString().toUpperCase());
		
		// now verify add route error when using existing route sub domain, domain, and path. 
		SelectAddRoute();
		ClearRouteTextFields();
		PopulateRouteAddFields(false);
		WaitForElementVisible(By.xpath("//div/small"), 2);
		Assert.assertEquals(driver.findElement(By.xpath("//div/small")).getText(), "There is already a route to this subdomain.domain/path"); // verify expected error. 
		
		CancelOpenAddRouteUI(false); // close route add UI.
		
	}
		
	// see first set of comments for description.
	public static void ValidatePrePopulatedItemsAndEdits_PartTwo() throws Exception
	{
		int indexLocator = 0;
		ShowCurrentTest("Routes: AddRoutePrePopulatedItems_PartTwo");

		// //////////////////////////////////////////////////////////////////////////////////////////////
		// save a route for these enabled/service-calls: false/false, false/true, true/false,true/true.
		// this also verifies these disabled reasons: unknown, off boarding, on boarding. 
		// //////////////////////////////////////////////////////////////////////////////////////////////

		// ///////////////////////////////////////////////////////////////////////////////
		// enabled/service-calls = false/false and leave disabled reason at default.
		// ///////////////////////////////////////////////////////////////////////////////
		SetupForAppRouteEnabledAndServiceCall(false, false, DisabledReason.noselect); // no select means the disabled reason pull-down is not touched. 
		SaveRoutePopup();

		indexLocator = StoreUI_WithPartialVerification();		

		// verify items of interest.
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_enabled, false);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_allowServiceCalls, false);		
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_disabledReason, DisabledReason.unknown.toString().toUpperCase());

		// ///////////////////////////////////////////////////////////////////////////////
		// enabled/service-calls = false/true. disabled reason will be at off-boarding  
		// ///////////////////////////////////////////////////////////////////////////////

		SetupForAppRouteEnabledAndServiceCall(false, true, DisabledReason.offboarding);
		SaveRoutePopup();

		indexLocator = StoreUI_WithPartialVerification();
	
		// verify items of interest.
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_enabled, false);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_allowServiceCalls, true);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_disabledReason, DisabledReason.offboarding.toString().toUpperCase());
		
		// ///////////////////////////////////////////////////////////////////////////////
		// enabled/service-calls = true/false. disabled reason will not matter.  
		// ///////////////////////////////////////////////////////////////////////////////

		SetupForAppRouteEnabledAndServiceCall(true, false, DisabledReason.noselect);
		SaveRoutePopup();

		indexLocator = StoreUI_WithPartialVerification();
		
		// verify items of interest.
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_enabled, true);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_allowServiceCalls, false);
		// Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_disabledReason, DisabledReason.offboarding.toString().toUpperCase());
		
		// ///////////////////////////////////////////////////////////////////////////////
		// enabled/service-calls = true/true. disabled reason will not matter  
		// ///////////////////////////////////////////////////////////////////////////////

		SetupForAppRouteEnabledAndServiceCall(true, true, DisabledReason.onboarding);
		SaveRoutePopup();

		indexLocator = StoreUI_WithPartialVerification();
		
		// verify items of interest.
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_enabled, true);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_allowServiceCalls, true);
		// Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_disabledReason, DisabledReason.onboarding.toString().toUpperCase());
	}
	
	
	public static void ValidatePrePopulatedItemsAndEdits_PartOne() throws Exception 
	{
		ShowCurrentTest("Routes: AddRoutePrePopulatedItems_PartOne");

		// store application list info from UI list - will be used later..
		CommonMethods_AppsRoutes.SetPageSizeToMax();
		Applications.listOfActualApps.clear();
		Applications.GoToApplications();
		Applications.SetPageSizeToMax();
		Applications.ShowActualApplicationsOrStore(ActionForApplications.Store);
		
		// create a list that holds tenant key and tenantID. get information from API - will be used later. 
		CreateTenantInformation();
		
		// go to routes list.
		GoToRoutes();

		SelectAddRoute();
				
		// select tenant and application that will populate tenantID and URL.
		SetTenantPulldown(GetTenantID(true)); // select tenant in pull-down that has tenantID.
		SetAppPulldown(AppHostPathRequest(true)); // select application in pull-down that has application host and path.
		Thread.sleep(500);
		
		// verify host, path, and tenantID in add UI.
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("domain"))).getAttribute("value"),  tempHost, "");
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("path"))).getAttribute("value"),  tempPath, "");
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).getAttribute("value"),  tempTenantID, "");		

		// select tenant and application that will not populate tenantID and URL.
		SetTenantPulldown(GetTenantID(false)); // select tenant in pull-down that has no tenantID.
		SetAppPulldown(AppHostPathRequest(false)); // select application in pull-down that has application no host and path.
		Thread.sleep(500);
		
		// verify host, path, and tenantID are empty add UI.
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("domain"))).getAttribute("value"),  "", "");
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("path"))).getAttribute("value"),  "", "");
		Assert.assertEquals(driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).getAttribute("value"),  "", "");		
	
		ClickItem("(" + pullDown_Locator + ")[5]", 3); // select tenants pull-down.
		Thread.sleep(500);
		
		// compare tenant keys and names in pull-down to expected tenant keys and names.
		VerifyItemsInTenantPulldown();
		
		ClickItem("(" + pullDown_Locator + ")[6]", 3); // select application pull-down 
		Thread.sleep(500);		

		// compare application keys and names in pull-down to expected application keys and names.
		VerifyItemsInAppPulldown();
		
		// ////////////////////////////////////////////////////////////////////////////////////////////////
		// select an application in applications pull-down that has the most deployments and verify the
		// deployments in deployment list (key, application, and deploy version).
		// ////////////////////////////////////////////////////////////////////////////////////////////////		
		
		// this builds a list called 'listOfDeploymentItems' that will have all the expected
		// deployment keys, application keys, and deploy versions that should be shown in the 
		// deployments pull-down, after the application with the most deployments is selected.
		// the API is used to get the information for the 'listOfDeploymentItems' list.
		// this also finds an application that has no deployments.
		FindAppWithTheMostDeploys();
		
		ClickItem("(" + pullDown_Locator + ")[6]", 3); // select applications pull-down.
		
		// select application that's common to all items in the 'listOfDeploymentItems' list.
		// this will be the application with the most deployments.
		SetAppPulldown(listOfMaxDeploymentItems.get(0).m_appName);  
		
		SelectDeploymentPulldown();
		
		VerifyItemsInDeployPulldown();
		
		// ///////////////////////////////////////////////////////////////////////////////////////
		// verify application with no deployment shows no deployments in deployment pull-down.
		// ///////////////////////////////////////////////////////////////////////////////////////

		SelectDeploymentPulldown();
		//ClickItem("(.//*[@id='sortMenu'])[7]", 3); // select deployment pull-down to close it.
		
		SetTenantPulldown(GetTenantID(true)); // select any tenant.
		
		SetAppPulldown(appWithNoDeploys); // select application that has no deployments
		
		SelectDeploymentPulldown();
		//ClickItem("(.//*[@id='sortMenu'])[7]", 3); // select deployment pull-down to open it.
		
		VerifyNoItemsDeployPulldown();
		
		CancelOpenAddRouteUI(false); // close UI.
	}
	
	// this is used as a basic add route test and can also be called by other tests that need a test route added.
	// if 'callFromAnotherTest' is true the test message will not be shown and the row index will saved so the 
	// route can be selected from the routes list.   
	public static void AddRoute(boolean callFromAnotherTest) throws Exception 
	{
		int indexLocator = 0;
		
		if(!callFromAnotherTest) // this is here because this method can be called to add a route from some other tests.
		{
			ShowCurrentTest("Routes: AddRoute");			
		}
		
		// delete test route if it exists.
		DeleteRouteByKeyFromRow(automationRouteKey);

		SelectAddRoute();
		
		// select correct pull-downs and fill in fields for test route.
		PopulatePulldownsForAddTestRoute();
		ClearRouteTextFields();
		PopulateRouteAddFields(true);
		
		SaveRoutePopup();
		
		// store routes from UI
		listOfActualRoutes.clear();
		ShowActualRoutesOrStore(ActionForApplications.Store);

		// see if added route exist by searching for it in routes list collection.
		indexLocator = TestRouteIsInRoutesList();
		
		if(indexLocator == -1)
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
		
		if(callFromAnotherTest)
		{
			indexToSelectForRoute = ++indexLocator;			
		}
	}
	
	public static void AddValidations() throws Exception 
	{
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
		
		// make sure test application is not on the list.
		//DeleteAppByKeyFromRow(testAppKey); 
		
		SelectAddRoute();
		
		CancelOpenAddRouteUI(false);
		
		WaitForElementVisible(By.xpath("(//button/span[text()='View'])[3]"), 3); // wait for third row in routes list to be visible.
		
		SelectAddRoute();
		
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
	
	public static void GoToRoutes() throws Exception
	{
		CommonMethods.selectItemPlatformDropdown("Routes");
		WaitForPageLoad();
	}	
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	// 															HELPERS 
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	public static void MakeSureUserOnRoutespage() throws Exception
	{
		if(WaitForElementNotVisibleNoThrow(By.xpath("//span[contains(text(),'Add Route')]"), 3))
		{
			GoToRoutes();
		}
	}
	
	// have to go to different pages to test routes. 
	public static void WaitForPageLoad() throws Exception
	{
		Thread.sleep(500);
		WaitForElementVisible(By.xpath("//span[text()='лл']"), 2);
	}
	
	public static void SetupRowIndexesForTestAppTenantDeployment() throws Exception
	{
		boolean foundRow = false;
		
		// need to store tenants and deployments. this comes from the API.
		CreateTenantInformation(); 
		BuildDeploymentsList();
		
		// go to applications page and store applications in UI.
		Applications.GoToApplications(); 
		WaitForPageLoad();
		CommonMethods_AppsRoutes.SetPageSizeToMax();
		
		Applications.listOfActualApps.clear();
		Applications.ShowActualApplicationsOrStore(ActionForApplications.Store); // applications comes from UI.

		// ///////////////////////////////////////////
		// get index of test tenant in tenants list
		// ///////////////////////////////////////////
		for(TenantInfo tInfo : listOfTenantItems)
		{
			if(tInfo.m_key.equals(automationTenant))
			{
				foundRow = true;
				break;
			}
			indexToSelectForTenant++;
		}
		
		if(!foundRow)
		{
			Assert.fail("Did not find index for test tenant");
		}
		
		foundRow = false;
		
		// ////////////////////////////////////////////////////////////
		// get index of test application in application list.
		// ////////////////////////////////////////////////////////////
		for(ApplicationClass appClass : Applications.listOfActualApps)
		{
			if(appClass.m_Key.equals(automationApp))
			{
				foundRow = true;
				break;
			}
			indexToSelectForApp++;
		}		
		
		if(!foundRow)
		{
			Assert.fail("Did not find index for test app");			
		}
		
		foundRow = false;
		
		// ////////////////////////////////////////////////////////////
		// get index of test deployment in deployment list.
		// ////////////////////////////////////////////////////////////
		for(DeployInfo dInfo : listOfMaxDeploymentItems)
		{
			if(dInfo.m_key.equals(automationDeploy))
			{
				foundRow = true;
				break;
			}
			indexToSelectForDeployment++;
		}
		
		if(!foundRow)
		{
			Assert.fail("Did not find index for test deployment");
		}
	}
	
	// this sets the enable and disable for the test application and tenant. the indexToSelectForTenant and indexToSelectForApp were set outside 
	// of this method. throw error if wrong application or tenant is  selected for edit.   
	public static void SetTenantAppEnabledState(boolean tenantState, boolean appState) throws Exception
	{
		// ///////////////////////////////////////////////////////////////////////////
		// go to tenants page, wait for title, and setup enabled for test tenant.
		// ///////////////////////////////////////////////////////////////////////////
		CommonMethods.selectItemPlatformDropdown("Tenants");
		WaitForPageLoad();
		CommonMethods_AppsRoutes.SetPageSizeToMax();
		Thread.sleep(500);
	
		
		SelectEditByRow(indexToSelectForTenant);
		WaitForElementVisible(By.xpath(uiTitle_Locator), 3);
		Assert.assertEquals(GetUiKey(), automationTenant, "The test tenant has not been selected for edit."); // make sure have the test tenant in edit UI.

		if(tenantState)
		{
			driver.findElement(By.xpath("(" + uiRadioButton_Locator + ")[2]")).click();
			driver.findElement(By.xpath("(" + uiRadioButton_Locator + ")[1]")).click();
			ClickItem(saveButtonPoUp_Locator, 2);
		}
		else
		{
			driver.findElement(By.xpath("(" + uiRadioButton_Locator + ")[1]")).click();
			driver.findElement(By.xpath("(" + uiRadioButton_Locator + ")[2]")).click();
			ClickItem(saveButtonPoUp_Locator, 2);
		}

		Thread.sleep(1000); // wait for element not  click-able doesn't work here - 'is not clickable at point (1525, 31). Other element would receive the click'
		
		// //////////////////////////////////////////////////////////////////////////////////
		// go to applications page, wait for title, and setup enabled for test application.
		// //////////////////////////////////////////////////////////////////////////////////
		CommonMethods.selectItemPlatformDropdown("Applications");
		WaitForPageLoad();
		CommonMethods_AppsRoutes.SetPageSizeToMax();
		Thread.sleep(500);
		
		SelectEditByRow(indexToSelectForApp);
		WaitForElementVisible(By.xpath(uiTitle_Locator), 3);
		Assert.assertEquals(GetUiKey(), automationApp, "The test app has not been selected for edit."); // make sure have the test application in edit UI.
		
		if(appState)
		{
			driver.findElement(By.xpath("(" + uiRadioButton_Locator + ")[2]")).click();
			driver.findElement(By.xpath("(" + uiRadioButton_Locator + ")[1]")).click();
			driver.findElement(By.xpath(saveButtonPoUp_Locator)).click();
		}
		else
		{
			driver.findElement(By.xpath("(" + uiRadioButton_Locator + ")[1]")).click();
			driver.findElement(By.xpath("(" + uiRadioButton_Locator + ")[2]")).click();
			driver.findElement(By.xpath(saveButtonPoUp_Locator)).click();
		}
		
		Thread.sleep(1000); // wait for element not click-able doesn't work here - 'is not clickable at point (1525, 31). Other element would receive the click'
	}
	
	public static void SetDeploymentEnabledState(boolean deployState) throws Exception
	{
	
		CommonMethods.selectItemPlatformDropdown("Deployments");
		WaitForPageLoad();
		CommonMethods_AppsRoutes.SetPageSizeToMax();
		Thread.sleep(500);
		
		SelectEditByRow(indexToSelectForDeployment);
		WaitForElementVisible(By.xpath(uiTitle_Locator), 3);
		//Assert.assertEquals(GetUiKey(), automationDeploy, "The test deployment has not been selected for edit."); // make sure have the test deploy edit UI.
		Assert.assertEquals(driver.findElement(By.xpath("(//dl[@class='row']/dd)[1]")).getText(), automationDeploy, "The test deployment has not been selected for edit."); // make sure have the test deploy edit UI.		
		

		
		if(deployState)
		{
			driver.findElement(By.xpath("(" + uiRadioButton_Locator + ")[2]")).click();
			driver.findElement(By.xpath("(" + uiRadioButton_Locator + ")[1]")).click();
			driver.findElement(By.xpath(saveButtonPoUp_Locator)).click();
		}
		else
		{
			driver.findElement(By.xpath("(" + uiRadioButton_Locator + ")[1]")).click();
			driver.findElement(By.xpath("(" + uiRadioButton_Locator + ")[2]")).click();
			driver.findElement(By.xpath(saveButtonPoUp_Locator)).click();
		}
		
		Thread.sleep(1000); // wait for element not click-able doesn't work here - 'is not clickable at point (1525, 31). Other element would receive the click'	
	}
	
	public static String GetUiKey()
	{
		return driver.findElement(By.cssSelector(".col-sm-11")).getText();
	}
	
	// select deployment pull-down.
	public static void SelectDeploymentPulldown()
	{
		ClickItem("(" + pullDown_Locator + ")[7]", 3); 
	}
	
	public static void SetTestTenantEnableDisable(int index, boolean state)
	{
		SelectEditByRow(index);
	}

	// select the edit button in the the row passed in (1 based).
	public static void SelectEditByRow(int row)
	{
		ClickItem("(//button[@class='btn btn-primary btn-sm'])" + "[" + row + "]", 3);
	}
	
	// this is used to get what's in the UI, find a route that was supposed to be added, and verify some of the added route values.
	// this is used where testing of route enabled, allow services calls, and disabled reason combinations are tested.
	public static int StoreUI_WithPartialVerification() throws Exception
	{
		int indexLocator = -1;
		
		// clear actual routes list, store current routes in the UI, and get index of added route. 
		listOfActualRoutes.clear();
		ShowActualRoutesOrStore(ActionForApplications.Store);
		indexLocator = TestRouteIsInRoutesList();
		
		if(indexLocator == -1) // error if added route not found in routes list.
		{
			Assert.fail("Route that was added is not found in 'ValidatePrePopulatedItemsAndEdits_PartTwo'.");
		}		
		
		// verify route is in list.
		VerifyRoutePartial(indexLocator);
		
		return indexLocator;
	}
	
	// this sets up pull-downs and populates some fields in the route add pop-up with test variables. the enabled, allow service, and disabled reason 
	// are set according to what the user passes in.
	public static void SetupForAppRouteEnabledAndServiceCall(boolean enabledState,  boolean serviceCallState, DisabledReason dReason) throws Exception
	{
		// delete test route and select add.
		DeleteRouteByKeyFromRow(automationRouteKey);
		SelectAddRoute();
		
		// select correct pull-downs and fill in fields for test route.
		PopulatePulldownsForAddTestRoute();
		ClearRouteTextFields();
		PopulateRouteAddFields(false);
		
		if(enabledState)
		{
			driver.findElement(By.xpath("(//input[@formcontrolname='enabled'])[1]")).click();
		}
		else 
		{
			driver.findElement(By.xpath("(//input[@formcontrolname='enabled'])[2]")).click();			
		}
		
		if(serviceCallState)
		{
			driver.findElement(By.xpath("(//input[@formcontrolname='allowServiceCalls'])[1]")).click();			
		}
		else
		{
			driver.findElement(By.xpath("(//input[@formcontrolname='allowServiceCalls'])[2]")).click();			
		}
		
		if(!enabledState)
		{
			switch(dReason)
			{
				case unknown:
				{
					new Select(driver.findElement(By.xpath("//select[@formcontrolname='disabledReason']"))).selectByIndex(0);
					break;
				}
				case upgrading:
				{
					new Select(driver.findElement(By.xpath("//select[@formcontrolname='disabledReason']"))).selectByVisibleText("Upgrading");
					break;
				}
				case onboarding:
				{
					new Select(driver.findElement(By.xpath("//select[@formcontrolname='disabledReason']"))).selectByVisibleText("On boarding");
					break;
				}
				case offboarding:
				{
					new Select(driver.findElement(By.xpath("//select[@formcontrolname='disabledReason']"))).selectByVisibleText("Off boarding");
					break;
				}				
				case maintenance:
				{
					new Select(driver.findElement(By.xpath("//select[@formcontrolname='disabledReason']"))).selectByVisibleText("Maintenance");
					break;
				}
				case noselect:
				{
					break;
				}
				default:
				{
					Assert.fail("Method 'SetupForAppRouteEnabledAndServiceCall' has been sent a bad enumerated type.");
				}
			}				
		}
	}
	
	// verify some of the items in a test route that's in the route list.
	public static void VerifyRoutePartial(int indexLocator)
	{
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_Key, automationRouteKey);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_tenantId, automationTenantID);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_tennantKey, automationTenant);
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_appKey, automationApp);		
		Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_deployKey, automationDeploy);
		//Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_allowServiceCalls, automationAllowServiceCalls);		
		//Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_enabled, automationEnabled);
		//Assert.assertEquals(listOfActualRoutes.get(indexLocator).m_disabledReason, automationDisabledReason);		
	}
	
	// see of test route is in routes list and return index.
	public static int TestRouteIsInRoutesList() throws Exception
	{
		boolean foundAddedRoute = false;
		int indexLocator = 0;
		
		// see if added route exist by searching for it in UI.
		for(RouteClass rtClass: listOfActualRoutes)
		{
			if((rtClass.m_host + rtClass.m_path).equals(automationfullPath.toLowerCase()))
			{
				foundAddedRoute = true;
				break;
			}
			indexLocator++;
		}
		
		if(!foundAddedRoute)
		{
			return -1;
		}
		else
		{
			return indexLocator;
		}
	}
	
	
	public static void SaveRoutePopup() throws Exception
	{
		driver.findElement(By.xpath(saveButtonPoUp_Locator)).click();
		WaitForElementNotVisibleNoThrow(By.xpath(saveButtonPoUp_Locator) ,3); 
	}
	
	
	// fill in fields in add route UI with test route values. disabled reason is optional.
	public static void PopulateRouteAddFields(boolean includeDisabledreason)
	{
		driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).sendKeys(automationSubDomain);
		driver.findElement(By.xpath(GetXpathForTextBox("domain"))).sendKeys(automationDomain);
		driver.findElement(By.xpath(GetXpathForTextBox("path"))).sendKeys(automationPath);
		driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).sendKeys(automationTenantID);
		driver.findElement(By.xpath("//textarea[@formcontrolname='description']")).sendKeys(automationDescription);		
		driver.findElement(By.xpath(("(//label[@class='radio-inline'])[4]"))).click();
		driver.findElement(By.xpath(("(" + uiRadioButton_Locator + ")[4]"))).click();		
		driver.findElement(By.xpath("(//input[@formcontrolname='enabled'])[2]")).click(); // bladd-- change all of these to 
		
		if(includeDisabledreason)
		{
			new Select(driver.findElement(By.xpath("//select[@formcontrolname='disabledReason']"))).selectByVisibleText("Upgrading");			
		}
	}
	
	// fill in fields in add route UI with data that is not the test route.
	public static void PopulateRouteAddFieldsChangeData(boolean includeDisabledreason)
	{
		String textToAdd = "newData";
		//driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).sendKeys(automationSubDomain + textToAdd);
		//driver.findElement(By.xpath(GetXpathForTextBox("domain"))).sendKeys(automationDomain + textToAdd);
		//driver.findElement(By.xpath(GetXpathForTextBox("path"))).sendKeys(automationPath + textToAdd);
		driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).sendKeys(automationTenantID + textToAdd);
		driver.findElement(By.xpath("//textarea[@formcontrolname='description']")).sendKeys(automationDescription + textToAdd);		
		driver.findElement(By.xpath(("(//label[@class='radio-inline'])[3]"))).click();
		driver.findElement(By.xpath(("(" + uiRadioButton_Locator + ")[3]"))).click();		
		driver.findElement(By.xpath("(//input[@formcontrolname='enabled'])[2]")).click(); // bladd-- change all of these to 
		
		if(includeDisabledreason)
		{
			new Select(driver.findElement(By.xpath("//select[@formcontrolname='disabledReason']"))).selectByVisibleText("On boarding");			
		}
	}
	
	// clear all text fields in route add UI.
	public static void ClearRouteTextFields()
	{
		driver.findElement(By.xpath(GetXpathForTextBox("subdomain"))).clear();
		driver.findElement(By.xpath(GetXpathForTextBox("domain"))).clear();
		driver.findElement(By.xpath(GetXpathForTextBox("path"))).clear();
		driver.findElement(By.xpath(GetXpathForTextBox("tenantID"))).clear();
		driver.findElement(By.xpath("//textarea[@formcontrolname='description']")).clear();		
	}
	
	public static void SelectAddRoute() throws Exception
	{
		// select add and wait for title
		ClickItem(addButton_Locator, 3); // add
		WaitForElementVisible(By.xpath("//strong[text()='Add Route']"), 3); // title
	}
	
	
	// compare tenant key and name in pull-down to expected application key and name.
	public static void VerifyItemsInTenantPulldown()
	{
		int cntr = 0;
		String errTenants = "Error in verifying Tenants pulldown items,";

		// this gets everything in the tenants pull-down.
		List<WebElement> listOfTenantsInPulldown = driver.findElements(By.xpath(("(//div[@class='dropdown-menu'])[5]/div")));
		
		// compare tenant key and name in pull-down to expected tenant key and name. 
		for(WebElement ele: listOfTenantsInPulldown)
		{
			if(ele.getText().length() > 0) // the first item on the list is blank. don't include it
			{
				// compare tenant key in pull-down list to tenant key from UI.
				Assert.assertEquals(CommonMethods_AppsRoutes.SeparateKeyAndName(ele.getText())[0], listOfTenantItems.get(cntr).m_key, errTenants);

				// compare tenant name in pull-down list to tenant key from UI.
				Assert.assertEquals(CommonMethods_AppsRoutes.SeparateKeyAndName(ele.getText())[1], listOfTenantItems.get(cntr).m_name, errTenants);
				
				cntr++;
				if(cntr > listOfTenantsInPulldown.size() || cntr > maxNumberOfItemsInPulldown)
				{
					break;
				}
			}
		}		
	}
	
	// compare application key and name in pull-down to expected application key and name.
	public static void VerifyItemsInAppPulldown()
	{
		int cntr = 0;
		String errApps = "Error in verifying Apps pulldown items,";
		
		// this gets everything in the applications pull-down.
		List<WebElement> listOfAppsInPulldown = driver.findElements(By.xpath(("(//div[@class='dropdown-menu'])[6]/div")));

		// compare tenant key and name in pull-down to expected tenant key and name. 
		for(WebElement ele: listOfAppsInPulldown)
		{
			if(ele.getText().length() > 0) // the first item on the list is blank. don't include it
			{
				// compare application key in pull-down list to application key from UI.
				Assert.assertEquals(CommonMethods_AppsRoutes.SeparateKeyAndName(ele.getText())[0], Applications.listOfActualApps.get(cntr).m_Key, errApps);

				// compare tenant name in pull-down list to tenant key from UI.
				Assert.assertEquals(CommonMethods_AppsRoutes.SeparateKeyAndName(ele.getText())[1], Applications.listOfActualApps.get(cntr).m_Name, errApps);
				
				cntr++;
				if(cntr > listOfAppsInPulldown.size() || cntr > maxNumberOfItemsInPulldown)
				{
					break;
				}
			}
		}
	}
	
	// compare deploy key, application name, and version in deploy pull-down to expected deploy key, application name, and version 
	public static void VerifyItemsInDeployPulldown()
	{
		// get list of items in the deployment drop down.
		List<WebElement> listOfDeploysInPulldown = driver.findElements(By.xpath(("(//div[@class='dropdown-menu'])[7]/div")));
		
		int cntr = 0; 
		String errDeploy = "Failure in verification of deployment pulldown in 'VerifyItemsInDeployPulldown'.";
		
		// compare   
		for(WebElement ele: listOfDeploysInPulldown)
		{
			if(ele.getText().length() > 0) // the first item on the list is blank. don't include it
			{
				// /////////////////////////////////////////////////////////////////////////////////////
				// 				NOTE --- The 'listOfMaxDeploymentItems' was built using the API.
				// /////////////////////////////////////////////////////////////////////////////////////
				
				// compare deployment key in pull-down list to deployment key from 'listOfMaxDeploymentItems'. 
				Assert.assertEquals(CommonMethods_AppsRoutes.GetDeploymentInfoArray(ele.getText())[0] , listOfMaxDeploymentItems.get(cntr).m_key, errDeploy);
				
				// compare deploy application in pull-down list to deploy application from 'listOfMaxDeploymentItems'.
				Assert.assertEquals(CommonMethods_AppsRoutes.GetDeploymentInfoArray(ele.getText())[1] , listOfMaxDeploymentItems.get(cntr).m_appName, errDeploy);
				
				// compare deploy version in pull-down list to deploy version from 'listOfMaxDeploymentItems'.
				Assert.assertEquals(CommonMethods_AppsRoutes.GetDeploymentInfoArray(ele.getText())[2] , listOfMaxDeploymentItems.get(cntr).m_version, errDeploy);
				
				cntr++;
				if(cntr > listOfDeploysInPulldown.size() || cntr > maxNumberOfItemsInPulldown)
				{
					break;
				}
			}
		}
	}
	
	// if list is empty there will be an in list returned.
	public static void VerifyNoItemsDeployPulldown()
	{
		// get list of items in the deployment drop down.
		List<WebElement> listOfDeploysInPulldown = driver.findElements(By.xpath(("(//div[@class='dropdown-menu'])[7]/div")));

		for(WebElement ele: listOfDeploysInPulldown)
		{
			//ShowText("data " + ele.getText());
			Assert.assertTrue(ele.getText().length() == 0);
		}
		// ShowInt(listOfDeploysInPulldown.size());
	}
	
	// depending on 'hasHostAndPath' boolean passed in: 
	// find an application that has host and path variables or find an application that doesn't have host and path variables. return the application key.
	// if a request to find an application that has host and path variables is requested, set class variables 'tempHost' and 'tempPath' to application 
	// path and host.
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
	
	
	// depending on the value of 'hasTenantId' passed in:
	// find a tenant key that has a tenant Id or doesn't have a tenant Id. 
	// if the tenantID is missing for the tenant key, string character "null" is what the tenantID is.
	// return the tenant key for what is being requested.
	public static String GetTenantID(boolean hasTenantId)
	{
		String retStr = "";
		
		boolean foundRequest = false;
		
		for(TenantInfo tInfo : listOfTenantItems)
		{
			if(hasTenantId)
			{
				if(!tInfo.m_tenantID.equals("null"))
				{
					foundRequest = true;
					retStr = tInfo.m_key;
					tempTenantID = tInfo.m_tenantID;
					break;
				}
			}
			else
			{
				if(tInfo.m_tenantID.equals("null"))
				{
					foundRequest = true;
					retStr = tInfo.m_key;
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
	
	// this assumes applications list from UI has been populated. comments below explain.
	// this will find the application with the most deployments.
	public static void FindAppWithTheMostDeploys() throws Exception 
	{
		String url =  deploymentsURL + "?pageSize=300" + "&applicationKey=";
		String apiType = "\"deployments\":";
		int maxNumDeploysFound = 0;
		JSONArray jArray;
		JSONObject jo;
		
		// go through the applications list. for each application see if there are any related deployments using the API deployments
		// APi with application filter. store some deployment information, for the application with the most deployments, onto a list
		// of DeployInfo objects.
		for(ApplicationClass appClass : Applications.listOfActualApps)
		{
			// call deployments on current application.
			jArray = CommonMethods.GetJsonArrayWithUrl(token, url + appClass.m_Key, apiType);
		
			if(jArray.length() >= 1 && jArray.length() > maxNumDeploysFound) 
			{
				listOfMaxDeploymentItems.clear();
				for(int x = 0; x < jArray.length(); x++)
				{
					jo = jArray.getJSONObject(x); // get current json object from the list		
					listOfMaxDeploymentItems.add(new DeployInfo(jo.getString("key"), jo.getString("applicationKey"), jo.getString("version")));
				}
				maxNumDeploysFound = jArray.length();
			}
			
			if(jArray.length() == 0 && appWithNoDeploys.equals("")) // store application name that has no deployments.
			{
				appWithNoDeploys = appClass.m_Key;
			}
		}
	}
	
	// store off deployment by calling API.
	public static void BuildDeploymentsList() throws Exception
	{
		String url =  deploymentsURL + "?pageSize=300";
		String apiType = "\"deployments\":";
		JSONArray jArray;
		JSONObject jo;

		listOfMaxDeploymentItems.clear();
		
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		for(int x = 0; x < jArray.length(); x++)
		{
			jo = jArray.getJSONObject(x); // get current json object from the list		
			listOfMaxDeploymentItems.add(new DeployInfo(jo.getString("key"), jo.getString("applicationKey"), jo.getString("version")));
		}
	}
	
	// store all tenant keys and their corresponding tenantID/name 
	public static void CreateTenantInformation() throws Exception
	{
		JSONArray jArray;
		String url = tenantsURL + "?pageSize=300";
		String apiType = "\"tenants\":";
		int cntr = 0;
		
		jArray = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		// store some tenant info from the API onto listOfTenantItems list.
		for(int x = 0; x < jArray.length(); x++)
		{
			JSONObject jo = jArray.getJSONObject(x); // get current json object from the list  

			if(CommonMethods.GetNonRequiredItem(jo, "defaultTenantID").equals(""))
			{
				//listOfTenantItems.add(jo.getString("key") + charSeparatorForTenantItems + "null" + charSeparatorForTenantItems + jo.getString("name"));
				listOfTenantItems.add(new CommonMethods_AppsRoutes.TenantInfo(jo.getString("key"), jo.getString("name"), "null", jo.getBoolean("enabled")));
			}
			else
			{
				//listOfTenantItems.add(jo.getString("key") + charSeparatorForTenantItems + jo.getString("defaultTenantID") + charSeparatorForTenantItems + jo.getString("name"));
				listOfTenantItems.add(new CommonMethods_AppsRoutes.TenantInfo(jo.getString("key"), jo.getString("name"), jo.getString("defaultTenantID"), jo.getBoolean("enabled")));
			}
		}		

		// for(TenantInfo tInfo : listOfTenantItems){tInfo.Show();}		
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

	public static void PopulatePulldownsForAddTestRoute() throws Exception
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
		// ClickItem("(.//*[@id='sortMenu'])[7]", 3);
		SelectDeploymentPulldown();
		
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
		
		// ClickItem("(//span[text()='" + tenantName  + "']/../..)[2]", 3);
		ClickItem("(//div[@class='dropdown-menu'])[5]/div[2]", 2); // this seems more consistent with different selections.
	}
	
	public static void SetAppPulldown(String appName)
	{
		// application
		ClickItem("(.//*[@id='sortMenu'])[6]", 3);
		
		// send search text.		
		WaitForElementClickable(By.xpath("(//input[@placeholder='Search...'])[5]"), 3, "");
		driver.findElement(By.xpath("(//input[@placeholder='Search...'])[5]")).sendKeys(appName);

		// ClickItem("(//div[@class='dropdown-menu'])[6]/div[2]", 2);
		ClickItem("(//div[@class='dropdown-menu'])[6]/div[2]", 2); // this seems more consistent with different selections.
	}
	
	public static void SetDeployPulldown(String appName)
	{
		// application
		// ClickItem("(.//*[@id='sortMenu'])[7]", 3);
		SelectDeploymentPulldown();
		
		// send search text.		
		WaitForElementClickable(By.xpath("(//input[@placeholder='Search...'])[6]"), 3, "");
		driver.findElement(By.xpath("(//input[@placeholder='Search...'])[6]")).sendKeys(appName);

		// ClickItem("(//div[@class='dropdown-menu'])[6]/div[2]", 2);
		ClickItem("(//div[@class='dropdown-menu'])[7]/div[2]", 2); // this seems more consistent with different selections.
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
			SelectAddRoute();
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

		// 5/23/18
		// this is used to decide whether to select the first page when the loop is done. if the loop stays on only one page, this
		// boolean stays false and selecting the first page is not needed. selecting the first page when on the first page gave selenium
		// locator error. tried looking for isEnabled in selector but isEnabled is true, even when on the first page.
		boolean haveSelectedNextPage = false;  
		
		for(int x = 0; x < numberOfPages; x++) // go through each page.
		{
			// this stores API info for page x + 1  into listOfExpectedApps 
			PassDataAndStoreApiRequest(apiType, pageSize, x + 1, sortDirection, sortBy);
			
			// store the application info in UI to listOfActualApps
			ShowActualRoutesOrStore(ActionForApplications.Store);

			// verify actual and expected are equal.
			VerifyRouteCollectionsExpectedAndActual();
			
			if(x == (numberOfPages - 1)) // last page has been verified - exit.
			{
				break;
			}
			
			CommonMethods_AppsRoutes.SelectNextPage();
			haveSelectedNextPage = true;
			Thread.sleep(1000);
		}

		if(haveSelectedNextPage)
		{
			CommonMethods_AppsRoutes.SelectFirstPage();			
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
