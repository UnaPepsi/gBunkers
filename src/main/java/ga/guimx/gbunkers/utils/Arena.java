package ga.guimx.gbunkers.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;

@Setter
@Builder
@Getter
public class Arena {
    private String name;
    private World world;
    private Location border1;
    private Location border2;
    private Team redTeam;
    private Team blueTeam;
    private Team greenTeam;
    private Team yellowTeam;
    private Koth koth;
    private Location spectatorSpawn;

    @Setter
    @Builder
    @Getter
    public static class Team{
        private Color color;
        private Location home;
        private Location claimBorder1;
        private Location claimBorder2;
        private Location blockShop;
        private Location equipmentShop;
        private Location sellShop;
    }
    @Setter
    @Builder
    @Getter
    public static class Koth{
        private String name;
        private boolean arePearlsDisabled;
        private Location claimBorder1;
        private Location claimBorder2;
        private Location lowestCapzoneCorner;
        private Location highestCapzoneCorner;
    }
}
