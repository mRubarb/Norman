package testItems;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import baseItems.BaseMain;
import common.CommonMethods;
import pages.Applications;

public class TC_ApplicationsList extends BaseMain
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
		Applications.GoToApplications();
		CommonMethods.verifyTitle("Applications");	
		Applications.VerifyFullList(); // TC948 step 1. // fixed on 3/20/18
		//Applications.VerifyPageSettings(); // TC948 step 2	
		//Applications.VerifyPaging(); // TC948 step 3		
		//Applications.VerifySortingFullPage(); // fixed 3/20/18
		//Applications.VerifyFilteringByTenant(); // TC948 step 7 (tenant filter). 
		
		Applications.VerifySortingMultiplePages(); // done 3/23/18 -- sorting with different page sizes applications.
	}
	
	
	@AfterClass
	public void Finish() 
	{
		JOptionPane.showMessageDialog(null, "Ready To Close Driver.");		
		driver.close();
	}
}
