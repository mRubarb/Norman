package testItems;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import baseItems.BaseMain;
import common.CommonMethods;
import pages.Deployments;


public class TC_DeploymentsList extends BaseMain {

	@BeforeClass
	public static void setUp() throws Exception
	{
		SetupDiver();
		OpenConsole();
		LoginOneClick();
		Thread.sleep(3000);
	}
	
	
	
	@Test
	public void testCase_DeploymentsList() throws InterruptedException, IOException, JSONException 
	{
					
		CommonMethods.selectItemPlatformDropdown("Deployments");
		CommonMethods.verifyTitle("Deployments");
		/*
		Deployments.verifyColumnsNames();
		
		Deployments.verifyData();
		*/
		Deployments.verifyDataAndSorting();
		
		//Deployments.verifyPaging();
		
		
			
	}
	
	
	@AfterClass
	public void finish() 
	{
		JOptionPane.showMessageDialog(null, "Ready To Close Driver.");		
		driver.close();

	}
	
}
