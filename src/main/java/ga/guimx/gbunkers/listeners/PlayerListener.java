package ga.guimx.gbunkers.listeners;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.event.ApolloListener;
import com.lunarclient.apollo.event.Listen;
import com.lunarclient.apollo.event.player.ApolloRegisterPlayerEvent;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.game.ArenaInfo;
import ga.guimx.gbunkers.utils.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

    @EventHandler
    void onBlockInteract(PlayerInteractEvent event){
        if (event.getClickedBlock() == null) return;
        Chat.bukkitSend("1");
        Player player = event.getPlayer();
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())) return;
        Chat.bukkitSend("2");
        event.setUseInteractedBlock(Event.Result.DENY);
        ArenaInfo.getArenasInUse().forEach((arena,map) -> {
            Chat.bukkitSend("3");
            map.values().forEach(team -> {
                Chat.bukkitSend("4");
                Arena.Team arenaTeam = arena.getTeams().get(team.getColor().name().toLowerCase());
                if (LocationCheck.isInside2D(event.getClickedBlock().getLocation(), arenaTeam.getClaimBorder1(),arenaTeam.getClaimBorder2()) &&
                    (team.getMembers().contains(player) || team.getDtr() <= 0)){
                    event.setUseInteractedBlock(Event.Result.ALLOW);
                    player.sendMessage(team.getColor()+"si");
                }else{
                    player.sendMessage(team.getColor()+"no");
                }
                //if (!LocationCheck.isInside2D(event.getClickedBlock().getLocation(), arenaTeam.getClaimBorder1(),arenaTeam.getClaimBorder2()) &&
                //        (!team.getMembers().contains(player) || team.getDtr() > 0 )){
                //    event.setUseInteractedBlock(Event.Result.DENY);
                //    player.sendMessage("dsasd");
                //    //event.setCancelled(true);
                //}else{
                //    player.sendMessage("else");
                //}
            });
        });
    }
    @EventHandler
    void onBlockBreak(BlockBreakEvent event){
        if (!PlayerInfo.getPlayersInGame().contains(event.getPlayer().getUniqueId())){
            return;
        }
        short moneyToGive;
        switch (event.getBlock().getType()){
            case IRON_ORE:
                moneyToGive = 30;
                break;
            case COAL_ORE:
                moneyToGive = 20;
                break;
            case GOLD_ORE:
                moneyToGive = 100;
                break;
            case DIAMOND_ORE:
            case EMERALD_ORE:
                moneyToGive = 300;
                break;
            default:
                return;
        }
        event.setCancelled(true);
        PlayerInfo.getPlayersBalance().put(event.getPlayer(),
                PlayerInfo.getPlayersBalance().get(event.getPlayer())+moneyToGive);

        Material originalBlockType = event.getBlock().getType();
        event.getBlock().setType(Material.COBBLESTONE);
        Task.runLater(task -> {
            event.getBlock().setType(originalBlockType);
        },20*5);
    }
}
