package pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import baseItems.BaseMain;
import classes.Tenant;
import common.CommonMethods;


public class Tenants extends BaseMain
{

	private static String[] propertiesNames = {"Key", "Name", "Default Tenant ID", "Enabled"};
	
	// xpaths 
	
	private static String xpathKey = "//input[@formcontrolname='key']";
	private static String xpathName = "//input[@formcontrolname='name']";
	private static String xpathDefTenantId = "//input[@formcontrolname='defaultTenantID']";
	private static String xpathEnabled = "//label[1]/input[@formcontrolname='enabled']";
	private static String xpathDisabled = "//label[2]/input[@formcontrolname='enabled']";
	
	
	public static void verifyColumnsNames() {
		
		List<WebElement> columnNames = driver.findElements(By.xpath("//div[@class='table-responsive']/table/thead/tr/th/span[1]"));
		
		for (int i = 0; i < columnNames.size(); i++) {
			
			System.out.println(columnNames.get(i).getText());
			Assert.assertEquals(propertiesNames[i], columnNames.get(i).getText());
		
		}
		
	}
	
	
	// It verifies that the data on the UI matches the data from the API
	public static void verifyDataFromUIMatchesAPI(int page, int pageSize) throws IOException, JSONException {
		
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/tenants";
		String apiType = "\"" + "tenants" + "\"" + ":";
				
		String queryParameters = "?page=" + page + "&pageSize=" + pageSize;
		
		JSONArray jsonArrayTenants = CommonMethods.GetJsonArrayWithUrl(token, url + queryParameters, apiType);
		
		List<Tenant> actualTenantsList = CommonMethods.putJsonArrayTenantsIntoList(jsonArrayTenants);
		List<Tenant> expectedTenantsList = addTenantsFromUItoList();
		
		for (int i = 0; i < actualTenantsList.size(); i++) {
			
			Tenant actual = actualTenantsList.get(i);
			Tenant expected = expectedTenantsList.get(i);
			
			/*
			System.out.println(i+1);
			System.out.println("Actual Key: " + actual.getKey() + ", Expected Key: " + expected.getKey());
			System.out.println("Actual Name: " + actual.getName() + ", Expected Name: " + expected.getName());
			System.out.println("Actual Default Tenant ID: " + actual.getDefaultTenantID() + ", Expected Default Tenant ID: " + expected.getDefaultTenantID());
			System.out.println("Actual isEnabled: " + actual.isEnabled() + ", Expected isEnabled: " + expected.isEnabled());
			*/
			
			Assert.assertEquals(actual.getKey(), expected.getKey());
			Assert.assertEquals(actual.getName(), expected.getName());
			Assert.assertEquals(actual.getDefaultTenantID(), expected.getDefaultTenantID());
			Assert.assertEquals(actual.isEnabled(), expected.isEnabled());
			
		}
		
		
	}

	// Methods added to Common MEthods ---- To be removed ********
	
	/*
	// Puts the JSON response obtained from the API GET /tenants into a list.
	public static List<Tenant> putJsonArrayTenantsIntoList(JSONArray jsonArrayTenants) throws JSONException {
		
		List<Tenant> actualTenantsList = new ArrayList<>();
		
		// Put the deployments in the json array into a list of tenants
		for (int i = 0; i < jsonArrayTenants.length(); i++) {
			
			JSONObject jo = jsonArrayTenants.getJSONObject(i);
			Tenant tenant = new Tenant();
			
						
			System.out.print(i+1 + "  Key: " + jo.getString("key"));
			System.out.print(", Name: " + jo.getString("name"));
			System.out.print(", Default Tenant ID: " + GetNonRequiredItem(jo, "defaultTenantID")
			System.out.println(", Enabled: " + jo.getBoolean("enabled"));
			
			
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
			
						
			System.out.print(i+1 + "  Key: " + jo.getString("key"));
			System.out.print(", Name: " + jo.getString("name"));
			System.out.print(", Default Tenant ID: " + GetNonRequiredItem(jo, "defaultTenantID")
			System.out.println(", Enabled: " + jo.getBoolean("enabled"));
			
			
			applicationList.add(app);
			
		}
		
		return applicationList;
			
	}
	
	
	// Puts the JSON response obtained from the API GET /deployments into a list.
	private static List<Deployment> putJsonArrayDeploymentsIntoList(JSONArray jsonArrayDeps) throws JSONException {
		
		List<Deployment> deploymentList = new ArrayList<>();
		
		// Put the deployments in the json array into a list of deployments
		for (int i = 0; i < jsonArrayDeps.length(); i++) {
			
			JSONObject jo = jsonArrayDeps.getJSONObject(i);
			Deployment dep = new Deployment(jo.getString("key"), "", jo.getString("version"), "", jo.getBoolean("enabled"));
			
					
			System.out.print(i+1 + "  Key: " + jo.getString("key"));
			System.out.print(", Version: " + jo.getString("version"));
			System.out.println(", Enabled: " + jo.getBoolean("enabled"));
			
			
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
			RouteClass route = new RouteClass(jo.getString("key"), "", "", "", "", "", "", "", "", jo.getBoolean("enabled"), "", true, "", "");
			
						
			System.out.print(i+1 + "  Key: " + jo.getString("key"));
			
			System.out.println(", Enabled: " + jo.getBoolean("enabled"));
			
			
			routeList.add(route);
			
		}
		
		return routeList;
	}
*/

	// Puts the JSON response obtained from the API GET /tenants/{id} into an object.
	private static Tenant putJsonObjectIntoTenantObject(JSONObject jsonObject) throws JSONException {
		
		Tenant tenant = new Tenant();
		
		/*			
		System.out.print(i+1 + "  Key: " + jo.getString("key"));
		System.out.print(", Name: " + jo.getString("name"));
		System.out.print(", Default Tenant ID: " + GetNonRequiredItem(jo, "defaultTenantID")
		System.out.println(", Enabled: " + jo.getBoolean("enabled"));
		*/
		
		tenant.setKey(jsonObject.getString("key"));
		tenant.setName(jsonObject.getString("name"));
		tenant.setDefaultTenantID(CommonMethods.GetNonRequiredItem(jsonObject, "defaultTenantID"));
		tenant.setEnabled(jsonObject.getBoolean("enabled"));
		
		try { 
			tenant.setApplicationCount(jsonObject.getJSONObject("statistics").getInt("applicationCount")); 
		}
		catch (NoSuchElementException e) { }
		
		try { 
			tenant.setDeploymentCount(jsonObject.getJSONObject("statistics").getInt("deploymentCount")); 
		}
		catch (NoSuchElementException e) { }
		
		try { 
			tenant.setRouteCount(jsonObject.getJSONObject("statistics").getInt("routeCount")); 
		}
		catch (NoSuchElementException e) { }
		
		return tenant;
			
	}
		
	

