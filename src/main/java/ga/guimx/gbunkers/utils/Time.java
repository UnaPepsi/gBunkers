package ga.guimx.gbunkers.utils;

public class Time {
    public static long timePassedSecs(long startedTimems, long endingTimems){
        return (endingTimems - startedTimems) / 1000;
    }
    public static String formatSecs(long secs){
        return String.format("%02d:%02d", secs / 60, secs % 60);
    }
}
