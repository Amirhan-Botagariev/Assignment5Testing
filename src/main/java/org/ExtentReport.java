package org;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;

public class ExtentReport {
    public static void main(String[] args) throws IOException {
        // Настройка Extent Reports
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter("extent-report.html");
        htmlReporter.config().setTheme(Theme.DARK);
        htmlReporter.config().setDocumentTitle("Extent Report Demo");
        htmlReporter.config().setReportName("Test Report");

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        ExtentTest test = extent.createTest("DemoQA Test", "Testing Text Box functionality");

        // Настройка WebDriver
        System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver");
        WebDriver driver = new ChromeDriver();

        try {
            test.info("Запуск браузера Chrome");
            driver.manage().window().maximize();
            test.info("Открыто окно браузера");

            driver.get("https://demoqa.com/text-box");
            test.info("Открыт веб-сайт: https://demoqa.com/text-box");

            // Ввод данных
            WebElement userName = driver.findElement(By.id("userName"));
            userName.sendKeys("Test User");
            test.pass("Имя пользователя введено");

            WebElement userEmail = driver.findElement(By.id("userEmail"));
            userEmail.sendKeys("testuser@example.com");
            test.pass("Email введен");

            // Сделать скриншот до отправки формы
            File screenshotBefore = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshotBefore, new File("screenshot-before.png"));
            test.addScreenCaptureFromPath("screenshot-before.png", "Перед отправкой формы");

            // Нажатие кнопки
            WebElement button = driver.findElement(By.id("submit"));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", button);
            button.click();

            // Сделать скриншот после отправки формы
            File screenshotAfter = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshotAfter, new File("screenshot-after.png"));
            test.addScreenCaptureFromPath("screenshot-after.png", "После отправки формы");

            // Проверка результата
            WebElement output = driver.findElement(By.id("output"));
            if (output.isDisplayed()) {
                test.pass("Результат отображается успешно");
            }
        } catch (Exception e) {
            test.fail("Тест завершился с ошибкой: " + e.getMessage());
        } finally {
            driver.quit();
            test.info("Браузер закрыт");
            extent.flush();
        }
    }
}
