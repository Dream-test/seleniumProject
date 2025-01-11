package org.webproject;

import com.github.javafaker.Faker;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

import static java.lang.Thread.sleep;


public class WebQaTests {
    final private String userName = "newTestUser";
    final private String password = "H@ppyNewYear[2024]";
    private WebDriver driver;
    final private Faker faker = new Faker();

    @BeforeMethod
    public void configureDriver() {
        // 1. Указать путь к скаченному chromedriver: можно через PATH или Java Properties
        //System.setProperty("webdriver.chrome.driver", "C:\\Windows\\System32\\chromedriver\\chromedriver.exe");

        // 2. Создание объекта драйвер, который будет общаться с chromedriver
        this.driver = new ChromeDriver();

        // 3. Зададим время неявного ожидания при поиске любого элемента
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

        // 4. Запускаем браузер в полноэкранном режиме
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDownDriver() {
        if(driver != null) driver.quit();
    }

    @Test
    public void loginUser() throws InterruptedException {
        //final String userName = "newTestUser";
        //final String password = "H@ppyNewYear[2024]";

        driver.get("https://demoqa.com/login");

        WebElement loginHeader = driver.findElement(By.cssSelector("h1.text-center"));
        Assert.assertEquals(loginHeader.getText(), "Login", "This is not login page");

        WebElement userNameInput = driver.findElement(By.id("userName"));
        userNameInput.sendKeys(userName);

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys(password);
        scrollToBottomUsing(driver);
        sleep(1000);

        WebElement loginButton = driver.findElement(By.cssSelector("button#login"));
        loginButton.click();

        WebElement userNameValue = driver.findElement(By.cssSelector("label#userName-value"));
        Assert.assertEquals(userNameValue.getText(), userName, "Wrong User Name value");

        WebElement logOutButton = driver.findElement(By.xpath("//button[text()=\"Log out\"]"));
        Assert.assertTrue(logOutButton.isDisplayed(), "Log Out button is not displayed");
        Assert.assertTrue(logOutButton.isEnabled(), "Log Out button is not enabled");
    }

    @Test
    public void loginUser_useNonExistentUserName() throws InterruptedException {
        //Faker faker = new Faker();
        String randomUserName = this.faker.name().username();
        String randomPassword = UUID.randomUUID().toString();

        driver.get("https://demoqa.com/login");

        WebElement loginHeader = driver.findElement(By.cssSelector("h1.text-center"));
        Assert.assertEquals(loginHeader.getText(), "Login", "This is not login page");

        WebElement userNameInput = driver.findElement(By.id("userName"));
        userNameInput.sendKeys(randomUserName);

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys(randomPassword);
        scrollToBottomUsing(driver);
        sleep(1000);

        WebElement loginButton = driver.findElement(By.cssSelector("button#login"));
        loginButton.click();

        WebElement errorMessage = driver.findElement(By.id("name"));
        Assert.assertEquals(errorMessage.getText(), "Invalid username or password!", "Authorization error information is missing");
    }

    @Test
    public void loginUser_incorrectPassword() throws InterruptedException {
        String randomPassword = UUID.randomUUID().toString();

        driver.get("https://demoqa.com/login");

        WebElement loginHeader = driver.findElement(By.cssSelector("h1.text-center"));
        Assert.assertEquals(loginHeader.getText(), "Login", "This is not login page");

        WebElement userNameInput = driver.findElement(By.id("userName"));
        userNameInput.sendKeys(userName);

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys(randomPassword);
        scrollToBottomUsing(driver);
        sleep(1000);

        WebElement loginButton = driver.findElement(By.cssSelector("button#login"));
        loginButton.click();

        WebElement errorMessage = driver.findElement(By.id("name"));
        Assert.assertEquals(errorMessage.getText(), "Invalid username or password!", "Authorization error information is missing");
    }

    @Test
    public void createNewUser() {
        Random additionalNumber = new Random(900);
        String newUserFirstName = this.faker.name().firstName();
        String newUserLastName = this.faker.name().lastName();
        String newUserName = this.faker.name().username();
        String newUserPassword = password + additionalNumber.nextInt();

        driver.get("https://demoqa.com/login");

        WebElement registrationHeader = driver.findElement(By.xpath("//*[@id=\"userForm\"]/div[1]/h4"));
        Assert.assertEquals(registrationHeader.getText(), "Register to Book Store", "This is not Register page");

        WebElement newUserFirstNameInput = driver.findElement(By.cssSelector("input#firstname"));
        newUserFirstNameInput.sendKeys(newUserFirstName);

        WebElement newUserLastNameInput = driver.findElement(By.cssSelector("input#lastname"));
        newUserLastNameInput.sendKeys(newUserLastName);

        WebElement newUserNameInput = driver.findElement(By.cssSelector("input#userName"));
        newUserNameInput.sendKeys(newUserName);

        WebElement newUserPasswordInput = driver.findElement(By.cssSelector("input#password"));
        newUserPasswordInput.sendKeys(newUserPassword);

        scrollToBottomUsing(driver);

        WebElement recaptchaIframe = driver.findElement(By.cssSelector("iframe[title=\"reCAPTCHA\"]"));
        driver.switchTo().frame(recaptchaIframe);

        WebElement captchaCheckbox = driver.findElement(By.cssSelector("#recaptcha-anchor"));
        captchaCheckbox.click();

        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.attributeToBe(captchaCheckbox, "aria-checked", "true"));

        driver = driver.switchTo().defaultContent();

        WebElement registerButton = driver.findElement(By.cssSelector("button#register"));
        registerButton.click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        Assert.assertEquals(alert.getText(), "User Register Successfully.", "Alert contains an incorrect message about successful registration");
        alert.accept();
    }

    // метод для скроллинга страницы до конца, используя JavaScript
    private static void scrollToBottomUsing(WebDriver webDriver) {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }
}