	// Adds the tenants that are listed on the UI to a list
	private static List<Tenant> addTenantsFromUItoList() {

		List<Tenant> expectedTenantsList = new ArrayList<>();
		List<WebElement> row = new ArrayList<>();
		
		int rowTotalCount = driver.findElements(By.xpath("//div[@class='table-responsive']/table/tbody/tr")).size();
		
		// System.out.println("# rows: " + rowTotalCount);
				
		for (int i = 1; i <= rowTotalCount; i++) {
			
			row = driver.findElements(By.xpath("//div[@class='table-responsive']/table/tbody/tr[" + i + "]/td"));
			
			Tenant tenant = new Tenant();
			
			
			System.out.print(i + "  Key: " + row.get(0).getText());
			System.out.print(", Name: " + row.get(1).getText());
			System.out.print(", Default Tenant ID: " + row.get(2).getText());
			System.out.println(", Enabled: " + row.get(3).getText());
			
			
			tenant.setKey(row.get(0).getText());
			tenant.setName(row.get(1).getText());
			tenant.setDefaultTenantID(row.get(2).getText());
			/*if (row.get(2).getText().isEmpty()) { tenant.setDefaultTenantID(" "); }
			else { tenant.setDefaultTenantID(row.get(2).getText()); } */
			tenant.setEnabled(CommonMethods.convertToBoolean(row.get(3).getText()));
					
			expectedTenantsList.add(tenant);
			
		}
		
		return expectedTenantsList;
		
	}


