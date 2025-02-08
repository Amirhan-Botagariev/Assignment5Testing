package org.assingment6;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.HashMap;

public class DemoQATest {
    public static final String USERNAME = "amirhanbotagarie_wIx3rb";
    public static final String AUTOMATE_KEY = "xffy3BpxudzzHrBQ3FA1";
    public static final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";

    public static void main(String[] args) {
        try {
            MutableCapabilities capabilities = new MutableCapabilities();
            HashMap<String, Object> bstackOptions = new HashMap<String, Object>();
            capabilities.setCapability("browserName", "Chrome");
            bstackOptions.put("os", "OS X");
            bstackOptions.put("osVersion", "Sequoia");
            bstackOptions.put("browserVersion", "latest");
            bstackOptions.put("userName", "amirhanbotagarie_wIx3rb");
            bstackOptions.put("accessKey", "xffy3BpxudzzHrBQ3FA1");
            bstackOptions.put("consoleLogs", "info");
            bstackOptions.put("seleniumVersion", "4.27.0");
            HashMap<String, Object> chromeOptions = new HashMap<String, Object>();
            chromeOptions.put("driver", "132.0.6834.83");
            bstackOptions.put("chrome", chromeOptions);
            capabilities.setCapability("bstack:options", bstackOptions);


            // Инициализация WebDriver
            WebDriver driver = new RemoteWebDriver(new URL(URL), capabilities);

            // Тестирование сайта DemoQA
            driver.get("https://demoqa.com/");
            System.out.println("Title of the page is: " + driver.getTitle());

            // Завершение работы
            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}