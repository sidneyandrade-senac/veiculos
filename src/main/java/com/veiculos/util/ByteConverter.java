package com.veiculos.util;

public class ByteConverter {
    public static String humanReadable(long bytes) {
        if (bytes < 1024) return bytes + " bytes";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = ("KMGTPE").charAt(exp-1) + (exp == 1 ? "B" : "B");
        double value = bytes / Math.pow(1024, exp);
        return String.format("%.2f %s", value, pre);
    }

    public static long toBytes(double value, String unit) {
        switch (unit.toUpperCase()) {
            case "KB": return (long) (value * 1024);
            case "MB": return (long) (value * 1024 * 1024);
            case "GB": return (long) (value * 1024 * 1024 * 1024);
            case "TB": return (long) (value * 1024 * 1024 * 1024 * 1024);
            default: return (long) value;
        }
    }
}
