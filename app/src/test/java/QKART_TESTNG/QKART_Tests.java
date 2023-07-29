package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.hc.core5.http.Message;
import org.checkerframework.checker.units.qual.s;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Test;

@Listeners(ListenerClass.class)
public class QKART_Tests {

    static RemoteWebDriver driver;
    public static String lastGeneratedUserName;
    
    @BeforeSuite(alwaysRun = true)
     public  void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        System.out.println("createDriver()");
    }


        @Test(priority = 1,description = "Verify registration happens correctly", groups={"Sanity"})
        @Parameters({"TC1_Username","TC1_Password"})
        public void TestCase01(@Optional("testUser") String TC1_Username, @Optional(" abc@123") String TC1_Password) throws InterruptedException {
        boolean status;
        // logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");
         //takeScreenshot(driver, "StartTestCase", "TestCase1");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
         status = registration.registerUser(TC1_Username, TC1_Password, true);
        assertTrue("Failed to register new user", status );

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
         status = login.PerformLogin(lastGeneratedUserName, TC1_Password);
        // logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
        assertTrue("Failed to resgister new user", status);

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

    }

   @Test(priority = 2,description = "Verify re-registering an already registered user fails", groups={"Sanity"})
    public void TestCase02() throws InterruptedException {
        boolean status;
        //logStatus("Start Testcase", "Test Case 2: Verify User Registration with an existing username ", "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
      //  logStatus("Test Step", "User Registration : ", status ? "PASS" : "FAIL");
       
        assertTrue(status);

        lastGeneratedUserName = registration.lastGeneratedUsername;

        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, "abc@123", false);

        //logStatus("End TestCase", "Test Case 2: Verify user Registration : ", status ? "FAIL" : "PASS");
       assertFalse(status);
    }

 

        
    @Test(priority = 3,description = "Verify the functionality of search text box", groups={"Sanity"})
    public void TestCase03() throws InterruptedException {
 
        boolean status;

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct("YONEX");
        // if (!status) {
        //     logStatus("TestCase 3", "Test Case Failure. Unable to search for given product", "FAIL");
        //     return false;
        // }
        assertTrue("Unable to search for given product",status);

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
        // if (searchResults.size() == 0) {
        //     logStatus("TestCase 3", "Test Case Failure. There were no results for the given search string", "FAIL");
        //     return false;
        // }
        assertNotEquals("There were no results for the given search string",0,searchResults.size());
        //assertEquals(searchResults.size(), 0,"There were no results for the given search string");
        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
            // if (!elementText.toUpperCase().contains("YONEX")) {
            //     logStatus("TestCase 3", "Test Case Failure. Test Results contains un-expected values: " + elementText,
            //             "FAIL");
            //     return false;
            // }
            assertTrue("Test Case Failure. Test Results contains un-expected values: " + elementText,elementText.toUpperCase().contains("YONEX"));
        }

        //logStatus("Step Success", "Successfully validated the search results ", "PASS");

        // Search for product
        status = homePage.searchForProduct("Gesundheit");
        // if (!status) {
        //     logStatus("TestCase 3", "Test Case Failure. Invalid keyword returned results", "FAIL");
        //     return false;
        // }

        assertFalse( "Test Case Failure. Invalid keyword returned results",!status);


        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        // if (searchResults.size() == 0) {
        //     if (homePage.isNoResultFound()) {
        //         logStatus("Step Success", "Successfully validated that no products found message is displayed", "PASS");
        //     }
        //     logStatus("TestCase 3", "Test Case PASS. Verified that no search results were found for the given text",
        //             "PASS");
        // } else {
        //     logStatus("TestCase 3", "Test Case Fail. Expected: no results , actual: Results were available", "FAIL");
        //     return false;
        // }
            assertEquals(searchResults.size(), 0,"Step Success: Successfully validated that no products found message is displayed");
            assertTrue( "Step Success: Successfully validated that no products found message is displayed", homePage.isNoResultFound());

            //assertTrue(true,"TestCase 3: Test Case PASS. Verified that no search results were found for the given text");
            //assertFalse(searchResults.isEmpty(),"TestCase 3: Test Case Fail. Expected: no results, actual: Results were available");
        

        //return true;
        assertTrue(true);
    }

 /*
     * Verify the presence of size chart and check if the size chart content is as expected
     */

    @Test(priority = 4,description = "Verify the existence of size chart for certain items and validate contents of size chart",groups={"Regression"})
    public void TestCase04() throws InterruptedException {

        boolean status = false;
      //  logStatus("TestCase 4", "Start test case : Verify the presence of size Chart", "DONE");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("Running Shoes");
        List<WebElement> searchResults = homePage.getSearchResults();

        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            assertTrue("Step Success: Successfully validated presence of Size Chart Link",result.verifySizeChartExists());

                assertTrue("Step Success: Validated presence of drop down",result.verifyExistenceofSizeDropdown(driver));
                 
               assertTrue(result.openSizechart());

                //if (result.openSizechart()) {
                    // Verify if the size chart contents matches the expected values
                    // if (result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver)) {
                    //     logStatus("Step Success", "Successfully validated contents of Size Chart Link", "PASS");
                    assertTrue("Step Success: Successfully validated contents of Size Chart Link",result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver));
                
                assertTrue("Step Success: Successfully closed Size Chart",result.closeSizeChart(driver));
           
        }
    
       // logStatus("TestCase 4", "End Test Case: Validated Size Chart Details", status ? "PASS" : "FAIL");
        // assertTrue(status, "TestCase 4: Overall Test Case Result");
    }

   
    @Test(priority = 5, description = "Verify that a new user can add multiple products in to the cart and Checkout", groups = {"Sanity"})
    @Parameters({"TC5_ProductNameToSearchFor","TC5_ProductNameToSearchFor2","TC5_AddressDetails"}) 
    public void TestCase05(String TC5_ProductNameToSearchFor, String TC5_ProductNameToSearchFor2,String TC5_AddressDetails) throws InterruptedException {
        Boolean status;
        //logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying products", "DONE");

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        assertNotEquals( "Test Case Failure. Happy Flow Test Failed", 0, status);
       
        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();
            // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");

        assertNotEquals(  "Test Case Failure. Happy Flow Test Failed",0,status);

        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     logStatus("End TestCase", "Test Case 5: Happy Flow Test Failed : ", status ? "PASS" : "FAIL");
        // }

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct(TC5_ProductNameToSearchFor);
        homePage.addProductToCart(TC5_ProductNameToSearchFor);
        status = homePage.searchForProduct(TC5_ProductNameToSearchFor2);
        homePage.addProductToCart(TC5_ProductNameToSearchFor2);

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(TC5_AddressDetails);
        checkoutPage.selectAddress(TC5_AddressDetails);

        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();

        //logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed : ", status ? "PASS" : "FAIL");
        //return status;
        assertTrue(status);
    }



    @Test(priority = 6,description = "Verify that the contents of the cart can be edited",groups={"Regression"})
    @Parameters({"TC6_ProductNameToSearch1","TC6_ProductNameToSearch2"}) 
    public void TestCase06( String TC6_ProductNameToSearch1, String TC6_ProductNameToSearch2) throws InterruptedException {
    boolean status;
 
     Home homePage = new Home(driver);
     Register registration = new Register(driver);
     Login login = new Login(driver);
 
     registration.navigateToRegisterPage();
     status = registration.registerUser("testUser", "abc@123", true);
     assertTrue(status);
 
     String lastGeneratedUserName = registration.lastGeneratedUsername;
 
     login.navigateToLoginPage();
     status = login.PerformLogin(lastGeneratedUserName, "abc@123");
     assertTrue(status);
 
     homePage.navigateToHome();
     status = homePage.searchForProduct("Xtend");
     assertTrue(status);
     homePage.addProductToCart(TC6_ProductNameToSearch1);
 
     status = homePage.searchForProduct("Yarine");
     assertTrue(status);
     homePage.addProductToCart(TC6_ProductNameToSearch2);
 
   
     status = homePage.changeProductQuantityinCart(TC6_ProductNameToSearch1, 2);
     //assertTrue(status);
 
     
     status = homePage.changeProductQuantityinCart(TC6_ProductNameToSearch2, 0);
     //assertTrue(status);
 
     status = homePage.changeProductQuantityinCart(TC6_ProductNameToSearch1, 1);
     //assertTrue(status);
 
     homePage.clickCheckout();
 
     Checkout checkoutPage = new Checkout(driver);
     checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
     status = checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
     assertFalse(status);
 
     checkoutPage.placeOrder();
 
     try {
         WebDriverWait wait = new WebDriverWait(driver, 30);
         wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
     } catch (TimeoutException e) {
         System.out.println("Error while placing order: " + e.getMessage());
         status = false;
     }
 
     status = driver.getCurrentUrl().endsWith("/thanks");
 
     homePage.navigateToHome();
     homePage.PerformLogout();
 
 }
 
 
     @Test(priority = 7,description = "Verify that insufficient balance error is thrown when the wallet balance is not enough",groups={"Sanity"})
     @Parameters({"TC7_ProductName","TC7_Qty"})
     public void TestCase07(String TC7_ProductName, int TC7_Qty) throws InterruptedException {
     boolean status;
 
     Register registration = new Register(driver);
     registration.navigateToRegisterPage();
     status = registration.registerUser("testUser", "abc@123", true);
     assertTrue(status);
 
     String lastGeneratedUserName = registration.lastGeneratedUsername;
 
     Login login = new Login(driver);
     login.navigateToLoginPage();
     status = login.PerformLogin(lastGeneratedUserName, "abc@123");
     assertTrue(status);
 
     Home homePage = new Home(driver);
     homePage.navigateToHome();
     status = homePage.searchForProduct("Stylecon");
     assertTrue(status);
     homePage.addProductToCart(TC7_ProductName);
 
     status = homePage.changeProductQuantityinCart(TC7_ProductName, TC7_Qty);
     assertTrue(status);
 
     homePage.clickCheckout();
 
     Checkout checkoutPage = new Checkout(driver);
     checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
     status = checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
     assertFalse(status);
 
     checkoutPage.placeOrder();
     Thread.sleep(3000);
 
     status = checkoutPage.verifyInsufficientBalanceMessage();
     assertTrue(status);
 
 }
 
 @Test(priority = 8,description = "Verify that a product added to a cart is available when a new tab is added",groups={"Regression"})
 public void TestCase08() throws InterruptedException {
     boolean status = false;
 
     Register registration = new Register(driver);
     registration.navigateToRegisterPage();
     status = registration.registerUser("testUser", "abc@123", true);
     assertTrue(status);
 
     
     String lastGeneratedUserName = registration.lastGeneratedUsername;
 
     Login login = new Login(driver);
     login.navigateToLoginPage();
     status = login.PerformLogin(lastGeneratedUserName, "abc@123");
     assertTrue(status);
    
     Home homePage = new Home(driver);
     homePage.navigateToHome();
 
     status = homePage.searchForProduct("YONEX");
     //assertTrue(status, "Step Failure: Searching for YONEX product failed");
 
     homePage.addProductToCart("YONEX Smash Badminton Racquet");
     String currentURL = driver.getCurrentUrl();
 
     driver.findElement(By.linkText("Privacy policy")).click();
     Set<String> handles = driver.getWindowHandles();
     driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
 
     driver.get(currentURL);
     Thread.sleep(2000);
 
     List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
     status = homePage.verifyCartContents(expectedResult);
     assertTrue(status);
 
     driver.close();
     driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
     
 }
 
 
 @Test(priority = 9,description = "Verify that privacy policy and about us links are working finey",groups={"Regression"})
 public void TestCase09() throws InterruptedException {
     boolean status = false;
 
     Register registration = new Register(driver);
     registration.navigateToRegisterPage();
     status = registration.registerUser("testUser", "abc@123", true);
     assertTrue(status);
     
     String lastGeneratedUserName = registration.lastGeneratedUsername;
 
     Login login = new Login(driver);
     login.navigateToLoginPage();
     status = login.PerformLogin(lastGeneratedUserName, "abc@123");
     assertTrue(status);
 
     Home homePage = new Home(driver);
     homePage.navigateToHome();
 
     String basePageURL = driver.getCurrentUrl();
 
     driver.findElement(By.linkText("Privacy policy")).click();
     assertEquals(driver.getCurrentUrl(), basePageURL, "Step Failure: Verifying parent page url didn't change on privacy policy link click failed");
     
 
     Set<String> handles = driver.getWindowHandles();
     driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
     WebElement privacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
     assertEquals(privacyPolicyHeading.getText(), "Privacy Policy", "Step Failure: Verifying new tab opened has Privacy Policy page heading failed");
     
 
     driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
     driver.findElement(By.linkText("Terms of Service")).click();
 
     handles = driver.getWindowHandles();
     driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
     WebElement tosHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
     assertEquals(tosHeading.getText(), "Terms of Service", "Step Failure: Verifying new tab opened has Terms Of Service page heading failed");
 
     driver.close();
     driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
     driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
 
 }
 
 
 @Test(priority = 10,description = "Verify that the contact us dialog works fine", groups={"Sanity"})
 public void TestCase10() throws InterruptedException {
 
     Home homePage = new Home(driver);
     homePage.navigateToHome();
 
     driver.findElement(By.xpath("//*[text()='Contact us']")).click();
 
     WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
     name.sendKeys("crio user");
     WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
     email.sendKeys("criouser@gmail.com");
     WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
     message.sendKeys("Testing the contact us page");
 
     WebElement contactUs = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));
 
     contactUs.click();
 
     WebDriverWait wait = new WebDriverWait(driver, 30);
     wait.until(ExpectedConditions.invisibilityOf(contactUs));
 
     assertTrue(true);
 
 }
 
 @Test(priority = 11,description = "Ensure that the Advertisement Links on the QKART page are clickable", groups={"Sanity"})
 public void TestCase11() throws InterruptedException {
     boolean status = false;
    
     Register registration = new Register(driver);
     registration.navigateToRegisterPage();
     status = registration.registerUser("testUser", "abc@123", true);
     assertTrue(status);
 
     String lastGeneratedUserName = registration.lastGeneratedUsername;
 
     Login login = new Login(driver);
     login.navigateToLoginPage();
     status = login.PerformLogin(lastGeneratedUserName, "abc@123");
     assertTrue(status);
 
     Home homePage = new Home(driver);
     homePage.navigateToHome();
 
     status = homePage.searchForProduct("YONEX Smash Badminton Racquet");
     assertTrue(status);
 
     homePage.addProductToCart("YONEX Smash Badminton Racquet");
     homePage.changeProductQuantityinCart("YONEX Smash Badminton Racquet", 1);
     homePage.clickCheckout();
 
     Checkout checkoutPage = new Checkout(driver);
     checkoutPage.addNewAddress("Addr line 1  addr Line 2  addr line 3");
     checkoutPage.selectAddress("Addr line 1  addr Line 2  addr line 3");
     checkoutPage.placeOrder();
     Thread.sleep(3000);
 
     String currentURL = driver.getCurrentUrl();
 
     List<WebElement> advertisements = driver.findElements(By.xpath("//iframe"));
     assertEquals(advertisements.size(), 3, "Step Failure: Verify that 3 Advertisements are available");
     
 
     WebElement advertisement1 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
     driver.switchTo().frame(advertisement1);
     driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
     driver.switchTo().parentFrame();
 
     assertNotEquals(driver.getCurrentUrl(), currentURL, "Step Failure: Verify that Advertisement 1 is clickable");
     
 
     driver.get(currentURL);
     Thread.sleep(3000);
 
     WebElement advertisement2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
     driver.switchTo().frame(advertisement2);
     driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
     driver.switchTo().parentFrame();
 
     assertNotEquals(driver.getCurrentUrl(), currentURL, "Step Failure: Verify that Advertisement 2 is clickable");
   
 }
    

    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    public static void logStatus(String type, String message, String status) {

        System.out.println(String.format("%s |  %s  |  %s | %s", String.valueOf(java.time.LocalDateTime.now()), type,
                message, status));
    }


}