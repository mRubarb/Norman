package common;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.testng.Assert;

import baseItems.BaseMain;

public class CommonMethods_AppsRoutes extends BaseMain
{

	public static final String saveButtonPoUp_Locator = "//button[@class='btn btn-primary']";	
	public static final String addButton_Locator = "//button[@class='btn btn-primary ml-auto p-2']";
	public static final String uiCancel_Locator = "(//button[@class='btn btn-secondary'])[2]"; 	

	public static String GetXpathForTextBox(String item)
	{
		return "//input[@formcontrolname='" + item + "']";
	}
	
	public static void ShowPopup(String message)		
	{
		JOptionPane.showMessageDialog(null, message);
	}	

	// this will select the delete button in a specified applications list row. 
	public static void SelectDeleteByRowInList(int rowToSelect)
	{
		String xpath =  "(//button[@class='btn btn-danger btn-sm'])[" + rowToSelect + "]";
		ClickItem(xpath, 3);
	}

	public static void ClickItem(String xpath, int timeOut)
	{
		WaitForElementClickable(By.xpath(xpath), timeOut, "");
		driver.findElement(By.xpath(xpath)).click();
	}
	
	// this sets page to 5, 10, 20, or 50.
	// TODO: duplicate
	static public void SetUiPageSizeSelector(int index) throws InterruptedException 
	{
		if(index > 4 || index < 1)
		{
			Assert.fail("Bad index number sent to 'SetPageSize' method in Applicatiins page class.");
		}
		
		// set page size.
		WaitForElementClickable(By.xpath("(//span/label)[" + index + "]"), 5, "");
		driver.findElement(By.xpath("(//span/label)[" + index + "]")).click(); 		

		Thread.sleep(500);
		WaitForElementClickable(By.xpath("(//li[@class='page-item disabled'])[1]"), 5, "");
	}
	
	public static void SetPageSizeToMax() throws Exception 
	{
		// set page size to max.
		// CommonMethods.selectSizeOfList(50);
		SetUiPageSizeSelector(4);
		WaitForElementClickable(By.xpath("(//button[@class='btn btn-info btn-sm'])[5]"), 3, "");
	}
	
	
}
