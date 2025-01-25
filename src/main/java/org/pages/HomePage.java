package org.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
    @FindBy(name = "fromPort")
    private WebElement departureCity;

    @FindBy(name = "toPort")
    private WebElement destinationCity;

    @FindBy(css = "input[value='Find Flights']")
    private WebElement findFlightsButton;

    public HomePage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void searchFlights(String from, String to) {
        departureCity.sendKeys(from);
        destinationCity.sendKeys(to);
        findFlightsButton.click();
    }
}