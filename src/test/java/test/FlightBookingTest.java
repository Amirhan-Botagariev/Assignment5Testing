package test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.pages.HomePage;
import org.pages.FlightsPage;
import org.pages.PurchasePage;
import org.pages.ConfirmationPage;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class FlightBookingTest {
    private WebDriver driver;

    @BeforeTest
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver"); // Укажите путь к ChromeDriver
        driver = new ChromeDriver();
        driver.get("https://www.blazedemo.com/");
    }

    @Test
    public void testFlightBooking() {
        // Step 1: Search for flights
        HomePage homePage = new HomePage(driver);
        homePage.searchFlights("Boston", "New York");

        // Step 2: Select a flight
        FlightsPage flightsPage = new FlightsPage(driver);
        flightsPage.selectFlight();

        // Step 3: Enter passenger details and purchase
        PurchasePage purchasePage = new PurchasePage(driver);
        purchasePage.enterPassengerDetails(
                "John Doe", "123 Elm Street", "New York", "NY", "10001"
        );

        // Step 4: Verify confirmation
        ConfirmationPage confirmationPage = new ConfirmationPage(driver);
        String confirmationMessage = confirmationPage.getConfirmationMessage();
        System.out.println("Actual confirmation message: " + confirmationMessage);
        Assert.assertTrue(confirmationMessage.contains("Thank you for your purchase today!"));
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }
}