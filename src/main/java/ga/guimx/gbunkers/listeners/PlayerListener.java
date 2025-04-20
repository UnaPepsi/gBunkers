package ga.guimx.gbunkers.listeners;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.event.ApolloListener;
import com.lunarclient.apollo.event.Listen;
import com.lunarclient.apollo.event.player.ApolloRegisterPlayerEvent;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.utils.Chat;
import ga.guimx.gbunkers.utils.PlayerInfo;
import ga.guimx.gbunkers.utils.Task;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerListener implements Listener, ApolloListener {
    @EventHandler
    void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Task.runLater(c -> {
            if (!Apollo.getPlayerManager().hasSupport(player.getUniqueId())){
                player.sendMessage(Chat.trans(PluginConfig.getMessages().get("joined_without_lunar")));
            }
        },20);
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())){
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(PluginConfig.getLobbyLocation());
            //player.getInventory().setContents(PluginConfig.getLobbyInventory().values().toArray(new ItemStack[0]));
            player.getInventory().setItem(0,PluginConfig.getLobbyInventory().get("not_queued"));
        }
    }

    @Listen
    void onJoin(ApolloRegisterPlayerEvent event){
        event.getPlayer().sendMessage(Chat.toComponent(PluginConfig.getMessages().get("joined_with_lunar")));
    }

    @EventHandler
    void onDamage(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    void onDrop(PlayerDropItemEvent event){
        if (!PlayerInfo.getPlayersInGame().contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    void onPickItem(PlayerPickupItemEvent event){
        if (!PlayerInfo.getPlayersInGame().contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }
}
