package ga.guimx.gbunkers.utils;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerInfo {
    @Getter
    private static List<UUID> playersQueued = new ArrayList<>();
    @Getter
    private static List<UUID> playersInGame = new ArrayList<>();
    @Getter
    private static Map<Player,Integer> playersBalance = Maps.newHashMap();
}