	// Verify that the list of tenants is sorted by Name, Default Tenant ID, Enabled and Key, in Asc and Desc order
	public static void verifyListSorted() throws InterruptedException {		
		
		System.out.println("\n  ** Sort List by Name in Ascending Order **");
		
		CommonMethods.clickArrowSorting("Name", "ASC");
				
		verifySortingObjects("NAME", "ASC");
		
		System.out.println("\n  ** Sort List by Name in Descending Order **");
		
		CommonMethods.clickArrowSorting("Name", "DESC");
				
		verifySortingObjects("NAME", "DESC");
	
		System.out.println("\n  ** Sort List by Default Tenant ID in Ascending Order **");
		
		CommonMethods.clickArrowSorting("Default Tenant ID", "ASC");
		
		verifySortingObjects("DEFAULT_TENANT_ID", "ASC");
		
		System.out.println("\n  ** Sort List by Default Tenant ID in Descending Order **");
		
		CommonMethods.clickArrowSorting("Default Tenant ID", "DESC");
				
		verifySortingObjects("DEFAULT_TENANT_ID", "DESC");

		System.out.println("\n  ** Sort List by Enabled in Ascending Order **");
		
		CommonMethods.clickArrowSorting("Enabled", "ASC");
				
		verifySortingObjects("IS_ENABLED", "ASC");
		
		System.out.println("\n  ** Sort List by Enabled in Descending Order **");
		
		CommonMethods.clickArrowSorting("Enabled", "DESC");
				
		verifySortingObjects("IS_ENABLED", "DESC");
		
		System.out.println("\n  ** Sort List by Key in Ascending Order **");
		
		CommonMethods.clickArrowSorting("Key", "ASC");
				
		verifySortingObjects("KEY", "ASC");
		
		System.out.println("\n  ** Sort List by Key in Descending Order **");
		
		CommonMethods.clickArrowSorting("Key", "DESC");
		
		verifySortingObjects("KEY", "DESC");
	
		CommonMethods.clickArrowSorting("Key", "ASC");
		
	}
	
	
	// Verify that list of tenants is sorted according to the sortBy and sortDirection parameters. 
	// This method sorts the list of tenants (objects), not only the String representing the sortBy criteria
	public static void verifySortingObjects(String sortBy, String sortDirection) {
		
		List<Tenant> tenantsListFromUI = addTenantsFromUItoList();
		List<Tenant> tenantsListSorted = new ArrayList<>();
		
		for (int i = 0; i < tenantsListFromUI.size(); i++) {
			
			tenantsListSorted.add(tenantsListFromUI.get(i));
			
		}
		
		
		// Trying different approach to do sorting --- March 22
		// Using Comparator so the list with the tenants is sorted, 
		// and not only the property to be sorted by is sorted 
		if (sortBy.equals("KEY") && sortDirection.equals("ASC")) {
			
			Collections.sort(tenantsListSorted, Tenant.keyComparatorAsc);			
			
		} else if (sortBy.equals("KEY") && sortDirection.equals("DESC")) {
			
			Collections.sort(tenantsListSorted, Tenant.keyComparatorDesc);
			
		} else if (sortBy.equals("NAME") && sortDirection.equals("ASC")) {
			
			Collections.sort(tenantsListSorted, Tenant.nameComparatorAsc);
			
		} else if (sortBy.equals("NAME") && sortDirection.equals("DESC")) {
			
			Collections.sort(tenantsListSorted, Tenant.nameComparatorDesc);
			
		} else if (sortBy.equals("DEFAULT_TENANT_ID") && sortDirection.equals("ASC")) {
			
			Collections.sort(tenantsListSorted, Tenant.tenantIDComparatorAsc);
			
		} else if (sortBy.equals("DEFAULT_TENANT_ID") && sortDirection.equals("DESC")) {
			
			Collections.sort(tenantsListSorted, Tenant.tenantIDComparatorDesc);
			
		} else if (sortBy.equals("IS_ENABLED") && sortDirection.equals("ASC")) {
		
			Collections.sort(tenantsListSorted, Tenant.enabledComparatorAsc);
			
		} else if (sortBy.equals("IS_ENABLED") && sortDirection.equals("DESC")) {
		
			Collections.sort(tenantsListSorted, Tenant.enabledComparatorDesc);
			
		}
		
		System.out.println("Sorted List Expected");
		
		for (int i = 0; i < tenantsListSorted.size(); i++) {
			
			System.out.print(i + "  Key: " + tenantsListSorted.get(i).getKey());
			System.out.print(", Name: " + tenantsListSorted.get(i).getName());
			System.out.print(", Default Tenant ID: " + tenantsListSorted.get(i).getDefaultTenantID());
			System.out.println(", Enabled: " + tenantsListSorted.get(i).isEnabled());
			
		}
		
		System.out.println("Sorted List Actual - from UI");
		
		for (int i = 0; i < tenantsListFromUI.size(); i++) {
			
			System.out.print(i + "  Key: " + tenantsListFromUI.get(i).getKey());
			System.out.print(", Name: " + tenantsListFromUI.get(i).getName());
			System.out.print(", Default Tenant ID: " + tenantsListFromUI.get(i).getDefaultTenantID());
			System.out.println(", Enabled: " + tenantsListFromUI.get(i).isEnabled());
			
		}
		
		// ****************************************************************
		
		List<Tenant> sortedListActualTmp = new ArrayList<Tenant>();
		List<Tenant> sortedListExpectedTmp = new ArrayList<Tenant>();
		
			
		System.out.println("Sorted List Expected");
		
		for (int i = 0; i < tenantsListSorted.size(); i++) {
			
			System.out.println("  * " + tenantsListSorted.get(i).getKey());
			
		}
		
		// *** Get the Expected Sorted List with no empty values ** IN ORDER TO GET RID OF THE PROBLEM WITH THE SORTING WHEN THERE ARE NULL/EMPTY STRINGS **
		if (sortBy.equals("DEFAULT_TENANT_ID")) {
		
			for (int i = 0; i < tenantsListSorted.size(); i++) {
				
				if (tenantsListSorted.get(i).getDefaultTenantID().length() > 0) {	
				
					sortedListExpectedTmp.add(tenantsListSorted.get(i));
				}
							
			} 
		
			tenantsListSorted = sortedListExpectedTmp;
			
		}
		
				
		System.out.println("Sorted List Actual");
		
		for (int i = 0; i < tenantsListFromUI.size(); i++) {
			
			System.out.println("  * " + tenantsListFromUI.get(i).getKey());
			
		}
		
		// *** Get the Actual Sorted List with no empty values ** IN ORDER TO GET RID OF THE PROBLEM WITH THE SORTING WHEN THERE ARE NULL/EMPTY STRINGS **
		if (sortBy.equals("DEFAULT_TENANT_ID")) {
			
			for (int i = 0; i < tenantsListFromUI.size(); i++) {
				
				if (tenantsListFromUI.get(i).getDefaultTenantID().length() > 0) {	
				
					sortedListActualTmp.add(tenantsListFromUI.get(i));
				}
				
			} 
			
			tenantsListFromUI = sortedListActualTmp;
		
		}
		
		Assert.assertEquals(tenantsListFromUI, tenantsListSorted);	
		
	}
	
	
	// Verify that list of tenants is sorted according to the sortBy and sortDirection parameters. 
	public static void verifySorting(String sortBy, String sortDirection) {
		
		List<Tenant> tenantsList = addTenantsFromUItoList();
		List<String> sortedListActual = new ArrayList<String>();
		List<String> sortedListExpected = new ArrayList<String>();
		
			
		for (int i = 0; i < tenantsList.size(); i++) {
			
			if (sortBy.equals("KEY")) {
				
				sortedListActual.add(tenantsList.get(i).getKey());
				sortedListExpected.add(tenantsList.get(i).getKey());
				
			} else if (sortBy.equals("NAME")) {
				
				sortedListActual.add(tenantsList.get(i).getName());
				sortedListExpected.add(tenantsList.get(i).getName());
				
			} else if (sortBy.equals("DEFAULT_TENANT_ID")) {
				
				sortedListActual.add(tenantsList.get(i).getDefaultTenantID());
				sortedListExpected.add(tenantsList.get(i).getDefaultTenantID());
				
			} else if (sortBy.equals("IS_ENABLED")) {
			
				sortedListActual.add(Boolean.toString(tenantsList.get(i).isEnabled()));
				sortedListExpected.add(Boolean.toString(tenantsList.get(i).isEnabled()));
				
			}
			
		}
		
		
		List<String> sortedListActualTmp = new ArrayList<String>();
		List<String> sortedListExpectedTmp = new ArrayList<String>();
		
		Collections.sort(sortedListExpected, String.CASE_INSENSITIVE_ORDER);
				
		if (sortDirection.equals("DESC")) Collections.reverse(sortedListExpected); 
		
		System.out.println("Sorted List Expected");
		
		for (int i = 0; i < sortedListExpected.size(); i++) {
			
			System.out.println("  * " + sortedListExpected.get(i));
			
		}
		
		// *** Get the Expected Sorted List with no empty values ** IN ORDER TO GET RID OF THE PROBLEM WITH THE SORTING WHEN THERE ARE NULL/EMPTY STRINGS **
		for (int i = 0; i < sortedListExpected.size(); i++) {
			
			if (sortedListExpected.get(i).length() > 0) {	
			
				sortedListExpectedTmp.add(sortedListExpected.get(i));
			}
						
		} 
		
		sortedListExpected = sortedListExpectedTmp;
				
		
		System.out.println("Sorted List Actual");
		
		for (int i = 0; i < sortedListActual.size(); i++) {
			
			System.out.println("  * " + sortedListActual.get(i));
			
		}
		
		// *** Get the Actual Sorted List with no empty values ** IN ORDER TO GET RID OF THE PROBLEM WITH THE SORTING WHEN THERE ARE NULL/EMPTY STRINGS **
		for (int i = 0; i < sortedListActual.size(); i++) {
			
			if (sortedListActual.get(i).length() > 0) {	
			
				sortedListActualTmp.add(sortedListActual.get(i));
			}
			
		} 
		
		sortedListActual = sortedListActualTmp;
		
		Assert.assertEquals(sortedListActual, sortedListExpected);
		
	}
	

	public static void verifyDataAndSorting() throws InterruptedException, IOException, JSONException {

		// Get all the "sizes" into a list
		
		List<WebElement> listSizesElements = CommonMethods.getSizesOfPages();  //driver.findElements(By.xpath("//div/span[text()='Size: ']/following-sibling::span/label/input"));
		
		
		for (int i = 0; i < listSizesElements.size(); i++) {
			
			int pageSize = Integer.parseInt(listSizesElements.get(i).getAttribute("value"));
			
			CommonMethods.selectSizeOfList(pageSize);
			
			Thread.sleep(2000);  // -- see if it can be changed to waitfor.... 
			
			verifyDataFromUIMatchesAPI(1, pageSize);
			
			Thread.sleep(2000);
			
			// ** T-960:Tenants list can be sorted
			verifyListSorted();
			
		}	
		
	}

