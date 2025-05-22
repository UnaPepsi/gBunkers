package ga.guimx.gbunkers.config;

import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.utils.Arena;
import ga.guimx.gbunkers.utils.Chat;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArenasConfig {
    @Getter
    private static final ArenasConfig instance = new ArenasConfig();
    private File file;
    private YamlConfiguration config;
    @Getter
    private static List<Arena> arenas = new ArrayList<>();
    public void load(){
        file = new File(GBunkers.getInstance().getDataFolder(),"arenas.yml");
        if (!file.exists()){
            GBunkers.getInstance().saveResource("arenas.yml",false);
        }
        config = new YamlConfiguration();
        try{
            config.load(file);
        }catch (Exception e){
            e.printStackTrace();
        }
        loadConfig();
    }
    public void reload(){
        file = new File(GBunkers.getInstance().getDataFolder(),"arenas.yml");
        if (!file.exists()){
            GBunkers.getInstance().saveResource("arenas.yml",false);
            config = new YamlConfiguration();
        }
        try{
            config = YamlConfiguration.loadConfiguration(file);
            loadConfig();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void loadConfig(){
        arenas.clear();
        config.getKeys(false).forEach(key -> {
            String path = key+".";
            if (arenas.stream().anyMatch(arena -> arena.getName().equalsIgnoreCase(key))){
                throw new IllegalArgumentException(String.format("There are multiple arenas named \"%s\"",key));
            }
            World world = Bukkit.getWorld(config.getString(path+"world"));
            arenas.add(Arena.builder()
                    .name(config.getConfigurationSection(key).getName())
                    .world(world)
                    .border1(new Location(world,
                            config.getDouble(path+"border_1.x"),
                            config.getDouble(path+"border_1.y"),
                            config.getDouble(path+"border_1.z")))
                    .border2(new Location(world,
                            config.getDouble(path+"border_2.x"),
                            config.getDouble(path+"border_2.y"),
                            config.getDouble(path+"border_2.z")))
                    .redTeam(Arena.Team.builder()
                            .color(ChatColor.RED)
                            .home(new Location(world,
                                    config.getDouble(path+"red_team.home.x"),
                                    config.getDouble(path+"red_team.home.y"),
                                    config.getDouble(path+"red_team.home.z"),
                                    config.getFloat(path+"red_team.home.yaw"),
                                    config.getFloat(path+"red_team.home.pitch"))
                            )
                            .claimBorder1(new Location(world,
                                    config.getDouble(path+"red_team.claim_border_1.x"),
                                    0,
                                    config.getDouble(path+"red_team.claim_border_1.z")))
                            .claimBorder2(new Location(world,
                                    config.getDouble(path+"red_team.claim_border_2.x"),
                                    0,
                                    config.getDouble(path+"red_team.claim_border_2.z")))
                            .blockShop(new Location(world,
                                    config.getDouble(path+"red_team.block_shop.x"),
                                    config.getDouble(path+"red_team.block_shop.y"),
                                    config.getDouble(path+"red_team.block_shop.z"),
                                    config.getFloat(path+"red_team.block_shop.yaw"),
                                    config.getFloat(path+"red_team.block_shop.pitch")))
                            .equipmentShop(new Location(world,
                                    config.getDouble(path+"red_team.equipment_shop.x"),
                                    config.getDouble(path+"red_team.equipment_shop.y"),
                                    config.getDouble(path+"red_team.equipment_shop.z"),
                                    config.getFloat(path+"red_team.equipment_shop.yaw"),
                                    config.getFloat(path+"red_team.equipment_shop.pitch")))
                            .sellShop(new Location(world,
                                    config.getDouble(path+"red_team.sell_shop.x"),
                                    config.getDouble(path+"red_team.sell_shop.y"),
                                    config.getDouble(path+"red_team.sell_shop.z"),
                                    config.getFloat(path+"red_team.sell_shop.yaw"),
                                    config.getFloat(path+"red_team.sell_shop.pitch")))
                            .build()
                    )
                    .blueTeam(Arena.Team.builder()
                            .color(ChatColor.BLUE)
                            .home(new Location(world,
                                    config.getDouble(path+"blue_team.home.x"),
                                    config.getDouble(path+"blue_team.home.y"),
                                    config.getDouble(path+"blue_team.home.z"),
                                    config.getFloat(path+"blue_team.home.yaw"),
                                    config.getFloat(path+"blue_team.home.pitch"))
                            )
                            .claimBorder1(new Location(world,
                                    config.getDouble(path+"blue_team.claim_border_1.x"),
                                    0,
                                    config.getDouble(path+"blue_team.claim_border_1.z")))
                            .claimBorder2(new Location(world,
                                    config.getDouble(path+"blue_team.claim_border_2.x"),
                                    0,
                                    config.getDouble(path+"blue_team.claim_border_2.z")))
                            .blockShop(new Location(world,
                                    config.getDouble(path+"blue_team.block_shop.x"),
                                    config.getDouble(path+"blue_team.block_shop.y"),
                                    config.getDouble(path+"blue_team.block_shop.z"),
                                    config.getFloat(path+"blue_team.block_shop.yaw"),
                                    config.getFloat(path+"blue_team.block_shop.pitch")))
                            .equipmentShop(new Location(world,
                                    config.getDouble(path+"blue_team.equipment_shop.x"),
                                    config.getDouble(path+"blue_team.equipment_shop.y"),
                                    config.getDouble(path+"blue_team.equipment_shop.z"),
                                    config.getFloat(path+"blue_team.equipment_shop.yaw"),
                                    config.getFloat(path+"blue_team.equipment_shop.pitch")))
                            .sellShop(new Location(world,
                                    config.getDouble(path+"blue_team.sell_shop.x"),
                                    config.getDouble(path+"blue_team.sell_shop.y"),
                                    config.getDouble(path+"blue_team.sell_shop.z"),
                                    config.getFloat(path+"blue_team.sell_shop.yaw"),
                                    config.getFloat(path+"blue_team.sell_shop.pitch")))
                            .build()
                    )
                    .yellowTeam(Arena.Team.builder()
                            .color(ChatColor.YELLOW)
                            .home(new Location(world,
                                    config.getDouble(path+"yellow_team.home.x"),
                                    config.getDouble(path+"yellow_team.home.y"),
                                    config.getDouble(path+"yellow_team.home.z"),
                                    config.getFloat(path+"yellow_team.home.yaw"),
                                    config.getFloat(path+"yellow_team.home.pitch"))
                            )
                            .claimBorder1(new Location(world,
                                    config.getDouble(path+"yellow_team.claim_border_1.x"),
                                    0,
                                    config.getDouble(path+"yellow_team.claim_border_1.z")))
                            .claimBorder2(new Location(world,
                                    config.getDouble(path+"yellow_team.claim_border_2.x"),
                                    0,
                                    config.getDouble(path+"yellow_team.claim_border_2.z")))
                            .blockShop(new Location(world,
                                    config.getDouble(path+"yellow_team.block_shop.x"),
                                    config.getDouble(path+"yellow_team.block_shop.y"),
                                    config.getDouble(path+"yellow_team.block_shop.z"),
                                    config.getFloat(path+"yellow_team.block_shop.yaw"),
                                    config.getFloat(path+"yellow_team.block_shop.pitch")))
                            .equipmentShop(new Location(world,
                                    config.getDouble(path+"yellow_team.equipment_shop.x"),
                                    config.getDouble(path+"yellow_team.equipment_shop.y"),
                                    config.getDouble(path+"yellow_team.equipment_shop.z"),
                                    config.getFloat(path+"yellow_team.equipment_shop.yaw"),
                                    config.getFloat(path+"yellow_team.equipment_shop.pitch")))
                            .sellShop(new Location(world,
                                    config.getDouble(path+"yellow_team.sell_shop.x"),
                                    config.getDouble(path+"yellow_team.sell_shop.y"),
                                    config.getDouble(path+"yellow_team.sell_shop.z"),
                                    config.getFloat(path+"yellow_team.sell_shop.yaw"),
                                    config.getFloat(path+"yellow_team.sell_shop.pitch")))
                            .build()
                    )
                    .greenTeam(Arena.Team.builder()
                            .color(ChatColor.GREEN)
                            .home(new Location(world,
                                    config.getDouble(path+"green_team.home.x"),
                                    config.getDouble(path+"green_team.home.y"),
                                    config.getDouble(path+"green_team.home.z"),
                                    config.getFloat(path+"green_team.home.yaw"),
                                    config.getFloat(path+"green_team.home.pitch"))
                            )
                            .claimBorder1(new Location(world,
                                    config.getDouble(path+"green_team.claim_border_1.x"),
                                    0,
                                    config.getDouble(path+"green_team.claim_border_1.z")))
                            .claimBorder2(new Location(world,
                                    config.getDouble(path+"green_team.claim_border_2.x"),
                                    0,
                                    config.getDouble(path+"green_team.claim_border_2.z")))
                            .blockShop(new Location(world,
                                    config.getDouble(path+"green_team.block_shop.x"),
                                    config.getDouble(path+"green_team.block_shop.y"),
                                    config.getDouble(path+"green_team.block_shop.z"),
                                    config.getFloat(path+"green_team.block_shop.yaw"),
                                    config.getFloat(path+"green_team.block_shop.pitch")))
                            .equipmentShop(new Location(world,
                                    config.getDouble(path+"green_team.equipment_shop.x"),
                                    config.getDouble(path+"green_team.equipment_shop.y"),
                                    config.getDouble(path+"green_team.equipment_shop.z"),
                                    config.getFloat(path+"green_team.equipment_shop.yaw"),
                                    config.getFloat(path+"green_team.equipment_shop.pitch")))
                            .sellShop(new Location(world,
                                    config.getDouble(path+"green_team.sell_shop.x"),
                                    config.getDouble(path+"green_team.sell_shop.y"),
                                    config.getDouble(path+"green_team.sell_shop.z"),
                                    config.getFloat(path+"green_team.sell_shop.yaw"),
                                    config.getFloat(path+"green_team.sell_shop.pitch")))
                            .build()
                    )
                    .koth(Arena.Koth.builder()
                            .name(Chat.trans(config.getString(path+"koth.name")))
                            .claimBorder1(new Location(world,
                                    config.getDouble(path+"koth.claim_border_1.x"),
                                    0,
                                    config.getDouble(path+"koth.claim_border_1.z")))
                            .claimBorder2(new Location(world,
                                    config.getDouble(path+"koth.claim_border_2.x"),
                                    0,
                                    config.getDouble(path+"koth.claim_border_2.z")))
                            .lowestCapzoneCorner(new Location(world,
                                    config.getDouble(path+"koth.lowest_capzone_corner.x"),
                                    config.getDouble(path+"koth.lowest_capzone_corner.y"),
                                    config.getDouble(path+"koth.lowest_capzone_corner.z")))
                            .highestCapzoneCorner(new Location(world,
                                    config.getDouble(path+"koth.highest_capzone_corner.x"),
                                    config.getDouble(path+"koth.highest_capzone_corner.y"),
                                    config.getDouble(path+"koth.highest_capzone_corner.z")))
                            .pearlsDisabled(config.getBoolean(path+"koth.pearls_disabled"))
                            .build())
                    .spectatorSpawn(new Location(world,
                        config.getDouble(path+"spectator_spawn.x"),
                        config.getDouble(path+"spectator_spawn.y"),
                        config.getDouble(path+"spectator_spawn.z"),
                        config.getFloat(path+"spectator_spawn.yaw"),
                        config.getFloat(path+"spectator_spawn.pitch")))
                    .build()
            );
        });
    }
    public void addArena(Arena arena) throws IOException {
        if (arenas.stream().anyMatch(a -> arena.getName().equalsIgnoreCase(a.getName()))){
            throw new IllegalArgumentException(String.format("There are multiple arenas named \"%s\"",arena.getName()));
        }
        ConfigurationSection configSection = config.createSection(arena.getName());
        configSection.set("world",arena.getWorld().getName());
        configSection.set("border_1.x",arena.getBorder1().getX());
        configSection.set("border_1.y",arena.getBorder1().getY());
        configSection.set("border_1.z",arena.getBorder1().getZ());
        configSection.set("border_2.x",arena.getBorder2().getX());
        configSection.set("border_2.y",arena.getBorder2().getY());
        configSection.set("border_2.z",arena.getBorder2().getZ());
        arena.getTeams().forEach((c,a) -> {
            configSection.set(c+"_team.home.x",a.getHome().getX());
            configSection.set(c+"_team.home.y",a.getHome().getY());
            configSection.set(c+"_team.home.z",a.getHome().getZ());
            configSection.set(c+"_team.home.yaw",a.getHome().getYaw());
            configSection.set(c+"_team.home.pitch",a.getHome().getPitch());
            configSection.set(c+"_team.claim_border_1.x",a.getClaimBorder1().getX());
            configSection.set(c+"_team.claim_border_1.z",a.getClaimBorder1().getZ());
            configSection.set(c+"_team.claim_border_2.x",a.getClaimBorder2().getX());
            configSection.set(c+"_team.claim_border_2.z",a.getClaimBorder2().getZ());
            configSection.set(c+"_team.block_shop.x",a.getBlockShop().getX());
            configSection.set(c+"_team.block_shop.y",a.getBlockShop().getY());
            configSection.set(c+"_team.block_shop.z",a.getBlockShop().getZ());
            configSection.set(c+"_team.equipment_shop.x",a.getEquipmentShop().getX());
            configSection.set(c+"_team.equipment_shop.y",a.getEquipmentShop().getY());
            configSection.set(c+"_team.equipment_shop.z",a.getEquipmentShop().getZ());
            configSection.set(c+"_team.sell_shop.x",a.getSellShop().getX());
            configSection.set(c+"_team.sell_shop.y",a.getSellShop().getY());
            configSection.set(c+"_team.sell_shop.z",a.getSellShop().getZ());
        });
        configSection.set("koth.name",arena.getKoth().getName());
        configSection.set("koth.claim_border_1.x",arena.getKoth().getClaimBorder1().getX());
        configSection.set("koth.claim_border_1.z",arena.getKoth().getClaimBorder1().getZ());
        configSection.set("koth.claim_border_2.x",arena.getKoth().getClaimBorder2().getX());
        configSection.set("koth.claim_border_2.z",arena.getKoth().getClaimBorder2().getZ());
        configSection.set("koth.lowest_capzone_corner.x",arena.getKoth().getLowestCapzoneCorner().getX());
        configSection.set("koth.lowest_capzone_corner.y",arena.getKoth().getLowestCapzoneCorner().getY());
        configSection.set("koth.lowest_capzone_corner.z",arena.getKoth().getLowestCapzoneCorner().getZ());
        configSection.set("koth.highest_capzone_corner.x",arena.getKoth().getHighestCapzoneCorner().getX());
        configSection.set("koth.highest_capzone_corner.y",arena.getKoth().getHighestCapzoneCorner().getY());
        configSection.set("koth.highest_capzone_corner.z",arena.getKoth().getHighestCapzoneCorner().getZ());
        configSection.set("koth.pearls_disabled",arena.getKoth().isPearlsDisabled());
        configSection.set("spectator_spawn.x",arena.getSpectatorSpawn().getX());
        configSection.set("spectator_spawn.y",arena.getSpectatorSpawn().getY());
        configSection.set("spectator_spawn.z",arena.getSpectatorSpawn().getZ());
        configSection.set("spectator_spawn.yaw",arena.getSpectatorSpawn().getYaw());
        configSection.set("spectator_spawn.pitch",arena.getSpectatorSpawn().getPitch());

        config.save(file);
    }

    public void deleteArena(Arena arena) throws IOException{
        config.set(arena.getName(),null);
        config.save(file);
    }
    public void deleteArena(String arena) throws IOException{
        Optional<Arena> arena1 = arenas.stream().filter(a -> a.getName().equalsIgnoreCase(arena)).findFirst();
        if (!arena1.isPresent()){
            throw new IllegalArgumentException("Invalid arena: "+arena);
        }
        deleteArena(arena1.get());
    }
}
