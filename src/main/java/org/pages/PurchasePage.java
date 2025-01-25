package org.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PurchasePage {
    @FindBy(id = "inputName")
    private WebElement nameField;

    @FindBy(id = "address")
    private WebElement addressField;

    @FindBy(id = "city")
    private WebElement cityField;

    @FindBy(id = "state")
    private WebElement stateField;

    @FindBy(id = "zipCode")
    private WebElement zipCodeField;

    @FindBy(css = "input[value='Purchase Flight']")
    private WebElement purchaseButton;

    public PurchasePage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void enterPassengerDetails(String name, String address, String city, String state, String zipCode) {
        nameField.sendKeys(name);
        addressField.sendKeys(address);
        cityField.sendKeys(city);
        stateField.sendKeys(state);
        zipCodeField.sendKeys(zipCode);
        purchaseButton.click();
    }
}