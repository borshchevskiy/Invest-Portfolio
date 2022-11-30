package ru.investportfolio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareUpdateDTO {
    private MarketData marketdata;

    public MarketData getMarketdata() {
        return marketdata;
    }
    public void setMarketdata(MarketData marketdata) {
        this.marketdata = marketdata;
    }

    @Getter
    @Setter
    public static class MarketData {

        private String[] columns;
        private Double[][] data;

    }
}
