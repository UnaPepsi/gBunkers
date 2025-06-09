package ga.guimx.gbunkers;

import com.lunarclient.apollo.event.EventBus;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.message.LiteMessages;
import ga.guimx.gbunkers.commands.*;
import ga.guimx.gbunkers.config.ArenasConfig;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.listeners.EntityListener;
import ga.guimx.gbunkers.listeners.LobbyItemsListener;
import ga.guimx.gbunkers.listeners.PlayerListener;
import ga.guimx.gbunkers.utils.*;
import lombok.Getter;
import lombok.Setter;
import mc.obliviate.inventory.InventoryAPI;
import mc.obliviate.inventory.configurable.ConfigurableGuiCache;
import mc.obliviate.inventory.configurable.GuiConfigurationTable;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;

public class GBunkers extends JavaPlugin {
    @Getter
    private static GBunkers instance;
    @Getter
    @Setter
    private static String prefix;
    @Getter
    private static final TeamManager teamManager = new TeamManager();
    private LiteCommands<CommandSender> liteCommands;
    private PlayerListener playerListener;
    @Override
    public void onEnable(){
        instance = this;
        playerListener = new PlayerListener();
        PluginConfig.getInstance().load();
        ArenasConfig.getInstance().load();
        EventBus.getBus().register(playerListener);
        liteCommands = LiteBukkitFactory.builder("gbunkers",this)
                .commands(new TestCommand(), new QueueCommand(),new LobbyCommand(),new ArenaCommand(), new FactionCommand())
                .argument(Arena.class,new ArenaArgument())
                .message(LiteMessages.MISSING_PERMISSIONS, permissions -> Chat.transPrefix(PluginConfig.getMessages().get("no_permissions")
                        .replace("%missing_permissions%",permissions.asJoinedText())))
                .build();
        enableListeners();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            PAPIHook.registerHook();
            Chat.bukkitSend("Hooked to PlaceholderAPI");
        }
        Bukkit.getWorlds().forEach(world -> {
            world.setGameRuleValue("doMobSpawning","false");
            world.setGameRuleValue("randomTickSpeed","0");
            world.setGameRuleValue("doFireTick","false");
        });
        customGuiConfig();
        lobbyScoreboard();
        Chat.bukkitSend(PluginConfig.getMessages().get("plugin_enabled"));
        checkForUpdates();
    }
    @Override
    public void onDisable(){
        if (liteCommands != null){
            liteCommands.unregister();
        }
        Chat.bukkitSend(PluginConfig.getMessages().get("plugin_disabled"));
    }
    void enableListeners(){
        getServer().getPluginManager().registerEvents(playerListener,this);
        getServer().getPluginManager().registerEvents(new LobbyItemsListener(),this);
        getServer().getPluginManager().registerEvents(new EntityListener(),this);
    }
    void checkForUpdates(){
        Task.runAsync(c -> {
            try{
                String newPossibleVersion = PluginUpdates.getLatestVersion();
                if (!newPossibleVersion.equals(getDescription().getVersion())){
                    Bukkit.getConsoleSender().sendMessage(Chat.transPrefix(PluginConfig.getMessages().get("new_plugin_version_available")
                            .replace("%current_version%", getDescription().getVersion())
                            .replace("%new_version%", newPossibleVersion)
                            .replace("%repository%", "https://github.com/UnaPepsi/gBunkers/releases")));
                }
            }catch (IOException e) {
                getLogger().warning("gBunkers couldn't get the latest version of the plugin");
            }
        });
    }
    void customGuiConfig(){
        new InventoryAPI(this).init();
        ConfigurableGuiCache.resetCaches();
        File file = new File(getDataFolder(), "villager-gui.yml");
        if (!file.exists()) GBunkers.getInstance().saveResource("villager-gui.yml",false);
        FileConfiguration villagerGui = YamlConfiguration.loadConfiguration(file);
        GuiConfigurationTable.setDefaultConfigurationTable(new GuiConfigurationTable(villagerGui));
    }
    void lobbyScoreboard(){
        Task.runTimer(t -> {
            Bukkit.getOnlinePlayers().stream().filter(p -> !PlayerInfo.getPlayersInGame().contains(p.getUniqueId())).forEach(p -> {
                Scoreboard sc = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective ob = sc.getObjective("lobby") == null ? sc.registerNewObjective("lobby","dummy") : sc.getObjective("lobby");
                ob.setDisplayName(Chat.trans("&cgBunkers &7┃ &aLobby"));
                ob.setDisplaySlot(DisplaySlot.SIDEBAR);
                ob.getScore("  ").setScore(2);
                ob.getScore(Chat.trans("&aIn Queue: &e"+PlayerInfo.getPlayersQueued().size())).setScore(1);
                ob.getScore(Chat.trans("&aIn Game: &e"+PlayerInfo.getPlayersInGame().size())).setScore(0);
                p.setScoreboard(sc);
            });
        },0,20);
    }
}
