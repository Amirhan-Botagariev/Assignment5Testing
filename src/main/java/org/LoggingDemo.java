package org;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoggingDemo {
    private static final Logger logger = LogManager.getLogger(LoggingDemo.class);

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver");
        WebDriver driver = new ChromeDriver();

        try {
            logger.info("Запуск браузера Chrome");
            driver.manage().window().maximize();
            logger.info("Окно браузера увеличено");

            driver.get("https://demoqa.com/text-box");
            logger.info("Открыт веб-сайт DemoQA: https://demoqa.com/text-box");

            WebElement userName = driver.findElement(By.id("userName"));
            userName.sendKeys("Test User");
            logger.info("Введено имя пользователя: Test User");

            WebElement userEmail = driver.findElement(By.id("userEmail"));
            userEmail.sendKeys("testuser@example.com");
            logger.info("Введен email пользователя: testuser@example.com");

            WebElement button = driver.findElement(By.id("submit"));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", button);
            button.click();

            WebElement output = driver.findElement(By.id("output"));
            if (output.isDisplayed()) {
                logger.info("Результат отображается успешно");
            }

        } catch (Exception e) {
            logger.error("Произошла ошибка: " + e.getMessage());
        } finally {
            driver.quit();
            logger.info("Браузер закрыт");
        }
    }
}
