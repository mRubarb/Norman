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

public class bobTest_SELENIUM extends BaseMain
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
	public void SmokeTest() throws InterruptedException, IOException, JSONException 
	{
		Applications.GoToApplications();
		CommonMethods.verifyTitle("Applications");	
		Applications.VerifyFullList(); // TC948 step 1.
		//Applications.VerifyPageSettings(); // TC948 step 2	
		//Applications.VerifyPaging(); // TC948 step 3		
		// Applications.VerifySortingFullPage(); // TC948 step 7
		//Applications.VerifyFilteringByTenant(); // TC948 step 7 (tenant filter). 
		
		//Applications.VerifySortingMultiplePages(); // start 2/7/17 -- sorting with different page sizes applications.
	}
	
	
	@AfterClass
	public void Finish() 
	{
		JOptionPane.showMessageDialog(null, "Ready To Close Driver.");		
		driver.close();
	}
}
