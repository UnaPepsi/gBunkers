package ga.guimx.gbunkers.utils;

import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.game.ArenaInfo;
import ga.guimx.gbunkers.game.Team;
import lombok.var;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Helpers {
    public static void anvil(Player player){
        if (PlayerInfo.getPlayersBalance().get(player) < player.getItemInHand().getDurability()*2){
            player.playSound(player.getLocation(), Sound.VILLAGER_NO,1,1);
        }else{
            PlayerInfo.getPlayersBalance().put(player,PlayerInfo.getPlayersBalance().get(player)-player.getItemInHand().getDurability()*2);
            player.getItemInHand().setDurability((short)0);
            player.playSound(player.getLocation(), Sound.LEVEL_UP,1,1);
        }
    }
    public static void blockPlaced(PlayerInteractEvent event){
        event.setUseInteractedBlock(Event.Result.DENY);
        ArenaInfo.getArenasInUse().forEach((arena, map) -> {
            map.values().forEach(team -> {
                Arena.Team arenaTeam = arena.getTeams().get(team.getColor().name().toLowerCase());
                if (LocationCheck.isInside2D(event.getClickedBlock().getLocation(), arenaTeam.getClaimBorder1(),arenaTeam.getClaimBorder2()) &&
                        (team.getMembers().contains(event.getPlayer()) || team.getDtr() <= 0)){
                    event.setUseInteractedBlock(Event.Result.ALLOW);
                }
            });
        });
    }
    public static void archerEffect(Player player){
        long cd;
        switch (player.getItemInHand().getType()){
            case SUGAR:
                cd = Time.timePassedSecs(PlayerInfo.getArcherSpeedCD().get(player),System.currentTimeMillis());
                if (cd < 35){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20*7,3));
                }else{
                    player.sendMessage(Chat.trans(String.format("&cSpeed CD for: %d seconds",35-cd)));
                }
                break;
            case FEATHER:
                cd = Time.timePassedSecs(PlayerInfo.getArcherJumpCD().get(player),System.currentTimeMillis());
                if (cd < 35){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,20*7,6));
                }else{
                    player.sendMessage(Chat.trans(String.format("&cJump CD for: %d seconds",35-cd)));
                }
                break;
        }
    }
    public static void bardEffect(Player player, boolean isPasive){
        if (!isPasive){
            long cd = Time.timePassedSecs(PlayerInfo.getBardCD().get(player),System.currentTimeMillis());
            if (cd < 10) {
                player.sendMessage(Chat.trans(String.format("&cBard CD for: %d seconds", 10-cd)));
                return;
            }
            if (PlayerInfo.getBardEnergy().get(player) < 35){
                player.sendMessage(Chat.trans(String.format("&cNot enough energy. %d < %d", PlayerInfo.getBardEnergy().get(player),35)));
                return;
            }
            PlayerInfo.getBardEnergy().put(player, (short)(PlayerInfo.getBardEnergy().get(player)-35));
            PlayerInfo.getBardCD().put(player,System.currentTimeMillis());
        }
        AtomicReference<List<Player>> teamatesAtomic = new AtomicReference<>(new ArrayList<>());
        ArenaInfo.getArenasInUse().forEach((arena,map) -> {
            map.values().stream().map(Team::getMembers).filter(members -> members.contains(player)).findFirst().ifPresent(teamatesAtomic::set);
        });
        if (teamatesAtomic.get().isEmpty()){
            GBunkers.getInstance().getLogger().warning("tF?? no teamateS?"+teamatesAtomic.get()+"|"+player.getName());
        }
        List<Player> teamates = teamatesAtomic.get();
        switch (player.getItemInHand().getType()){
            case SUGAR:
                teamates.forEach(p -> {
                    if (p.getActivePotionEffects().stream().noneMatch(potE -> potE.getType().equals(PotionEffectType.SPEED) && potE.getAmplifier() >= (isPasive ? 1 : 2))){
                        var effectToRecover = p.getActivePotionEffects().stream().filter(potE -> potE.getType().equals(PotionEffectType.SPEED)).findFirst();
                        effectToRecover.ifPresent(potionEffect -> Task.runLater(xdxdxdxdxdxdxdxdxd -> p.addPotionEffect(potionEffect), 20 * 11));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20*10,(isPasive ? 1 : 2)),true);
                    }
                });
                if (!isPasive){
                    player.sendMessage(Chat.trans(String.format("&aGave speed to &4%d &ateamates",teamates.size())));
                }
                break;
            case FEATHER: teamates.forEach(p -> {
                    if (p.getActivePotionEffects().stream().noneMatch(potE -> potE.getType().equals(PotionEffectType.JUMP) && potE.getAmplifier() >= (isPasive ? 1 : 7))){
                        var effectToRecover = p.getActivePotionEffects().stream().filter(potE -> potE.getType().equals(PotionEffectType.JUMP)).findFirst();
                        effectToRecover.ifPresent(potionEffect -> Task.runLater(xdxdxdxdxdxdxdxdxd -> p.addPotionEffect(potionEffect), 20 * 11));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,20*10,(isPasive ? 1 : 7)),true);
                    }
                });
                if (!isPasive){
                    player.sendMessage(Chat.trans(String.format("&aGave jump to &4%d &ateamates",teamates.size())));
                }
                break;
            case BLAZE_POWDER: teamates.forEach(p -> {
                    if (p.getActivePotionEffects().stream().noneMatch(potE -> potE.getType().equals(PotionEffectType.INCREASE_DAMAGE) && potE.getAmplifier() >= (isPasive ? 0 : 1))){
                        var effectToRecover = p.getActivePotionEffects().stream().filter(potE -> potE.getType().equals(PotionEffectType.INCREASE_DAMAGE)).findFirst();
                        effectToRecover.ifPresent(potionEffect -> Task.runLater(xdxdxdxdxdxdxdxdxd -> p.addPotionEffect(potionEffect), 20 * 6));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,20*5,(isPasive ? 0 : 1)),true);
                    }
                });
                if (!isPasive){
                    player.sendMessage(Chat.trans(String.format("&aGave strength to &4%d &ateamates",teamates.size())));
                }
                break;
            case GHAST_TEAR: teamates.forEach(p -> {
                    if (p.getActivePotionEffects().stream().noneMatch(potE -> potE.getType().equals(PotionEffectType.REGENERATION) && potE.getAmplifier() >= (isPasive ? 0 : 2))){
                        var effectToRecover = p.getActivePotionEffects().stream().filter(potE -> potE.getType().equals(PotionEffectType.REGENERATION)).findFirst();
                        effectToRecover.ifPresent(potionEffect -> Task.runLater(xdxdxdxdxdxdxdxdxd -> p.addPotionEffect(potionEffect), 20 * 6));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,20*5,(isPasive ? 0 : 2)),true);
                    }
                });
                if (!isPasive){
                    player.sendMessage(Chat.trans(String.format("&aGave regeneration to &4%d &ateamates",teamates.size())));
                }
                break;
            case IRON_INGOT: teamates.forEach(p -> {
                    if (p.getActivePotionEffects().stream().noneMatch(potE -> potE.getType().equals(PotionEffectType.DAMAGE_RESISTANCE) && potE.getAmplifier() >= (isPasive ? 0 : 2))){
                        var effectToRecover = p.getActivePotionEffects().stream().filter(potE -> potE.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)).findFirst();
                        effectToRecover.ifPresent(potionEffect -> Task.runLater(xdxdxdxdxdxdxdxdxd -> p.addPotionEffect(potionEffect), 20 * 6));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,20*5,(isPasive ? 0 : 2)),true);
                    }
                });
                if (!isPasive){
                    player.sendMessage(Chat.trans(String.format("&aGave resistance to &4%d &ateamates",teamates.size())));
                }
                break;
        }
    }
}
