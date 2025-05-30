package ga.guimx.gbunkers.listeners;

import com.google.common.collect.Lists;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.event.ApolloListener;
import com.lunarclient.apollo.event.Listen;
import com.lunarclient.apollo.event.player.ApolloRegisterPlayerEvent;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import com.lunarclient.apollo.recipients.Recipients;
import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.game.ArenaInfo;
import ga.guimx.gbunkers.game.Game;
import ga.guimx.gbunkers.utils.*;
import ga.guimx.gbunkers.utils.guis.BlockShop;
import ga.guimx.gbunkers.utils.guis.Enchanting;
import ga.guimx.gbunkers.utils.guis.EquipmentShop;
import ga.guimx.gbunkers.utils.guis.SellShop;
import lombok.var;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PlayerListener implements Listener, ApolloListener {
    @EventHandler
    void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Task.runLater(c -> {
            if (!Apollo.getPlayerManager().hasSupport(player.getUniqueId())){
                player.sendMessage(Chat.transPrefix(PluginConfig.getMessages().get("joined_without_lunar")));
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
        event.getPlayer().sendMessage(Chat.toComponentPrefix(PluginConfig.getMessages().get("joined_with_lunar")));
    }

    @EventHandler
    void onDamage(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())){
            event.setCancelled(true);
        }
        PlayerInfo.getPlayersFHomming().remove(player.getUniqueId());
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
    void onHeldItemChange(PlayerItemHeldEvent event){
        if (!(PlayerInfo.getPlayersInGame().contains(event.getPlayer().getUniqueId()) && Classes.isBard(event.getPlayer()))) return;
        Task.runLater(aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa -> {
            Helpers.bardEffect(event.getPlayer(),true);
        },1);
    }
    @EventHandler
    void onBlockInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())) return;
        Action action = event.getAction();
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && (player.getItemInHand() != null || player.getItemInHand().getType() != Material.AIR)){
            Classes.checkAndApply(player);
            if (Classes.isArcher(player) && player.getItemInHand() != null){
                Helpers.archerEffect(player);
            }else if (Classes.isBard(player) && player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR){
                Helpers.bardEffect(player,false);
            }
        }
        //stop stomping on crops
        if (action == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL){
            event.setCancelled(true);
        }

        if (event.getClickedBlock() == null) return;
        ItemStack itemInHand = player.getItemInHand();

        if (event.getClickedBlock().getType() == Material.ANVIL && itemInHand != null && itemInHand.getType() != Material.AIR){
            event.setCancelled(true);
            Helpers.anvil(player);
            return;
        }else if (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE){
            event.setCancelled(true);
            new Enchanting(player).open();
            return;
        }
        Helpers.blockPlaced(event);
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
            case CROPS:
                event.getPlayer().getInventory().addItem(new ItemStack(Material.COOKED_BEEF,5));
                break;
            default:
                return;
        }
        Material originalBlockType = event.getBlock().getType();
        if (originalBlockType == Material.CROPS){
            event.getBlock().setType(Material.AIR);
        }else{
            event.getBlock().setType(Material.COBBLESTONE);
        }
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
    @EventHandler
    void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())){
            return;
        }
        Location deathLoc = player.getLocation().clone();
        ArenaInfo.getArenasInUse().forEach((arena,map) ->{
            map.values().stream().filter(team -> team.getMembers().contains(player)).forEach(team -> {
                Task.runLater(task -> {
                    player.spigot().respawn();
                    player.setGameMode(GameMode.SPECTATOR);
                    player.teleport(deathLoc);
                    team.setDtr(team.getDtr() - 1);
                    if (team.getDtr() > 0) {
                        Task.runLater(run -> {
                            player.setGameMode(GameMode.SURVIVAL);
                            player.teleport(arena.getTeams().get(team.getColor().name().toLowerCase()).getHome());
                            player.getInventory().setContents(new ItemStack[]{new ItemStack(Material.STONE_PICKAXE), new ItemStack(Material.STONE_AXE)});
                        }, 20 * 10);
                    }
                    if (team.getDtr() == 0) { //to avoid spamming the message when people are killed with < 0 dtr
                        player.getWorld().getPlayers().forEach(p -> {
                            p.sendMessage(Chat.transPrefix("&cTeam %color%%team% &cis raidable!"
                                    .replace("%color%", team.getColor().toString())
                                    .replace("%team%", team.getColor().name())));
                        });
                    }
                },1);
            });
        });
    }
    @EventHandler
    void onMovement(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())) return;
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (
                event.getFrom().getX() != event.getTo().getX() ||
                event.getFrom().getY() != event.getTo().getY() ||
                event.getFrom().getZ() != event.getTo().getZ()
        ){
            PlayerInfo.getPlayersFHomming().remove(player.getUniqueId());
        }
        ArenaInfo.getArenasInUse().forEach((arena,map) -> {
            if (LocationCheck.isInside3D(player.getLocation(), arena.getKoth().getLowestCapzoneCorner(), arena.getKoth().getHighestCapzoneCorner())){
                if (!PlayerInfo.getPlayersCappingKoth().containsKey(arena)){
                    PlayerInfo.getPlayersCappingKoth().put(arena,player.getUniqueId());
                    player.sendMessage(Chat.transPrefix("&aYou're now capping."));
                    long startedAt = System.currentTimeMillis();
                    AtomicBoolean cd = new AtomicBoolean(false);
                    Task.runTimer(task -> {
                        if (!PlayerInfo.getPlayersCappingKoth().containsValue(player.getUniqueId())){
                            Chat.bukkitSend("knocked");
                            player.getWorld().getPlayers().forEach(p -> p.sendMessage(Chat.transPrefix("&e%player% &ehas been knocked".replace("%player%",player.getDisplayName()))));
                            task.cancel();
                            return;
                        }
                        if (Time.timePassedSecs(startedAt,System.currentTimeMillis()) % 15 == 0){
                            if (!cd.get()) {
                                Chat.bukkitSend(startedAt + "|" + System.currentTimeMillis() + "|" + (System.currentTimeMillis() - startedAt + "|" + Time.timePassedSecs(startedAt, System.currentTimeMillis())));
                                player.getWorld().getPlayers().forEach(p -> p.sendMessage(Chat.transPrefix("&eSomeone is capping (%timeCapped%)"
                                        .replace("%timeCapped%", Time.formatSecs(Time.timePassedSecs(startedAt, System.currentTimeMillis()))))));
                                cd.set(true);
                            }
                        }else{
                            cd.set(false);
                        }
                        if (Time.timePassedSecs(startedAt,System.currentTimeMillis()) >= 60 * 6){
                            Game.endGame(arena,player);
                        }
                    },0,1);
                }
            }else if (PlayerInfo.getPlayersCappingKoth().containsValue(player.getUniqueId())) {
                PlayerInfo.getPlayersCappingKoth().remove(arena);
                player.getNearbyEntities(20,10,20).stream().filter(e -> e instanceof Player && LocationCheck.isInside3D(e.getLocation(), arena.getKoth().getLowestCapzoneCorner(), arena.getKoth().getHighestCapzoneCorner())).findFirst().ifPresent(playerNowCapping -> {
                    PlayerInfo.getPlayersCappingKoth().put(arena,playerNowCapping.getUniqueId());
                    playerNowCapping.sendMessage(Chat.transPrefix("&aYou're now capping."));
                    playerNowCapping.getWorld().getPlayers().forEach(p -> Chat.transPrefix("&e%player% &eis now capping".replace("%player%",((Player)playerNowCapping).getDisplayName())));
                });
            }else{
                var teams = arena.getTeams();
                for (ChatColor color : map.keySet()) {
                    Arena.Team team = teams.get(color.name().toLowerCase());
                    if (LocationCheck.isInside2D(player.getLocation(),team.getClaimBorder1(),team.getClaimBorder2())){
                        if (!PlayerInfo.getPlayerLocation().containsKey(player) || !PlayerInfo.getPlayerLocation().get(player).equals(team.getColor())){
                            PlayerInfo.getPlayerLocation().put(player,team.getColor());
                            player.sendMessage(Chat.trans("&eYou're entering %color%%team%&e's territory"
                                    .replace("%color%", color.toString())
                                    .replace("%team%",color.name())));
                            break; //uhh if im correct this is to prevent buggy behavior from "PlayerInfo.getPlayerLocation().remove(player);" below
                        }
                    }else if (PlayerInfo.getPlayerLocation().containsKey(player) && PlayerInfo.getPlayerLocation().get(player) == team.getColor()){
                        player.sendMessage(Chat.trans("&eYou're leaving %color%%team%&e's territory"
                                .replace("%color%", color.toString())
                                .replace("%team%",color.name())));
                        PlayerInfo.getPlayerLocation().remove(player);
                    }
                }
            }
        });
    }
    @EventHandler
    void onArmorWear(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player)){
            GBunkers.getInstance().getLogger().warning("???????????"+event.getWhoClicked());
            return;
        }
        var player = (Player) event.getWhoClicked();
        Task.runLater(jqwyeiquydiasydui12t8ut78astdyuagsydjgqwyuetgqwytgxcuashdkjhqwyidhqwuiahdquwhdqw -> {
            Classes.checkAndApply(player);
        },1);
    }
    @EventHandler(priority = EventPriority.HIGH)
    void onShoot(EntityDamageByEntityEvent event){
        if (!(event.getEntity() instanceof Player)) return;
        Player victim = (Player) event.getEntity();
        if (PlayerInfo.getPlayersArcherTagged().containsKey(victim)){
            Chat.bukkitSend("dmg"+event.getDamage());
            event.setDamage(event.getDamage()*1.25);
            Chat.bukkitSend("dmg2"+event.getDamage());
        }
        if (!(event.getDamager() instanceof Arrow)) return;
        Arrow arrow = (Arrow) event.getDamager();
        if (!(arrow.getShooter() instanceof Player)) return;
        Player attacker = (Player) arrow.getShooter();
        if (!(PlayerInfo.getPlayersInGame().contains(victim.getUniqueId()) && PlayerInfo.getPlayersInGame().contains(attacker.getUniqueId()))) return;
        if (Classes.isArcher(attacker)) {
            victim.sendMessage(Chat.trans("&cYou've been Archer Tagged. Receiving 25% more damage for 10 seconds"));
            event.setDamage(0);
            victim.setHealth(victim.getHealth() - 4);
            long ms = System.currentTimeMillis();
            PlayerInfo.getPlayersArcherTagged().put(victim,ms);
            AtomicInteger timer = new AtomicInteger(11);
            ArenaInfo.getArenasInUse().forEach((arena, map) -> {
                map.values().stream().filter(team -> team.getMembers().contains(victim)).findAny().ifPresent(team -> {
                            List<UUID> memberUUIDs = team.getMembers().stream().map(Player::getUniqueId).collect(Collectors.toList());
                            Recipients recipients = Recipients.of(
                                    Apollo.getPlayerManager().getPlayers().stream()
                                            .filter(apolloPlayer -> memberUUIDs.contains(apolloPlayer.getUniqueId()))
                                            .collect(Collectors.toList())
                            );
                            Task.runTimer(task -> {
                                //if player got tagged again, let everything get handled by the other timer
                                if (PlayerInfo.getPlayersArcherTagged().getOrDefault(victim,-1L) != ms) {
                                    task.cancel();
                                    return;
                                }
                                timer.decrementAndGet();
                                if (timer.get() <= 0){
                                    task.cancel();
                                    PlayerInfo.getPlayersArcherTagged().remove(victim);
                                    Apollo.getModuleManager().getModule(NametagModule.class).resetNametag(Recipients.ofEveryone(),victim.getUniqueId());
                                    Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(recipients, victim.getUniqueId(), Nametag.builder()
                                            .lines(Lists.newArrayList(
                                                    Component.text()
                                                            .content(victim.getDisplayName())
                                                            .color(NamedTextColor.NAMES.value(team.getColor().name().toLowerCase()))
                                                            .build(),
                                                    Chat.toComponent("&a[TEAM]")))
                                            .build());
                                    return;
                                }
                                Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(Recipients.ofEveryone(), victim.getUniqueId(), Nametag.builder()
                                        .lines(Lists.newArrayList(
                                                Component.text()
                                                        .content(victim.getDisplayName())
                                                        .color(NamedTextColor.NAMES.value(team.getColor().name().toLowerCase()))
                                                        .build(),
                                                Chat.toComponent("&eArcher tag: " + timer.get() + "s")
                                        ))
                                        .build());
                                Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(recipients, victim.getUniqueId(), Nametag.builder()
                                        .lines(Lists.newArrayList(
                                                Component.text()
                                                        .content(victim.getDisplayName())
                                                        .color(NamedTextColor.NAMES.value(team.getColor().name().toLowerCase()))
                                                        .build(),
                                                Chat.toComponent("&a[TEAM]"),
                                                Chat.toComponent("&eArcher Tag: " + timer.get() + "s")
                                        ))
                                        .build());
                            }, 1, 20);
                        }
                );
            });
        }
    }
}
