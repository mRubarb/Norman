package testItems;

import javax.swing.JOptionPane;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import baseItems.BaseMain;
import common.CommonMethods;
import pages.Deployments;


public class TC_DeploymentsDetails extends BaseMain {

	@BeforeClass
	public static void setUp() throws Exception
	{
		SetupDiver();
		OpenConsole();
		LoginOneClick();
		Thread.sleep(3000);
	}
	
	
	@Test
	public void testCase_DeploymentsDetails() throws Exception 
	{
					
		CommonMethods.selectItemPlatformDropdown("Deployments");
		
		String deploymentKey = "DEP_AUTOM_ANA";
		String applicationKey = "RVM";
		
		Deployments.addDeployment(deploymentKey, applicationKey);
				
		Thread.sleep(5000);
		
		Deployments.editDeployment(deploymentKey, applicationKey);  
			
		Thread.sleep(3000);
		
		Deployments.deleteDeployment(deploymentKey);
				
		Thread.sleep(3000);
				
		String deploymentKey2 = "RVM_1";
		
		// Open Deployment details page and verify data displayed 
		Deployments.verifyDeploymentDetails(deploymentKey2);
		
		// Verify applications listed in the Applications tab, in Deployment's details page
		// Verify tenants listed in the Tenants tab, in Deployment's details page
		// Verify routes listed in the Routes tab, in Deployment's details page
		// Verify addresses listed in the ACL tab, in Deployment's details page
		Deployments.verifyDetailsPages(deploymentKey2);
			
		
	}
	
	
	@AfterClass
	public void finish() 
	{
		JOptionPane.showMessageDialog(null, "Deployments Tests Finished.");		
		driver.close();

	}
	
}
