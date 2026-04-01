package me.ginterloper.voice;

public final class VolumeAnalyzer {

    private VolumeAnalyzer() {
    }

    public static double calculateNormalizedRms(short[] samples) {
        if (samples == null || samples.length == 0) {
            return 0D;
        }
        double sum = 0D;
        for (short sample : samples) {
            sum += (double) sample * (double) sample;
        }
        double rms = Math.sqrt(sum / samples.length);
        double normalized = rms / 32768D;
        if (Double.isNaN(normalized) || Double.isInfinite(normalized) || normalized <= 0D) {
            return 0D;
        }
        return Math.min(normalized, 1D);
    }

    public static double toDb(double normalizedRms) {
        if (normalizedRms <= 0D) {
            return Double.NEGATIVE_INFINITY;
        }
        return 20D * Math.log10(normalizedRms);
    }

    public static boolean isShout(short[] samples, double activationThresholdDb) {
        double normalized = calculateNormalizedRms(samples);
        if (normalized <= 0D) {
            return false;
        }
        double db = toDb(normalized);
        double middleDb = activationThresholdDb / 2D;
        return db >= middleDb;
    }
}

