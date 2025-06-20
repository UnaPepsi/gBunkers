package ga.guimx.gbunkers.utils;

import com.google.common.collect.Lists;
import ga.guimx.gbunkers.config.PluginConfig;
import lombok.Getter;
import lombok.var;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.bukkit.Material.*;

public class Classes {
    private static final List<UUID> bardTimersIdfk = new ArrayList<>();
    @Getter
    private static final List<Material> armorSets = Lists.newArrayList(
            DIAMOND_HELMET,DIAMOND_CHESTPLATE,DIAMOND_LEGGINGS,DIAMOND_BOOTS,
            GOLD_HELMET,GOLD_CHESTPLATE,GOLD_LEGGINGS,GOLD_BOOTS,
            LEATHER_HELMET,LEATHER_CHESTPLATE,LEATHER_LEGGINGS,LEATHER_BOOTS
    );
    @Getter
    private static final List<Material> bardItems = Lists.newArrayList(
            BLAZE_POWDER,IRON_INGOT,GHAST_TEAR,FEATHER,SUGAR,MAGMA_CREAM
    );
    @Getter
    private static final List<Material> archerItems = Lists.newArrayList(
            FEATHER,SUGAR
    );
    public static void checkAndApply(Player player){
        if (isBard(player)) {
            applyBard(player);
        }else if (isArcher(player)) {
            PlayerInfo.getBardEnergy().remove(player.getUniqueId());
            applyArcher(player);
        }else{
            PluginConfig.getBardEffects().keySet().forEach(player::removePotionEffect);
            PluginConfig.getArcherEffects().keySet().forEach(player::removePotionEffect);
            PlayerInfo.getBardEnergy().remove(player.getUniqueId());
        }
    }
    public static boolean isBard(OfflinePlayer player){
        if (!player.isOnline()) return false;
        var equipment = player.getPlayer().getEquipment();
        return Arrays.stream(equipment.getArmorContents()).allMatch(i -> i != null && i.getType().name().startsWith("GOLD_"));

    }
    public static boolean isArcher(Player player){
        if (!player.isOnline()) return false;
        var equipment = player.getPlayer().getEquipment();
        return Arrays.stream(equipment.getArmorContents()).allMatch(i -> i != null && i.getType().name().startsWith("LEATHER_"));

    }
    public static void applyArcher(Player player){
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())) return;
        PluginConfig.getBardEffects().keySet().forEach(player::removePotionEffect);
        player.addPotionEffects(PluginConfig.getArcherEffects().keySet().stream().map(potionE -> new PotionEffect(potionE,Integer.MAX_VALUE,PluginConfig.getArcherEffects().get(potionE))).collect(Collectors.toList()));
        PlayerInfo.getArcherJumpCD().put(player.getUniqueId(),PlayerInfo.getArcherJumpCD().getOrDefault(player.getUniqueId(),System.currentTimeMillis()*Time.SECONDS+35)); //35 the cooldown for
        PlayerInfo.getArcherSpeedCD().put(player.getUniqueId(),PlayerInfo.getArcherSpeedCD().getOrDefault(player.getUniqueId(),System.currentTimeMillis()*Time.SECONDS+35)); //35 the cooldown for
    }
    public static void applyBard(Player player){
        if (!PlayerInfo.getPlayersInGame().contains(player.getUniqueId())) return;
        PluginConfig.getArcherEffects().keySet().forEach(player::removePotionEffect);
        player.addPotionEffects(PluginConfig.getBardEffects().keySet().stream().map(potionE -> new PotionEffect(potionE,Integer.MAX_VALUE,PluginConfig.getBardEffects().get(potionE))).collect(Collectors.toList()));
        PlayerInfo.getBardCD().put(player.getUniqueId(),PlayerInfo.getBardCD().getOrDefault(player.getUniqueId(),System.currentTimeMillis()));
        //to avoid multiple timers
        var nametag = Nametags.getPlayersLunarNametag().get(player.getUniqueId());
        if (!bardTimersIdfk.contains(player.getUniqueId())) {
            bardTimersIdfk.add(player.getUniqueId());
            PlayerInfo.getBardEnergy().put(player.getUniqueId(), (short) (PlayerInfo.getBardEnergy().getOrDefault(player.getUniqueId(), (short) 0) + 1));
            Task.runTimer(task -> {
                if (!PlayerInfo.getBardEnergy().containsKey(player.getUniqueId())) {
                    bardTimersIdfk.remove(player.getUniqueId());
                    nametag.stream().filter(comp -> Chat.toPlainString(comp).startsWith("Bard Energy")).findFirst().ifPresent(nametag::remove);
                    Nametags.apply(player);
                    task.cancel();
                    return;
                }
                if (PlayerInfo.getBardEnergy().get(player.getUniqueId()) >= 120){
                    return;
                }
                var opBardComponent = nametag.stream().filter(comp -> Chat.toPlainString(comp).startsWith("Bard Energy")).findFirst();
                if (opBardComponent.isPresent()){
                    int index = nametag.indexOf(opBardComponent.get());
                    nametag.set(index,Chat.toComponent("&aBard Energy: "+(PlayerInfo.getBardEnergy().get(player.getUniqueId())+1)));
                }else{
                    nametag.add(Chat.toComponent("&aBard Energy: "+(PlayerInfo.getBardEnergy().get(player.getUniqueId())+1)));
                }
                Nametags.apply(player);
                PlayerInfo.getBardEnergy().put(player.getUniqueId(), (short) (PlayerInfo.getBardEnergy().getOrDefault(player.getUniqueId(), (short) 0) + 1));
            }, 0, 20);
        }
    }
}
