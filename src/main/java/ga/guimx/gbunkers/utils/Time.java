package ga.guimx.gbunkers.utils;

public class Time {
    public static long timePassedSecs(long time1, long time2){
        return Math.abs(time1 - time2) / 1000;
    }
    public static String formatSecs(long secs){
        return String.format("%02d:%02d", secs / 60, secs % 60);
    }
    public static final int SECONDS = 1000;
    public static final int MINUTES = SECONDS * 60;
    public static final int HOURS = MINUTES * 60;
}
