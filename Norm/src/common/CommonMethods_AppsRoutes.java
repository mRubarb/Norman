package common;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.testng.Assert;

import baseItems.BaseMain;

public class CommonMethods_AppsRoutes extends BaseMain
{

	public static final String saveButtonPoUp_Locator = "//button[@class='btn btn-primary']";	
	public static final String addButton_Locator = "//button[@class='btn btn-primary ml-auto p-2']";
	public static final String uiCancel_Locator = "(//button[@class='btn btn-secondary'])[2]"; 	
	
	public static final String applicationsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/applications";
	public static final String tenantsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/tenants";	
	public static final String deploymentsURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/deployments";	
	public static final String routesURL = "http://dc1testrmapp03.prod.tangoe.com:4070/platformservice/api/v1/routes";

	public static String GetXpathForTextBox(String item)
	{
		return "//input[@formcontrolname='" + item + "']";
	}
	
	public static void ShowPopup(String message)		
	{
		JOptionPane.showMessageDialog(null, message);
	}	

	// this will select the delete button in a specified applications list row. 
	public static void SelectDeleteByRowInList(int rowToSelect)
	{
		String xpath =  "(//button[@class='btn btn-danger btn-sm'])[" + rowToSelect + "]";
		ClickItem(xpath, 3);
	}

	public static void ClickItem(String xpath, int timeOut)
	{
		WaitForElementClickable(By.xpath(xpath), timeOut, "");
		driver.findElement(By.xpath(xpath)).click();
	}
	
	// this sets page to 5, 10, 20, or 50.
	// TODO: duplicate
	static public void SetUiPageSizeSelector(int index) throws InterruptedException 
	{
		if(index > 4 || index < 1)
		{
			Assert.fail("Bad index number sent to 'SetPageSize' method in Applicatiins page class.");
		}
		
		// set page size.
		WaitForElementClickable(By.xpath("(//span/label)[" + index + "]"), 5, "");
		driver.findElement(By.xpath("(//span/label)[" + index + "]")).click(); 		

		Thread.sleep(500);
		WaitForElementClickable(By.xpath("(//li[@class='page-item disabled'])[1]"), 5, "");
	}
	
	public static void SetPageSizeToMax() throws Exception 
	{
		// set page size to max.
		SetUiPageSizeSelector(4);
		WaitForElementClickable(By.xpath("(//button[@class='btn btn-info btn-sm'])[5]"), 3, "");
	}

	// take a string format like this = "RIGBY [RVM 18.1]".
	// store the name that's left of the first space
	// separate items in the square brackets and store each.
	// return all three in an array.
	public static String[] GetDeploymentInfoArray(String allText) // bladd
	{
		String deployName = SeparateKeyAndName(allText)[0]; 
		String deployAppName = SeparateKeyAndName(SeparateKeyAndName(allText)[1])[0].replace("[",""); 
		String deployVersion = SeparateKeyAndName(SeparateKeyAndName(allText)[1])[1].replace("]","");
		
		//ShowText(SeparateKeyAndName(allText)[0]);		
		//ShowText(SeparateKeyAndName(SeparateKeyAndName(allText)[1])[0].replace("[",""));
		// ShowText(SeparateKeyAndName(SeparateKeyAndName(allText)[1])[1].replace("]",""));
		
		return new String[]{deployName, deployAppName, deployVersion};
	}
	
	
	
	// receive a key and name. the key and name are separated by one or more spaces. 
	// the key can have no spaces. return the key and name in a two element array.
	public static String[] SeparateKeyAndName(String tenantNameToken)
	{
		Assert.assertTrue(tenantNameToken.contains(" ")); // make sure  there is a space.
		int index = tenantNameToken.indexOf(" "); // find first space.
		
		// return key and name array.
		return new String[]{tenantNameToken.split(" ")[0].trim(), tenantNameToken.substring(index + 1).trim()};
	}	
	
	// store some tenant info.
	public static class TenantInfo
	{
		public String m_key = "";
		public String m_name = "";
		public String m_tenantID = "";
		
		public TenantInfo(String key, String name, String tenantID) 
		{
			m_key = key;
			m_name = name;
			m_tenantID = tenantID;
		}
		
		public void Show()
		{
			System.out.println("*******************");
			System.out.println("key= " + m_key);
			System.out.println("name= " + m_name);
			System.out.println("tenantID= " + m_tenantID);
		}
	}
	
	
	// store some tenant info.
	public static class DeployInfo
	{
		public String m_key = "";
		public String m_appName = "";
		public String m_version = "";
		
		public DeployInfo(String key, String appName, String version) 
		{
			m_key = key;
			m_appName = appName;
			m_version = version;
		}
		
		public void Show()
		{
			System.out.println("*******************");
			System.out.println("key = " + m_key);
			System.out.println("app name = " + m_appName);
			System.out.println("version = " + m_version);
		}
	}
	
}
