package org.demo.ota.nonblocking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable version here! Just in case ;)
 */
public class Script {
    private final String payload;

    @JsonCreator
    public Script(@JsonProperty("payload") String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Script [payload=" + payload + "]";
    }
}
