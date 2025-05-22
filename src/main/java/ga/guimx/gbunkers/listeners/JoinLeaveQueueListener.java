package ga.guimx.gbunkers.listeners;

import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.utils.Chat;
import ga.guimx.gbunkers.utils.PlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveQueueListener implements Listener {
    @EventHandler
    void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK ||
                PlayerInfo.getPlayersInGame().contains(player.getUniqueId())) {
            return;
        }
        if (event.getMaterial() == PluginConfig.getLobbyInventory().get("not_queued").getType() && !PlayerInfo.getPlayersQueued().contains(player.getUniqueId())){
            PlayerInfo.getPlayersQueued().add(player.getUniqueId());
            player.sendMessage(Chat.transPrefix(PluginConfig.getMessages().get("joined_queue")));
            player.getInventory().clear();
            player.getInventory().setItem(0,PluginConfig.getLobbyInventory().get("queued"));
        }else if (event.getMaterial() == PluginConfig.getLobbyInventory().get("not_queued").getType() && PlayerInfo.getPlayersQueued().contains(player.getUniqueId())) {
            PlayerInfo.getPlayersQueued().remove(player.getUniqueId());
            player.sendMessage(Chat.transPrefix(PluginConfig.getMessages().get("left_queue")));
            player.getInventory().clear();
            player.getInventory().setItem(0, PluginConfig.getLobbyInventory().get("not_queued"));
        }
    }

    @EventHandler
    void onDisconnect(PlayerQuitEvent event){
        PlayerInfo.getPlayersQueued().remove(event.getPlayer().getUniqueId());
    }
}
