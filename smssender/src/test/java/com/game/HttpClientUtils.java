package com.game;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @Description:
 * @Author: Jason
 * @CreateDate: 2019/1/9 23:19
 */
public class HttpClientUtils {

    public static String post(String url, String params) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        // 设置参数
        StringEntity stringEntity = new StringEntity(params, "UTF-8");
        httpPost.setEntity(stringEntity);

        HttpClient httpClient = HttpClients.createDefault();
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            return inputstreamToString(httpResponse.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String get(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-Type", "application/json");
        HttpClient httpClient = HttpClients.createDefault();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            return inputstreamToString(httpResponse.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String inputstreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        return stringBuilder.toString();
    }
}
