package com.squall1744;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class main {
    public static void main(String[] args) {
        saveCrawlerResults();
    }

    private static List<String> linkPool = new ArrayList<>();
    private static Set<String> handledLink = new HashSet<>();

    public static void saveCrawlerResults() {
        linkPool.add("https://sina.cn");

        while (!linkPool.isEmpty()) {
            String url = linkPool.remove(0);

            if (handledLink.contains(url)) {
                continue;
            }

            if (isInterestedUrlOrIndexUrl(url)) {
                getNewsAndSave(url);
            }

        }
    }

    public static void getNewsAndSave(String url) {

        CloseableHttpClient httpclient = HttpClients.createDefault();

        url = url.startsWith("//") ? ("https:" + url) : url;

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");

        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();

            String html = EntityUtils.toString(entity);
            Document doc = Jsoup.parse(html);

            ArrayList<Element> aTags = doc.getElementsByTag("a");

            if (!aTags.isEmpty()) {
                aTags.forEach(item -> linkPool.add(item.attr("href")));
            }

            ArrayList<Element> article = doc.getElementsByTag("article");

            if (!article.isEmpty()) {
                System.out.println(article.get(0).text());
            }

            handledLink.add(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isInterestedUrlOrIndexUrl(String url) {
        return url.contains("news.sina.cn") || url.equals("https://sina.cn");
    }
}
