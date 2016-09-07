package org.demo.ota.blocking.model;

public class Script {

    private String payload;

    public Script() {
    }

    public Script(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Script [payload=" + payload + "]";
    }

}
