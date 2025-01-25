package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class DemoQALoginTest {

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() throws IOException {
        String excelPath = "excel.xlsx";
        FileInputStream fis = new FileInputStream(excelPath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        int rowCount = sheet.getPhysicalNumberOfRows();
        Object[][] data = new Object[rowCount - 1][2];

        for (int i = 1; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            data[i - 1][0] = row.getCell(0).getStringCellValue(); // Username
            data[i - 1][1] = row.getCell(1).getStringCellValue(); // Password
        }

        workbook.close();
        fis.close();
        return data;
    }

    @Test(dataProvider = "loginData")
    public void loginTest(String username, String password) {
        System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get("https://demoqa.com/login");

        // Найдите элементы на странице
        WebElement usernameField = driver.findElement(By.id("userName"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login"));

        // Введите данные и выполните вход
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();

        // Проверьте результат (например, наличие сообщения об ошибке)
        WebElement successMessage = driver.findElement(By.id("name"));
        if (successMessage.isDisplayed()) {
            System.out.println("Login successful: " + username);
        } else {
            System.out.println("Login failed: " + username);
        }

        driver.quit();
    }
}