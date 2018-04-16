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
import classes.Deployment;
import common.CommonMethods;


public class Deployments extends BaseMain 
{
	
	private static String xpathKey = "//input[@formcontrolname='key']";
	private static String xpathVersion = "//input[@formcontrolname='version']";
	private static String xpathDescription = "//textarea[@formcontrolname='description']";
	private static String xpathEnabled = "//label[1]/input[@formcontrolname='enabled']";
	private static String xpathDisabled = "//label[2]/input[@formcontrolname='enabled']";
	
	public static String[] propertiesNames = {"Key", "Application Key", "Name", "Version", "Description", "Enabled"};
	
	
	public static void verifyColumnsNames() {
		
		List<WebElement> columnNamesElements = driver.findElements(By.xpath("//div[@class='table-responsive']/table/thead/tr/th"));
				
		List<String> columnNames = new ArrayList<>();
		
		for (int i = 0; i < columnNamesElements.size(); i++) {
			
			String name = columnNamesElements.get(i).getText();
			
			if (!name.isEmpty()) {
				
				columnNames.add(name);
				
			}
			
		}
		
		for (int i = 0; i < columnNames.size(); i++) {
			
			System.out.println(columnNames.get(i));
			
			Assert.assertTrue(columnNames.contains(propertiesNames[i]));
		
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
			
			/*
			System.out.println(i+1);
			System.out.println("Actual Key: " + actual.getKey() + ", Expected Key: " + expected.getKey());
			System.out.println("Actual Application key: " + actual.getApplicationKey() + ", Expected Application Key: " + expected.getApplicationKey());
			System.out.println("Actual Description: " + actual.getDescription() + ", Expected Description: " + expected.getDescription());
			System.out.println("Actual Version: " + actual.getVersion() + ", Expected Version: " + expected.getVersion());
			System.out.println("Actual isEnabled: " + actual.isEnabled() + ", Expected isEnabled: " + expected.isEnabled());
			*/
			
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
			deployment.setDescription(CommonMethods.GetNonRequiredItem(jo, "description"));
			deployment.setApplicationKey(jo.getString("applicationKey"));
			deployment.setVersion(jo.getString("version"));
			deployment.setEnabled(jo.getBoolean("enabled"));
		
			actualDeploymentsList.add(deployment);
			
		}
		
		return actualDeploymentsList;
			
	}
	

	private static List<Deployment> addDeploymentsFromUItoList() {

		List<Deployment> expectedDeploymentsList = new ArrayList<>();
		//List<WebElement> row = new ArrayList<>();
		
		int rowTotalCount = driver.findElements(By.xpath("//div[@class='table-responsive']/table/tbody/tr")).size();
		
		System.out.println("# rows: " + rowTotalCount);
		
		for (int i = 1; i <= rowTotalCount; i++) {
			
			//row = driver.findElements(By.xpath("//div[@class='table-responsive']/table/tbody/tr[" + i + "]/td"));
			String key = driver.findElement(By.xpath("//div[@class='table-responsive']/table/tbody/tr[" + i + "]/td[1]")).getText();
			String appKey = driver.findElement(By.xpath("//div[@class='table-responsive']/table/tbody/tr[" + i + "]/td[2]/div[1]/a")).getText();
			String appName = driver.findElement(By.xpath("//div[@class='table-responsive']/table/tbody/tr[" + i + "]/td[2]/div[2]")).getText();
			String version = driver.findElement(By.xpath("//div[@class='table-responsive']/table/tbody/tr[" + i + "]/td[3]")).getText();
			String description = driver.findElement(By.xpath("//div[@class='table-responsive']/table/tbody/tr[" + i + "]/td[4]")).getText();
			String enabled = driver.findElement(By.xpath("//div[@class='table-responsive']/table/tbody/tr[" + i + "]/td[5]")).getText();

			Deployment deployment = new Deployment();

			deployment.setKey(key);
			deployment.setApplicationKey(appKey);
			deployment.setVersion(version);
			deployment.setDescription(description);
			deployment.setEnabled(CommonMethods.convertToBoolean(enabled));
			
			expectedDeploymentsList.add(deployment);
			
			/*
			System.out.print(i + "  Key: " + key);
			System.out.print(", Application Key: " + appKey);
			System.out.print(", Application Name: " + appName);
			System.out.print(", Version: " + version);
			System.out.print(", Description: " + description);
			System.out.println(", Enabled: " + enabled);
			*/
		}
		
		return expectedDeploymentsList;
		
	}

