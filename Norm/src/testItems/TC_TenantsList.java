package testItems;

import javax.swing.JOptionPane;

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
	public void testCase_TenantsList() throws Exception 
	{
				
		CommonMethods.selectItemPlatformDropdown("Tenants");
		/*
		CommonMethods.verifyTitle("Tenants");
		
		Tenants.verifyColumnsNames();
		
		Tenants.verifyDataAndSorting();
		
		Tenants.verifyPaging();
		
		Tenants.verifyFiltering("RVM", "RVM_1", "Show Enabled Tenants Only");

		Tenants.verifyFilteringCombined("RVM", "RVM_1", "Show Enabled Tenants Only");
		*/
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
		
		//Tenants.clearTenantSearch();
	
		String tenantKey2 = "CTTI";
		
		Tenants.searchTenant("C");
		
		Thread.sleep(2000);
		
		// view tenant
		Tenants.viewTenant(tenantKey2);
		*/
		String tenantKey3 = "APR";
		
		Tenants.searchTenant("A");
		
		Thread.sleep(2000);
		
		// view tenant
		//Tenants.viewTenant(tenantKey3);
		
		// Verify applications listed in the Applications tab, in Tenant's details page
		
		
		// ********** CONTINUE HERE ************
		// ********** CONTINUE HERE ************
		// ********** CONTINUE HERE ************
		
		Tenants.verifyApplicationDataTabInDetailsPage(tenantKey3);
		
		// ********** CONTINUE HERE ************
		// ********** CONTINUE HERE ************
		// ********** CONTINUE HERE ************
		
		// Verify deployments listed in the Deployments tab, in Tenant's details page
		
		
		// Verify routes listed in the Routes tab, in Tenant's details page
		
		
	}
	
	
	@AfterClass
	public void finish() 
	{
		JOptionPane.showMessageDialog(null, "Tenants Tests Finished.");		
		driver.close();

	}
	
}
