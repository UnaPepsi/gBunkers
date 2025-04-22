package ga.guimx.gbunkers.game;

import com.google.common.collect.Maps;
import ga.guimx.gbunkers.utils.Arena;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class ArenaInfo {
    @Getter
    private static final HashMap<Arena, Map<ChatColor, Team>> arenasInUse = Maps.newHashMap();
    public static boolean isArenaOccupied(Arena arena) {
        return arenasInUse.containsKey(arena);
    }
}
