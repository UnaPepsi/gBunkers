package ga.guimx.gbunkers.config;

import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.utils.Arena;
import ga.guimx.gbunkers.utils.Chat;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ArenasConfig {
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
                    .name(config.getString(path+"name"))
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
                            .color(Color.RED)
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
                            .color(Color.BLUE)
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
                            .color(Color.YELLOW)
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
                            .color(Color.GREEN)
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
                            .name(Chat.transNoPrefix(config.getString(path+"koth.name")))
                            .claimBorder1(new Location(world,
                                    config.getDouble(path+"koth.claim_border_1.x"),
                                    0,
                                    config.getDouble(path+"koth.claim_border_1.z"),
                                    config.getFloat(path+"koth.claim_border_1.yaw"),
                                    config.getFloat(path+"koth.claim_border_1.pitch")))
                            .claimBorder2(new Location(world,
                                    config.getDouble(path+"koth.claim_border_2.x"),
                                    0,
                                    config.getDouble(path+"koth.claim_border_2.z"),
                                    config.getFloat(path+"koth.claim_border_2.yaw"),
                                    config.getFloat(path+"koth.claim_border_2.pitch")))
                            .lowestCapzoneCorner(new Location(world,
                                    config.getDouble(path+"koth.lowest_capzone_corner.x"),
                                    config.getDouble(path+"koth.lowest_capzone_corner.y"),
                                    config.getDouble(path+"koth.lowest_capzone_corner.z"),
                                    config.getFloat(path+"koth.lowest_capzone_corner.yaw"),
                                    config.getFloat(path+"koth.lowest_capzone_corner.pitch")))
                            .highestCapzoneCorner(new Location(world,
                                    config.getDouble(path+"koth.highest_capzone_corner.x"),
                                    config.getDouble(path+"koth.highest_capzone_corner.y"),
                                    config.getDouble(path+"koth.highest_capzone_corner.z"),
                                    config.getFloat(path+"koth.highest_capzone_corner.yaw"),
                                    config.getFloat(path+"koth.highest_capzone_corner.pitch")))
                            .arePearlsDisabled(config.getBoolean(path+"koth.pearls_disabled"))
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
}
