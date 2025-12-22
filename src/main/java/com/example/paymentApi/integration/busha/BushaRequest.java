package com.example.paymentApi.integration.busha;

public class BushaRequest {

    private String source_currency;

    private String target_currency;

    private String source_amount;

    public String getSource_currency() {
        return source_currency;
    }

    public void setSource_currency(String source_currency) {
        this.source_currency = source_currency;
    }

    public String getTarget_currency() {
        return target_currency;
    }

    public void setTarget_currency(String target_currency) {
        this.target_currency = target_currency;
    }

    public String getSource_amount() {
        return source_amount;
    }

    public void setSource_amount(String source_amount) {
        this.source_amount = source_amount;
    }
}
