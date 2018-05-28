package testItems;

import javax.swing.JOptionPane;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import baseItems.BaseMain;
import common.CommonMethods;
import pages.Tenants;

public class TC_TenantsDetails extends BaseMain {

	@BeforeClass
	public static void setUp() throws Exception
	{
		SetupDiver();
		OpenConsole();
		LoginOneClick();
		Thread.sleep(3000);
	}
	
	
	
	@Test
	public void testCase_TenantsDetails() throws Exception 
	{
				
		CommonMethods.selectItemPlatformDropdown("Tenants");
		/*
		String tenantKey = "T_AUTOM_ANA";
				
		Tenants.addTenant(tenantKey);
		
		Thread.sleep(3000);
		
		Tenants.searchTenant("T_A");
		
		Thread.sleep(2000);
		
		Tenants.editTenant(tenantKey);
		
		Tenants.searchTenant("T_A");
		
		Thread.sleep(2000);
		
		Tenants.deleteTenant(tenantKey);
			*/
		String tenantKey3 = "BAUTO"; //"INI"; //"APR"; // "ANA_1";  
		
		//Tenants.searchTenant("B");
		
		Thread.sleep(2000);
		
		// Open Tenant details page and verify data displayed 
		Tenants.verifyTenantDetails(tenantKey3);
		
		// Verify applications listed in the Applications tab, in Tenant's details page
		// Verify deployments listed in the Deployments tab, in Tenant's details page
		// Verify routes listed in the Routes tab, in Tenant's details page
		// Verify addresses listed in the ACL tab, in Tenant's details page
		
		Tenants.verifyDetailsPages(tenantKey3);
		
	}
	
	
	@AfterClass
	public void finish() 
	{
		JOptionPane.showMessageDialog(null, "Tenants Tests Finished.");		
		driver.close();

	}
	
	
}
