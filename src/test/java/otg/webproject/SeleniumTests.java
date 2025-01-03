package otg.webproject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

import java.time.Duration;

public class SeleniumTests {
    final private String loginName = "test";
    final private String loginPassword = "test";

    @Test
    public void loginTestSelenium() {
        //create a webDriver interface using ChromeDriver
        WebDriver webDriver = new ChromeDriver();
        //set the maximum waiting time for transmitted conditions for webDriver
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        webDriver.manage().window().maximize();
        //open the page in browser
        webDriver.get("https://demoblaze.com");

        //open the authorization window
        WebElement loginMenuLink = webDriver.findElement(By.id("login2"));
        wait.until(ExpectedConditions.elementToBeClickable(loginMenuLink));
        loginMenuLink.click();

        //find the input fields and the authorization button
        WebElement loginUserNameInput = webDriver.findElement(By.id("loginusername"));
        WebElement loginPasswordInput = webDriver.findElement(By.id("loginpassword"));
        WebElement loginButton = webDriver.findElement(By.xpath("//button[text()=\"Log in\"]"));

        //enter authorization data and log in
        wait.until(ExpectedConditions.visibilityOf(loginUserNameInput));
        loginUserNameInput.sendKeys(loginName);
        loginPasswordInput.sendKeys(loginPassword);

        loginButton.click();
        wait.until(ExpectedConditions.invisibilityOf(loginUserNameInput));

        //checking for successful authorization
        WebElement welcomeUserLink = webDriver.findElement(By.id("nameofuser"));
        wait.until(ExpectedConditions.visibilityOf(welcomeUserLink));
        Assert.assertEquals(welcomeUserLink.getText(), "Welcome test", "Authorization failed");
        //wait.until(ExpectedConditions.textToBePresentInElement(welcomeUserLink, "Welcome test"));

        //closing the browser
        webDriver.quit();




    }
}
