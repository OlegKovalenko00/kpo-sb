package ru.hse.antiplag.analysis.web;

public class WordCloudResponse {

    private String url;

    public WordCloudResponse() {
    }

    public WordCloudResponse(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
