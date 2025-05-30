package ga.guimx.gbunkers.utils;

import ga.guimx.gbunkers.config.PluginConfig;
import lombok.var;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Classes {
    private static final List<UUID> bardTimersIdfk = new ArrayList<>();
    public static void checkAndApply(Player player){
        if (isBard(player)) {
            applyBard(player);
        }else if (isArcher(player)) {
            PlayerInfo.getBardEnergy().remove(player);
            applyArcher(player);
        }else{
            PluginConfig.getBardEffects().keySet().forEach(player::removePotionEffect);
            PluginConfig.getArcherEffects().keySet().forEach(player::removePotionEffect);
            PlayerInfo.getBardEnergy().remove(player);
        }
    }
    public static boolean isBard(Player player){
        var equipment = player.getEquipment();
        return Arrays.stream(equipment.getArmorContents()).allMatch(i -> i != null && i.getType().name().startsWith("GOLD_"));

    }
    public static boolean isArcher(Player player){
        var equipment = player.getEquipment();
        return Arrays.stream(equipment.getArmorContents()).allMatch(i -> i != null && i.getType().name().startsWith("LEATHER_"));

    }
    public static void applyArcher(Player player){
        PluginConfig.getBardEffects().keySet().forEach(player::removePotionEffect);
        player.addPotionEffects(PluginConfig.getArcherEffects().keySet().stream().map(potionE -> new PotionEffect(potionE,100000,PluginConfig.getArcherEffects().get(potionE))).collect(Collectors.toList()));
        PlayerInfo.getArcherJumpCD().put(player,PlayerInfo.getArcherJumpCD().getOrDefault(player,System.currentTimeMillis()));
        PlayerInfo.getArcherSpeedCD().put(player,PlayerInfo.getArcherSpeedCD().getOrDefault(player,System.currentTimeMillis()));
    }
    public static void applyBard(Player player){
        PluginConfig.getArcherEffects().keySet().forEach(player::removePotionEffect);
        player.addPotionEffects(PluginConfig.getBardEffects().keySet().stream().map(potionE -> new PotionEffect(potionE,100000,PluginConfig.getBardEffects().get(potionE))).collect(Collectors.toList()));
        PlayerInfo.getBardCD().put(player,PlayerInfo.getBardCD().getOrDefault(player,System.currentTimeMillis()));
        if (!bardTimersIdfk.contains(player.getUniqueId())) {
            bardTimersIdfk.add(player.getUniqueId());
            PlayerInfo.getBardEnergy().put(player, (short) (PlayerInfo.getBardEnergy().getOrDefault(player, (short) 0) + 1));
            Task.runTimer(task -> {
                if (!PlayerInfo.getBardEnergy().containsKey(player)) {
                    bardTimersIdfk.remove(player.getUniqueId());
                    task.cancel();
                    return;
                }
                PlayerInfo.getBardEnergy().put(player, (short) (PlayerInfo.getBardEnergy().getOrDefault(player, (short) 0) + 1));
            }, 0, 20);
        }
    }
}
