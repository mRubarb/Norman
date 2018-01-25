package pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import baseItems.BaseMain;
import classes.Deployment;
import common.CommonMethods;


public class Deployments extends BaseMain 
{
	
	public static String[] propertiesNames = {"Key", "Application Key", "Version", "Description", "Enabled"};
	
	
	public static void verifyColumnsNames() {
		
		List<WebElement> columnNames = driver.findElements(By.xpath("//div[@class='table-responsive']/table/thead/tr/th"));
		
		for (int i = 0; i < columnNames.size(); i++) {
			
			System.out.println(columnNames.get(i).getText());
			Assert.assertEquals(propertiesNames[i], columnNames.get(i).getText());
		
		}
		
	}
	
	public static void verifyData() throws IOException, JSONException {
		
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/deployments"; //"http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/deployments";
		String apiType = "\"" + "deployments" + "\"" + ":";
		
		JSONArray jsonArrayDeployments = CommonMethods.GetJsonArrayWithUrl(token, url, apiType);
		
		List<Deployment> actualDeploymentsList = putJsonArrayIntoList(jsonArrayDeployments);
		List<Deployment> expectedDeploymentsList = addDeploymentsFromUItoList();
		
		for (int i = 0; i < actualDeploymentsList.size(); i++) {
			
			Deployment actual = actualDeploymentsList.get(i);
			Deployment expected = expectedDeploymentsList.get(i);
			
			System.out.println(i+1);
			System.out.println("Actual Key: " + actual.getKey() + ", Expected Key: " + expected.getKey());
			System.out.println("Actual Application key: " + actual.getApplicationKey() + ", Expected Application Key: " + expected.getApplicationKey());
			System.out.println("Actual Description: " + actual.getDescription() + ", Expected Description: " + expected.getDescription());
			System.out.println("Actual Version: " + actual.getVersion() + ", Expected Version: " + expected.getVersion());
			System.out.println("Actual isEnabled: " + actual.isEnabled() + ", Expected isEnabled: " + expected.isEnabled());
			
			Assert.assertEquals(actual.getKey(), expected.getKey());
			Assert.assertEquals(actual.getApplicationKey(), expected.getApplicationKey());
			Assert.assertEquals(actual.getDescription(), expected.getDescription());
			Assert.assertEquals(actual.getVersion(), expected.getVersion());
			Assert.assertEquals(actual.isEnabled(), expected.isEnabled());
			
		}
		
		
	}
	
	
	private static List<Deployment> putJsonArrayIntoList(JSONArray jsonArrayDeployments) throws JSONException {
		
		List<Deployment> actualDeploymentsList = new ArrayList<>();
		
		// Put the deployments in the json array into a list of deployments
		for (int i = 0; i < jsonArrayDeployments.length(); i++) {
			
			JSONObject jo = jsonArrayDeployments.getJSONObject(i);
			Deployment deployment = new Deployment();
			
			/*			
			System.out.print(i+1 + "  Key: " + jo.getString("key"));
			System.out.print(", Application key: " + jo.getString("applicationKey"));
			System.out.print(", Version: " + jo.getString("version"));
			System.out.print(", Description: " + jo.getString("description"));
			System.out.println(", Enabled: " + jo.getBoolean("enabled"));
			*/
			
			deployment.setKey(jo.getString("key"));
			deployment.setDescription(jo.getString("description"));
			deployment.setApplicationKey(jo.getString("applicationKey"));
			deployment.setVersion(jo.getString("version"));
			deployment.setEnabled(jo.getBoolean("enabled"));
		
			actualDeploymentsList.add(deployment);
			
		}
		
		return actualDeploymentsList;
			
	}
	

	private static List<Deployment> addDeploymentsFromUItoList() {

		List<Deployment> expectedDeploymentsList = new ArrayList<>();
		List<WebElement> row = new ArrayList<>();
		
		int rowTotalCount = driver.findElements(By.xpath("//div[@class='table-responsive']/table/tbody/tr")).size();
		
		System.out.println("# rows: " + rowTotalCount);
		
		for (int i = 1; i <= rowTotalCount; i++) {
			
			row = driver.findElements(By.xpath("//div[@class='table-responsive']/table/tbody/tr[" + i + "]/td"));
			
			Deployment deployment = new Deployment();
			
			/*
			System.out.print(i + "  Key: " + row.get(0).getText());
			System.out.print(", Application key: " + row.get(1).getText());
			System.out.print(", Version: " + row.get(2).getText());
			System.out.print(", Description: " + row.get(3).getText());
			System.out.println(", Enabled: " + row.get(4).getText());
			*/
			
			deployment.setKey(row.get(0).getText());
			deployment.setApplicationKey(row.get(1).getText());
			deployment.setVersion(row.get(2).getText());
			deployment.setDescription(row.get(3).getText());
			deployment.setEnabled(Boolean.parseBoolean(row.get(4).getText()));
					
			expectedDeploymentsList.add(deployment);
		}
		
		return expectedDeploymentsList;
		
	}

	
}
