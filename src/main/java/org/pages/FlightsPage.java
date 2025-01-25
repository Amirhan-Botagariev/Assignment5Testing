package org.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class FlightsPage {
    @FindBy(css = "input[type='submit']")
    private WebElement chooseFlightButton;

    public FlightsPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void selectFlight() {
        chooseFlightButton.click();
    }
}