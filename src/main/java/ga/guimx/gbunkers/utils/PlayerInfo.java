package ga.guimx.gbunkers.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerInfo {
    @Getter
    private static List<UUID> playersQueued = new ArrayList<>();
    @Getter
    private static List<UUID> playersInGame = new ArrayList<>();
}