	// not used -- to be removed ***

	//public static void verifyPaging() throws InterruptedException, IOException, JSONException {
		
		
		/*
		 * 1- Select a size -- repeat for each size listed (5 / 10 / 20 / 50)
		 * 2- Obtain the json for that pageSize and page number
		 * 3- Obtain the list displayed on UI
		 * 4- Compare lists
		 * 5- if pageSize < totalCount {
		 * 	click next page.
		 * 	go to step 2 
		 * }
		 * 6- loop --> step 1
		 * 
		 * 
		 * 
		 * 	
		 * */
		/*
		// Get all the "sizes" into a list
		
		List<WebElement> listSizesElements = CommonMethods.getSizesOfPages(); // xpath("//div/span[text()='Size: ']/following-sibling::span/label/input"));
		
		System.out.println("listSizesElements.size(): " + listSizesElements.size());
		
		for (int i = 0; i < listSizesElements.size(); i++) {
			
			System.out.println("listSizesElements " + i + ": " + listSizesElements.get(i).getAttribute("value"));
			
		}
		
		String totalCountItems = CommonMethods.getTotalCountItems();
		
		int index = totalCountItems.indexOf("of");
		totalCountItems = totalCountItems.substring(index).replace("of", "").replace("items.", "").trim();
				
		int totalCount = Integer.parseInt(totalCountItems);
		
		System.out.println("totalCount: " + totalCount);		
		
		
		// Verify data for each pageSize  (5 / 10 / 20 / 50)
		
		for (int i = 0; i < listSizesElements.size(); i++) {
			
			
			// remove --System.out.println("pageSize = " + listSizesElements.get(i).getAttribute("value"));
			
			int pageSize = Integer.parseInt(listSizesElements.get(i).getAttribute("value"));
			
			System.out.println("pageSize = " + pageSize);
			
			CommonMethods.selectSizeOfList(pageSize);
			Thread.sleep(2000);  // -- see if it can be changed to waitfor....
			
			int totalPages = 1;
			
			if ((totalCount % pageSize) > 0)
				totalPages = (totalCount / pageSize) + 1;
			
			if ((totalCount % pageSize) == 0)
				totalPages = totalCount / pageSize;
			
			// Go through all the pages and verify data on each page
			
			for (int page = 1; page <= totalPages; page++) {
			
				CommonMethods.clickPageNumber(page);
				
				System.out.println("page = " + page);
				verifyDataFromUIMatchesAPI(page, pageSize);
				
			}
			
				
		}
			
		
	}
	*/


