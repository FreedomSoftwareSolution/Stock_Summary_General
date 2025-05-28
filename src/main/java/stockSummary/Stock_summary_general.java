package stockSummary;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class Stock_summary_general {

	private static final String BASE_URL = "https://qa.freedomnote360.com/Member/Login";
	private static final String EXCEL_PATH = "C:\\Users\\mages.LAPTOP-JT7OLNTB\\git\\Stock_Summary_General\\ExcelData\\Stock Summary Tested.xlsx";
	public static String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	public String REPORT_PATH = System.getProperty("user.dir") + "./reports/"+"/StockSummaryReport-"+timeStamp+".html";
	private static  String userName="mageshwar@fss";
	private static String passWord="Mageshwar248@";

	private WebDriver driver;
	private WebDriverWait wait;
	private ExtentReports extent;
	private ExtentTest test;
	private int totalTestCount = 0;
	private static final int EXPECTED_TEST_COUNT =177;
	public Logger logger;

	@BeforeClass
	public void setup() throws InterruptedException {
		logger=LogManager.getLogger(this.getClass()); 

		setupExtentReports();
		setupWebDriver();
		loginToApplication();
		navigateToGeneralStockSummary();	

	}


	private void setupExtentReports() {
		ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH);
		extent = new ExtentReports();
		extent.attachReporter(sparkReporter);
		extent.setSystemInfo("Tester", "Mageshwar");
		extent.setSystemInfo("Environment", "QA");
		extent.setSystemInfo("Type of Testing", "Regression");
		sparkReporter.config().setDocumentTitle("StockSummary Report");
		sparkReporter.config().setReportName("Functional Testing");
		sparkReporter.config().setTheme(Theme.STANDARD);
	}

	private void setupWebDriver() {
		//	ChromeOptions options = new ChromeOptions();
		//	options.addArguments("--disable-notifications");
		//options.addArguments("--headless");
		//	driver = new ChromeDriver(options);

		// Configure EdgeOptions
		EdgeOptions options = new EdgeOptions();
		options.addArguments("--disable-notifications");
		driver = new EdgeDriver(options); 

		wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
	}

	private void loginToApplication() throws InterruptedException {
		driver.get(BASE_URL);
		test = extent.createTest("Setup and Login", "Setting up WebDriver and logging in");

		try {
			driver.findElement(By.id("username")).sendKeys(userName);
			driver.findElement(By.id("id_password")).sendKeys(passWord);
			WebElement submitButton = driver.findElement(By.id("submit1"));
			submitButton.click();
			Thread.sleep(3000);
			submitButton.click(); // In case of initial failure

			handleAlertIfPresent();
			test.pass("Login successful and landed on dashboard.");
		} catch (Exception e) {
			test.fail("Login failed: " + e.getMessage());
			TakeScreenshots.takeScreenshot(driver, "LoginFailure.png");
		}
	}

	private void handleAlertIfPresent() {
		try {
			Alert alert = wait.until(ExpectedConditions.alertIsPresent());
			if (alert != null) {
				alert.accept();
			}
		} catch (TimeoutException e) {
			// No alert to handle
		}
	}

	private void navigateToGeneralStockSummary() {
		Actions actions = new Actions(driver);
		WebElement storeMenu = driver.findElement(By.xpath("//*[@id=\"cssmenu\"]/ul/li[8]/a"));
		WebElement stockSummaryMenu = driver.findElement(By.xpath("//*[@id=\"cssmenu\"]/ul/li[8]/ul/li[8]/a"));
		WebElement generalStockMenu = driver.findElement(By.xpath("//*[@id=\"cssmenu\"]/ul/li[8]/ul/li[8]/ul/li[1]/a"));

		actions.moveToElement(storeMenu)
		.moveToElement(stockSummaryMenu)
		.moveToElement(generalStockMenu)
		.click()
		.perform();
	}

	@Test(dataProvider = "filterData1")
	public void testFilters(String stock, String stockType, String stockQty, String order) throws InterruptedException {
		test = extent.createTest("Filter Test", "Applying filters and validating results");

		applyFilter("//*[@id=\"txtobj3_chosen\"]", stock);
		applyFilter("//*[@id=\"txtobj4_chosen\"]", stockType);
		applyFilter("//*[@id=\"txtobj14_chosen\"]", stockQty);
		applyFilter("//*[@id=\"txtobj15_chosen\"]", order);

		WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//*[@id=\"btnlistsearch\"]")));
		searchButton.click();

		validateResults();
	}
	private void applyFilter(String dropdownXPath, String input) {
		WebElement dropdown = driver.findElement(By.xpath(dropdownXPath));
		dropdown.click();
		WebElement inputField = dropdown.findElement(By.xpath(".//div/div/input"));
		inputField.sendKeys(input + Keys.TAB);
	}

	private void validateResults() throws InterruptedException {

		try {
			WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(100));
			List<WebElement> resultsTable = wait1.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
					By.xpath("//*[@id=\"rpttable\"]/tbody")));//thead/tr
			if (!resultsTable.isEmpty() && resultsTable.get(0).isDisplayed()) {
				test.pass("Filters applied successfully and results displayed.");
				System.out.println("Filters applied successfully and results displayed. Total tests: " + totalTestCount);
                   Thread.sleep(3000);
				// Capture the screenshot
				String screenshotPath = TakeScreenshots.takeScreenshot(driver, "ResultScreenShot");
				test.addScreenCaptureFromPath(screenshotPath);
			//	System.out.println("Screenshot saved at: " + screenshotPath);

				// Increment test count after successful execution
				totalTestCount++;
			}
		} catch (Exception e) {
			test.fail("Failed to validate results: " + e.getMessage());
			TakeScreenshots.takeScreenshot(driver, "FilterError.png");
		}
		Thread.sleep(4000);
	}

	@AfterClass
	public void summarizeResults() {
		String message = (totalTestCount == EXPECTED_TEST_COUNT) 
				? "ALL TEST CASES EXECUTED SUCCESSFULLY" 
						: "Test cases executed: " + totalTestCount;
		System.out.println(message);
	}

	@DataProvider(name = "filterData")
	public Object[][] getFilterData() throws IOException {
		List<Object[]> data = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(EXCEL_PATH);
				Workbook workbook = WorkbookFactory.create(fis)) {
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();

			int rowIndex = 0;
			while (rows.hasNext()) {
				Row row = rows.next();
				if (rowIndex++ == 0 || isRowEmpty(row)) continue;

				data.add(new Object[]{
						getCellValue(row.getCell(0)),
						getCellValue(row.getCell(1)),
						getCellValue(row.getCell(2)),
						getCellValue(row.getCell(3))
				});
			}
		}

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

	@DataProvider(name = "filterData1")	
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

	@AfterTest
	public void tearDown() {
		extent.flush();
		String pathOfExtentReport = REPORT_PATH;
		File extentReport = new File(pathOfExtentReport);

		try {
			Desktop.getDesktop().browse(extentReport.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//if (driver != null) {
		//	driver.quit();
		//}
	}

}
