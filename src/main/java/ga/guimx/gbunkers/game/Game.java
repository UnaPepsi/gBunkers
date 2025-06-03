package ga.guimx.gbunkers.game;

import com.google.common.collect.Maps;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.BukkitApollo;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.recipients.Recipients;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.utils.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.awt.Color;
import java.util.List;
import java.util.*;
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
            ChatColor color = colors[i%2];
            teams.get(color).getMembers().add(Bukkit.getPlayer(PlayerInfo.getPlayersQueued().get(i)));
            PlayerInfo.getPlayersInGame().add(PlayerInfo.getPlayersQueued().get(i));
            PlayerInfo.getPlayersBalance().put(Bukkit.getPlayer(PlayerInfo.getPlayersQueued().get(i)),0);
            Bukkit.getPlayer(PlayerInfo.getPlayersQueued().get(i)).setGameMode(GameMode.SURVIVAL);
            Bukkit.getPlayer(PlayerInfo.getPlayersQueued().get(i)).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,20*10,5),true);
            Bukkit.getPlayer(PlayerInfo.getPlayersQueued().get(i)).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,20*10,5),true);
            Bukkit.getPlayer(PlayerInfo.getPlayersQueued().get(i)).addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,20*10,5),true);
            Bukkit.getPlayer(PlayerInfo.getPlayersQueued().get(i)).getInventory().clear();
            Bukkit.getPlayer(PlayerInfo.getPlayersQueued().get(i)).getInventory().setContents(new ItemStack[]{new ItemStack(Material.STONE_PICKAXE),new ItemStack(Material.STONE_AXE)});
        }
        giveMoneyToPlayers(playersInQueue);
        ArenaInfo.getArenasInUse().put(arena,teams);
        PlayerInfo.getPlayersQueued().clear();
        teams.get(ChatColor.RED)
                .setDtr(teams.get(ChatColor.RED).getMembers().size())
                .setTeamViewToMembers()
                .setLunarNametags().getMembers().forEach(p -> p.teleport(arena.getRedTeam().getHome()));
        teams.get(ChatColor.BLUE)
                .setDtr(teams.get(ChatColor.BLUE).getMembers().size())
                .setTeamViewToMembers()
                .setLunarNametags().getMembers().forEach(p -> p.teleport(arena.getBlueTeam().getHome()));
        teams.get(ChatColor.GREEN)
                .setDtr(teams.get(ChatColor.GREEN).getMembers().size())
                .setTeamViewToMembers()
                .setLunarNametags().getMembers().forEach(p -> p.teleport(arena.getGreenTeam().getHome()));
        teams.get(ChatColor.YELLOW)
                .setDtr(teams.get(ChatColor.YELLOW).getMembers().size())
                .setTeamViewToMembers()
                .setLunarNametags().getMembers().forEach(p -> p.teleport(arena.getYellowTeam().getHome()));
        applyWaypoints(arena,playersInQueue);
        spawnVillagers(arena);
        setScoreboard(teams.values(),arena);
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
            if (players.isEmpty()){
                task.cancel();
                return;
            }
            new ArrayList<>(players).forEach(player -> {
                if (!PlayerInfo.getPlayersBalance().containsKey(player)){
                    players.remove(player);
                    return;
                }
                PlayerInfo.getPlayersBalance().put(player,PlayerInfo.getPlayersBalance().get(player)+10);

            });
        },0,20*5);
    }

    public static void spawnVillagers(Arena arena){
        arena.getTeams().forEach((color,team) -> {
            if (!team.getSellShop().getChunk().isLoaded()){
                team.getSellShop().getChunk().load();
            }
            if (!team.getEquipmentShop().getChunk().isLoaded()){
                team.getEquipmentShop().getChunk().load();
            }
            if (!team.getBlockShop().getChunk().isLoaded()){
                team.getBlockShop().getChunk().load();
            }
            Task.runLater(_________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________ -> {
                Villager blockshop = team.getBlockShop().getWorld().spawn(team.getBlockShop(), Villager.class);
                Chat.bukkitSend(blockshop.isDead()+"|"+blockshop.getCustomName());
                blockshop.setAdult();
                blockshop.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,100000,10));
                blockshop.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,100000,10));
                blockshop.setCustomName(ChatColor.valueOf(color.toUpperCase())+"Block Shop");
                Villager sellshop = team.getSellShop().getWorld().spawn(team.getSellShop(), Villager.class);
                sellshop.setAdult();
                sellshop.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,100000,10));
                sellshop.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,100000,10));
                sellshop.setCustomName(ChatColor.valueOf(color.toUpperCase())+"Sell Shop");
                Villager equipmentshop = team.getEquipmentShop().getWorld().spawn(team.getEquipmentShop(), Villager.class);
                equipmentshop.setAdult();
                equipmentshop.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,100000,10));
                equipmentshop.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,100000,10));
                equipmentshop.setCustomName(ChatColor.valueOf(color.toUpperCase())+"Equipment Shop");
                Task.runTimer(a -> {
                    blockshop.setVelocity(new Vector(0,0,0));
                    sellshop.setVelocity(new Vector(0,0,0));
                    equipmentshop.setVelocity(new Vector(0,0,0));
                },0,1);
            },20*5); //if the server takes more than 5 seconds to load a chunk that's a skill issue idc
        });
    }

    public static void setScoreboard(Collection<Team> teams, Arena arena){
        Task.runTimer(task -> {
            if (!ArenaInfo.isArenaOccupied(arena)){
                teams.forEach(team -> team.getMembers().forEach(member -> {
                    member.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }));
                task.cancel();
                return;
            }
            teams.forEach(team -> team.getMembers().forEach(member -> {
                Scoreboard sc = Bukkit.getScoreboardManager().getNewScoreboard();;
                Objective objective = sc.getObjective("scoreboard") == null ? sc.registerNewObjective("scoreboard","dummy") : sc.getObjective("scoreboard");
                objective.setDisplayName(Chat.trans("&cgBunkers &7┃ &f"+arena.getName()));
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.getScore(Chat.trans("           ")).setScore(5);
                objective.getScore(Chat.trans(arena.getName()+"&e: "+PlayerInfo.getArenaKothCapTime().getOrDefault(arena,"06:00"))).setScore(4);
                objective.getScore(Chat.trans("&aDTR: "+(team.getDtr() > 0 ? team.getDtr() > 1 ? ChatColor.GREEN : ChatColor.YELLOW : ChatColor.RED) + (team.getDtr()+""))).setScore(3);
                objective.getScore(Chat.trans("&aBalance: &e"+PlayerInfo.getPlayersBalance().get(member))).setScore(2);
                if (Classes.isBard(member)){
                    objective.getScore(Chat.trans("&aEnergy: &e"+PlayerInfo.getBardEnergy().get(member))).setScore(1);
                }
                if (PlayerInfo.getPlayersEnderPearlCD().containsKey(member) && Time.timePassedSecs(PlayerInfo.getPlayersEnderPearlCD().get(member),System.currentTimeMillis()) <= 16){
                    objective.getScore(Chat.trans("&aPearl CD: &e"+(16-Time.timePassedSecs(PlayerInfo.getPlayersEnderPearlCD().get(member),System.currentTimeMillis()))+"s")).setScore(0);
                }
                member.setScoreboard(sc);
            }));
        },0,5);
    }

    public static void endGame(Arena arena, Player whoCapped){
        ArenaInfo.getArenasInUse().get(arena).values().forEach(Team::removeTeamViewFromMembers);
        PlayerInfo.getBlocksChanged().stream().filter(loc -> loc.getWorld().equals(arena.getWorld())).forEach(loc -> loc.getBlock().setType(Material.AIR));
        arena.getTeams().values().forEach(team -> {
            if (!team.getSellShop().getChunk().isLoaded()){
                team.getSellShop().getChunk().load();
            }
            if (!team.getEquipmentShop().getChunk().isLoaded()){
                team.getEquipmentShop().getChunk().load();
            }
            if (!team.getBlockShop().getChunk().isLoaded()){
                team.getBlockShop().getChunk().load();
            }
            Task.runLater(a -> arena.getWorld().getEntities().stream().filter(e -> e instanceof Villager).forEach(Entity::remove),1);
        });
        arena.getWorld().getPlayers().forEach(p -> {
            p.teleport(PluginConfig.getLobbyLocation());
            PlayerInfo.getPlayersInGame().remove(p.getUniqueId());
            PlayerInfo.getPlayersBalance().remove(p);
            p.setGameMode(GameMode.ADVENTURE);
        });
        ArenaInfo.getArenasInUse().get(arena).values().stream().filter(team -> team.getMembers().contains(whoCapped)).findFirst().ifPresent(team -> {
            Bukkit.broadcastMessage(Chat.trans("&aTeam %color%%team% (%members%) &awon a game!"
                    .replace("%color%",team.getColor().toString())
                    .replace("%team%",team.getColor().name())
                    .replace("%members%", team.getMembers().stream().map(Player::getDisplayName).collect(Collectors.joining(", ")))));
            team.getMembers().forEach(p -> {
                for (int i = 1; i <= 10; i++){
                    Firework fw = p.getWorld().spawn(p.getLocation(), Firework.class);
                    FireworkMeta fwm = fw.getFireworkMeta();
                    fwm.setPower(2);
                    fwm.addEffect(FireworkEffect.builder().withColor(org.bukkit.Color.LIME).withFade().withTrail().build());
                    fw.setFireworkMeta(fwm);
                    Task.runLater(t -> fw.detonate(),20*i);
                }
            });
        });
        PlayerInfo.getPlayersCappingKoth().remove(arena);
        ArenaInfo.getArenasInUse().remove(arena);
    }
}
