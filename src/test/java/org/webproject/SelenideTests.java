package org.webproject;

import com.codeborne.selenide.*;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;


public class SelenideTests {
    final private String loginName = "test";
    final private String loginPassword = "test";

    @BeforeTest
    private void configurationBrowser() {
        Configuration.browserSize = "1366x768";
        Configuration.headless = true;
        Configuration.timeout = 5000;
    }

    @AfterMethod
    private void cleanBrowser() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }

    @Test
    public void loginTestSelenide() {
        //open the page in browser
        Selenide.open("https://demoblaze.com/");

        //open the authorization window
        SelenideElement loginMenuLink = Selenide.element(By.id("login2"));
        loginMenuLink.click();

        //find the input fields and the authorization button
        //SelenideElement loginUserNameInput = Selenide.element(By.id("loginusername"));
        Selenide.element(By.id("loginusername")).setValue(loginName);
        //SelenideElement loginPasswordInput = Selenide.element(By.id("loginpassword"));
        Selenide.element(By.id("loginpassword")).setValue(loginPassword);
        SelenideElement loginButton = Selenide.element(Selectors.byTagAndText("button", "Log in"));

        //enter authorization data and log in
        //loginUserNameInput.setValue(loginName);
        //loginPasswordInput.setValue(loginPassword);

        loginButton.click();
        loginButton.should(Condition.disappear);

        //checking for successful authorization
        Selenide.element(By.id("nameofuser"))
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Welcome test"));
    }

    @Test
    public void collectionTestSelenide() {
        Selenide.open("https://demoblaze.com/");

        //get all product cards on the page and check that there are at least 6 of them
        ElementsCollection cards = Selenide.elements(By.className("card"));
        cards.shouldHave(CollectionCondition.sizeGreaterThanOrEqual(6));

        //output the total number of cards and the text of all products to the console
        System.out.println("Number of cards on the page = " + cards.size());
        List<String> allCardsOnPageTexts = cards.texts();
        System.out.println("Text of all product cards:");
        System.out.println(allCardsOnPageTexts);

        //get the first card and check that it has text Samsung galaxy s6
        SelenideElement firstCard = cards.first();
        firstCard.shouldHave(Condition.text("Samsung galaxy s6"));

        SelenideElement samsungGalaxyS7Card = cards.find(Condition.text("Samsung galaxy s7"));
        samsungGalaxyS7Card.shouldHave(Condition.text("1.6GHz octa-core"));
        System.out.println("Samsung galaxy s7 card content: " + samsungGalaxyS7Card.text());

        cards.filter(Condition.text("Sony"))
                .shouldHave(CollectionCondition.size(3))
                .should(CollectionCondition.texts("$320", "$790", "$790"));

        SelenideElement iPhoneCard = cards
                .filter(Condition.text("Iphone"))
                .shouldHave(CollectionCondition.size(1))
                .first();

        //SelenideElement imgForClick =
        iPhoneCard.find(By.tagName("img")).click();
        //imgForClick.click();
        iPhoneCard.should(Condition.disappear);
    }
}
