package me.alpestrine.c.reward.util;

public interface IMath {
    static double round(double value, int decimals) {
        double scale = Math.pow(10, decimals);
        return Math.round(value * scale) / scale;
    }

    static int round(int value, int decimals) {
        double scale = Math.pow(10, decimals);
        return (int) (Math.round(value * scale) / scale);
    }
}
