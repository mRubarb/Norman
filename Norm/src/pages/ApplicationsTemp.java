package pages;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;

import baseItems.BaseMain;
import classes.ApplicationClass;

import common.CommonMethods;

public class ApplicationsTemp extends BaseMain {

	// ******************************************************************
	// ***** ALL METHODS HERE WILL BE MOVED TO pages.Applications CLASS
	// ******************************************************************
	
	// Verify the application's data in the Application details page
	public static void verifyApplicationDetails(String applicationKey) throws Exception {
		
		CommonMethods.openElementDetailsPage(applicationKey, "application");
		
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/applications/" + applicationKey + "?includeStatistics=true";
						
		JSONObject jsonObject = CommonMethods.getSingleObject(token, url); 
		
		ApplicationClass applicationObject = putJsonObjectIntoApplicationObject(jsonObject); 
	
		String xpathName = "//jhi-application-detail/div/div/div[@class='row']/dt[text()='Name:']/following-sibling::dd";	
		String xpathDefaultHostPath = "//jhi-application-detail/div/div/div[@class='row']/dt[text()='Default host/path:']/following-sibling::dd";
		String xpathDescription = "//jhi-application-detail/div/div/div[@class='row']/dt[text()='Description:']/following-sibling::dd";
		String xpathEnabled = "//jhi-application-detail/div/div/div[@class='row']/dt[text()='Key:']/following-sibling::dd";
		
		// Verify Name
		String appNameUI = driver.findElement(By.xpath(xpathName)).getText().trim();
		Assert.assertEquals(appNameUI, applicationObject.m_Name);

		// Verify Host and Path
		String defHostPath = driver.findElement(By.xpath(xpathDefaultHostPath)).getText().trim();
		Assert.assertEquals(defHostPath, applicationObject.m_defaultHost + applicationObject.m_defaultPath);
		
		// Verify Description - in some cases there is no value for Description
		String descriptionUI = "";
		
		try {
			descriptionUI = driver.findElement(By.xpath(xpathDescription)).getText().trim();
			Assert.assertEquals(descriptionUI, applicationObject.m_Description);
			
		} catch (NoSuchElementException e) { }
		
		// Verify Enabled value
		String enabledUI = driver.findElement(By.xpath(xpathEnabled)).getText().split("   ")[1].trim();
		Assert.assertEquals(CommonMethods.convertToBoolean(enabledUI), applicationObject.m_Enabled);
		
		
		// Verify the # on each tab
		int tenantCount = Integer.parseInt(driver.findElement(By.xpath("//li[@class='nav-item']/a/div[contains(text(),'Tenants')]/span")).getText());
		int deploymentCount = Integer.parseInt(driver.findElement(By.xpath("//li[@class='nav-item']/a/div[contains(text(),'Deployments')]/span")).getText());
		int routeCount = Integer.parseInt(driver.findElement(By.xpath("//li[@class='nav-item']/a/div[contains(text(),'Routes')]/span")).getText());
		
		// System.out.println("tenantCount: " + tenantCount);
		// System.out.println("deploymentCount: " + deploymentCount);
		// System.out.println("routeCount: " + routeCount);
		
		Assert.assertEquals(tenantCount, applicationObject.m_tenantCount);
		Assert.assertEquals(deploymentCount, applicationObject.m_deploymentCount);
		Assert.assertEquals(routeCount, applicationObject.m_routeCount);
		
		driver.findElement(By.xpath("//button/span[text()=' Back']/..")).click();
		Thread.sleep(2000);
		
	}

	
	private static ApplicationClass putJsonObjectIntoApplicationObject(JSONObject jsonObject) throws JSONException {
		
		ApplicationClass application = new ApplicationClass();
		
		/*			
		System.out.print(i+1 + "  Key: " + jo.getString("key"));
		System.out.print(", Version: " + jo.getString("version"));
		System.out.print(", Application Key: " + jo.getString("applicationKey"));
		System.out.print(", Description: " + CommonMethods.GetNonRequiredItem(jsonObject, "description")
		System.out.println(", Enabled: " + jo.getBoolean("enabled"));
		*/
		
		application.m_Key = jsonObject.getString("key");
		application.m_Name = jsonObject.getString("name");
		application.m_Description = CommonMethods.GetNonRequiredItem(jsonObject, "description");
		application.m_defaultHost = CommonMethods.GetNonRequiredItem(jsonObject, "defaultHost");
		application.m_defaultPath = CommonMethods.GetNonRequiredItem(jsonObject, "defaultPath");
		application.m_Enabled = jsonObject.getBoolean("enabled");
		
		try { 
			application.m_tenantCount = jsonObject.getJSONObject("statistics").getInt("tenantCount"); 
		}
		catch (NoSuchElementException e) { }
		
		try { 
			application.m_deploymentCount = jsonObject.getJSONObject("statistics").getInt("deploymentCount"); 
		}
		catch (NoSuchElementException e) { }
		
		try { 
			application.m_routeCount = jsonObject.getJSONObject("statistics").getInt("routeCount"); 
		}
		catch (NoSuchElementException e) { }
		
		return application;
		
	}

	
	// In the application's details page verify the data displayed on each tab comparing it to the data coming from the API
	public static void verifyDetailsPages(String applicationKey) throws Exception {
		
		// 1. Open deployment's details page
		CommonMethods.openElementDetailsPage(applicationKey, "application");
		
		// 2. Verify data in Tenants tab
		CommonMethods.verifyTenantDataTabInDetailsPage(applicationKey, "application");
		
		// 3. Verify data in Deployments tab
		CommonMethods.verifyDeploymentDataTabInDetailsPage(applicationKey, "application");

		// 4. Verify data in Routes tab
		CommonMethods.verifyRouteDataTabInDetailsPage(applicationKey, "application");
		
	}

	
	
	
}