	public static void verifyPagingNew() throws InterruptedException, IOException, JSONException {
		
		
		/*
		 * 1- Obtain the json for the complete list of tenants
		 * 2- Select a size -- repeat for each size listed (5 / 10 / 20 / 50)
		 * 3- Obtain the list displayed on UI
		 * 4- Compare the tenants listed on the UI to the tenants in the complete list 
		 * 5- if pageSize < totalCount {
		 * 		click next page.
		 * 		go to step 2 
		 * 	}
		 * 6- loop --> step 1
		 * 
		 * 
		 * 
		 * 	
		 * */
		// Get all the "sizes" into a list
		
		List<WebElement> listSizesElements = CommonMethods.getSizesOfPages(); //driver.findElements(By.xpath("//div/span[text()='Size: ']/following-sibling::span/label/input"));
		
		String totalCountItems = CommonMethods.getTotalCountItems(); // driver.findElement(By.xpath("//div/jhi-item-count")).getText();
		
		int index = totalCountItems.indexOf("of");
		totalCountItems = totalCountItems.substring(index).replace("of", "").replace("items.", "").trim();
				
		int totalCount = Integer.parseInt(totalCountItems);
		
		System.out.println("totalCount: " + totalCount);		
		
		
		// Verify data for each pageSize  (5 / 10 / 20 / 50)
		
		for (int i = 0; i < listSizesElements.size(); i++) {
			
			int pageSize = Integer.parseInt(listSizesElements.get(i).getAttribute("value"));
			
			System.out.println("pageSize = " + pageSize);
			
			CommonMethods.selectSizeOfList(pageSize);
			Thread.sleep(2000);  // -- see if it can be changed to waitfor....
			
			int totalPages = 1;
			
			if ((totalCount % pageSize) > 0)
				totalPages = (totalCount / pageSize) + 1;
			
			if ((totalCount % pageSize) == 0)
				totalPages = totalCount / pageSize;
			
			// Go through all the pages and verify data on each page
			
			for (int page = 1; page <= totalPages; page++) {
			
				CommonMethods.clickPageNumber(page);
				
				System.out.println("page = " + page);
				verifyDataNew(page, pageSize, totalCount);
				
			}
			
				
		}
			
		
	}

	
	// It verifies that the data on the UI matches the data from the API
	public static void verifyDataNew(int page, int pageSize, int totalCount) throws IOException, JSONException {
		
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/tenants";
		String apiType = "\"" + "tenants" + "\"" + ":";
				
		String queryParameters = "?page=1&pageSize=" + totalCount;
		
		JSONArray jsonArrayTenants = CommonMethods.GetJsonArrayWithUrl(token, url + queryParameters, apiType);
		
		List<Tenant> actualTenantsList = CommonMethods.putJsonArrayTenantsIntoList(jsonArrayTenants);
		List<Tenant> expectedTenantsList = addTenantsFromUItoList();
		
		int indexStart = (page - 1) * pageSize;
		int indexEnd = indexStart + pageSize - 1;
		
		if (indexEnd >= totalCount) indexEnd = totalCount - 1;
		
		int index = 0;
		
		for (int i = indexStart; i <= indexEnd; i++) {
			
			Tenant actual = actualTenantsList.get(i);
			Tenant expected = expectedTenantsList.get(index);
			
			/*System.out.println(i+1);
			System.out.println("Actual Key: " + actual.getKey() + ", Expected Key: " + expected.getKey());
			System.out.println("Actual Name: " + actual.getName() + ", Expected Name: " + expected.getName());
			System.out.println("Actual isEnabled: " + actual.isEnabled() + ", Expected isEnabled: " + expected.isEnabled());
			*/
			
			Assert.assertEquals(actual.getKey(), expected.getKey());
			Assert.assertEquals(actual.getName(), expected.getName());
			Assert.assertEquals(actual.isEnabled(), expected.isEnabled());
			
			index++;
			
		}
		
		
	}


	
	public static void verifyFiltering(String applicationKey, String deploymentKey, String enabled) throws Exception {
		
		/*
		 * 1. Filter tenant by Application | Deployment | Enabled 
		 * 2. Get results from UI
		 * 3. Get tenants filtered by the same application | deployment | enabled from API
		 * 4. Compare results 
		 * */ 
		
		String[] filterToSelect = {"All Applications", "All Deployments", "Show Enabled and Disabled"};
		
		
		HashMap<String, String> filterNameMap = new HashMap<String, String>();
		
		filterNameMap.put("All Applications", "applicationKey");
		filterNameMap.put("All Deployments", "deploymentKey");
		filterNameMap.put("Show Enabled and Disabled", "enabled");
		
		String enabledValueForRequest = getEnabledValueForRequest(enabled);
		
		String[] filterValue = {applicationKey, deploymentKey, enabled};
		String[] queryParameterValues = {applicationKey, deploymentKey, enabledValueForRequest};
		
		int page = 1;
		int pageSize = 10;
			
		String queryParametersPagePortion = "?page=" + page + "&pageSize=" + pageSize;
		
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/tenants";
		String apiType = "\"" + "tenants" + "\"" + ":";
		
		
		for (int i = 0; i < filterNameMap.size(); i++) {

			String filter = filterToSelect[i];
			String value = filterValue[i];

			System.out.println("  Filter to select: " + filter + " by value: " + value);
			
			// 1.a. Click Application filter dropdown list
			CommonMethods.clickFilterDropdown(filter);
								
			// 1.b. Enter App Key / Name on the Search field
			CommonMethods.enterSearchCriteria(filter, value);
			
			
			// 2. 
			List<Tenant> filteredListOpsConsole = addTenantsFromUItoList();
			
			
			// 3.			
			String filterBy = filterNameMap.get(filter);
			String queryParameterValue = queryParameterValues[i];	
			
			String queryParametersFilterPortion = "&" + filterBy + "=" + queryParameterValue;
			
			String queryParameters = queryParametersPagePortion + queryParametersFilterPortion; 
			
			System.out.println("queryParameters: " + queryParameters);
				
			JSONArray jsonArrayTenants = CommonMethods.GetJsonArrayWithUrl(token, url + queryParameters, apiType);
			
			List<Tenant> filteredListAPI = CommonMethods.putJsonArrayTenantsIntoList(jsonArrayTenants);
		
			// 4. 
			
			for (int j = 0; j < filteredListOpsConsole.size(); j++) {
				
				Assert.assertEquals(filteredListOpsConsole.get(j).getKey(), filteredListAPI.get(j).getKey());
				Assert.assertEquals(filteredListOpsConsole.get(j).getName(), filteredListAPI.get(j).getName());
				Assert.assertEquals(filteredListOpsConsole.get(j).getDefaultTenantID(), filteredListAPI.get(j).getDefaultTenantID());
				Assert.assertEquals(filteredListOpsConsole.get(j).isEnabled(), filteredListAPI.get(j).isEnabled());
				
			}
			
			// 5. Reset filters
			CommonMethods.resetFilters();
			
		}
		
				
	}

	
	public static void verifyFilteringCombined(String applicationKey, String deploymentKey, String enabled) throws Exception {
		
		/*
		 * 1. Filter tenant by Application & Deployment & Enabled 
		 *   - Select "application" from dropdown list and append "application" query portion to queryParameters.
		 *   - Select "deployment" from dropdown list and append "deployment" query portion to queryParameters.
		 *   - Select "enabled" from dropdown list and append "enabled" query portion to queryParameters. 
		 * 2. Get tenants filtered by the same application & deployment & enabled from API
		 * 3. Get results from UI
		 * 4. Compare results 
		 * */ 
		
		String[] filterToSelect = {"All Applications", "All Deployments", "Show Enabled and Disabled"};
		
		
		HashMap<String, String> filterNameMap = new HashMap<String, String>();
		
		filterNameMap.put("All Applications", "applicationKey");
		filterNameMap.put("All Deployments", "deploymentKey");
		filterNameMap.put("Show Enabled and Disabled", "enabled");
		
		String enabledValueForRequest = getEnabledValueForRequest(enabled);
		
		String[] filterValue = {applicationKey, deploymentKey, enabled};
		String[] queryParameterValues = {applicationKey, deploymentKey, enabledValueForRequest};
		
		int page = 1;
		int pageSize = 10;
			
		String queryParametersPagePortion = "?page=" + page + "&pageSize=" + pageSize;
		
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/tenants";
		String apiType = "\"" + "tenants" + "\"" + ":";

		String queryParametersFilterPortion = "";
		
		// 1. 
		
		for (int i = 0; i < filterNameMap.size(); i++) {
				
			String filter = filterToSelect[i];
			String value = filterValue[i];

			System.out.println("  Filter to select: " + filter + " by value: " + value);
			
			// 1.a. Click filter dropdown list
			CommonMethods.clickFilterDropdown(filter);
								
			// 1.b. Enter value on the Search field
			CommonMethods.enterSearchCriteria(filter, value);
			
			String filterBy = filterNameMap.get(filter);
			String queryParameterValue = queryParameterValues[i];	
						
			String query = "&" + filterBy + "=" + queryParameterValue;
			
			queryParametersFilterPortion = queryParametersFilterPortion + query;
			
		}
	
		// 2.
		
		String queryParameters = queryParametersPagePortion + queryParametersFilterPortion; 
		
		System.out.println("queryParameters: " + queryParameters);
			
		JSONArray jsonArrayTenants = CommonMethods.GetJsonArrayWithUrl(token, url + queryParameters, apiType);
		
		List<Tenant> filteredListAPI = CommonMethods.putJsonArrayTenantsIntoList(jsonArrayTenants);
	
		
		// 3. 		
		
		List<Tenant> filteredListOpsConsole = addTenantsFromUItoList();
				
		
		// 4. 
		
		for (int j = 0; j < filteredListOpsConsole.size(); j++) {
			
			Assert.assertEquals(filteredListOpsConsole.get(j).getKey(), filteredListAPI.get(j).getKey());
			Assert.assertEquals(filteredListOpsConsole.get(j).getName(), filteredListAPI.get(j).getName());
			Assert.assertEquals(filteredListOpsConsole.get(j).getDefaultTenantID(), filteredListAPI.get(j).getDefaultTenantID());
			Assert.assertEquals(filteredListOpsConsole.get(j).isEnabled(), filteredListAPI.get(j).isEnabled());
			
		}
		
		// 5. Reset filters
		CommonMethods.resetFilters();
			
	}
	

	
	private static String getEnabledValueForRequest(String enabled) {
		
		switch(enabled) {
			
			case "Show Enabled Tenants Only": 
				return "true";
			
			case "Show Disabled Tenants Only": 
				return "false";
			
			case "Show Enabled and Disabled": 
				return "";
			
			default:
				return "";
		}
	}


