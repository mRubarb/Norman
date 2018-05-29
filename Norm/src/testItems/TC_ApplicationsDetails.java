package testItems;

import javax.swing.JOptionPane;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import baseItems.BaseMain;
import common.CommonMethods;
import pages.ApplicationsTemp;


public class TC_ApplicationsDetails extends BaseMain {

	@BeforeClass
	public static void setUp() throws Exception
	{
		SetupDiver();
		OpenConsole();
		LoginOneClick();
		Thread.sleep(3000);
	}
	
	
	@Test
	public void testCase_ApplicationsDetails() throws Exception 
	{
					
		CommonMethods.selectItemPlatformDropdown("Applications");
		/*
		 * Add
		 * Edit
		 * Delete ...
		 * 
		 * */	
		
		String applicationKey2 = "RVM";
		
		// Open Deployment details page and verify data displayed 
		ApplicationsTemp.verifyApplicationDetails(applicationKey2);
		
		// Verify deployments listed in the Deployments tab, in Application's details page
		// Verify tenants listed in the Tenants tab, in Application's details page
		// Verify routes listed in the Routes tab, in Application's details page
		ApplicationsTemp.verifyDetailsPages(applicationKey2);
			
		
	}
	
	
	@AfterClass
	public void finish() 
	{
		JOptionPane.showMessageDialog(null, "Applications Tests Finished.");		
		driver.close();

	}

	
}
