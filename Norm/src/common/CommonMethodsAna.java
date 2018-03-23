package common;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import baseItems.BaseMain;

public class CommonMethodsAna  extends BaseMain {

	
	public static List<WebElement> getSizesOfPages() {

		return driver.findElements(By.xpath("//span/label/input[@name='pageSize']"));    
		
	}

	
	// Clicks on the page number at the bottom - pagination bar
	public static void clickPageNumber(int page) throws InterruptedException {
		
		driver.findElement(By.xpath("//ul[@class='pagination']/li/a[contains(text()," + "\"" + page + "\"" + ")]")).click();
		Thread.sleep(3000);
	}
	
	
	public static String GetNonRequiredItem(JSONObject jo,  String item) throws JSONException
	{
		try
		{
			jo.getString(item);				
		}
		catch (Exception e) 
		{
			return "";
		}	
		
		return jo.getString(item);
	}
	
	
	// ENABLED/DISABLED values are converted to true/false
	public static boolean convertToBoolean(String isEnabled) {
		
		if (isEnabled.equals("ENABLED")) {
	
			return true;
			
		} else if (isEnabled.equals("DISABLED")) {
			
			return false;
			
		}
		return false; 
	
	}
	
	
	public static void selectSizeOfList(int size) {
		
		System.out.println("Size to select: " + size);
		
		String xpath1 = "//span/label";
		String xpath2 = "/input[@name='pageSize']";
		
		List<WebElement> sizeRadioButtons = driver.findElements(By.xpath(xpath1 + "/input"));
		
		for (int i = 0; i < sizeRadioButtons.size(); i++) {
			
			WebElement radioButtonSize = sizeRadioButtons.get(i); 
						
			if (radioButtonSize.getAttribute("value").equals(Integer.toString(size))) {
				
				By by = By.xpath(xpath1 + "[" + (i+1) + "]" + xpath2 + "/..");
				
				driver.findElement(By.xpath(xpath1 + "[" + (i+1) + "]" + xpath2 + "/..")).click();
				//driver.findElement(By.xpath(xpath + "[" + (i+1) + "]/..")).click();
				
				// Wait for size to be selected 
				WebDriverWait wait = new WebDriverWait(driver, 3);
				
				try {
					// Sometimes the class value ends with '.. focus active' 
					wait.until(ExpectedConditions.attributeToBe(by, "class", "btn btn-secondary btn-sm focus active"));
					
				} catch (TimeoutException e) {
					// and some other times value ends with '.. active focus'
					wait.until(ExpectedConditions.attributeToBe(by, "class", "btn btn-secondary btn-sm active focus"));
					
				}
				
				break;
				
			}
								
		}
		
	}
	
	
	public static void clickArrowSorting(String sortBy, String sortDirection) throws InterruptedException {
		
		WebElement arrow = driver.findElement(By.xpath("//div[@class='table-responsive']/table/thead/tr/th/span[text()='" + sortBy + "']/following-sibling::span"));
		
		// If sortDirection is ASC and the arrow is not already ascending, click arrow once
		if (sortDirection.equals("ASC") && !arrow.getAttribute("class").endsWith("sort-asc")) { 
			
			arrow.click();
			
		} 
		// If sortDirection is DESC and the arrow is ascending, click arrow once
		else if (sortDirection.equals("DESC") && arrow.getAttribute("class").endsWith("sort-asc")) { 
			
			arrow.click();
			
		}
		// If sortDirection is DESC and the arrow is NOT ascending (2 arrows displayed), click arrow twice 
		else if (sortDirection.equals("DESC") && !arrow.getAttribute("class").endsWith("sort-asc")) { 
			
			arrow.click();
			arrow.click();
			
		} 
		// If sortDirection is DESC and the arrow is already descending no click is needed  
		else if (sortDirection.equals("DESC") && arrow.getAttribute("class").endsWith("sort-desc")) { 
			// No clicks - already descending			
		}
		
		Thread.sleep(2000);
		
	}

	
	public static String getTotalCountItems() {
		
		return driver.findElement(By.xpath("//div/jhi-item-count")).getText(); 
		
	}
	
	
}
