package com.itsm.demo.model;

public enum Priority {
    P1(5, 30), P2(15, 120), P3(60, 480), P4(240, 1440);

    private final int responseMinutes;
    private final int resolutionMinutes;

    Priority(int responseMinutes, int resolutionMinutes) {
        this.responseMinutes = responseMinutes;
        this.resolutionMinutes = resolutionMinutes;
    }
    public int getResponseMinutes() { return responseMinutes; }
    public int getResolutionMinutes() { return resolutionMinutes; }
}
