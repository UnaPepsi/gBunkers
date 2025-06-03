package ga.guimx.gbunkers.utils;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.ChatColor;
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
    @Getter
    private static final Map<Arena,UUID> playersCappingKoth = Maps.newHashMap();
    @Getter
    private static final List<UUID> playersFHomming = new ArrayList<>();
    @Getter
    private static final Map<Player,ChatColor> playerLocation = Maps.newHashMap();
    @Getter
    private static final Map<Player,Long> startedCappingAt = Maps.newHashMap();
    @Getter
    private static final Map<Player,Short> playersArcherTagged = Maps.newHashMap();
    @Getter
    private static final Map<Player,Long> archerSpeedCD = Maps.newHashMap();
    @Getter
    private static final Map<Player,Long> archerJumpCD = Maps.newHashMap();
    @Getter
    private static final Map<Player,Short> bardEnergy = Maps.newHashMap();
    @Getter
    private static final Map<Player,Long> bardCD = Maps.newHashMap();
    @Getter
    private static final Map<Player,Long> playersEnderPearlCD = Maps.newHashMap();
    @Getter
    private static final Map<Arena,String> arenaKothCapTime = Maps.newHashMap();
    @Getter
    private static final List<UUID> playersInFactionChat = new ArrayList<>();
}
