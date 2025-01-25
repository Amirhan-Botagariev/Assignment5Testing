package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

public class DemoQATest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testTextBox() {
        driver.get("https://demoqa.com/text-box");
        WebElement userName = driver.findElement(By.id("userName"));
        WebElement userEmail = driver.findElement(By.id("userEmail"));

        userName.sendKeys("Test User");
        userEmail.sendKeys("testuser@example.com");

        WebElement button = driver.findElement(By.id("submit"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", button);
        button.click();

        WebElement output = driver.findElement(By.id("output"));
        assert output.isDisplayed();
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}