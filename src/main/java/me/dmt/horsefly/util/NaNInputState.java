package me.dmt.horsefly.util;

public final class NaNInputState {
    private volatile boolean active;
    private int ticks;

    public boolean active() {
        return active;
    }

    public void tick(boolean eligible, int delay) {
        active = eligible && ticks == 0;
        ticks = eligible && ticks < delay ? ticks + 1 : 0;
    }

    public void reset() {
        active = false;
        ticks = 0;
    }
}
