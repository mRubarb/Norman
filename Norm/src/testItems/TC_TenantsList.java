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
		
		CommonMethods.verifyTitle("Tenants");
		
		Tenants.verifyColumnsNames();
		
		Tenants.verifyDataAndSorting();
		
		Tenants.verifyPagingNew();
		
		Tenants.verifyFiltering("RVM", "RVM_1", "Show Enabled Tenants Only");

		Tenants.verifyFilteringCombined("RVM", "RVM_1", "Show Enabled Tenants Only");
		
		
	}
	
	
	@AfterClass
	public void finish() 
	{
		JOptionPane.showMessageDialog(null, "Tenants Tests Finished.");		
		driver.close();

	}
	
}
