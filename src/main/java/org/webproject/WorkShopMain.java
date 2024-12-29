package org.webproject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WorkShopMain {

    //Здесь содержание workshop

    public static void main(String[] args) {
        //1. Указание пути к chromedriver.exe если не указан в Path системы
        //System.setProperty("webdriver.chrome.driver", "C:\\Windows\\System32\\chromedriver\\chromedriver.exe");

        //2. Создание объекта драйвер, который будет взаимодействовать с chromedriver
        WebDriver driver = new ChromeDriver();

        //3. Команда драйверу открыть браузер и загрузить указанный ресурс
        driver.get("https://demoqa.com/books");

        //4. Пауза между выполняемыми шагами
        /*try {
            sleep(10000);
        } catch (InterruptedException exception) {
            System.out.println(exception.getMessage());
        } */

        //5. Вариант получения строк с фильтрацией от пустых (исключая -padRow в названии класса) без использования промежуточного tableRowsIncludingHeader
        List<WebElement> filteredTableRows = driver.findElements(By.cssSelector(".rt-tr:not(.-padRow)"));

        //5. Вариант с промежуточным List<WebElement> tableRowsIncludingHeader и фильтрации стрим как в лекции
        /*List<WebElement> tableRowsIncludingHeader = driver.findElements(By.className("rt-tr"));
        List<WebElement> filteredTableRows = tableRowsIncludingHeader.stream()
                //Три варианта реализации фильтра (убираю устаревшие классы и учитываю возможность генерации исключений)
                //.filter(row -> !row.getAttribute("class").contains("-padRow"))
                //.filter(row -> !row.getDomProperty("className").contains("-padRow"))
                .filter(row -> !Objects.requireNonNull(row.getDomProperty("className")).contains("-padRow"))
                .toList();
         */

        //6. Вывод на экран первого ряда таблицы (table header) где в ячейках только текстовое содержание.
        //Определяю разделитель
        final String infoDelimiter = " - ";

        //получаю WebElement - первая строка таблицы
        String headerValues = filteredTableRows.getFirst()
                .findElements(By.className("rt-resizable-header-content"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.joining(infoDelimiter));

        System.out.println(headerValues);



        //7. Строки таблицы с информацией по книгам
        filteredTableRows.stream().skip(1) //Стрим пропускает skip(1) первую строку из filterTableRows
                .map(tableRow -> {
                    String baseUrl = driver.getCurrentUrl();
                    URI baseUri = URI.create(baseUrl); //Определяю базовую часть URI

                    String relativeImgSrc = tableRow.findElement(By.tagName("img")).getDomAttribute("src"); //Определяю relative часть URI
                    URI fullImgSrc = baseUri.resolve(Objects.requireNonNull(relativeImgSrc)); // Формирую полный URL используя base + relative URI
                    String imgSrc = fullImgSrc.toString();

                    WebElement bookCell = tableRow.findElement(By.tagName("a"));
                    String relativeBookLink = bookCell.getDomAttribute("href");
                    URI fullBookLinkUri = baseUri.resolve(Objects.requireNonNull(relativeBookLink));
                    String bookLink = fullBookLinkUri.toString();

                    String bookTitle = bookCell.getText();

                    //ячейки автор и издатель можно идентифицировать только по имени тега и индексу
                    final int authorCellIndex = 2;
                    String bookAuthor = tableRow.findElements(By.className("rt-td")).get(authorCellIndex).getText();

                    final int publisherCellIndex = 3;
                    //String bookPublisher = tableRow.findElements(By.className("rt-td")).get(publisherCellIndex).getText();

                    //Вариант поиска элемента в tableRow с использованием индекса и относительного xPath - точка означает поиск вглубь элемента tableRow
                    String publisherCellXpath = String.format(".//div[%d]", publisherCellIndex + 1);
                    //Ошибка в xPath workshop которая приводит к дублированию автора вместо вывода издателя
                    //String publisherCellXpath = String.format("(.//div)[%d]", publisherCellIndex + 1);
                    String bookPublisher = tableRow.findElement(By.xpath(publisherCellXpath)).getText();

                    //Соединение полученных строковых переменных в одну строку
                    return String.join(infoDelimiter, imgSrc, "( " + bookLink + infoDelimiter + bookTitle + " )", bookAuthor, bookPublisher);
                    //return tableRowInfo;
                })
                .forEach(System.out::println);

        //Закрываю процесс chromedriver.exe
        driver.quit();
    }

}
