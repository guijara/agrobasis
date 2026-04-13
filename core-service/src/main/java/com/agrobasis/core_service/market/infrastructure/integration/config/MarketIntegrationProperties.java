package com.agrobasis.core_service.market.infrastructure.integration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "market.integration")
public class MarketIntegrationProperties {

    private String bcbBaseUrl = "https://olinda.bcb.gov.br";
    private String b3BaseUrl = "https://bvmf.bmfbovespa.com.br";
    private String b3HistoricalQuotePath = "/InstDados/SerHist/COTAHIST_A{year}.ZIP";
    private Duration connectTimeout = Duration.ofSeconds(5);
    private Duration readTimeout = Duration.ofSeconds(10);

    public String getBcbBaseUrl() {
        return bcbBaseUrl;
    }

    public void setBcbBaseUrl(String bcbBaseUrl) {
        this.bcbBaseUrl = bcbBaseUrl;
    }

    public String getB3BaseUrl() {
        return b3BaseUrl;
    }

    public void setB3BaseUrl(String b3BaseUrl) {
        this.b3BaseUrl = b3BaseUrl;
    }

    public String getB3HistoricalQuotePath() {
        return b3HistoricalQuotePath;
    }

    public void setB3HistoricalQuotePath(String b3HistoricalQuotePath) {
        this.b3HistoricalQuotePath = b3HistoricalQuotePath;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }
}
