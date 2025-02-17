package stockSummary;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.OutputType;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;


public class TakeScreenshots {

	String timeStamp=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format (new Date());
    private static final String SCREENSHOT_PATH = System.getProperty("user.dir") + "/screenshots/";

    // Method to capture a screenshot
    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        try {
            // Ensure the directory exists
            File screenshotDir = new File(SCREENSHOT_PATH);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs(); // Create directory if it doesn't exist
            }

            // Generate timestamp
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

            // Take a screenshot and store it as a file
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String path = SCREENSHOT_PATH + screenshotName + "_" + timeStamp + ".png";
            File destination = new File(path);
            FileUtils.copyFile(src, destination);

         //   System.out.println("Screenshot saved at: " + path); // Now reachable
            return path; // Return the correct path

        } catch (IOException ioException) {
            System.out.println("Error while saving the screenshot: " + ioException.getMessage());
        } catch (WebDriverException webDriverException) {
            System.out.println("Error capturing the screenshot: " + webDriverException.getMessage());
        }

        return null; // Return null if an exception occurs
    }
}


