package testItems;

import javax.swing.JOptionPane;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import baseItems.BaseMain;
import common.CommonMethods;
import pages.Applications;
import pages.Routes;

public class TC_RoutesList extends BaseMain 
{
	
		@BeforeClass
		public static void setUp()    throws Exception
		{
			SetupDiver();
			OpenConsole();
			LoginOneClick();
			Thread.sleep(3000);
		}
		
		
		@Test
		public void SmokeTest() throws Exception 
		{
			Routes.GoToRoutes();
			CommonMethods.verifyTitle("Routes");
			Routes.VerifyFullList();

			// TODO -- setup so it doesn't do all page sizes after reaching the page size that has all items.
			// Routes.VerifySortingMultiplePages();
			
			// RMINF-173
			Routes.AddValidations();
			Routes.AddRoute();
			
			// RMINF-173: Operations Console: ADD screen for Route
			Routes.ValidatePrePopulatedItemsAndEdits_PartOne();
			Routes.ValidatePrePopulatedItemsAndEdits_PartTwo();
			Routes.ValidatePrePopulatedItemsAndEdits_PartThree();
		}
		
		@AfterClass
		public void Finish() 
		{
			JOptionPane.showMessageDialog(null, "Ready To Close Driver.");		
			driver.close();
		}	
	
}
