package org.demo.ota.nonblocking.rest;

final class DiagnosticContext {
    private final String flow;
    private final String se;

    DiagnosticContext(String flow, String se) {
        this.flow = flow;
        this.se = se;
    }

    @Override
    public String toString() {
        return "[flow='" + flow + "',se='" + se + "']";
    }
}
