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
	}
	
	
	@Test 
	public void SmokeTest() throws Exception 
	{
		Applications.GoToApplications();
		CommonMethods.verifyTitle("Applications");	
		
		// RMINF-154
		Applications.VerifyFullList(); // TC948 step 1. 
		
		// ** Applications.VerifyPageSettings(); // TC948 step 2 -- NOTE: this is done in 'Applications.VerifySortingMultiplePages()' below. 	
		// ** Applications.VerifyPaging(); // TC948 step 3 - NOTE: never completed - this is done in 'Applications.VerifySortingMultiplePages()' below.		
		// ** Applications.VerifySortingFullPage(); // NOTE: this is done in 'Applications.VerifySortingMultiplePages()'.
		
		Applications.VerifyFilteringByTenant(); // TC1011 - filter on tenant with one application and filter on tenant with the most applications.  
		//Applications.VerifyFilteringByEnabledDisabled(); // TC1011 - filter in enabled/disabled full list.		
		//Applications.VerifyFilteringEnabledDisabledWithTenant(); // TC1011 - combined filtering.
		// Applications.VerifySortingMultiplePages(); // TC1156 sorting with different page sizes applications. FIX THIS!! has bug.
		
		// RMINF-172 -- Add application. 
		Applications.AddValidations(); // validations
		Applications.AddScenariosOne(); // only add key and name. 
		Applications.AddScenariosTwo(true); // add all fields.
		
		// RMINF-197 -- Edit existing application from row edit.
		Applications.EditApplicationReset(); // TC1184 - reset function and button states.
		Applications.EditApplicationFromRowEdit(); // TC1184 - edit application.
		
		// RMINF-220 - the test above, Applications.EditApplicationFromRowEdit(), has already deleted an application with no dependencies.
		// this test verifies the delete application operations in delete UI, from the application list delete button, for an application with and w/o dependencies.
		Applications.EditApplicationDeleteItemsFromRowSelect();   
	}
	
	
	@AfterClass
	public void Finish() 
	{
		JOptionPane.showMessageDialog(null, "Ready To Close Driver.");		
		driver.close();
	}
}
