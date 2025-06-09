package ga.guimx.gbunkers.listeners;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.event.ApolloListener;
import com.lunarclient.apollo.event.Listen;
import com.lunarclient.apollo.event.player.ApolloRegisterPlayerEvent;
import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.game.ArenaInfo;
import ga.guimx.gbunkers.game.Game;
import ga.guimx.gbunkers.game.Team;
import ga.guimx.gbunkers.utils.*;
import ga.guimx.gbunkers.utils.guis.BlockShop;
import ga.guimx.gbunkers.utils.guis.Enchanting;
import ga.guimx.gbunkers.utils.guis.EquipmentShop;
import ga.guimx.gbunkers.utils.guis.SellShop;
import lombok.var;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

//please split listeners in different files to organize stuff when making a plugin, don't do this sh*t
public class PlayerListener implements Listener, ApolloListener {
    private final HashMap<UUID, Long> playersDisconnectedtimer = Maps.newHashMap();
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
            player.getInventory().setItem(4,PluginConfig.getLobbyInventory().get("spectator"));
        }else{
            ArenaInfo.getArenasInUse().values().forEach(m -> m.values().stream().filter(t -> t.getMembers().contains(player.getUniqueId())).findFirst().ifPresent(t -> {
                if (t.getDtr() <= 0){
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }));
        }
    }

    @Listen
    void onJoin(ApolloRegisterPlayerEvent event){
        event.getPlayer().sendMessage(Chat.toComponentPrefix(PluginConfig.getMessages().get("joined_with_lunar")));
    }

    @EventHandler
    void onQuit(PlayerQuitEvent event){
        //omg
        Player player = event.getPlayer();
        var toRemove = PlayerInfo.getPlayersCappingKoth().entrySet().stream()
                .filter(entry -> entry.getValue().equals(player.getUniqueId()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        toRemove.forEach(arena -> PlayerInfo.getPlayersCappingKoth().remove(arena));
        Task.runLater(__ -> Classes.checkAndApply(player),1); //since player is not online this removes bard and archer stuff
        if (PlayerInfo.getPlayersInGame().contains(player.getUniqueId())){
            for (Map<ChatColor, Team> map : ArenaInfo.getArenasInUse().values()) {
                Team team = map.values().stream().filter(t -> t.getMembers().contains(player.getUniqueId())).findFirst().orElse(null);
                if (team != null){
                    long when = System.currentTimeMillis();
                    playersDisconnectedtimer.put(player.getUniqueId(),when);
                    player.getWorld().getPlayers().forEach(p -> p.sendMessage(Chat.trans(team.getColor()+player.getName()+" &ehas disconnected. They have 5 minutes to reconnect or else they'll be marked as dead")));
                    Task.runLater(task -> {
                        if (playersDisconnectedtimer.get(player.getUniqueId()) != when || player.isOnline()) return;
                        team.setDtr(team.getDtr()-1);
                        player.getWorld().getPlayers().forEach(p -> p.sendMessage(Chat.trans(team.getColor()+player.getName()+" &ahas been killed")));
                        if (team.getDtr() == 0) { //to avoid spamming the message when people are killed with < 0 dtr
                            player.getWorld().getPlayers().forEach(p -> {
                                p.sendMessage(Chat.transPrefix("&cTeam %color%%team% &cis raidable!"
                                        .replace("%color%", team.getColor().toString())
                                        .replace("%team%", team.getColor().name())));
                            });
                        }
                    },20*60*5);
                    return;
                }
            }
        }
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
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && player.getItemInHand() != null){
            if (Classes.getArmorSets().contains(player.getItemInHand().getType())){
                Task.runLater(__ -> Classes.checkAndApply(player),1);
            }
            if (Classes.isArcher(player)){
                Helpers.archerEffect(player);
            }else if (Classes.isBard(player) && Classes.getBardItems().contains(player.getItemInHand().getType())){
                Helpers.bardEffect(player,false);
            }
        }
        //stop stomping on crops
        //player.sendMessage(action.name()+"|"+event.getClickedBlock().getType().name()+"|"+event.getMaterial());
        if (action == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL){
            event.setCancelled(true);
            return;
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
        Helpers.blockInteract(event);
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
        if (!PlayerInfo.getPlayersInGame().contains(event.getPlayer().getUniqueId())){
            return;
        }
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
    void onPotionDrink(PlayerItemConsumeEvent event){
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        List<Player> playersCantSeePlayer = Lists.newArrayList();
        Task.runLater(r -> {
            if (item.getType() != Material.POTION || !player.hasPotionEffect(PotionEffectType.INVISIBILITY) || !PlayerInfo.getPlayersInGame().contains(event.getPlayer().getUniqueId())){
                //Chat.bukkitSend("asdasds"+player.hasPotionEffect(PotionEffectType.INVISIBILITY));
                return;
            }
            //player **should** only be in 1 team
            player.getScoreboard().getTeams().stream().collect(Collectors.toList()).get(0).setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
            Task.runTimer(task -> {
                if (!player.isOnline() || !player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                    playersCantSeePlayer.forEach(p -> p.showPlayer(player));
                    player.getScoreboard().getTeams().stream().collect(Collectors.toList()).get(0).setNameTagVisibility(NameTagVisibility.ALWAYS);
                    task.cancel();
                    return;
                }
                ArenaInfo.getArenasInUse().forEach((arena,map) -> {
                    map.values().forEach(team -> {
                        if (!team.getMembers().contains(player.getUniqueId())){
                            team.getMembers().forEach(u -> {
                                Player p = Bukkit.getPlayer(u);
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
            event.setCancelled(true);
            player.getInventory().remove(item);
            //item.setAmount(0); doesn't work idk why
            player.sendMessage(Chat.trans("&aYou have been cured!"));
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
            map.values().stream().filter(team -> team.getMembers().contains(player.getUniqueId())).forEach(team -> {
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
                            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,20*10,5),true);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,20*10,5),true);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,20*10,5),true);
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
        if (player.getGameMode() == GameMode.SPECTATOR){
            if (PlayerInfo.getPlayersSpectating().containsKey(player.getUniqueId())){
                Location loc1 = PlayerInfo.getPlayersSpectating().get(player.getUniqueId()).getBorder1();
                Location loc2 = PlayerInfo.getPlayersSpectating().get(player.getUniqueId()).getBorder2();
                if (!LocationCheck.isInside3D(event.getTo(),loc1,loc2)){
                    event.setCancelled(true);
                    player.playSound(event.getFrom(), Sound.VILLAGER_NO,1,1);
                    Vector vc = player.getVelocity().multiply(-1);
                    if (event.getTo().getY() >= loc2.getY()){
                        vc.setY(-2);
                    }else if (event.getTo().getY() <= loc1.getY()){
                        vc.setY(2);
                    }
                    player.setVelocity(vc);
                    //if whatever shenanigans happens just teleport the player back to where spectators spawn
                    Task.runLater(a -> {
                        if (!LocationCheck.isInside3D(player.getLocation(),loc1,loc2) && PlayerInfo.getPlayersSpectating().containsKey(player.getUniqueId())){
                            player.teleport(PlayerInfo.getPlayersSpectating().get(player.getUniqueId()).getSpectatorSpawn());
                        }
                    },20*5);
                }
            }
            return;
        }
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())) return;
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
                            PlayerInfo.getArenaKothCapTime().remove(arena);
                            Chat.bukkitSend("knocked");
                            player.getWorld().getPlayers().forEach(p -> p.sendMessage(Chat.transPrefix("&e%player% &ehas been knocked".replace("%player%",player.getDisplayName()))));
                            task.cancel();
                            return;
                        }
                        PlayerInfo.getArenaKothCapTime().put(arena,Time.formatSecs(6*60-Time.timePassedSecs(startedAt, System.currentTimeMillis())));
                        if (Time.timePassedSecs(startedAt,System.currentTimeMillis()) % 45 == 0){
                            if (!cd.get()) {
                                Chat.bukkitSend(startedAt + "|" + System.currentTimeMillis() + "|" + (System.currentTimeMillis() - startedAt + "|" + Time.timePassedSecs(startedAt, System.currentTimeMillis())));
                                player.getWorld().getPlayers().forEach(p -> p.sendMessage(Chat.transPrefix("&eSomeone is capping (%timeCapped%)"
                                        .replace("%timeCapped%", Time.formatSecs(6*60-Time.timePassedSecs(startedAt, System.currentTimeMillis()))))));
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
                        if (!PlayerInfo.getPlayerLocation().containsKey(player.getUniqueId()) || !PlayerInfo.getPlayerLocation().get(player.getUniqueId()).equals(team.getColor())){
                            PlayerInfo.getPlayerLocation().put(player.getUniqueId(),team.getColor());
                            player.sendMessage(Chat.trans("&eYou're entering %color%%team%&e's territory"
                                    .replace("%color%", color.toString())
                                    .replace("%team%",color.name())));
                            break; //uhh if im correct this is to prevent buggy behavior from "PlayerInfo.getPlayerLocation().remove(player);" below
                        }
                    }else if (PlayerInfo.getPlayerLocation().containsKey(player.getUniqueId()) && PlayerInfo.getPlayerLocation().get(player.getUniqueId()) == team.getColor()){
                        player.sendMessage(Chat.trans("&eYou're leaving %color%%team%&e's territory"
                                .replace("%color%", color.toString())
                                .replace("%team%",color.name())));
                        PlayerInfo.getPlayerLocation().remove(player.getUniqueId());
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
        if (!(event.getDamager() instanceof Player)) return;
        for (var map : ArenaInfo.getArenasInUse().values()) {
            if (map.values().stream().anyMatch(t -> t.getMembers().contains(event.getDamager().getUniqueId()) && t.getMembers().contains(victim.getUniqueId()))) {
                event.getDamager().sendMessage(Chat.trans("&cYou can't damage your own teammates"));
                event.setCancelled(true);
                return;
            }
        }
        if (PlayerInfo.getPlayersArcherTagged().containsKey(victim.getUniqueId())){
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
            if (Classes.isArcher(victim)){
                attacker.sendMessage(Chat.trans("&cYou can't archer tag another archer"));
                return;
            }
            victim.sendMessage(Chat.trans("&cYou've been Archer Tagged. Receiving 25% more damage for 10 seconds"));
            event.setDamage(0);
            victim.setHealth(victim.getHealth() <= 4 ? 0 : victim.getHealth() - 4);
            //don't start another timer if one's already running
            if (PlayerInfo.getPlayersArcherTagged().containsKey(victim.getUniqueId())){
                PlayerInfo.getPlayersArcherTagged().put(victim.getUniqueId(),(short)11);
                return;
            }
            PlayerInfo.getPlayersArcherTagged().put(victim.getUniqueId(),(short)11);
            var nametag = Nametags.getPlayersLunarNametag().get(victim.getUniqueId());
            ArenaInfo.getArenasInUse().forEach((arena, map) -> {
                map.values().stream().filter(team -> team.getMembers().contains(victim.getUniqueId())).findAny().ifPresent(team -> {
                            Task.runTimer(task -> {
                                short timer = (short)(PlayerInfo.getPlayersArcherTagged().get(victim.getUniqueId())-1);
                                PlayerInfo.getPlayersArcherTagged().put(
                                        victim.getUniqueId(),timer
                                );
                                if (timer <= 0){
                                    task.cancel();
                                    PlayerInfo.getPlayersArcherTagged().remove(victim.getUniqueId());
                                    nametag.remove(Chat.toComponent("&eArcher tag: " + (timer+1) + "s"));
                                    Nametags.apply(victim);
                                    return;
                                }
                                var opArcherTag = nametag.stream().filter(comp -> Chat.toPlainString(comp).startsWith("Archer tag")).findFirst();
                                if (opArcherTag.isPresent()){
                                    int index = nametag.indexOf(opArcherTag.get());
                                    nametag.set(index,Chat.toComponent("&eArcher tag: " + timer + "s"));
                                }else{
                                    nametag.add(Chat.toComponent("&eArcher tag: " + timer + "s"));
                                }
                                Nametags.apply(victim);
                            }, 1, 20);
                        }
                );
            });
        }
    }
    @EventHandler
    void onDropItem(PlayerDropItemEvent event){
        var itemDropped = event.getItemDrop().getItemStack();
        if (Classes.getArmorSets().contains(itemDropped.getType()) && Arrays.stream(event.getPlayer().getEquipment().getArmorContents()).anyMatch(item -> item == null || item.getType() == Material.AIR)){
            Classes.checkAndApply(event.getPlayer());
        }
    }
    @EventHandler
    void onEnderPearl(ProjectileLaunchEvent event){
        if (!(event.getEntity().getShooter() instanceof Player) || !(event.getEntity() instanceof EnderPearl)) return;
        Player player = (Player) event.getEntity().getShooter();
        long currentTime = System.currentTimeMillis();
        if (!PlayerInfo.getPlayersEnderPearlCD().containsKey(player.getUniqueId()) || Time.timePassedSecs(PlayerInfo.getPlayersEnderPearlCD().get(player.getUniqueId()),currentTime) >= 16){
            PlayerInfo.getPlayersEnderPearlCD().put(player.getUniqueId(),currentTime);
            return;
        }
        event.setCancelled(true);
        player.getItemInHand().setAmount(player.getItemInHand().getAmount()+1);
        player.sendMessage(Chat.trans(String.format("&cYou're on cooldown for %d seconds",16-Time.timePassedSecs(PlayerInfo.getPlayersEnderPearlCD().get(player.getUniqueId()),currentTime))));
    }
    @EventHandler
    void onPotionSplash(PotionSplashEvent event){
        //stop teamates from potting/debuffing themselves
        if (!(event.getPotion().getShooter() instanceof Player)) return;
        Player thrower = (Player) event.getPotion().getShooter();
        if (!PlayerInfo.getPlayersInGame().contains(thrower.getUniqueId())) return;
        AtomicReference<List<UUID>> throwerMembers = new AtomicReference<>(new ArrayList<>());
        ArenaInfo.getArenasInUse().forEach((arena,map) -> {
            map.values().stream().filter(t -> t.getMembers().contains(thrower.getUniqueId())).findFirst().ifPresent(t -> {
                throwerMembers.set(new ArrayList<>(t.getMembers()));
            });
        });
        event.getAffectedEntities().stream().filter(e -> e instanceof Player && !e.getUniqueId().equals(thrower.getUniqueId()) && throwerMembers.get().contains(e.getUniqueId())).forEach(player -> {
            event.setIntensity(player,0);
        });
    }

    @EventHandler
    void onChat(AsyncPlayerChatEvent event){
        //don't show messages to players in diff games or in lobby
        String msg = event.getMessage();
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())){
            //event.setFormat(Chat.trans("&f%s: %s"));
            player.getWorld().getPlayers().forEach(p -> p.sendMessage(Chat.trans(String.format("&f%s: %s",player.getName(),msg))));
            return;
        }
        AtomicReference<ChatColor> cc = new AtomicReference<>(ChatColor.WHITE);
        AtomicReference<List<UUID>> teamMembers = new AtomicReference<>(new ArrayList<>());
        ArenaInfo.getArenasInUse().forEach((arena,map) -> {
            map.values().stream().filter(t -> t.getMembers().contains(player.getUniqueId())).findFirst().ifPresent(t -> {
               cc.set(t.getColor());
               teamMembers.set(t.getMembers());
            });
        });
        if (msg.startsWith("@") || (PlayerInfo.getPlayersInFactionChat().contains(player.getUniqueId()) && !msg.startsWith("!"))){
            teamMembers.get().forEach(u -> Bukkit.getPlayer(u).sendMessage(Chat.trans(String.format("&3(Team) %s: &e%s",player.getName(),ChatColor.stripColor(msg.replaceFirst("@",""))))));
        }else{
            player.getWorld().getPlayers().forEach(p -> p.sendMessage(Chat.trans(String.format("&7[%s&7] %s&f: %s",cc.get()+cc.get().name(),player.getName(),msg.replaceFirst("!","")))));
        }
    }
}
