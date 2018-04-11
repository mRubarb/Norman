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
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import baseItems.BaseMain;
import classes.Tenant;
import common.CommonMethods;
import common.CommonMethodsAna;


public class Tenants extends BaseMain
{

	private static String[] propertiesNames = {"Key", "Name", "Default Tenant ID", "Enabled"};
	
	
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
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/tenants";  //"http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/tenants";
		String apiType = "\"" + "tenants" + "\"" + ":";
				
		String queryParameters = "?page=" + page + "&pageSize=" + pageSize;
		
		JSONArray jsonArrayTenants = CommonMethods.GetJsonArrayWithUrl(token, url + queryParameters, apiType);
		
		List<Tenant> actualTenantsList = putJsonArrayIntoList(jsonArrayTenants);
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
	
	// Puts the JSON response obtained from the API GET /tenants into a list.
	private static List<Tenant> putJsonArrayIntoList(JSONArray jsonArrayTenants) throws JSONException {
		
		List<Tenant> actualTenantsList = new ArrayList<>();
		
		// Put the deployments in the json array into a list of deployments
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
			tenant.setDefaultTenantID(CommonMethodsAna.GetNonRequiredItem(jo, "defaultTenantID"));
			tenant.setEnabled(jo.getBoolean("enabled"));
		
			actualTenantsList.add(tenant);
			
		}
		
