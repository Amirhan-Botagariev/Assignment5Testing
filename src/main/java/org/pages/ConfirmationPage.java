package org.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ConfirmationPage {

    @FindBy(css = "h1")
    private WebElement confirmationMessage;

    public ConfirmationPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public String getConfirmationMessage() {
        return confirmationMessage.getText();
    }
}