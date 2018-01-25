package testItems;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import baseItems.BaseMain;
import common.CommonMethods;
import pages.Tenants;


public class TC_TenantsList extends BaseMain {

	@BeforeClass
	public static void setUp() throws Exception
	{
		SetupDiver();
		OpenConsole();
		LoginOneClick();
		Thread.sleep(3000);
	}
	
	
	
	@Test
	public void testCase_TenantsList() throws InterruptedException, IOException, JSONException 
	{
			
		/*
		CommonMethods.selectItemPlatformDropdown("Applications");
		CommonMethods.verifyTitle("Applications");
		
		CommonMethods.selectItemPlatformDropdown("Routes");
		CommonMethods.verifyTitle("Routes");
		
		
		CommonMethods.selectItemPlatformDropdown("Deployments");
		CommonMethods.verifyTitle("Deployments");
		
		Deployments.verifyColumnsNames();
		Deployments.verifyData();
		
		Thread.sleep(3000);
		*/
		CommonMethods.selectItemPlatformDropdown("Tenants");
		CommonMethods.verifyTitle("Tenants");
		
		Tenants.verifyColumnsNames();
		Tenants.verifyData();
		
		// ** T-960:Tenants list can be sorted
		Tenants.verifyListSorted();
		
		
	}
	
	
	@AfterClass
	public void finish() 
	{
		JOptionPane.showMessageDialog(null, "Ready To Close Driver.");		
		driver.close();

	}
	
}