	public static void verifyDataAndSorting() throws InterruptedException, IOException, JSONException {
		
		// Get all the "sizes" into a list
		List<WebElement> listSizesElements = CommonMethods.getSizesOfPages();  //driver.findElements(By.xpath("//div/span[text()='Size: ']/following-sibling::span/label/input"));
		
		for (int i = 0; i < listSizesElements.size(); i++) {
			
			int pageSize = Integer.parseInt(listSizesElements.get(i).getAttribute("value"));
			
			CommonMethods.selectSizeOfList(pageSize);
			
			// ** T-960:Tenants list can be sorted
			verifyListSorted();
			
			verifyDataFromUIMatchesAPI(1, pageSize);
			
		}	
		
	}
	
	
	// It verifies that the data on the UI matches the data from the API
	public static void verifyDataFromUIMatchesAPI(int page, int pageSize) throws IOException, JSONException {
		
		String token = CommonMethods.GetTokenFromPost();
		String url = baseUrl.replace("#", "") + "platformservice/api/v1/deployments";  //"http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/deployments";
		String apiType = "\"" + "deployments" + "\"" + ":";
				
		String queryParameters = "?page=" + page + "&pageSize=" + pageSize;
		
		JSONArray jsonArrayDeployments = CommonMethods.GetJsonArrayWithUrl(token, url + queryParameters, apiType);
		
		List<Deployment> actualDeploymentsList = putJsonArrayIntoList(jsonArrayDeployments);
		List<Deployment> expectedDeploymentsList = addDeploymentsFromUItoList();
		
		System.out.println("  Verify Data From UI Matches API - Page: " + page + ", Page Size: " + pageSize); 
		
		for (int i = 0; i < actualDeploymentsList.size(); i++) {
			
			Deployment actual = actualDeploymentsList.get(i);
			Deployment expected = expectedDeploymentsList.get(i);
			
			/*
			System.out.println(i+1);
			System.out.println("Actual Key: " + actual.getKey() + ", Expected Key: " + expected.getKey());
			System.out.println("Actual Application Key: " + actual.getApplicationKey() + ", Expected Application Key: " + expected.getApplicationKey());
			System.out.println("Actual Version: " + actual.getVersion() + ", Expected Version: " + expected.getVersion());
			System.out.println("Actual Description: " + actual.getDescription() + ", Expected Description: " + expected.getDescription());
			System.out.println("Actual isEnabled: " + actual.isEnabled() + ", Expected isEnabled: " + expected.isEnabled());
			*/	
			Assert.assertEquals(actual.getKey(), expected.getKey());
			Assert.assertEquals(actual.getApplicationKey(), expected.getApplicationKey());
			Assert.assertEquals(actual.getVersion(), expected.getVersion());
			Assert.assertEquals(actual.isEnabled(), expected.isEnabled());
			
			// "Description" may be truncated on UI if it's too long
			if(expected.getDescription().endsWith("…") && expected.getDescription().length() < actual.getDescription().length()) expected.setDescription(expected.getDescription().replace("…", ""));
			Assert.assertTrue(actual.getDescription().contains(expected.getDescription()));  
			
		}
		
		
	}

	
	public static void verifyListSorted() throws InterruptedException {		
		
		System.out.println("\n  ** Sort List by Key in Ascending Order **");
		
		CommonMethods.clickArrowSorting("Key", "ASC");
				
		verifySortingObjects("KEY", "ASC");
		
		System.out.println("\n  ** Sort List by Key in Descending Order **");
		
		CommonMethods.clickArrowSorting("Key", "DESC");
		
		verifySortingObjects("KEY", "DESC");
	
		CommonMethods.clickArrowSorting("Key", "ASC");
		
		System.out.println("\n  ** Sort List by Version in Ascending Order **");
		
		CommonMethods.clickArrowSorting("Version", "ASC");
		
		verifySortingObjects("VERSION", "ASC");
		
		System.out.println("\n  ** Sort List by Version in Descending Order **");
		
		CommonMethods.clickArrowSorting("Version", "DESC");
				
		verifySortingObjects("VERSION", "DESC");
		
		System.out.println("\n  ** Sort List by Enabled in Ascending Order **");
		
		CommonMethods.clickArrowSorting("Enabled", "ASC");
				
		verifySortingObjects("IS_ENABLED", "ASC");
		
		System.out.println("\n  ** Sort List by Enabled in Descending Order **");
		
		CommonMethods.clickArrowSorting("Enabled", "DESC");
				
		verifySortingObjects("IS_ENABLED", "DESC");
	
		CommonMethods.clickArrowSorting("Key", "ASC");
		
	}
	
	
	// Verify that list of deployments is sorted according to the sortBy and sortDirection parameters. 
	// This method sorts the list of deployments (objects), not only the String representing the sortBy criteria
	public static void verifySortingObjects(String sortBy, String sortDirection) {
		
		List<Deployment> deploymentsListFromUI = addDeploymentsFromUItoList();
		List<Deployment> deploymentsListSorted = new ArrayList<>();
		
		for (int i = 0; i < deploymentsListFromUI.size(); i++) {
			
			deploymentsListSorted.add(deploymentsListFromUI.get(i));
			
		}
		
		
		// Trying different approach to do sorting --- March 22
		// Using Comparator so the list with the deployments is sorted, 
		// and not only the property to be sorted by is sorted 
		if (sortBy.equals("KEY") && sortDirection.equals("ASC")) {
			
			Collections.sort(deploymentsListSorted, Deployment.keyComparatorAsc);			
			
		} else if (sortBy.equals("KEY") && sortDirection.equals("DESC")) {
			
			Collections.sort(deploymentsListSorted, Deployment.keyComparatorDesc);
			
		} else if (sortBy.equals("VERSION") && sortDirection.equals("ASC")) {
			
			Collections.sort(deploymentsListSorted, Deployment.versionComparatorAsc);
			
		} else if (sortBy.equals("VERSION") && sortDirection.equals("DESC")) {
			
			Collections.sort(deploymentsListSorted, Deployment.versionComparatorDesc);
			
		} else if (sortBy.equals("IS_ENABLED") && sortDirection.equals("ASC")) {
		
			Collections.sort(deploymentsListSorted, Deployment.enabledComparatorAsc);
			
		} else if (sortBy.equals("IS_ENABLED") && sortDirection.equals("DESC")) {
		
			Collections.sort(deploymentsListSorted, Deployment.enabledComparatorDesc);
			
		}
		
		System.out.println("Sorted List Expected");
		
		for (int i = 0; i < deploymentsListSorted.size(); i++) {
			
			System.out.print(i + "  Key: " + deploymentsListSorted.get(i).getKey());
			System.out.print(", Application Key: " + deploymentsListSorted.get(i).getApplicationKey());
			System.out.print(", Version: " + deploymentsListSorted.get(i).getVersion());
			System.out.print(", Description: " + deploymentsListSorted.get(i).getDescription());
			System.out.println(", Enabled: " + deploymentsListSorted.get(i).isEnabled());
			
		}
		
		System.out.println("Sorted List Actual - from UI");
		
		for (int i = 0; i < deploymentsListFromUI.size(); i++) {
			
			System.out.print(i + "  Key: " + deploymentsListFromUI.get(i).getKey());
			System.out.print(", Application Key: " + deploymentsListFromUI.get(i).getApplicationKey());
			System.out.print(", Version: " + deploymentsListFromUI.get(i).getVersion());
			System.out.print(", Description: " + deploymentsListFromUI.get(i).getDescription());
			System.out.println(", Enabled: " + deploymentsListFromUI.get(i).isEnabled());
			
		}
		
		Assert.assertEquals(deploymentsListFromUI, deploymentsListSorted);	
		
	}
	
	
	// Verify that list of deployments is sorted according to the sortBy and sortDirection parameters. 
	public static void verifySorting(String sortBy, String sortDirection) {
		
		List<Deployment> deploymentsList = addDeploymentsFromUItoList();
		
		List<String> sortedListActual = new ArrayList<String>();
		List<String> sortedListExpected = new ArrayList<String>();
		
			
		for (int i = 0; i < deploymentsList.size(); i++) {
			
			if (sortBy.equals("KEY")) {
				
				sortedListActual.add(deploymentsList.get(i).getKey());
				sortedListExpected.add(deploymentsList.get(i).getKey());
				
			} else if (sortBy.equals("VERSION")) {
				
				sortedListActual.add(deploymentsList.get(i).getVersion());
				sortedListExpected.add(deploymentsList.get(i).getVersion());
				
			} else if (sortBy.equals("IS_ENABLED")) {
			
				sortedListActual.add(Boolean.toString(deploymentsList.get(i).isEnabled()));
				sortedListExpected.add(Boolean.toString(deploymentsList.get(i).isEnabled()));
				
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
		
		List<WebElement> listSizesElements = CommonMethods.getSizesOfPages(); // xpath("//div/span[text()='Size: ']/following-sibling::span/label/input"));
		
		/*System.out.println("listSizesElements.size(): " + listSizesElements.size());
		
		for (int i = 0; i < listSizesElements.size(); i++) {
			
			System.out.println("listSizesElements " + i + ": " + listSizesElements.get(i).getAttribute("value"));
			
		}*/
		
		String totalCountItems = CommonMethods.getTotalCountItems(); //driver.findElement(By.xpath("//div/jhi-item-count")).getText();
		
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

	
	public static void printDeploymentsList(List<Deployment> deploymentsList, String sortBy) {
		
		for (int i = 0; i < deploymentsList.size(); i++) {
			
			if (sortBy.equals("KEY")) {
				
				System.out.println("  Key: " + deploymentsList.get(i).getKey());
				
			} if (sortBy.equals("VERSION")) {
				
				System.out.print("  Version: " + deploymentsList.get(i).getVersion());
				
			} if (sortBy.equals("IS_ENABLED")) {
			
				System.out.println("  Enabled: " + deploymentsList.get(i).isEnabled());
			
			}
			
		}
		
	}

	public static void addDeployment(String deploymentKey, String applicationKey) throws Exception {
		
		String xpathButtonAdd = "//div/h2/button[2]";
		
		WaitForElementPresent(By.xpath(xpathButtonAdd), 3);
		
		driver.findElement(By.xpath(xpathButtonAdd)).click();
			
		driver.findElement(By.xpath(xpathKey)).sendKeys(deploymentKey);
		
		CommonMethods.clickFilterDropdown("Select Application");
		
		CommonMethods.enterSearchCriteria("Select Application", applicationKey);
		
		driver.findElement(By.xpath(xpathVersion)).sendKeys("18.1.1");
		
		driver.findElement(By.xpath(xpathDescription)).sendKeys("Testing add deployment - automation");
		
		String xpathButtonSave = "//button/span[text()='Save']/..";
		
		driver.findElement(By.xpath(xpathButtonSave)).submit();
	
	}

	
	public static void editDeployment(String deploymentKey, String applicationKey) throws Exception {
		
		int pageSize = 10;
		
		CommonMethods.selectSizeOfList(pageSize);	
		
		// Filter by Application
		CommonMethods.clickFilterDropdown("All Applications");
		
		CommonMethods.enterSearchCriteria("All Applications", applicationKey);
		
		// make sure that the Edit button clicked belongs to the deployment that needs to be edited
		
		for (int i = 1; i <= pageSize; i++) {
		
			String xpathButtonEdit = "//table/tbody/tr[" + i + "]/td[5]/div/button/span[text()='Edit']";
			
			driver.findElement(By.xpath(xpathButtonEdit)).click();	
			
			String xpathKeyPopUp = "//jhi-tenant-management-dialog/form/div[2]/div/dl/dd";
			
			if (driver.findElement(By.xpath(xpathKeyPopUp)).getText().equals(deploymentKey)) {
				
				System.out.println("Deployment found");
				break;
			}
			System.out.println("Deployment NOT found");
			
			// If the deployment clicked is not the deployment that we need then click Cancel button
			driver.findElement(By.xpath("//button/span[text()='Cancel']/..")).click();
			
		}
		
		// Modify Version
		driver.findElement(By.xpath(xpathVersion)).clear();
		driver.findElement(By.xpath(xpathVersion)).sendKeys("18.1.2.3");
	
		// Modify Description
		driver.findElement(By.xpath(xpathDescription)).clear();
		driver.findElement(By.xpath(xpathDescription)).sendKeys("Changing the deployment's description - Automation!!");
		
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
	
	
		
	
	// ***************************************************
	// **** METHODS TO BE ADDED TO COMMON METHODS ********
	// ***************************************************
	
}
