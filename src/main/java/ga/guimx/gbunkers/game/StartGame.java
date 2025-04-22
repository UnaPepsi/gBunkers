package ga.guimx.gbunkers.game;

import com.google.common.collect.Maps;
import ga.guimx.gbunkers.utils.Arena;
import ga.guimx.gbunkers.utils.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Map;

public class StartGame {
    public static void startGame(Arena arena){ //TODO: end game remove players from lunar teamview
        if (ArenaInfo.isArenaOccupied(arena)){
            throw new IllegalStateException("Arena already in use: "+arena.getName());
        }
        //Map<ChatColor, List<Player>> teams = Maps.newHashMap();
        Map<ChatColor, Team> teams = Maps.newHashMap();
        ChatColor[] colors = {ChatColor.RED,ChatColor.BLUE,ChatColor.GREEN,ChatColor.YELLOW};
        for (ChatColor color : colors) {
            teams.put(color,new Team(new ArrayList<>(),color));
        }
        for (int i = 0; i < PlayerInfo.getPlayersQueued().size(); i++){
            ChatColor color = colors[i%4];
            teams.get(color).getMembers().add(Bukkit.getPlayer(PlayerInfo.getPlayersQueued().get(i)));
            PlayerInfo.getPlayersInGame().add(PlayerInfo.getPlayersQueued().get(i));
        }
        ArenaInfo.getArenasInUse().put(arena,teams);
        PlayerInfo.getPlayersQueued().clear();
        teams.get(ChatColor.RED).getMembers().forEach(p -> {
            p.teleport(arena.getRedTeam().getHome());
            p.setDisplayName(ChatColor.RED+p.getName());
        });
        teams.get(ChatColor.RED).setDtr(teams.get(ChatColor.RED).getMembers().size());
        teams.get(ChatColor.RED).setTeamViewToMembers();
        teams.get(ChatColor.BLUE).getMembers().forEach(p -> {
            p.teleport(arena.getBlueTeam().getHome());
            p.setDisplayName(ChatColor.BLUE+p.getName());
        });
        teams.get(ChatColor.BLUE).setDtr(teams.get(ChatColor.BLUE).getMembers().size());
        teams.get(ChatColor.BLUE).setTeamViewToMembers();
        teams.get(ChatColor.GREEN).getMembers().forEach(p -> {
            p.teleport(arena.getGreenTeam().getHome());
            p.setDisplayName(ChatColor.GREEN+p.getName());
        });
        teams.get(ChatColor.GREEN).setDtr(teams.get(ChatColor.GREEN).getMembers().size());
        teams.get(ChatColor.GREEN).setTeamViewToMembers();
        teams.get(ChatColor.YELLOW).getMembers().forEach(p -> {
            p.teleport(arena.getYellowTeam().getHome());
            p.setDisplayName(ChatColor.YELLOW+p.getName());
        });
        teams.get(ChatColor.YELLOW).setDtr(teams.get(ChatColor.YELLOW).getMembers().size());
        teams.get(ChatColor.YELLOW).setTeamViewToMembers();
    }
}
