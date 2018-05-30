package pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import baseItems.BaseMain;
import classes.RouteClass;
import classes.Tenant;
import common.CommonMethods;

public class RoutesTmp extends BaseMain {

	
	public static void verifyFiltering(String tenantKey, String applicationKey, String deploymentKey, String enabled) throws Exception {
		
		/*
		 * 1. Filter routes by Tenant | Application | Deployment | Enabled 
		 * 2. Get results from UI
		 * 3. Get routes filtered by the same tenant | application | deployment | enabled from API
		 * 4. Compare results 
		 * */ 
		
		String[] filterToSelect = {"All Tenants", "All Applications", "All Deployments", "Show Enabled and Disabled"};
		
		
		HashMap<String, String> filterNameMap = new HashMap<String, String>();
		
		filterNameMap.put("All Tenants", "tenantKey");
		filterNameMap.put("All Applications", "applicationKey");
		filterNameMap.put("All Deployments", "deploymentKey");
		filterNameMap.put("Show Enabled and Disabled", "enabled");
		
		String enabledValueForRequest = getEnabledValueForRequest(enabled);
		
		String[] filterValue = {tenantKey, applicationKey, deploymentKey, enabled};
		String[] queryParameterValues = {tenantKey, applicationKey, deploymentKey, enabledValueForRequest};
		
		int page = 1;
		int pageSize = 10;
			
		String queryParametersPagePortion = "?page=" + page + "&pageSize=" + pageSize;
		
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/routes";
		String apiType = "\"" + "routes" + "\"" + ":";
		
		// ***** CONTINUE HERE ***********
		
		for (int i = 0; i < filterNameMap.size(); i++) {

			String filter = filterToSelect[i];
			String value = filterValue[i];

			System.out.println("  Filter to select: " + filter + " by value: " + value);
			
			// 1.a. Click Application filter dropdown list
			CommonMethods.clickFilterDropdown(filter);
								
			// 1.b. Enter App Key / Name on the Search field
			CommonMethods.enterSearchCriteria(filter, value);
			
			
			// 2. 
			List<RouteClass> filteredListOpsConsole = addRoutesFromUItoList();
			
			/*
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
			*/
		}
		
				
		
		
		
		
	}

	public static void verifyFilteringCombined(String string, String string2, String string3, String string4) {


		
		
		
	}


	private static String getEnabledValueForRequest(String enabled) {
		
		switch(enabled) {
			
			case "Show Enabled Routes Only": 
				return "true";
			
			case "Show Disabled Routes Only": 
				return "false";
			
			case "Show Enabled and Disabled": 
				return "";
			
			default:
				return "";
		}
	}
	
	
	// Adds the routes that are listed on the UI to a list
	private static List<RouteClass> addRoutesFromUItoList() {

		List<RouteClass> expectedRoutesList = new ArrayList<>();
		List<WebElement> row = new ArrayList<>();
		
		int rowTotalCount = driver.findElements(By.xpath("//div[@class='table-responsive']/table/tbody/tr")).size();
		
		// System.out.println("# rows: " + rowTotalCount);
				
		String xpathRowPart1 = "//div[@class='table-responsive']/table/tbody/tr[";
		String xpathRowPart2 = "]/td";
		
		for (int i = 1; i <= rowTotalCount; i++) {
			
			//row = driver.findElements(By.xpath("//div[@class='table-responsive']/table/tbody/tr[" + i + "]/td"));
			
			RouteClass route = new RouteClass();
			
			route.m_Key = driver.findElement(By.xpath(xpathRowPart1 + i + xpathRowPart2 + "[1]")).getText();
			route.m_host = driver.findElement(By.xpath(xpathRowPart1 + i + xpathRowPart2 + "[2]/div[1]")).getText();
			route.m_tenantId = driver.findElement(By.xpath(xpathRowPart1 + i + xpathRowPart2 + "[2]/div[2]")).getText();
			route.m_tennantKey = driver.findElement(By.xpath(xpathRowPart1 + i + xpathRowPart2 + "[3]/div[1]")).getText();
			route.m_tennantName = driver.findElement(By.xpath(xpathRowPart1 + i + xpathRowPart2 + "[3]/div[2]")).getText();
			route.m_appKey = driver.findElement(By.xpath(xpathRowPart1 + i + xpathRowPart2 + "[4]/div[1]")).getText();
			route.m_appname = driver.findElement(By.xpath(xpathRowPart1 + i + xpathRowPart2 + "[4]/div[2]")).getText();
			route.m_deployKey = driver.findElement(By.xpath(xpathRowPart1 + i + xpathRowPart2 + "[5]/div[1]")).getText();
			route.m_deployVersion = driver.findElement(By.xpath(xpathRowPart1 + i + xpathRowPart2 + "[5]/div[2]")).getText();
			route.m_enabled = CommonMethods.convertToBoolean(driver.findElement(By.xpath(xpathRowPart1 + i + xpathRowPart2 + "[6]/div[1]")).getText());
			route.m_disabledReason = driver.findElement(By.xpath(xpathRowPart1 + i + xpathRowPart2 + "[6]/div[2]")).getText();
			route.m_allowServiceCalls = CommonMethods.convertToBoolean(driver.findElement(By.xpath(xpathRowPart1 + i + xpathRowPart2 + "[7]")).getText());
			
			
			System.out.print(i + "  Key: " + route.m_Key);
			System.out.print(", Host: " + route.m_host);
			System.out.print(", Tenant ID: " + route.m_tenantId);
			System.out.print(", Tenant Key: " + route.m_tennantKey);
			System.out.print(", Tenant Name: " + route.m_tennantName);
			System.out.print(", App Key: " + route.m_appKey);
			System.out.print(", App Name: " + route.m_appname);
			System.out.print(", Deployment Key: " + route.m_deployKey);
			System.out.print(", Deployment Version: " + route.m_deployVersion);
			System.out.print(", Enabled: " + route.m_enabled);
			System.out.print(", Disabled Reason: " + route.m_disabledReason);
			System.out.println(", Allow Service Calls: " + route.m_allowServiceCalls);
			
			
			expectedRoutesList.add(route);
		
		}
		
		return expectedRoutesList;
		
	}
	
}

