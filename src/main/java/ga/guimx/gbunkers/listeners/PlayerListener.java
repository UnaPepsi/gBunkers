package ga.guimx.gbunkers.listeners;

import com.google.common.collect.Lists;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.event.ApolloListener;
import com.lunarclient.apollo.event.Listen;
import com.lunarclient.apollo.event.player.ApolloRegisterPlayerEvent;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.game.ArenaInfo;
import ga.guimx.gbunkers.utils.*;
import ga.guimx.gbunkers.utils.guis.BlockShop;
import ga.guimx.gbunkers.utils.guis.Enchanting;
import ga.guimx.gbunkers.utils.guis.EquipmentShop;
import ga.guimx.gbunkers.utils.guis.SellShop;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())) {
            Scoreboard sc = player.getScoreboard();
            sc.getTeams().forEach(t -> t.removePlayer(player));
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
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())) return;

        if (event.getClickedBlock().getType() == Material.ANVIL && itemInHand != null && itemInHand.getType() != Material.AIR){
            event.setCancelled(true);
            if (PlayerInfo.getPlayersBalance().get(player) < itemInHand.getDurability()*2){
                player.playSound(player.getLocation(), Sound.VILLAGER_NO,1,1);
            }else{
                PlayerInfo.getPlayersBalance().put(player,PlayerInfo.getPlayersBalance().get(player)-itemInHand.getDurability()*2);
                itemInHand.setDurability((short)0);
                player.playSound(player.getLocation(), Sound.LEVEL_UP,1,1);
            }
            return;
        }else if (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE){
            event.setCancelled(true);
            new Enchanting(player).open();
            return;
        }

        event.setUseInteractedBlock(Event.Result.DENY);
        ArenaInfo.getArenasInUse().forEach((arena,map) -> {
            map.values().forEach(team -> {
                Arena.Team arenaTeam = arena.getTeams().get(team.getColor().name().toLowerCase());
                if (LocationCheck.isInside2D(event.getClickedBlock().getLocation(), arenaTeam.getClaimBorder1(),arenaTeam.getClaimBorder2()) &&
                    (team.getMembers().contains(player) || team.getDtr() <= 0)){
                    event.setUseInteractedBlock(Event.Result.ALLOW);
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
        event.setCancelled(!(PlayerInfo.getBlocksChanged().contains(event.getBlock().getLocation())));
        switch (event.getBlock().getType()){
            case IRON_ORE:
                event.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_INGOT));
                break;
            case COAL_ORE:
                event.getPlayer().getInventory().addItem(new ItemStack(Material.COAL));
                break;
            case GOLD_ORE:
                event.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_INGOT));
                break;
            case DIAMOND_ORE:
                event.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
                break;
            case EMERALD_ORE:
                event.getPlayer().getInventory().addItem(new ItemStack(Material.EMERALD));
                break;
            default:
                return;
        }
        Material originalBlockType = event.getBlock().getType();
        event.getBlock().setType(Material.COBBLESTONE);
        Task.runLater(task -> {
            event.getBlock().setType(originalBlockType);
        },20*5);
    }
    @EventHandler(priority = EventPriority.HIGH)
    void onBlockPlace(BlockPlaceEvent event){
        if (event.isCancelled() || !PlayerInfo.getPlayersInGame().contains(event.getPlayer().getUniqueId()) || PlayerInfo.getBlocksChanged().contains(event.getBlockPlaced().getLocation())){
            return;
        }
        PlayerInfo.getBlocksChanged().add(event.getBlockPlaced().getLocation());
    }

    @EventHandler
    void onEntityInteract(PlayerInteractEntityEvent event){
        Entity who = event.getRightClicked();
        if (who.getType() != EntityType.VILLAGER){
            return;
        }
        event.setCancelled(true);
        switch (who.getCustomName().substring(2)){
            case "Sell Shop":
                new SellShop(event.getPlayer()).open();
                break;
            case "Equipment Shop":
                new EquipmentShop(event.getPlayer()).open();
                break;
            case "Block Shop":
                new BlockShop(event.getPlayer()).open();
                break;
        }
    }
    @EventHandler
    void onCraft(CraftItemEvent event){
        event.setCancelled(true);
    }
    @EventHandler
    void onWorldChange(PlayerPortalEvent event){event.setCancelled(PlayerInfo.getPlayersInGame().contains(event.getPlayer().getUniqueId()));}
    @EventHandler
    void onInvisDrink(PlayerItemConsumeEvent event){
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        List<Player> playersCantSeePlayer = Lists.newArrayList();
        Task.runLater(r -> {
            if (item.getType() != Material.POTION || !player.hasPotionEffect(PotionEffectType.INVISIBILITY) || !PlayerInfo.getPlayersInGame().contains(event.getPlayer().getUniqueId())){
                //Chat.bukkitSend("asdasds"+player.hasPotionEffect(PotionEffectType.INVISIBILITY));
                return;
            }
            //player **should** only be in 1 team
            player.getScoreboard().getTeams().stream().collect(Collectors.toList()).getFirst().setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
            Task.runTimer(task -> {
                if (!player.isOnline() || !player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                    playersCantSeePlayer.forEach(p -> p.showPlayer(player));
                    player.getScoreboard().getTeams().stream().collect(Collectors.toList()).getFirst().setNameTagVisibility(NameTagVisibility.ALWAYS);
                    task.cancel();
                    return;
                }
                ArenaInfo.getArenasInUse().forEach((arena,map) -> {
                    map.values().forEach(team -> {
                        if (!team.getMembers().contains(player)){
                            team.getMembers().forEach(p -> {
                                if (p.getLocation().distance(player.getLocation()) > 5 && Arrays.stream(player.getEquipment().getArmorContents()).allMatch(i -> i.getType() == Material.AIR)) {
                                    p.hidePlayer(player);
                                    playersCantSeePlayer.add(p);
                                }else{
                                    p.showPlayer(player);
                                    playersCantSeePlayer.remove(p);
                                }
                            });
                        }
                    });
                });
            },0,2);
        },1);
        ItemMeta meta = item.getItemMeta();
        if (meta.getItemFlags().contains(ItemFlag.HIDE_POTION_EFFECTS) && item.getType() == Material.POTION){
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.POISON);
            meta.removeItemFlags(ItemFlag.values());
            item.setItemMeta(meta);
            item.setType(Material.GLASS_BOTTLE);
        }
    }
}
