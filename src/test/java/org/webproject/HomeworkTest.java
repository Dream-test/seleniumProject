package org.webproject;

import java.util.List;
import com.codeborne.selenide.*;
import com.github.javafaker.Faker;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class HomeworkTest {
    final private Faker faker = new Faker();

    @BeforeTest
    private void configurationBrowser() {
        Configuration.browserSize = "1366x768";
        Configuration.headless = true;
        Configuration.timeout = 10000;
    }

    @AfterMethod
    private void cleanBrowser() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }

    @Test
    public void complexTest() {
        Selenide.open("https://demoblaze.com/");

        $(Selectors.byTagAndText("a", "Nokia lumia 1520")).click();
        addToCard();

        $(Selectors.byTagAndText("a", "Iphone 6 32gb")).click();
        addToCard();

        $(Selectors.byTagAndText("a", "Sony vaio i7")).click();
        addToCard();

        $(By.id("cartur")).click();

        ElementsCollection prices = $$(By.xpath("//*[@class=\"success\"]/td[3]"));
        prices.shouldHave(CollectionCondition.size(3));

// Преобразую коллекцию в список значений
        List<String> priceValues = prices.stream()
                .map(SelenideElement::getText)
                .toList();

// Использую статичный список для расчёта total
        int total = priceValues.stream()
                .mapToInt(Integer::parseInt)
                .sum();

        //System.out.println("Total Price = " + total);
        String stringTotal = String.valueOf(total);
        //checking that the total received matches Total Amount
        $(By.id("totalp")).shouldBe(Condition.text(stringTotal));

        $(Selectors.byTagAndText("button", "Place Order")).click();

        $(By.id("name")).setValue(faker.name().firstName());
        $(By.id("country")).setValue(faker.address().country());
        $(By.id("city")).setValue(faker.address().city());
        $(By.id("card")).setValue(faker.number().digits(16));
        $(By.id("month")).setValue("10");
        $(By.id("year")).setValue("30");
        $(Selectors.byTagAndText("button", "Purchase")).click();

        SelenideElement sweetAlert = $(By.xpath("//p[@class=\"lead text-muted \"]"));
        sweetAlert.shouldHave(Condition.text("Amount: " + stringTotal + " USD"));
        String fullText = sweetAlert.getText();
        String[] textParts = fullText.split("\\R");
        String idText = textParts[0];
        System.out.println(idText);
        SelenideElement buttonOk = $(Selectors.byTagAndText("button", "OK"));
        buttonOk.click();
        buttonOk.should(Condition.disappear);
    }

    private void addToCard() {
        $(Selectors.byTagAndText("a", "Add to cart")).click();
        Selenide.confirm();
        $(Selectors.byTagAndText("a", "Home")).click();
    }
}
