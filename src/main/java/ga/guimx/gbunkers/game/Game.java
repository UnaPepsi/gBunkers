package ga.guimx.gbunkers.game;

import com.google.common.collect.Maps;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.BukkitApollo;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.recipients.Recipients;
import ga.guimx.gbunkers.utils.Arena;
import ga.guimx.gbunkers.utils.PlayerInfo;
import ga.guimx.gbunkers.utils.Task;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Game {
    public static void startGame(Arena arena){ //TODO: end game remove players from lunar teamview
        if (ArenaInfo.isArenaOccupied(arena)){
            throw new IllegalStateException("Arena already in use: "+arena.getName());
        }
        List<Player> playersInQueue = new ArrayList<>(PlayerInfo.getPlayersQueued()).stream().map(Bukkit::getPlayer).collect(Collectors.toList());
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
            PlayerInfo.getPlayersBalance().put(Bukkit.getPlayer(PlayerInfo.getPlayersQueued().get(i)),0);
        }
        giveMoneyToPlayers(playersInQueue);
        ArenaInfo.getArenasInUse().put(arena,teams);
        PlayerInfo.getPlayersQueued().clear();
        teams.get(ChatColor.RED)
                .setDtr(teams.get(ChatColor.RED).getMembers().size())
                .setTeamViewToMembers()
                .setLunarNametags().getMembers().forEach(p -> {
                    p.teleport(arena.getRedTeam().getHome());
                    p.setDisplayName(ChatColor.RED+p.getName());
                    Scoreboard sc = p.getScoreboard();
                    org.bukkit.scoreboard.Team team = sc.getTeam("RED") == null ? sc.registerNewTeam("RED") : sc.getTeam("RED");
                    team.setPrefix("§c");
                    team.addPlayer(p);
                });
        teams.get(ChatColor.BLUE)
                .setDtr(teams.get(ChatColor.BLUE).getMembers().size())
                .setTeamViewToMembers()
                .setLunarNametags().getMembers().forEach(p -> {
                    p.teleport(arena.getBlueTeam().getHome());
                    p.setDisplayName(ChatColor.BLUE+p.getName());
                    Scoreboard sc = p.getScoreboard();
                    org.bukkit.scoreboard.Team team = sc.getTeam("BLUE") == null ? sc.registerNewTeam("BLUE") : sc.getTeam("BLUE");
                    team.setPrefix("§9");
                    team.addPlayer(p);
                });
        teams.get(ChatColor.GREEN)
                .setDtr(teams.get(ChatColor.GREEN).getMembers().size())
                .setTeamViewToMembers()
                .setLunarNametags().getMembers().forEach(p -> {
                    p.teleport(arena.getGreenTeam().getHome());
                    p.setDisplayName(ChatColor.GREEN+p.getName());
                    Scoreboard sc = p.getScoreboard();
                    org.bukkit.scoreboard.Team team = sc.getTeam("GREEN") == null ? sc.registerNewTeam("GREEN") : sc.getTeam("GREEN");
                    team.setPrefix("§a");
                    team.addPlayer(p);
                });
        teams.get(ChatColor.YELLOW)
                .setDtr(teams.get(ChatColor.YELLOW).getMembers().size())
                .setTeamViewToMembers()
                .setLunarNametags().getMembers().forEach(p -> {
                    p.teleport(arena.getYellowTeam().getHome());
                    p.setDisplayName(ChatColor.YELLOW+p.getName());
                    Scoreboard sc = p.getScoreboard();
                    org.bukkit.scoreboard.Team team = sc.getTeam("YELLOW") == null ? sc.registerNewTeam("YELLOW") : sc.getTeam("YELLOW");
                    team.setPrefix("§e");
                    team.addPlayer(p);
                });
        applyWaypoints(arena,playersInQueue);
        spawnVillagers(arena);
    }
    public static void applyWaypoints(Arena arena, List<Player> players){
        List<UUID> playersUUID = players.stream().map(Player::getUniqueId).collect(Collectors.toList());
        Recipients recipients = Recipients.of(
                Apollo.getPlayerManager().getPlayers().stream()
                        .filter(apolloPlayer -> playersUUID.contains(apolloPlayer.getUniqueId()))
                        .collect(Collectors.toList())
        );
        arena.getTeams().forEach((name,team) -> {
            try {
                Apollo.getModuleManager().getModule(WaypointModule.class).displayWaypoint(recipients, Waypoint.builder()
                        .name(StringUtils.capitalize(name)+"'s Home")
                        .location(BukkitApollo.toApolloBlockLocation(team.getHome()))
                        .color((Color) Class.forName("java.awt.Color").getField(name).get(null))
                        .preventRemoval(false)
                        .hidden(false)
                        .build()
                );
            } catch (Exception ignored) {}
        });
        Apollo.getModuleManager().getModule(WaypointModule.class).displayWaypoint(recipients, Waypoint.builder()
                .name("KOTH")
                .location(BukkitApollo.toApolloBlockLocation(arena.getKoth().getLowestCapzoneCorner().clone().add(arena.getKoth().getHighestCapzoneCorner()).multiply(0.5)))
                .color(Color.ORANGE)
                .preventRemoval(false)
                .hidden(false)
                .build()
        );
    }
    public static void giveMoneyToPlayers(List<Player> players){
        Task.runTimer(task -> {
            for (Player player : players) {
                if (!PlayerInfo.getPlayersBalance().containsKey(player)){
                    players.remove(player);
                    continue;
                }
                if (players.isEmpty()){
                    task.cancel();
                    return;
                }
                PlayerInfo.getPlayersBalance().put(player,PlayerInfo.getPlayersBalance().get(player)+10);
            }
        },0,20*5);
    }

    public static void spawnVillagers(Arena arena){
        arena.getTeams().forEach((color,team) -> {
            Villager blockshop = team.getBlockShop().getWorld().spawn(team.getBlockShop(), Villager.class);
            blockshop.setAdult();
            blockshop.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,Integer.MAX_VALUE,Integer.MAX_VALUE));
            blockshop.setCustomName(ChatColor.valueOf(color)+"Block Shop");
            Villager sellshop = team.getSellShop().getWorld().spawn(team.getSellShop(), Villager.class);
            sellshop.setAdult();
            sellshop.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,Integer.MAX_VALUE,Integer.MAX_VALUE));
            sellshop.setCustomName(ChatColor.valueOf(color)+"Sell Shop");
            Villager equipmentshop = team.getEquipmentShop().getWorld().spawn(team.getEquipmentShop(), Villager.class);
            equipmentshop.setAdult();
            equipmentshop.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,Integer.MAX_VALUE,Integer.MAX_VALUE));
            equipmentshop.setCustomName(ChatColor.valueOf(color)+"Equipment Shop");

        });
    }
}
