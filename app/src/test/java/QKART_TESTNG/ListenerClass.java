package QKART_TESTNG;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ListenerClass extends QKART_Tests implements ITestListener {
    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType, description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onTestStart(ITestResult result) {					
        System.out.println("on Test Started" +result.getName());
        takeScreenshot(driver, "TestStart", result.getName());			       		
    }
 	
    
    public void onTestSuccess(ITestResult result) {					
        System.out.println("on Test Success Method" +result.getName());	
        takeScreenshot(driver, "TestSuccess", result.getName());		
        		
    }
    
    
    public void onTestFailure(ITestResult result) {					
        System.out.println("onTestFailure Method" +result.getName());	
        takeScreenshot(driver, "onTestFailure", result.getName());		
        		
    }	


}