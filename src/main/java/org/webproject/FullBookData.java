package org.webproject;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FullBookData {
    OkHttpClient client = new OkHttpClient();

    public String fullBookData(String bookUrl) {
        String response = "";
        Request request = new Request.Builder()
                .url(bookUrl)
                .build();

        try {
            response = this.client.newCall(request).execute().body().string();
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        }
        System.out.println(response);
        return response;
    }
}