	public static void addTenant(String tenantKey) throws Exception {
		
		String xpathButtonAdd = "//div/h2/button[2]";
		
		WaitForElementPresent(By.xpath(xpathButtonAdd), 3);
		
		driver.findElement(By.xpath(xpathButtonAdd)).click();
			
		driver.findElement(By.xpath(xpathKey)).sendKeys(tenantKey);
		
		driver.findElement(By.xpath(xpathName)).sendKeys("Automation Tenant Ana");
		
		driver.findElement(By.xpath(xpathDefTenantId)).sendKeys("TEN-ID-AUTO");
		
		String xpathButtonSave = "//button/span[text()='Save']/..";
		
		driver.findElement(By.xpath(xpathButtonSave)).submit();
	
		String xpathButtonClose = "//div[@class='modal-footer']/button[text()='Close']";
		
		driver.findElement(By.xpath(xpathButtonClose)).click();
	}
	

	public static void searchTenant(String tenantKey) {
		
		String xpathSearchTypeSelect = "//select[@formcontrolname='searchTypeSelect']";
		
		new Select(driver.findElement(By.xpath(xpathSearchTypeSelect))).selectByValue("KEY");
		
		String xpathSearchTextInput = "//input[@formcontrolname='searchTextInput']";
		
		driver.findElement(By.xpath(xpathSearchTextInput)).clear();
		
		driver.findElement(By.xpath(xpathSearchTextInput)).sendKeys(tenantKey);
				
	}
	
	
	public static void deleteTenant(String tenantKey) {
		
		int pageSize = 10;
		
		// make sure that the Delete button clicked belongs to the tenant that needs to be deleted
		
		for (int i = 1; i <= pageSize; i++) {
		
			String xpathButtonDelete = "//table/tbody/tr[" + i + "]/td[5]/div/button/span[text()='Delete']";
			
			driver.findElement(By.xpath(xpathButtonDelete)).click();	
			
			String xpathKeyPopUp = "//jhi-tenant-mgmt-delete-dialog/form/div[2]/div/div[2]/dd[1]";
			
			if (driver.findElement(By.xpath(xpathKeyPopUp)).getText().equals(tenantKey)) {
				
				//System.out.println("Tenant found");
				break;
			}
			//System.out.println("Tenant NOT found");
			
			// If the tenant clicked is not the tenant that we need then click Cancel button
			driver.findElement(By.xpath("//button/span[text()='Cancel']/..")).click();
			
		}
		
		// click checkbox
		String xpathCheckbox = "//label/input[@type='checkbox']";
		
		driver.findElement(By.xpath(xpathCheckbox)).click();
		
		// Was getting error "... other element would receive the click..." because of the other Delete buttons in the UI
		// Added div[@class='modal-footer']/ to the xpath to get rid of this error 
		String xpathButtonDeleteConfirm = "//div[@class='modal-footer']/button/span[text()='Delete']/..";
		
		WaitForElementClickable(By.xpath(xpathButtonDeleteConfirm), 3, "Button Delete is not clickable");
		
		driver.findElement(By.xpath(xpathButtonDeleteConfirm)).submit();
	
		
	}


	public static void editTenant(String tenantKey) throws Exception {
		
		
		int pageSize = 10;
		
		// make sure that the Edit button clicked belongs to the tenant that needs to be edited
		
		for (int i = 1; i <= pageSize; i++) {
		
			String xpathButtonEdit = "//table/tbody/tr[" + i + "]/td[5]/div/button/span[text()='Edit']";
			
			driver.findElement(By.xpath(xpathButtonEdit)).click();	
			
			WaitForElementVisible(By.xpath("//h4[@id='myUserLabel']/strong[text()='Edit Tenant']"), 3);
			
			String xpathKeyPopUp = "//jhi-tenant-management-dialog/form/div[2]/div/dl/dd";
			
			if (driver.findElement(By.xpath(xpathKeyPopUp)).getText().equals(tenantKey)) {
				
				System.out.println("Tenant found");
				break;
			}
			System.out.println("Tenant NOT found");
			
			// If the tenant clicked is not the tenant that we need then click Cancel button
			driver.findElement(By.xpath("//button/span[text()='Cancel']/..")).click();
			
		}
		
		// Modify Name
		driver.findElement(By.xpath(xpathName)).clear();
		driver.findElement(By.xpath(xpathName)).sendKeys("New name for edited tenant");
	
		// Modify Default Tenant ID
		driver.findElement(By.xpath(xpathDefTenantId)).clear();
		driver.findElement(By.xpath(xpathDefTenantId)).sendKeys("Def tenant ID changed");
		
		// Modify Enabled
		if (driver.findElement(By.xpath(xpathEnabled)).getAttribute("value").equals("false")) {
			
			driver.findElement(By.xpath(xpathEnabled)).click();
		
		} else if (driver.findElement(By.xpath(xpathDisabled)).getAttribute("value").equals("false")) {
		
			driver.findElement(By.xpath(xpathDisabled)).click();
		}
	
	
		// Was getting error "... other element would receive the click..." because of the other Delete buttons in the UI
		// Added div[@class='modal-footer']/ to the xpath to get rid of this error 
		String xpathButtonSave = "//div[@class='modal-footer']/button/span[text()='Save']/..";
		
		WaitForElementClickable(By.xpath(xpathButtonSave), 3, "Button Save is not clickable");
		
		driver.findElement(By.xpath(xpathButtonSave)).submit();
	
		
		
	}



