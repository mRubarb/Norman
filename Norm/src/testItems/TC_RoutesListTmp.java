package testItems;

import javax.swing.JOptionPane;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import baseItems.BaseMain;
import common.CommonMethods;
import pages.RoutesTmp;


public class TC_RoutesListTmp extends BaseMain {

	@BeforeClass
	public static void setUp() throws Exception
	{
		SetupDiver();
		OpenConsole();
		LoginOneClick();
		Thread.sleep(3000);
	}
	
	
	@Test
	public void testCase_RoutesList() throws Exception 
	{
				
		CommonMethods.selectItemPlatformDropdown("Routes");
		
		RoutesTmp.verifyFiltering("APR", "RVM", "RIGBY", "Show Disabled Routes Only");  // *** IN PROGRESS ****

		//RoutesTmp.verifyFilteringCombined("APR", "RVM", "RVM_1", "Show Enabled Routes Only");
		
		
	}
	
	
	@AfterClass
	public void finish() 
	{
		JOptionPane.showMessageDialog(null, "Tenants Tests Finished.");		
		driver.close();

	}
	
	
	
	
}
