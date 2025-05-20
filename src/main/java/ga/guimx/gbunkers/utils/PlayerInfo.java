package ga.guimx.gbunkers.utils;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerInfo {
    @Getter
    private static final List<UUID> playersQueued = new ArrayList<>();
    @Getter
    private static final List<UUID> playersInGame = new ArrayList<>();
    @Getter
    private static final Map<Player,Integer> playersBalance = Maps.newHashMap();
    @Getter
    private static final List<Location> blocksChanged = new ArrayList<>();
}
