package org.webproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {
    public String isbn;
    public String title;
    public String subTitle;
    public String author;
    public String publish_date;
    public String publisher;
    public int pages;
    public String description;
    public String website;

    @Override
    public String toString() {
        return "isbn: " + isbn + "\n" +
                "title: " + title + "\n" +
                "subTitle: " + subTitle + "\n" +
                "author: " + author + "\n" +
                "publish_date: " + publish_date + "\n" +
                "publisher: " + publisher + "\n" +
                "pages: " + pages + "\n" +
                "description: " + description + "\n" +
                "website: " + website;
    }
}
