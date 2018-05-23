package testItems;

import javax.swing.JOptionPane;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import baseItems.BaseMain;
import common.CommonMethods;
import pages.Deployments;


public class TC_DeploymentsList extends BaseMain {

	@BeforeClass
	public static void setUp() throws Exception
	{
		SetupDiver();
		OpenConsole();
		LoginOneClick();
		Thread.sleep(3000);
	}
	
	
	
	@Test
	public void testCase_DeploymentsList() throws Exception 
	{
					
		CommonMethods.selectItemPlatformDropdown("Deployments");
		
		String deploymentKey = "APR";
		String applicationKey = "RVM";
		
		CommonMethods.verifyTitle("Deployments");
		
		Deployments.verifyColumnsNames();
		
		Deployments.verifyData();
		
		Deployments.verifyDataAndSorting();
		
		Deployments.verifyPaging();
	
		Deployments.verifyFiltering(deploymentKey, applicationKey, "Show Enabled Deployments Only");

		Deployments.verifyFilteringCombined(deploymentKey, applicationKey, "Show Enabled Deployments Only");
		
	}
	
	
	@AfterClass
	public void finish() 
	{
		JOptionPane.showMessageDialog(null, "Deployments Tests Finished.");		
		driver.close();

	}
	
}
