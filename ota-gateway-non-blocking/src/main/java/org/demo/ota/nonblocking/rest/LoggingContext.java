package org.demo.ota.nonblocking.rest;

class LoggingContext {
    private final String flow;
    private final String se;

    LoggingContext(String flow, String se) {
        this.flow = flow;
        this.se = se;
    }

    @Override
    public String toString() {
        return "[" +
                "flow='" + flow + '\'' +
                ", se='" + se + '\'' +
                ']';
    }
}
