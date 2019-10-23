package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    static class GitHubPullRequest {
        private static final GitHubPullRequest DEFAULT_GITHUBPULLREQUEST = new GitHubPullRequest(10000, "a", "a");
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的 GitHub 用户名
        String author;

        public static GitHubPullRequest newGitHubPullRequest() {
            return DEFAULT_GITHUBPULLREQUEST;
        }

        private GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/gradle/gradle/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        List<GitHubPullRequest> list = new ArrayList<>();
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is, "UTF-8");
            Document doc = Jsoup.parse(html);

            ArrayList<Element> issues = doc.select(".js-issue-row");

            for (Element element : issues) {
                GitHubPullRequest git = GitHubPullRequest.newGitHubPullRequest();
                git.setNumber(Integer.parseInt(element.child(0).child(1).child(3).child(0).text().split(" ")[0].substring(1)));
                git.setTitle(element.child(0).child(1).child(0).text());
                git.setAuthor(element.child(0).child(1).child(3).child(0).child(1).text());
                list.add(git);
            }


            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        return list;
    }
}
