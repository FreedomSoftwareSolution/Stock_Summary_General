package stockSummary;

import java.io.File;
import java.io.IOException;
import org.openqa.selenium.OutputType;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;


public class TakeScreenshots {
	
	// Method to capture a screenshot
    public static void takeScreenshot(WebDriver driver, String fileName) {
        try {
            // Take a screenshot and store it as a file
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File srcFile = screenshot.getScreenshotAs(OutputType.FILE);

            String folderpath="D:\\eclipse\\FreedomSoftScreens.com.in\\FailedScreenshot";
            // Specify the location to save the screenshot
            File destFile = new File(folderpath + File.separator + fileName);

            // Copy the screenshot to the destination
            FileUtils.copyFile(srcFile, destFile);

            System.out.println("Screenshot saved at: " + destFile.getAbsolutePath());
        } catch (IOException ioException) {
            System.out.println("Error while saving the screenshot: " + ioException.getMessage());
        } catch (WebDriverException webDriverException) {
            System.out.println("Error capturing the screenshot: " + webDriverException.getMessage());
        }
    }
}


