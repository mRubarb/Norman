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
			//Routes.VerifyFullList();
			//Routes.VerifySortingMultiplePages();
			Routes.AddValidations();
			//Routes.AddRoute();
		}
		
		
		@AfterClass
		public void Finish() 
		{
			JOptionPane.showMessageDialog(null, "Ready To Close Driver.");		
			driver.close();
		}	
	
}