		return actualTenantsList;
			
	}
	

	// It adds the tenants that are listed on the UI to a list
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
			tenant.setEnabled(CommonMethodsAna.convertToBoolean(row.get(3).getText()));
					
			expectedTenantsList.add(tenant);
			
		}
		
		return expectedTenantsList;
		
	}


	
	public static void verifyListSorted() throws InterruptedException {		
		
		System.out.println("\n  ** Sort List by Name in Ascending Order **");
		
		CommonMethodsAna.clickArrowSorting("Name", "ASC");
				
		verifySortingObjects("NAME", "ASC");
		
		System.out.println("\n  ** Sort List by Name in Descending Order **");
		
		CommonMethodsAna.clickArrowSorting("Name", "DESC");
				
		verifySortingObjects("NAME", "DESC");
	
		System.out.println("\n  ** Sort List by Default Tenant ID in Ascending Order **");
		
		CommonMethodsAna.clickArrowSorting("Default Tenant ID", "ASC");
		
		verifySortingObjects("DEFAULT_TENANT_ID", "ASC");
		
		System.out.println("\n  ** Sort List by Default Tenant ID in Descending Order **");
		
		CommonMethodsAna.clickArrowSorting("Default Tenant ID", "DESC");
				
		verifySortingObjects("DEFAULT_TENANT_ID", "DESC");

		System.out.println("\n  ** Sort List by Enabled in Ascending Order **");
		
		CommonMethodsAna.clickArrowSorting("Enabled", "ASC");
				
		verifySortingObjects("IS_ENABLED", "ASC");
		
		System.out.println("\n  ** Sort List by Enabled in Descending Order **");
		
		CommonMethodsAna.clickArrowSorting("Enabled", "DESC");
				
		verifySortingObjects("IS_ENABLED", "DESC");
		
		System.out.println("\n  ** Sort List by Key in Ascending Order **");
		
		CommonMethodsAna.clickArrowSorting("Key", "ASC");
				
		verifySortingObjects("KEY", "ASC");
		
		System.out.println("\n  ** Sort List by Key in Descending Order **");
		
		CommonMethodsAna.clickArrowSorting("Key", "DESC");
		
		verifySortingObjects("KEY", "DESC");
	
		CommonMethodsAna.clickArrowSorting("Key", "ASC");
		
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
	
	
	
	// **** COMMON????
	/*public static void clickArrowSorting(String sortBy, String sortDirection) throws InterruptedException {
		
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
		
	}*/


	// **** COMMON????
	/*public static void selectSizeOfList(int size) {
		
		System.out.println("Size to select: " + size);
		
		String xpath1 = "//span/label";
		String xpath2 = "/input[@name='pageSize']";
		
		List<WebElement> sizeRadioButtons = driver.findElements(By.xpath(xpath1 + "/input"));
		
		for (int i = 0; i < sizeRadioButtons.size(); i++) {
			
			WebElement radioButtonSize = sizeRadioButtons.get(i); 
						
			if (radioButtonSize.getAttribute("value").equals(Integer.toString(size))) {
				
				driver.findElement(By.xpath(xpath1 + "[" + (i+1) + "]" + xpath2 + "/..")).click();
				//driver.findElement(By.xpath(xpath + "[" + (i+1) + "]/..")).click();
				break;
			}
								
		}
		
	}*/
	
	/*
	 * 
	 * public static void selectSizeOfList(int size) {
		
		System.out.println("Size to select: " + size);
		
		String xpath = "//div/span[text()='Size: ']/following-sibling::span/label";
		
		List<WebElement> sizeRadioButtons = driver.findElements(By.xpath(xpath + "/input"));
		
		for (int i = 0; i < sizeRadioButtons.size(); i++) {
			
			WebElement radioButtonSize = sizeRadioButtons.get(i); 
						
			if (radioButtonSize.getAttribute("value").equals(Integer.toString(size))) {
				
				driver.findElement(By.xpath(xpath + "[" + (i+1) + "]")).click();
				break;
			}
								
		}
		
	}*/


	public static void verifyDataAndSorting() throws InterruptedException, IOException, JSONException {

		// Get all the "sizes" into a list
		
		List<WebElement> listSizesElements = CommonMethodsAna.getSizesOfPages();  //driver.findElements(By.xpath("//div/span[text()='Size: ']/following-sibling::span/label/input"));
		
		
		for (int i = 0; i < listSizesElements.size(); i++) {
			
			int pageSize = Integer.parseInt(listSizesElements.get(i).getAttribute("value"));
			
			CommonMethodsAna.selectSizeOfList(pageSize);
			
			Thread.sleep(2000);  // -- see if it can be changed to waitfor.... 
			
			verifyDataFromUIMatchesAPI(1, pageSize);
			
			Thread.sleep(2000);
			
			// ** T-960:Tenants list can be sorted
			verifyListSorted();
			
		}	
		
	}


	public static void verifyPaging() throws InterruptedException, IOException, JSONException {
		
		
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
		// Get all the "sizes" into a list
		
		List<WebElement> listSizesElements = CommonMethodsAna.getSizesOfPages(); // xpath("//div/span[text()='Size: ']/following-sibling::span/label/input"));
		
		/*System.out.println("listSizesElements.size(): " + listSizesElements.size());
		
		for (int i = 0; i < listSizesElements.size(); i++) {
			
			System.out.println("listSizesElements " + i + ": " + listSizesElements.get(i).getAttribute("value"));
			
		}*/
		
		String totalCountItems = CommonMethodsAna.getTotalCountItems();
		
		int index = totalCountItems.indexOf("of");
		totalCountItems = totalCountItems.substring(index).replace("of", "").replace("items.", "").trim();
				
		int totalCount = Integer.parseInt(totalCountItems);
		
		System.out.println("totalCount: " + totalCount);		
		
		
		// Verify data for each pageSize  (5 / 10 / 20 / 50)
		
		for (int i = 0; i < listSizesElements.size(); i++) {
			
			
			// remove --System.out.println("pageSize = " + listSizesElements.get(i).getAttribute("value"));
			
			int pageSize = Integer.parseInt(listSizesElements.get(i).getAttribute("value"));
			
			System.out.println("pageSize = " + pageSize);
			
			CommonMethodsAna.selectSizeOfList(pageSize);
			Thread.sleep(2000);  // -- see if it can be changed to waitfor....
			
			int totalPages = 1;
			
			if ((totalCount % pageSize) > 0)
				totalPages = (totalCount / pageSize) + 1;
			
			if ((totalCount % pageSize) == 0)
				totalPages = totalCount / pageSize;
			
			// Go through all the pages and verify data on each page
			
			for (int page = 1; page <= totalPages; page++) {
			
				CommonMethodsAna.clickPageNumber(page);
				
				System.out.println("page = " + page);
				verifyDataFromUIMatchesAPI(page, pageSize);
				
			}
			
				
		}
			
		
	}
	
	
	// **** COMMON????
	/*
	private static List<WebElement> getSizesOfPages() {

		return driver.findElements(By.xpath("//span/label/input[@name='pageSize']"));    
		
	}*/


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
		
		List<WebElement> listSizesElements = driver.findElements(By.xpath("//div/span[text()='Size: ']/following-sibling::span/label/input"));
		
		String totalCountItems = driver.findElement(By.xpath("//div/jhi-item-count")).getText();
		
		int index = totalCountItems.indexOf("of");
		totalCountItems = totalCountItems.substring(index).replace("of", "").replace("items.", "").trim();
				
		int totalCount = Integer.parseInt(totalCountItems);
		
		System.out.println("totalCount: " + totalCount);		
		
		
		// Verify data for each pageSize  (5 / 10 / 20 / 50)
		
		for (int i = 0; i < listSizesElements.size(); i++) {
			
			int pageSize = Integer.parseInt(listSizesElements.get(i).getAttribute("value"));
			
			System.out.println("pageSize = " + pageSize);
			
			CommonMethodsAna.selectSizeOfList(pageSize);
			Thread.sleep(2000);  // -- see if it can be changed to waitfor....
			
			int totalPages = 1;
			
			if ((totalCount % pageSize) > 0)
				totalPages = (totalCount / pageSize) + 1;
			
			if ((totalCount % pageSize) == 0)
				totalPages = totalCount / pageSize;
			
			// Go through all the pages and verify data on each page
			
			for (int page = 1; page <= totalPages; page++) {
			
				CommonMethodsAna.clickPageNumber(page);
				
				System.out.println("page = " + page);
				verifyDataNew(page, pageSize, totalCount);
				
			}
			
				
		}
			
		
	}

	
	// It verifies that the data on the UI matches the data from the API
	public static void verifyDataNew(int page, int pageSize, int totalCount) throws IOException, JSONException {
		
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/tenants";  //"http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/tenants";
		String apiType = "\"" + "tenants" + "\"" + ":";
				
		String queryParameters = "?page=1&pageSize=" + totalCount;
		
		JSONArray jsonArrayTenants = CommonMethods.GetJsonArrayWithUrl(token, url + queryParameters, apiType);
		
		List<Tenant> actualTenantsList = putJsonArrayIntoList(jsonArrayTenants);
		List<Tenant> expectedTenantsList = addTenantsFromUItoList();
		
		int indexStart = (page - 1) * pageSize;
		int indexEnd = indexStart + pageSize - 1;
		
		if (indexEnd >  totalCount) 
			indexEnd = totalCount - 1;
				
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


	
	public static void verifyFiltering() throws Exception {
		
		/*
		 * 1. Filter tenant by Application / Deployment / Enabled 
		 * 2. Get results from UI
		 * 3. Get tenants filtered by the same application from API
		 * 4. Compare results 
		 * */ 
		
		String[] filterToSelect = {"All Applications", "All Deployments", "Show Enabled and Disabled"};
		
		
		HashMap<String, String> filterNameMap = new HashMap<String, String>();
		
		filterNameMap.put("All Applications", "applicationKey");
		filterNameMap.put("All Deployments", "deploymentKey");
		filterNameMap.put("Show Enabled and Disabled", "enabled");
		
		String[] filterValue = {"RVM", "DEP_RVM_1", "Show Enabled Tenants Only"};
		
		
		for (int i = 0; i < filterNameMap.size(); i++) {

			String filter = filterToSelect[i];
			String value = filterValue[i];

			System.out.println("  Filter to select: " + filter + " by value: " + value);
			
			// 1.a. Click Application filter dropdown list
			CommonMethodsAna.clickFilterDropdown(filter);
								
			// 1.b. Enter App Key / Name on the Search field
			CommonMethodsAna.enterSearchCriteria(filter, value);  // ***** IT FAILS WHEN IT NEEDS TO SELECT THE DEPLOYMENT AFTER IS FILTERED
			
			
			// 2. 
			List<Tenant> filteredListOpsConsole = addTenantsFromUItoList();
			
			
			// 3.
			int page = 1;
			int pageSize = 10;
			
			String filterBy = filterNameMap.get(filter);
				
			String token = CommonMethods.GetTokenFromPost();
			String url = baseUrl.replace("#", "") + "platformservice/api/v1/tenants";
			String apiType = "\"" + "tenants" + "\"" + ":";
						
			String queryParameters = "?page=" + page + "&pageSize=" + pageSize + "&" + filterBy + "=" + value;
			
			System.out.println("queryParameters: " + queryParameters);
				
			JSONArray jsonArrayTenants = CommonMethods.GetJsonArrayWithUrl(token, url + queryParameters, apiType);
			
			List<Tenant> filteredListAPI = putJsonArrayIntoList(jsonArrayTenants);
		
			// 4. 
			
			for (int j = 0; j < filteredListOpsConsole.size(); j++) {
				
				Assert.assertEquals(filteredListOpsConsole.get(j).getKey(), filteredListAPI.get(j).getKey());
				Assert.assertEquals(filteredListOpsConsole.get(j).getName(), filteredListAPI.get(j).getName());
				Assert.assertEquals(filteredListOpsConsole.get(j).getDefaultTenantID(), filteredListAPI.get(j).getDefaultTenantID());
				Assert.assertEquals(filteredListOpsConsole.get(j).isEnabled(), filteredListAPI.get(j).isEnabled());
				
			}
			
			// 5. Reset filters
			resetFilters();
			
		}
		
				
	}


	private static void resetFilters() throws InterruptedException {
		
		String xpathApp = "//jhi-application-selector/form/div/div/button[@id='sortMenu']";
		driver.findElement(By.xpath(xpathApp)).click();
				
		String xpathAllApp = "//jhi-application-selector/form/div/div/div/button/span[text()='All Applications']";
		driver.findElement(By.xpath(xpathAllApp)).click();
		
		String xpathDep = "//jhi-deployment-selector/form/div/div/button[@id='sortMenu']";  //"//button[@id='sortMenu'][2]";
		driver.findElement(By.xpath(xpathDep)).click();
				
		String xpathAllDep = "//jhi-deployment-selector/form/div/div/div/button/span[text()='All Deployments']";
		driver.findElement(By.xpath(xpathAllDep)).click();
		
		String xpathEnabled = "//jhi-value-selector/div/div/button[@id='sortMenu']";  //"//button[@id='sortMenu'][3]";
		driver.findElement(By.xpath(xpathEnabled)).click();
				
		String xpathAllEnabled = "//jhi-value-selector/div/div/div/button/span[text()='Show Enabled and Disabled']";
		driver.findElement(By.xpath(xpathAllEnabled)).click();
		
		Thread.sleep(2000); 
	}


	
	// ***************************************************
	// **** METHODS TO BE ADDED TO COMMON METHODS ********
	// ***************************************************
	/*
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

	

	// ENABLED/DISABLED values are converted to true/false
	private static boolean convertToBoolean(String isEnabled) {
		
		if (isEnabled.equals("ENABLED")) {
	
			return true;
			
		} else if (isEnabled.equals("DISABLED")) {
			
			return false;
			
		}
		return false; 
	
	}
	
	
	// ***** COMMON METHOD??? 
	// Clicks on the page number at the bottom - pagination bar
	private static void clickPageNumber(int page) throws InterruptedException {
		
		driver.findElement(By.xpath("//ul[@class='pagination']/li/a[contains(text()," + "\"" + page + "\"" + ")]")).click();
		Thread.sleep(3000);
	}
*/

	
}
