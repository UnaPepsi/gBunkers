package ga.guimx.gbunkers;

import com.lunarclient.apollo.event.EventBus;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.message.LiteMessages;
import ga.guimx.gbunkers.commands.ArenaCommand;
import ga.guimx.gbunkers.commands.LobbyCommand;
import ga.guimx.gbunkers.commands.QueueCommand;
import ga.guimx.gbunkers.commands.TestCommand;
import ga.guimx.gbunkers.config.ArenasConfig;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.listeners.JoinLeaveQueueListener;
import ga.guimx.gbunkers.listeners.PlayerListener;
import ga.guimx.gbunkers.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

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
    public void onEnable(){
        instance = this;
        playerListener = new PlayerListener();
        PluginConfig.getInstance().load();
        ArenasConfig.getInstance().load();
        EventBus.getBus().register(playerListener);
        liteCommands = LiteBukkitFactory.builder("gbunkers",this)
                .commands(new TestCommand(), new QueueCommand(),new LobbyCommand(),new ArenaCommand())
                .argument(Arena.class,new ArenaArgument())
                .message(LiteMessages.MISSING_PERMISSIONS, permissions -> Chat.trans(PluginConfig.getMessages().get("no_permissions")
                        .replace("%missing_permissions%",permissions.asJoinedText())))
                .build();
        enableListeners();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            PAPIHook.registerHook();
        }
        PluginConfig.getLobbyLocation().getWorld().setGameRuleValue("doMobSpawning","false");
        Chat.bukkitSend(PluginConfig.getMessages().get("plugin_enabled"));
        checkForUpdates();
    }

    public void onDisable(){
        if (liteCommands != null){
            liteCommands.unregister();
        }
        Chat.bukkitSend(PluginConfig.getMessages().get("plugin_disabled"));
    }
    void enableListeners(){
        getServer().getPluginManager().registerEvents(playerListener,this);
        getServer().getPluginManager().registerEvents(new JoinLeaveQueueListener(),this);
    }
    void checkForUpdates(){
        Task.runAsync(c -> {
            try{
                String newPossibleVersion = PluginUpdates.getLatestVersion();
                if (!newPossibleVersion.equals(getDescription().getVersion())){
                    Bukkit.getConsoleSender().sendMessage(Chat.trans(prefix + PluginConfig.getMessages().get("new_plugin_version_available")
                            .replace("%current_version%", getDescription().getVersion())
                            .replace("%new_version%", newPossibleVersion)
                            .replace("%repository%", "https://github.com/UnaPepsi/gBunkers/releases")));
                }
            }catch (IOException e) {
                getLogger().warning("gAbility couldn't get the latest version of the plugin");
            }
        });
    }
}
