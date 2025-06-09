package ga.guimx.gbunkers.listeners;

import ga.guimx.gbunkers.config.ArenasConfig;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.game.ArenaInfo;
import ga.guimx.gbunkers.game.Game;
import ga.guimx.gbunkers.utils.Arena;
import ga.guimx.gbunkers.utils.Chat;
import ga.guimx.gbunkers.utils.PlayerInfo;
import ga.guimx.gbunkers.utils.Task;
import ga.guimx.gbunkers.utils.guis.Spectator;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LobbyItemsListener implements Listener {
    Random rand = new Random();
    int timer = 10;
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
            player.getInventory().setItem(4,PluginConfig.getLobbyInventory().get("spectator"));
            if (PlayerInfo.getPlayersQueued().size() < PluginConfig.getPlayersNeededToStartGame() || timer != 10) return;
            event.getPlayer().getWorld().getPlayers().forEach(p -> p.sendMessage(Chat.trans("&aQueue is enough to start a game, starting 10 second timer...")));
            Task.runTimer(task -> {
                if (PlayerInfo.getPlayersQueued().size() < PluginConfig.getPlayersNeededToStartGame()){
                    event.getPlayer().getWorld().getPlayers().forEach(p -> p.sendMessage(Chat.trans("&cNot enough players. Timer cancelled")));
                    event.getPlayer().getWorld().getPlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ZOMBIE_METAL,1,0));
                    timer = 10;
                    task.cancel();
                    return;
                }
                if (timer <= 0){
                    List<Arena> arenasAvailable = ArenasConfig.getArenas().stream().filter(arena -> !ArenaInfo.isArenaOccupied(arena)).collect(Collectors.toList());
                    Game.startGame(arenasAvailable.get(rand.nextInt(0,arenasAvailable.size())-1));
                    event.getPlayer().getWorld().getPlayers().forEach(p -> p.sendMessage(Chat.trans("&aGood luck!")));
                    event.getPlayer().getWorld().getPlayers().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_PLING,1,2));
                    timer = 10;
                    task.cancel();
                    return;
                }
                event.getPlayer().getWorld().getPlayers().forEach(p -> p.sendMessage(Chat.trans("&a"+timer)));
                timer--;
                event.getPlayer().getWorld().getPlayers().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_PLING,1,1));
            },0,20);
        }else if (event.getMaterial() == PluginConfig.getLobbyInventory().get("not_queued").getType() && PlayerInfo.getPlayersQueued().contains(player.getUniqueId())) {
            PlayerInfo.getPlayersQueued().remove(player.getUniqueId());
            player.sendMessage(Chat.transPrefix(PluginConfig.getMessages().get("left_queue")));
            player.getInventory().clear();
            player.getInventory().setItem(0, PluginConfig.getLobbyInventory().get("not_queued"));
            player.getInventory().setItem(4,PluginConfig.getLobbyInventory().get("spectator"));
        }else if (event.getMaterial() == PluginConfig.getLobbyInventory().get("spectator").getType() && !PlayerInfo.getPlayersInGame().contains(player.getUniqueId())){
            new Spectator(player).open();
        }
    }

    @EventHandler
    void onDisconnect(PlayerQuitEvent event){
        PlayerInfo.getPlayersQueued().remove(event.getPlayer().getUniqueId());
    }
}
