package ga.guimx.gbunkers.config;

import ga.guimx.gbunkers.GBunkers;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class PluginConfig {
    @Getter
    private final static PluginConfig instance = new PluginConfig();
    private File file;
    private YamlConfiguration config;
    @Getter
    private final static HashMap<String,String> messages = new HashMap<>();
    @Getter
    private static Location lobbyLocation;
    @Getter
    private static HashMap<String,ItemStack> lobbyInventory = new HashMap<>();
    @Getter
    private static List<String> scoreboard;
    public void load(){
        file = new File(GBunkers.getInstance().getDataFolder(),"config.yml");
        if (!file.exists()){
            GBunkers.getInstance().saveResource("config.yml",false);
        }
        config = new YamlConfiguration();
        try{
            config.load(file);
        }catch (Exception e){
            e.printStackTrace();
        }
        loadMessages();
        loadConfig();
    }
    public void reload(){
        file = new File(GBunkers.getInstance().getDataFolder(),"config.yml");
        if (!file.exists()){
            GBunkers.getInstance().saveResource("config.yml",false);
            config = new YamlConfiguration();
        }
        try{
            config = YamlConfiguration.loadConfiguration(file);
            loadMessages();
            loadConfig();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void loadMessages(){
        GBunkers.setPrefix(config.getString("prefix"));
        config.getConfigurationSection("messages").getKeys(false).forEach(key -> {
            messages.put(key,config.getString("messages."+key));
        });
    }
    private void loadConfig(){
        lobbyLocation = new Location(
                Bukkit.getWorld(config.getString("lobby.world")),
                config.getDouble("lobby.x"),
                config.getDouble("lobby.y"),
                config.getDouble("lobby.z"),
                config.getFloat("lobby.yaw"),
                config.getFloat("lobby.pitch")
        );
        config.getConfigurationSection("lobby_items").getKeys(false).forEach(key -> {
            String[] t = config.getString("lobby_items."+key).split(":");
            lobbyInventory.put(key, new ItemStack(Material.valueOf(t[0]),1,Byte.parseByte(t[1])));
        });
        scoreboard = config.getStringList("scoreboard");
    }
    public static void setLobbyLocation(Location location){
        instance.config.set("lobby.world",location.getWorld());
        instance.config.set("lobby.x",location.getX());
        instance.config.set("lobby.y",location.getY());
        instance.config.set("lobby.z",location.getZ());
        instance.config.set("lobby.yaw",location.getYaw());
        instance.config.set("lobby.pitch",location.getPitch());
        lobbyLocation = location.clone();
    }
}
