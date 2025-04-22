package ga.guimx.gbunkers.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

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
    public Map<String,Team> getTeams(){
        return new HashMap<String,Team>(){{
            put("red",redTeam);
            put("blue",blueTeam);
            put("green",greenTeam);
            put("yellow",yellowTeam);
        }};
    }

    @Setter
    @Builder
    @Getter
    public static class Team{
        private ChatColor color;
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
        private boolean pearlsDisabled;
        private Location claimBorder1;
        private Location claimBorder2;
        private Location lowestCapzoneCorner;
        private Location highestCapzoneCorner;
    }
}
