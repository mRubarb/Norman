package pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

public class Tenants extends BaseMain
{

	private static String[] propertiesNames = {"Key", "Name", "Enabled"};
	
	
	public static void verifyColumnsNames() {
		
		List<WebElement> columnNames = driver.findElements(By.xpath("//div[@class='table-responsive']/table/thead/tr/th/span[1]"));
		
		for (int i = 0; i < columnNames.size(); i++) {
			
			System.out.println(columnNames.get(i).getText());
			Assert.assertEquals(propertiesNames[i], columnNames.get(i).getText());
		
		}
		
	}
	
	
	// It verifies that the data on the UI matches the data from the API
	public static void verifyData(int page, int pageSize) throws IOException, JSONException {
		
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
			
			/*System.out.println(i+1);
			System.out.println("Actual Key: " + actual.getKey() + ", Expected Key: " + expected.getKey());
			System.out.println("Actual Name: " + actual.getName() + ", Expected Name: " + expected.getName());
			System.out.println("Actual isEnabled: " + actual.isEnabled() + ", Expected isEnabled: " + expected.isEnabled());
			*/
			
			Assert.assertEquals(actual.getKey(), expected.getKey());
			Assert.assertEquals(actual.getName(), expected.getName());
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
			System.out.println(", Enabled: " + jo.getBoolean("enabled"));
			*/
			
			tenant.setKey(jo.getString("key"));
			tenant.setName(jo.getString("name"));
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
			
			/*
			System.out.print(i + "  Key: " + row.get(0).getText());
			System.out.print(", Name: " + row.get(1).getText());
			System.out.println(", Enabled: " + row.get(2).getText());
			*/
			
			tenant.setKey(row.get(0).getText());
			tenant.setName(row.get(1).getText());
			tenant.setEnabled(convertToBoolean(row.get(2).getText()));
					
			expectedTenantsList.add(tenant);
			
		}
		
		return expectedTenantsList;
		
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

	
	public static void verifyListSorted() throws InterruptedException {		
		
		System.out.println("\n  ** Sort List by Name in Ascending Order **");
		
		clickArrowSorting("Name", "ASC");
				
		verifySorting("NAME", "ASC");
		
		System.out.println("\n  ** Sort List by Name in Descending Order **");
		
		clickArrowSorting("Name", "DESC");
				
		verifySorting("NAME", "DESC");
		
		System.out.println("\n  ** Sort List by Enabled in Ascending Order **");
		
		clickArrowSorting("Enabled", "ASC");
				
		verifySorting("IS_ENABLED", "ASC");
		
		System.out.println("\n  ** Sort List by Enabled in Descending Order **");
		
		clickArrowSorting("Enabled", "DESC");
				
		verifySorting("IS_ENABLED", "DESC");
		
		System.out.println("\n  ** Sort List by Key in Ascending Order **");
		
		clickArrowSorting("Key", "ASC");
				
		verifySorting("KEY", "ASC");
		
		System.out.println("\n  ** Sort List by Key in Descending Order **");
		
		clickArrowSorting("Key", "DESC");
		
		verifySorting("KEY", "DESC");
		
		clickArrowSorting("Key", "ASC");
		
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
				
			} else if (sortBy.equals("IS_ENABLED")) {
			
				sortedListActual.add(Boolean.toString(tenantsList.get(i).isEnabled()));
				sortedListExpected.add(Boolean.toString(tenantsList.get(i).isEnabled()));
				
			}
			
		}
		
		Collections.sort(sortedListExpected, String.CASE_INSENSITIVE_ORDER);
				
		if (sortDirection.equals("DESC")) Collections.reverse(sortedListExpected); 
		
		System.out.println("Sorted List Expected");
		
		for (int i = 0; i < sortedListExpected.size(); i++) {
			
			System.out.println("  * " + sortedListExpected.get(i));
			
		}
		
		System.out.println("Sorted List Actual");
		
		for (int i = 0; i < sortedListActual.size(); i++) {
			
			System.out.println("  * " + sortedListActual.get(i));
			
		}
		
		Assert.assertEquals(sortedListActual, sortedListExpected);
		
	}
	
	
	public static void clickArrowSorting(String sortBy, String sortDirection) throws InterruptedException {
		
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
		
	}


	public static void selectSizeOfList(int size) {
		
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
		
	}


	public static void verifyDataAndSorting() throws InterruptedException, IOException, JSONException {

		// Get all the "sizes" into a list
		
		List<WebElement> listSizesElements = driver.findElements(By.xpath("//div/span[text()='Size: ']/following-sibling::span/label/input"));
		
		
		for (int i = 0; i < listSizesElements.size(); i++) {
			
			int pageSize = Integer.parseInt(listSizesElements.get(i).getAttribute("value"));
			
			selectSizeOfList(pageSize);
			
			Thread.sleep(2000);  // -- see if it can be changed to waitfor.... 
			
			verifyData(1, pageSize);
			
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
			
			selectSizeOfList(pageSize);
			Thread.sleep(2000);  // -- see if it can be changed to waitfor....
			
			int totalPages = 1;
			
			if ((totalCount % pageSize) > 0)
				totalPages = (totalCount / pageSize) + 1;
			
			if ((totalCount % pageSize) == 0)
				totalPages = totalCount / pageSize;
			
			// Go through all the pages and verify data on each page
			
			for (int page = 1; page <= totalPages; page++) {
			
				clickPageNumber(page);
				
				System.out.println("page = " + page);
				verifyData(page, pageSize);
				
			}
			
				
		}
			
		
	}
	
	
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
			
			selectSizeOfList(pageSize);
			Thread.sleep(2000);  // -- see if it can be changed to waitfor....
			
			int totalPages = 1;
			
			if ((totalCount % pageSize) > 0)
				totalPages = (totalCount / pageSize) + 1;
			
			if ((totalCount % pageSize) == 0)
				totalPages = totalCount / pageSize;
			
			// Go through all the pages and verify data on each page
			
			for (int page = 1; page <= totalPages; page++) {
			
				clickPageNumber(page);
				
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



	// Clicks on the page number at the bottom - pagination bar
	private static void clickPageNumber(int page) throws InterruptedException {
		
		driver.findElement(By.xpath("//ul[@class='pagination']/li/a[contains(text()," + "\"" + page + "\"" + ")]")).click();
		Thread.sleep(3000);
	}


	
}
