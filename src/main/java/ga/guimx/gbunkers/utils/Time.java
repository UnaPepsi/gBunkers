package ga.guimx.gbunkers.utils;

public class Time {
    public static long timePassedSecs(long startedTimems, long endingTimems){
        return (endingTimems - startedTimems) / 1000;
    }
    public static String formatSecs(long secs){
        return String.format("%02d:%02d", secs / 60, secs % 60);
    }
    private final int SECONDS = 1000;
    private final int MINUTES = SECONDS * 60;
    private final int HOURS = MINUTES * 60;
}