	// Open the tenant details page and verify if the tenant matches the sent in parameter 
	public static void openTenantDetailsPage(String tenantKey) throws InterruptedException {
		
		int pageSize = 10;
		
		// make sure that the View button clicked belongs to the tenant that is going to be viewed
		
		for (int i = 1; i <= pageSize; i++) {
		
			String xpathButtonView = "//table/tbody/tr[" + i + "]/td[5]/div/button/span[text()='View']";
			
			driver.findElement(By.xpath(xpathButtonView)).click();	
			
			String xpathKeyTenant = "//jhi-tenant-detail/div/div/div[@class='row']/dt[text()='Key:']/following-sibling::dd";
			
			String tenantKeyUI = driver.findElement(By.xpath(xpathKeyTenant)).getText().split("   ")[0].trim();
			
			System.out.println("Tenant key: " + tenantKeyUI);
			
			if (tenantKeyUI.equals(tenantKey)) {
				
				// System.out.println("Tenant found");
				break;
			}
			//System.out.println("Tenant NOT found");
			
			// If the tenant clicked is not the tenant that we need then click Back button
			driver.findElement(By.xpath("//button/span[text()=' Back']/..")).click();
			Thread.sleep(2000);
			
		}
	
	}

	
	public static void verifyTenantDetails(String tenantKey) throws Exception {
		
		CommonMethods.openElementDetailsPage(tenantKey, "tenant");
		
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/tenants/" + tenantKey + "?includeStatistics=true";
						
		JSONObject jsonObject = CommonMethods.getSingleObject(token, url); 
		
		Tenant tenantObject = putJsonObjectIntoTenantObject(jsonObject);
		
		String xpathNameTenant = "//jhi-tenant-detail/div/div/div[@class='row']/dt[text()='Name:']/following-sibling::dd";	
		String xpathDefIdTenant = "//jhi-tenant-detail/div/div/div[@class='row']/dt[text()='Default Tenant ID:']/following-sibling::dd";
		String xpathEnabledTenant = "//jhi-tenant-detail/div/div/div[@class='row']/dt[text()='Key:']/following-sibling::dd";
		
		// Verify Name
		String nameUI = driver.findElement(By.xpath(xpathNameTenant)).getText().trim();
		Assert.assertEquals(nameUI, tenantObject.getName());
				
		// Verify Default Tenant ID - in some cases there is no value for Default Tenant ID 
		String defTenIdUI = "";
		
		try {
			defTenIdUI = driver.findElement(By.xpath(xpathDefIdTenant)).getText().trim();
			Assert.assertEquals(defTenIdUI, tenantObject.getDefaultTenantID());
			
		} catch (NoSuchElementException e) { }
		
		// Verify Enabled value
		String enabledUI = driver.findElement(By.xpath(xpathEnabledTenant)).getText().split("   ")[1].trim();
		Assert.assertEquals(CommonMethods.convertToBoolean(enabledUI), tenantObject.isEnabled());
		
		
		// Verify the # on each tab
		int applicationCount = Integer.parseInt(driver.findElement(By.xpath("//li[@class='nav-item']/a/div[text()='Applications ']/span")).getText());
		int deploymentCount = Integer.parseInt(driver.findElement(By.xpath("//li[@class='nav-item']/a/div[text()='Deployments ']/span")).getText()); 
		int routeCount = Integer.parseInt(driver.findElement(By.xpath("//li[@class='nav-item']/a/div[text()='Routes ']/span")).getText());
		
		System.out.println("applicationCount: " + applicationCount);
		System.out.println("deploymentCount: " + deploymentCount);
		System.out.println("routeCount: " + routeCount);
	
		Assert.assertEquals(applicationCount, tenantObject.getApplicationCount());
		Assert.assertEquals(deploymentCount, tenantObject.getDeploymentCount());
		Assert.assertEquals(routeCount, tenantObject.getRouteCount());
		
		driver.findElement(By.xpath("//button/span[text()=' Back']/..")).click();
		Thread.sleep(2000);
		
	}


	

	// In the tenant's details page verify the data displayed on each tab comparing it to the data coming from the API
	public static void verifyDetailsPages(String tenantKey) throws Exception {
		
		// 1. Open tenant's details page 
		CommonMethods.openElementDetailsPage(tenantKey, "tenant");
		
		// 2. Verify data in Applications tab
		CommonMethods.verifyApplicationDataTabInDetailsPage(tenantKey, "tenant");
		
		// 3. Verify data in Deployments tab
		CommonMethods.verifyDeploymentDataTabInDetailsPage(tenantKey, "tenant");
		
		// 4. Verify data in Routes tab
		CommonMethods.verifyRouteDataTabInDetailsPage(tenantKey, "tenant");
		
		// 5. Verify data in ACL tab
		verifyACLDataTabInDetailsPage(tenantKey); //  -- ACL is included only in tenants details page so it's not in CommonMethods 
		
	}
	
	
	private static void verifyACLDataTabInDetailsPage(String tenantKey) throws Exception {
		
		/*
		 * 1. Click 'ACL' tab
		 *  	* Wait for tab to be selected
		 * 2. Run request for GET /acl/tenant/<tenantKey>?defaultIfMissing=true to get a list with the IP/CIDR in the API
		 * 3. Get a list with the IPs/CIDRs listed in the UI
		 * 4. Compare the ACL listed on each list - they should be the same 
		 * 
		 */
		
			
		// 1. Click 'ACL' tab
		String xpathACLTab = "//li/a[@id='acl_tab']/div[text()='ACL']";
		driver.findElement(By.xpath(xpathACLTab)).click();
			
		// * Wait for tab to be selected
		WebDriverWait wait = new WebDriverWait(driver, 4);
		wait.until(ExpectedConditions.attributeToBe(By.id("acl_tab"), "aria-expanded", "true"));
		WaitForElementVisible(By.xpath("//table/thead/tr/th[@jhisortby='ADDRESS']"), 4);
		
		
		// 2. Run request for GET /acl/tenant/<tenantKey>?defaultIfMissing=true to get a list with the IP/CIDR in the API
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/acl/tenant/" + tenantKey + "?defaultIfMissing=true";
		String apiType = "\"" + "addresses" + "\"" + ":";
		
		JSONArray jsonArrayAddresses = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		List<String> addressesFromAPI = putJsonArrayAddressesIntoList(jsonArrayAddresses);	
		List<String> addressesKeysFromAPI = new ArrayList<>();
		
		int addressesCount = addressesFromAPI.size();
		
		for (int i = 0; i < addressesCount; i++) {
			
			addressesKeysFromAPI.add(addressesFromAPI.get(i));
			
		}
		
				
		// 3. Get a list with the IP/CIDR listed in the UI
		List<String> addressesInTab = new ArrayList<>();
				
		for (int i = 1; i <= addressesCount; i++) {
		
			String addrInTab = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td")).getText();
			
			addressesInTab.add(addrInTab);
			
			System.out.println("addrInTab: " + addrInTab);
			
		}
		
		
		
		
		// 4. Compare the addresses listed on each list - they should be the same 
		Collections.sort(addressesInTab);
		Collections.sort(addressesKeysFromAPI);
		
		for (int i = 0; i < addressesCount; i++) {
			
			Assert.assertEquals(addressesKeysFromAPI.get(i), addressesInTab.get(i));
			
		}
		
	}


