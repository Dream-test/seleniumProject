package org.webproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) {

        WebDriver driver = configureAndCreateDriver();
        try {
            printAllFullBookInfoToTerminal(driver);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        tearDownDriver(driver);
    }

    private static WebDriver configureAndCreateDriver() {
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        driver.manage().window().maximize();
        return driver;
    }

    public static void tearDownDriver(WebDriver driver) {
        if(driver != null) driver.quit();
    }

    public static List<String> getAllBookUrls(WebDriver driver) {
        driver.get("https://demoqa.com/books");
        List<WebElement> allBookUrlWebElements = driver.findElements(By.xpath("//span[@class=\"mr-2\"]/a"));
        return allBookUrlWebElements.stream()
                .map(webElement -> {
                    String baseUrl = driver.getCurrentUrl();
                    assert baseUrl != null;
                    URI baseUri = URI.create(baseUrl);

                    String relativeBookLink = webElement.getDomAttribute("href");
                    URI fullBookLinkUri = baseUri.resolve(Objects.requireNonNull(relativeBookLink));
                    return fullBookLinkUri.toString();
                })
                .collect(Collectors.toList());
    }

    public static void printFullBookInfoToTerminal(String bookUrl) throws JsonProcessingException {
        String bookIsbn = bookUrl.replaceAll("\\D+", "");
        String newFullBookDataUrl = "https://demoqa.com/BookStore/v1/Book?ISBN=" + bookIsbn;
        FullBookData currentBookData = new FullBookData();
        String fullBookInfo = currentBookData.fullBookData(newFullBookDataUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(fullBookInfo, new TypeReference<Map<String, Object>>() {});
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

    }

    public static void printAllFullBookInfoToTerminal(WebDriver driver) throws JsonProcessingException {
        List<String> allBooksUrls = getAllBookUrls(driver);
        for (String bookUrl : allBooksUrls) {
            printFullBookInfoToTerminal(bookUrl);
            System.out.println(" ");
        }

    }
}