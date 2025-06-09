package ga.guimx.gbunkers.game;

import com.google.common.collect.Maps;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.BukkitApollo;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.recipients.Recipients;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.utils.*;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
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

import java.awt.Color;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class Game {
    public static void startGame(Arena arena){
        if (ArenaInfo.isArenaOccupied(arena)){
            throw new IllegalStateException("Arena already in use: "+arena.getName());
        }
        List<Player> playersInQueue = new ArrayList<>(PlayerInfo.getPlayersQueued()).stream().map(Bukkit::getPlayer).collect(Collectors.toList()).subList(0,Math.min(PlayerInfo.getPlayersQueued().size(),PluginConfig.getPlayersNeededToStartGame()));
        //Map<ChatColor, List<Player>> teams = Maps.newHashMap();
        Map<ChatColor, Team> teams = Maps.newHashMap();
        ChatColor[] colors = {ChatColor.RED,ChatColor.BLUE,ChatColor.GREEN,ChatColor.YELLOW};
        for (ChatColor color : colors) {
            teams.put(color,new Team(new ArrayList<>(),color));
        }
        for (Player player : playersInQueue) {
            ChatColor color = colors[playersInQueue.indexOf(player)%PluginConfig.getPlayersPerTeam()];
            teams.get(color).getMembers().add(player.getUniqueId());
            PlayerInfo.getPlayersInGame().add(player.getUniqueId());
            PlayerInfo.getPlayersBalance().put(player.getUniqueId(),0);
            player.setGameMode(GameMode.SURVIVAL);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,20*10,5),true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,20*10,5),true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,20*10,5),true);
            player.getInventory().clear();
            player.getInventory().setContents(new ItemStack[]{new ItemStack(Material.STONE_PICKAXE),new ItemStack(Material.STONE_AXE)});
            PlayerInfo.getPlayersQueued().remove(player.getUniqueId());
            PlayerInfo.getPlayersSpectating().remove(player.getUniqueId());
        }
        giveMoneyToPlayers(playersInQueue);
        ArenaInfo.getArenasInUse().put(arena,teams);
        teams.get(ChatColor.RED)
                .setDtr(teams.get(ChatColor.RED).getMembers().size())
                .setTeamViewToMembers()
                .setLunarNametags().getMembers().forEach(uuid -> Bukkit.getPlayer(uuid).teleport(arena.getRedTeam().getHome()));
        teams.get(ChatColor.BLUE)
                .setDtr(teams.get(ChatColor.BLUE).getMembers().size())
                .setTeamViewToMembers()
                .setLunarNametags().getMembers().forEach(uuid -> Bukkit.getPlayer(uuid).teleport(arena.getBlueTeam().getHome()));
        teams.get(ChatColor.GREEN)
                .setDtr(teams.get(ChatColor.GREEN).getMembers().size())
                .setTeamViewToMembers()
                .setLunarNametags().getMembers().forEach(uuid -> Bukkit.getPlayer(uuid).teleport(arena.getGreenTeam().getHome()));
        teams.get(ChatColor.YELLOW)
                .setDtr(teams.get(ChatColor.YELLOW).getMembers().size())
                .setTeamViewToMembers()
                .setLunarNametags().getMembers().forEach(uuid -> Bukkit.getPlayer(uuid).teleport(arena.getYellowTeam().getHome()));
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
                .name(arena.getKoth().getName())
                .location(BukkitApollo.toApolloBlockLocation(arena.getKoth().getLowestCapzoneCorner().clone().add(arena.getKoth().getHighestCapzoneCorner()).multiply(0.5)))
                .color(Chat.getColor(arena.getKoth().getName()))
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
                if (!PlayerInfo.getPlayersBalance().containsKey(player.getUniqueId())){
                    players.remove(player);
                    return;
                }
                PlayerInfo.getPlayersBalance().put(player.getUniqueId(),PlayerInfo.getPlayersBalance().get(player.getUniqueId())+10);

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
                WorldServer nmsWorld = ((CraftWorld) team.getHome().getWorld()).getHandle();
                NBTTagCompound tag;
                EntityVillager blockshop = new EntityVillager(nmsWorld);
                blockshop.setPosition(team.getBlockShop().getX(),team.getBlockShop().getY(),team.getBlockShop().getZ());
                blockshop.b(true); //https://www.spigotmc.org/threads/making-a-mob-silent.122090/
                blockshop.setCustomName(ChatColor.valueOf(color.toUpperCase())+"Block Shop");
                blockshop.setAge(1);
                // https://www.spigotmc.org/threads/how-to-give-a-mob-noai.44337/
                tag = blockshop.getNBTTag();
                if (tag == null) {
                    tag = new NBTTagCompound();
                }
                blockshop.c(tag);
                tag.setInt("NoAI", 1);
                blockshop.f(tag);
                nmsWorld.addEntity(blockshop);
                EntityVillager sellshop = new EntityVillager(nmsWorld);
                sellshop.setPosition(team.getSellShop().getX(),team.getSellShop().getY(),team.getSellShop().getZ());
                sellshop.b(true);
                sellshop.setCustomName(ChatColor.valueOf(color.toUpperCase())+"Sell Shop");
                sellshop.setAge(1);
                tag = sellshop.getNBTTag();
                if (tag == null) {
                    tag = new NBTTagCompound();
                }
                sellshop.c(tag);
                tag.setInt("NoAI", 1);
                sellshop.f(tag);
                nmsWorld.addEntity(sellshop);
                EntityVillager equipmentshop = new EntityVillager(nmsWorld);
                equipmentshop.setPosition(team.getBlockShop().getX(),team.getBlockShop().getY(),team.getBlockShop().getZ());
                equipmentshop.b(true);
                equipmentshop.setCustomName(ChatColor.valueOf(color.toUpperCase())+"Equipment Shop");
                equipmentshop.setAge(1);
                tag = equipmentshop.getNBTTag();
                if (tag == null) {
                    tag = new NBTTagCompound();
                }
                equipmentshop.c(tag);
                tag.setInt("NoAI", 1);
                equipmentshop.f(tag);
                nmsWorld.addEntity(equipmentshop);
            },20*5); //if the server takes more than 5 seconds to load a chunk that's a skill issue idc
        });
    }

    public static void setScoreboard(Collection<Team> teams, Arena arena){
        Task.runTimer(task -> {
            if (!ArenaInfo.isArenaOccupied(arena)){
                teams.forEach(team -> team.getMembers().forEach(uuid -> {
                    Bukkit.getPlayer(uuid).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }));
                task.cancel();
                return;
            }
            teams.forEach(team -> team.getMembers().forEach(uuid -> {
                if (Bukkit.getPlayer(uuid) == null) return;
                Scoreboard sc = Bukkit.getScoreboardManager().getNewScoreboard();;
                Objective objective = sc.getObjective("scoreboard") == null ? sc.registerNewObjective("scoreboard","dummy") : sc.getObjective("scoreboard");
                objective.setDisplayName(Chat.trans("&cgBunkers &7┃ &f"+arena.getName()));
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.getScore(Chat.trans("           ")).setScore(5);
                objective.getScore(Chat.trans(arena.getName()+"&e: "+PlayerInfo.getArenaKothCapTime().getOrDefault(arena,"06:00"))).setScore(4);
                objective.getScore(Chat.trans("&aDTR: "+(team.getDtr() > 0 ? team.getDtr() > 1 ? ChatColor.GREEN : ChatColor.YELLOW : ChatColor.RED) + (team.getDtr()+""))).setScore(3);
                objective.getScore(Chat.trans("&aBalance: &e"+PlayerInfo.getPlayersBalance().get(uuid))).setScore(2);
                if (Classes.isBard(Bukkit.getPlayer(uuid))){
                    objective.getScore(Chat.trans("&aEnergy: &e"+PlayerInfo.getBardEnergy().get(uuid))).setScore(1);
                }
                if (PlayerInfo.getPlayersEnderPearlCD().containsKey(uuid) && Time.timePassedSecs(PlayerInfo.getPlayersEnderPearlCD().get(uuid),System.currentTimeMillis()) <= 16){
                    objective.getScore(Chat.trans("&aPearl CD: &e"+(16-Time.timePassedSecs(PlayerInfo.getPlayersEnderPearlCD().get(uuid),System.currentTimeMillis()))+"s")).setScore(0);
                }
                Bukkit.getPlayer(uuid).setScoreboard(sc);
            }));
        },0,5);
    }

    public static void endGame(Arena arena, Player whoCapped){
        ArenaInfo.getArenasInUse().get(arena).values().forEach(team -> {
            team.removeTeamViewFromMembers();
            team.removeWaypoints();
        });
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
            PlayerInfo.getPlayersBalance().remove(p.getUniqueId());
            p.setGameMode(GameMode.ADVENTURE);
        });
        ArenaInfo.getArenasInUse().get(arena).values().stream().filter(team -> team.getMembers().contains(whoCapped.getUniqueId())).findFirst().ifPresent(team -> {
            Bukkit.broadcastMessage(Chat.trans("&aTeam %color%%team% (%members%) &awon a game!"
                    .replace("%color%",team.getColor().toString())
                    .replace("%team%",team.getColor().name())
                    .replace("%members%", team.getMembers().stream().map(uuid -> Bukkit.getPlayer(uuid).getDisplayName()).collect(Collectors.joining(", ")))));
            team.getMembers().forEach(uuid -> {
                Player p = Bukkit.getPlayer(uuid);
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
