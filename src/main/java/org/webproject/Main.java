package org.webproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.net.URI;
import java.net.URISyntaxException;
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

    public static void printFullBookInfoToTerminal(String bookUrl) throws JsonProcessingException, URISyntaxException {
        //String bookIsbn = bookUrl.replaceAll("\\D+", "");
        String newFullBookDataUrl = "https://demoqa.com/BookStore/v1/Book?ISBN=" + extractQueryParam(bookUrl, "book");
        BookService currentBookData = new BookService();
        String fullBookInfo = currentBookData.getData(newFullBookDataUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        Book currentBook = objectMapper.readValue(fullBookInfo, Book.class);
        System.out.println(currentBook);

        /*Map<String, Object> map = objectMapper.readValue(fullBookInfo, new TypeReference<Map<String, Object>>() {});
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        } */

    }

    public static void printAllFullBookInfoToTerminal(WebDriver driver) throws JsonProcessingException, URISyntaxException {
        List<String> allBooksUrls = getAllBookUrls(driver);
        for (String bookUrl : allBooksUrls) {
            printFullBookInfoToTerminal(bookUrl);
            System.out.println(" ");
        }
    }

    public static String extractQueryParam(String url, String paramName) throws URISyntaxException {
        URI uri = new URI(url);
        String query = uri.getQuery();
        if (query != null) {
            String[] queryParts = query.split("&");
            for (String part : queryParts) {
                String[] keyValue = part.split("=");
                if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }
}