	private static List<String> putJsonArrayAddressesIntoList(JSONArray jsonArrayAddresses) throws JSONException {
	
		List<String> actualAddressesList = new ArrayList<>();
		
		// Put the deployments in the json array into a list of deployments
		for (int i = 0; i < jsonArrayAddresses.length(); i++) {
			
			JSONObject jo = jsonArrayAddresses.getJSONObject(i);
						
			// System.out.println(i+1 + "  Address: " + jo.getString("addr"));
			
			actualAddressesList.add(jo.getString("addr"));
			
		}
		
		return actualAddressesList;
	
	}
	
	

	// ***************************************************
	// **** METHODS ADDED TO COMMON METHODS ********
	// ***************************************************
	
	//public static void verifyApplicationDataTabInDetailsPage(String tenantKey) throws Exception {
		
		/*
		 * 1. Click 'Applications' tab
		 *  	* Wait for tab to be selected
		 * 2. Get a list with the apps listed in the UI
		 * 3. Run request for GET /applications?tenantKey=tenantKeyValue to get a list with the apps in the API
		 * 4. Compare the apps listed on each list - they should be the same 
		 * 
		 */
		/*
		
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
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/applications?tenantKey=" + tenantKey;
		String apiType = "\"" + "applications" + "\"" + ":";
		
		JSONArray jsonArrayApplications = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		List<ApplicationClass> applicationsFromAPI = CommonMethods.putJsonArrayAppsIntoList(jsonArrayApplications);	
		List<String> applicationKeysFromAPI = new ArrayList<>();
		
		for (int i = 0; i < applicationsFromAPI.size(); i++) {
			
			applicationKeysFromAPI.add(applicationsFromAPI.get(i).m_Key);
			
		}
		

		// 4. Compare the apps listed on each list - they should be the same 
		Collections.sort(applicationsKeysInTab);
		Collections.sort(applicationKeysFromAPI);
		
		for (int i = 0; i < applicationCount; i++) {
			
			Assert.assertEquals(applicationKeysFromAPI.get(i), applicationsKeysInTab.get(i));
			
		}
		
	}
	*/
	//public static void verifyDeploymentDataTabInDetailsPage(String tenantKey) throws Exception {
		
		/*
		 * 1. Click 'Deployments' tab
		 *  	* Wait for tab to be selected
		 * 2. Get a list with the deployments listed in the UI
		 * 3. Run request for GET /deployments?tenantKey=tenantKeyValue to get a list with the deployments in the API
		 * 4. Compare the deployments listed on each list - they should be the same 
		 * 
		 */
		/*
				
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
			String depVersion = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[3]")).getText();
			String depEnabled = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[5]/span")).getText();
			
			Deployment dep = new Deployment(depKey, "", depVersion, "", CommonMethods.convertToBoolean(depEnabled));
			deploymentsInTab.add(dep);
			deploymentsKeysInTab.add(depKey);
			
			System.out.println("key: " + depKey);
			
		}
		
		
		// 3. Run request for GET /deployments?tenantKey=tenantKeyValue to get a list with the deployments in the API
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/deployments?tenantKey=" + tenantKey;
		String apiType = "\"" + "deployments" + "\"" + ":";
		
		JSONArray jsonArrayDeployments = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		List<Deployment> deploymentsFromAPI = CommonMethods.putJsonArrayDeploymentsIntoList(jsonArrayDeployments);	
		List<String> deploymentKeysFromAPI = new ArrayList<>();
		
		for (int i = 0; i < deploymentsFromAPI.size(); i++) {
			
			deploymentKeysFromAPI.add(deploymentsFromAPI.get(i).getKey());
			
		}
		
		
		// 4. Compare the deployments listed on each list - they should be the same
		Collections.sort(deploymentsKeysInTab);
		Collections.sort(deploymentKeysFromAPI);
		
		for (int i = 0; i < deploymentCount; i++) {
			
			Assert.assertEquals(deploymentKeysFromAPI.get(i), deploymentsKeysInTab.get(i));
			
		}
		
	}*/
	
	//public static void verifyRouteDataTabInDetailsPage(String tenantKey) throws Exception {
		
		/*
		 * 1. Click 'Routes' tab
		 *  	* Wait for tab to be selected
		 * 2. Get a list with the routes listed in the UI
		 * 3. Run request for GET /routes?tenantKey=tenantKeyValue to get a list with the routes in the API
		 * 4. Compare the routes listed on each list - they should be the same 
		 * 
		 */
		
		/*	
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
			String routeAppKey = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[3]/div/a")).getText();
			String routeEnabled = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]/td[5]/div/span")).getText();
			
			RouteClass route = new RouteClass(routeKey, "", "", routeAppKey, "", "", "", "", "", 
					CommonMethods.convertToBoolean(routeEnabled), "", true, "", "");
			routesInTab.add(route);
			routesKeysInTab.add(routeKey);
			
			System.out.println("key: " + routeKey);
			
		}
		
		
		// 3. Run request for GET /routes?tenantKey=tenantKeyValue to get a list with the routes in the API
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/routes?tenantKey=" + tenantKey + "&pageSize=" + sizeOfList;
		String apiType = "\"" + "routes" + "\"" + ":";
		
		JSONArray jsonArrayRoutes = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		List<RouteClass> routesFromAPI = CommonMethods.putJsonArrayRoutesIntoList(jsonArrayRoutes);	
		List<String> routeKeysFromAPI = new ArrayList<>();
		
		for (int i = 0; i < routesFromAPI.size(); i++) {
			
			routeKeysFromAPI.add(routesFromAPI.get(i).m_Key);
			
		}
		
		
		// 4. Compare the routes listed on each list - they should be the same 
		Collections.sort(routesKeysInTab);
		Collections.sort(routeKeysFromAPI);
		
		for (int i = 0; i < routeCount; i++) {
			
			Assert.assertEquals(routeKeysFromAPI.get(i), routesKeysInTab.get(i));
			
		}
		
	}
*/
	


		
	
	
	
	
	// ***************************************************
	// **** METHODS TO BE ADDED TO COMMON METHODS ********
	// ***************************************************
	

	
}
