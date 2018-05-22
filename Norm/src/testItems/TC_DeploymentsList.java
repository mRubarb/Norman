package testItems;

import javax.swing.JOptionPane;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import baseItems.BaseMain;
import common.CommonMethods;
import pages.Deployments;
import pages.Tenants;


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
		/*
		CommonMethods.verifyTitle("Deployments");
		
		Deployments.verifyColumnsNames();
		
		Deployments.verifyData();
		
		Deployments.verifyDataAndSorting();
		
		Deployments.verifyPaging();
	
		Deployments.verifyFiltering("APR", "RVM", "Show Enabled Deployments Only");

		Deployments.verifyFilteringCombined("APR", "RVM", "Show Enabled Deployments Only");
		
		String deploymentKey = "DEP_AUTOM_ANA";
				
		String applicationKey = "RVM";
		
		Deployments.addDeployment(deploymentKey, applicationKey);
				
		Thread.sleep(5000);
		
		Deployments.editDeployment(deploymentKey, applicationKey);  
			
		Thread.sleep(3000);
		
		Deployments.deleteDeployment(deploymentKey);
		*/
		
		String deploymentKey = "RVM_1";
		
		// Verify applications listed in the Applications tab, in Deployment's details page
		// Verify tenants listed in the Tenants tab, in Deployment's details page
		// Verify routes listed in the Routes tab, in Deployment's details page
		// Verify addresses listed in the ACL tab, in Deployment's details page
		Deployments.verifyDetailsPages(deploymentKey);
			
		//// ****** IT FAILED *** CONTINUE HERE ****
		
		
	}
	
	
	@AfterClass
	public void finish() 
	{
		JOptionPane.showMessageDialog(null, "Ready To Close Driver.");		
		driver.close();

	}
	
}
