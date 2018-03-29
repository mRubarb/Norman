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
		
		// RMINF-154
		//Applications.VerifyFullList(); // TC948 step 1. 
		// ** Applications.VerifyPageSettings(); // TC948 step 2 -- this is done in 'Applications.VerifySortingMultiplePages()' below. 	
		// ** Applications.VerifyPaging(); // TC948 step 3 - never completed - this is done in 'Applications.VerifySortingMultiplePages()' below.		
		// ** Applications.VerifySortingFullPage(); // this is done in 'Applications.VerifySortingMultiplePages()'.
		//Applications.VerifyFilteringByTenant(); // TC948 step 7 (tenant filter). 
		// Applications.VerifySortingMultiplePages(); // TC1156 sorting with different page sizes applications.
		
		// RMINF-172 
		//Applications.AddValidations();
		Applications.AddScenarios();
		
	}
	
	
	@AfterClass
	public void Finish() 
	{
		JOptionPane.showMessageDialog(null, "Ready To Close Driver.");		
		driver.close();
	}
}
