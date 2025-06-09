package ga.guimx.gbunkers.utils;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;

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
    private static final Map<UUID,Integer> playersBalance = Maps.newHashMap();
    @Getter
    private static final List<Location> blocksChanged = new ArrayList<>();
    @Getter
    private static final Map<Arena,UUID> playersCappingKoth = Maps.newHashMap();
    @Getter
    private static final List<UUID> playersFHomming = new ArrayList<>();
    @Getter
    private static final Map<UUID,ChatColor> playerLocation = Maps.newHashMap();
    @Getter
    private static final Map<UUID,Long> startedCappingAt = Maps.newHashMap();
    @Getter
    private static final Map<UUID,Short> playersArcherTagged = Maps.newHashMap();
    @Getter
    private static final Map<UUID,Long> archerSpeedCD = Maps.newHashMap();
    @Getter
    private static final Map<UUID,Long> archerJumpCD = Maps.newHashMap();
    @Getter
    private static final Map<UUID,Short> bardEnergy = Maps.newHashMap();
    @Getter
    private static final Map<UUID,Long> bardCD = Maps.newHashMap();
    @Getter
    private static final Map<UUID,Long> playersEnderPearlCD = Maps.newHashMap();
    @Getter
    private static final Map<Arena,String> arenaKothCapTime = Maps.newHashMap();
    @Getter
    private static final List<UUID> playersInFactionChat = new ArrayList<>();
    @Getter
    private static final Map<UUID,Arena> playersSpectating = Maps.newHashMap();
}
