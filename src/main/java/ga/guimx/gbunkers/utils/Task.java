package ga.guimx.gbunkers.utils;

import ga.guimx.gbunkers.GBunkers;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class Task {
    public static void runLater(Consumer<BukkitTask> taskConsumer, long delay) {
        BukkitRunnable runnable = new BukkitRunnable() {
            BukkitTask task;

            @Override
            public void run() {
                if (task == null) return;
                taskConsumer.accept(task);
            }
        };

        BukkitTask task = runnable.runTaskLater(GBunkers.getInstance(), delay);
        try {
            Field taskField = runnable.getClass().getDeclaredField("task");
            taskField.setAccessible(true);
            taskField.set(runnable, task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runTimer(Consumer<BukkitTask> bukkitTask, long delay, long period){
        BukkitRunnable runnable = new BukkitRunnable() {
            BukkitTask task;

            @Override
            public void run() {
                if (task == null) return; // wait until task is assigned
                bukkitTask.accept(task);
            }
        };

        BukkitTask task = runnable.runTaskTimer(GBunkers.getInstance(), delay, period);
        // manually set the task into the runnable
        try {
            Field taskField = runnable.getClass().getDeclaredField("task");
            taskField.setAccessible(true);
            taskField.set(runnable, task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runAsync(Consumer<BukkitTask> taskConsumer) {
        BukkitRunnable runnable = new BukkitRunnable() {
            BukkitTask task;

            @Override
            public void run() {
                if (task == null) return;
                taskConsumer.accept(task);
            }
        };

        BukkitTask task = runnable.runTaskAsynchronously(GBunkers.getInstance());
        try {
            Field taskField = runnable.getClass().getDeclaredField("task");
            taskField.setAccessible(true);
            taskField.set(runnable, task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
