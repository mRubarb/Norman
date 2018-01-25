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

	public static String[] propertiesNames = {"Key", "Name", "Enabled"};
	
	
	public static void verifyColumnsNames() {
		
		List<WebElement> columnNames = driver.findElements(By.xpath("//div[@class='table-responsive']/table/thead/tr/th/span[1]"));
		
		for (int i = 0; i < columnNames.size(); i++) {
			
			System.out.println(columnNames.get(i).getText());
			Assert.assertEquals(propertiesNames[i], columnNames.get(i).getText());
		
		}
		
	}
	
	public static void verifyData() throws IOException, JSONException {
		
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/tenants";  //"http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/tenants";
		String apiType = "\"" + "tenants" + "\"" + ":";
		
		JSONArray jsonArrayTenants = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		List<Tenant> actualTenantsList = putJsonArrayIntoList(jsonArrayTenants);
		List<Tenant> expectedTenantsList = addTenantsFromUItoList();
		
		for (int i = 0; i < actualTenantsList.size(); i++) {
			
			Tenant actual = actualTenantsList.get(i);
			Tenant expected = expectedTenantsList.get(i);
			
			System.out.println(i+1);
			System.out.println("Actual Key: " + actual.getKey() + ", Expected Key: " + expected.getKey());
			System.out.println("Actual Name: " + actual.getName() + ", Expected Name: " + expected.getName());
			System.out.println("Actual isEnabled: " + actual.isEnabled() + ", Expected isEnabled: " + expected.isEnabled());
			
			Assert.assertEquals(actual.getKey(), expected.getKey());
			Assert.assertEquals(actual.getName(), expected.getName());
			Assert.assertEquals(actual.isEnabled(), expected.isEnabled());
			
		}
		
		
	}
	
	
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
		Thread.sleep(3000);
		
		verifySorting("NAME", "ASC");
		
		System.out.println("\n  ** Sort List by Name in Descending Order **");
		
		clickArrowSorting("Name", "DESC");
		Thread.sleep(3000);
		
		verifySorting("NAME", "DESC");
		
		System.out.println("\n  ** Sort List by Enabled in Ascending Order **");
		
		clickArrowSorting("Enabled", "ASC");
		Thread.sleep(3000);
		
		verifySorting("IS_ENABLED", "ASC");
		
		System.out.println("\n  ** Sort List by Enabled in Descending Order **");
		
		clickArrowSorting("Enabled", "DESC");
		Thread.sleep(3000);
		
		verifySorting("IS_ENABLED", "DESC");
		
		System.out.println("\n  ** Sort List by Key in Ascending Order **");
		
		clickArrowSorting("Key", "ASC");
		Thread.sleep(3000);
		
		verifySorting("KEY", "ASC");
		
		System.out.println("\n  ** Sort List by Key in Descending Order **");
		
		clickArrowSorting("Key", "DESC");
		Thread.sleep(3000);	
		
		verifySorting("KEY", "DESC");
		
	}
	
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
		
		Collections.sort(sortedListExpected);
				
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
	
	public static void clickArrowSorting(String sortBy, String sortDirection) {
		
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
		
	}

	
	
}
