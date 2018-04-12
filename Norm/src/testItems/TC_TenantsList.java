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
	public void testCase_TenantsList() throws Exception 
	{
				
		CommonMethods.selectItemPlatformDropdown("Tenants");
		
		CommonMethods.verifyTitle("Tenants");
		
		// Tenants.verifyColumnsNames();
		
		// Tenants.verifyDataAndSorting();
		
		// Tenants.verifyPaging();
		
		// Tenants.verifyFiltering();

		//Tenants.addTenant();
		
		Tenants.searchTenant("T_A");
		
		Thread.sleep(3000);
		
		Tenants.deleteTenant("T_AUTOM_ANA");
		
		
	}
	
	
	@AfterClass
	public void finish() 
	{
		JOptionPane.showMessageDialog(null, "Ready To Close Driver.");		
		driver.close();

	}
	
}
