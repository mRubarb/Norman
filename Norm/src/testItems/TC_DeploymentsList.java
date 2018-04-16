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
		CommonMethods.verifyTitle("Deployments");
		/*
		Deployments.verifyColumnsNames();
		
		Deployments.verifyData();
		
		Deployments.verifyDataAndSorting();
		
		Deployments.verifyPaging();
		
		*/
		
		// ******* TO DO ***************
		
		// Deployments.verifyFiltering("RVM", "DEP_RVM_1", "Show Enabled Deployments Only");

		// Deployments.verifyFilteringCombined("RVM", "DEP_RVM_1", "Show Enabled Deployments Only");
		
		String deploymentKey = "DEP_AUTOM_ANA";
				
		String applicationKey = "CMD";
		
		//Deployments.addDeployment(deploymentKey, applicationKey);
				
		Thread.sleep(3000);
																	// *** CONTINUE HERE ***
		Deployments.editDeployment(deploymentKey, applicationKey);  // *** IT'S FAILING ***
																	// *** CONTINUE HERE ***
		
		/*
		
			
		Thread.sleep(3000);
		
		Deployments.deleteDeployment("T_AUTOM_ANA");
		*/
		
			
	}
	
	
	@AfterClass
	public void finish() 
	{
		JOptionPane.showMessageDialog(null, "Ready To Close Driver.");		
		driver.close();

	}
	
}
