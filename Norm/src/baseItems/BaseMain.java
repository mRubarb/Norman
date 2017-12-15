package baseItems;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaseMain 
{

	public static WebDriver driver;
	public static String baseUrl = "http://www.google.com/";
	
	public static void SetupDiver()
	{
		String projectPath = ""; 
		File currentDirectory = new File(".");
		projectPath = currentDirectory.getAbsolutePath();
		System.out.println(projectPath);
		System.setProperty("webdriver.chrome.driver", projectPath + "\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-extensions");
		options.addArguments("disable-infobars");  // <-- Line added by Ana. Needed because with the chromedriver 2.28, there's an info bar that we don't want to have when browser is launched
		driver = new ChromeDriver(options);			
	}
	
	public static void OpenConsole()
	{
        driver.get(baseUrl);
		
        // maximize and configure timeouts
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);        
	}
	
	public static void ShowText(String str) 
	{
		System.out.println(str);
	}	
	
	public static void ShowInt(int intIn) 
	{
		System.out.println(intIn);
	}
	
	public static void ShowListOfStrings(List<String> strList) 
	{
		for(String str : strList)
		{
			ShowText(str);
		}
	}	

	public static void WaitForElementClickable(By by, int waitTime, String message)
	{
	    try
	    {
	    	WebDriverWait wait = new WebDriverWait(driver, waitTime);
	    	wait.until(ExpectedConditions.elementToBeClickable(by));
	    }
	    catch (WebDriverException e)
	    {
	        throw new WebDriverException(message);
	    }
	}	
    
	public static boolean WaitForElementVisible(By by, int timeOut) throws Exception 
	{
	    try
	    {
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
		    wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	    }
        catch (Exception e)
        {
	        //System.out.println(e.toString());
	        throw new Exception(e.toString());
        }	    
	    return true;
	}	

	public static boolean WaitForElementPresent(By by, int timeOut) throws Exception 
	{
	    try
	    {
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
		    wait.until(ExpectedConditions.presenceOfElementLocated(by));
	    }
        catch (Exception e)
        {
	        //System.out.println(e.toString());
	        throw new Exception(e.toString());
        }	    
	    return true;
	}			
	
}
