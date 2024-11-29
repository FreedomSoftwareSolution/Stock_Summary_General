package stockSummary;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Stock_summary_general {

	WebDriverWait wait;
	WebDriver driver;
	boolean result=false;
	//@Test /* (retryAnalyzer = RetryAnalyzer.class) */ // Add Retry Analyzer here

	@BeforeTest
	public void stock_Summary() throws InterruptedException {

		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--disable-notifications");

		driver = new ChromeDriver(ops);


		driver.get("https://beta.freedomsoft.in/Member/Login");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		String title = driver.getTitle();
		System.out.println(title);
		System.out.println("Freedom beta page launched successfully");

		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();

		WebElement userName = driver.findElement(By.id("username"));
		userName.sendKeys("mageshwar@fss");

		WebElement passWord = driver.findElement(By.id("id_password"));
		passWord.sendKeys("Fss@123#");

		WebElement subMit = driver.findElement(By.xpath("//*[@id=\"submit1\"]"));
		subMit.click();
		Thread.sleep(3000);
		subMit.click();
		System.out.println("Freedom beta Login successfully");

		wait = new WebDriverWait(driver, Duration.ofSeconds(1));
		try {
			Alert alert = wait.until(ExpectedConditions.alertIsPresent());
			if (alert != null) {
				System.out.println("Alert is present! Accepting alert...");
				alert.accept();
			}
		} catch (Exception e) {
			System.out.println("No alert was present within the timeout period.");
		}

		WebElement store = driver.findElement(By.xpath("//*[@id=\"cssmenu\"]/ul/li[8]/a"));
		WebElement stock_summary = driver.findElement(By.xpath("//*[@id=\"cssmenu\"]/ul/li[8]/ul/li[8]/a"));
		WebElement general_stock = driver.findElement(By.xpath("//*[@id=\"cssmenu\"]/ul/li[8]/ul/li[8]/ul/li[1]/a"));

		Actions actions = new Actions(driver);

		actions.moveToElement(store)
		.moveToElement(stock_summary)
		.moveToElement(general_stock)
		.click()
		.build()
		.perform();
	}

	@Test(dataProvider = "filterData",retryAnalyzer = RetryAnalyzer.class)
	public void xpath(String stocks,String stocktype,String stockqty ,String Order) throws InterruptedException
	{

		WebElement Stocklist = driver.findElement(By.xpath("//*[@id=\"txtobj3_chosen\"]"));
		Stocklist.click();

		driver.findElement(By.xpath("//*[@id=\"txtobj3_chosen\"]/div/div/input")).sendKeys(stocks+Keys.ENTER);

		WebElement StockType = driver.findElement(By.xpath("//*[@id=\"txtobj4_chosen\"]"));
		StockType.click();

		driver.findElement(By.xpath("//*[@id=\"txtobj4_chosen\"]/div/div/input")).sendKeys(stocktype+Keys.ENTER);
		
		WebElement Stockqty = driver.findElement(By.xpath("//*[@id=\"txtobj14_chosen\"]"));
		Stockqty.click();

		driver.findElement(By.xpath("//*[@id=\"txtobj14_chosen\"]/div/div/input")).sendKeys(stockqty +Keys.ENTER);

		WebElement Stockorder = driver.findElement(By.xpath("//*[@id=\"txtobj15_chosen\"]"));
		Stockorder.click();

		driver.findElement(By.xpath("//*[@id=\"txtobj15_chosen\"]/div/div/input")).sendKeys(Order+Keys.ENTER);

		
		wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		WebElement search = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[69]/table[1]/tbody[1]/tr[1]/td[2]/a[1]")));
		search.click();

		// Wait and Validate Results
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Set max wait time
		 WebElement resultsTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"rpttable\"]/thead/tr/th")));

		 // Assert visibility
			result= resultsTable.isDisplayed();
			System.out.println("Filter Successfully Loaded"+" "+result);
			
		} catch (Exception e) {

			System.out.println("Filter Unsuccessfully"+ result);
			
			if (!result) {
              TakeScreenshots.takeScreenshot(driver, "FilterErrorScreenshot.png");
            }
		}
		
		Thread.sleep(8000);
	}

	

	@DataProvider(name = "filterData")
	public Object[][] readExcelData() throws IOException {
        String filePath = "D:\\eclipse\\FreedomSoftScreens.com.in\\ExcelData\\Stock Summary Tested.xlsx"; // Update with your Excel file path
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = WorkbookFactory.create(fis);
        Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
        Iterator<Row> rowIterator = sheet.iterator();

        List<Object[]> data = new ArrayList<>();
        int rowIndex = 0;

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (rowIndex == 0 || isRowEmpty(row)) { // Skip header and empty rows
                rowIndex++;
                continue;
            }

            String stock = getCellValue(row.getCell(0));
            String stockType = getCellValue(row.getCell(1));
            String stockQty = getCellValue(row.getCell(2));
            String orderBy = getCellValue(row.getCell(3));

            data.add(new Object[]{stock, stockType, stockQty, orderBy});
            rowIndex++;
        }
        workbook.close();
        fis.close();

        return data.toArray(new Object[0][]);
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

 @DataProvider	
public Object[][] filterData1(){
	Object login[][]= {

			{"Consolidated", "General", "All", "Group Wise"},
			{"Consolidated", "General", "All", "Material Wise"},
			{"Consolidated", "General", "All", "HSN Wise"},
			{"Consolidated", "Labour", "All", "Group Wise"},
			{"Consolidated", "Labour", "All", "Material Wise"},
			{"Consolidated", "Labour", "All", "HSN Wise"}
			

	};
	return login;
}

 //2508--user id

  @AfterTest
  public void tearDown() 
  { 
	  if (driver != null)
	  { 
		  driver.quit();
		  
	  }
  }
 
}


