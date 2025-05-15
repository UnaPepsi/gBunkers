package ga.guimx.gbunkers.utils.guis;

import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.utils.Chat;
import ga.guimx.gbunkers.utils.PlayerInfo;
import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class EquipmentShop extends Gui {
    public EquipmentShop(Player player){
        super(player,"equipment-shop","Equipment Shop",6);
    }
    @Override
    public void onOpen(InventoryOpenEvent event){
        //this sucks
        Icon grayGlass = new Icon(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)7));
        grayGlass.setName(Chat.transNoPrefix("&7 "));
        fillRow(grayGlass,0);
        addItem(grayGlass);
        for (Material helmet : new Material[]{Material.DIAMOND_HELMET, Material.GOLD_HELMET, Material.LEATHER_HELMET}){
            addItem(new Icon(helmet).onClick(e -> {
                if (PlayerInfo.getPlayersBalance().get(player) >= 200) {
                    player.getEquipment().setHelmet(new ItemStack(helmet));
                    PlayerInfo.getPlayersBalance().put(player,
                            PlayerInfo.getPlayersBalance().get(player)-200);
                    player.playSound(player.getLocation(),Sound.LEVEL_UP,1,1);
                }else{
                    player.playSound(player.getLocation(),Sound.VILLAGER_NO,1,1);
                }
            }));
        }
        addItem(grayGlass);
        for (ItemStack pot : new ItemStack[]{new ItemStack()})


        addItem(grayGlass);
        fillRow(grayGlass,2);
        for (Material material : PluginConfig.getMoneyFromOres().keySet()) {
            int moneyValue = PluginConfig.getMoneyFromOres().get(material);
            Icon icon = new Icon(new ItemStack(material));
            icon.setName(Chat.transNoPrefix("&a"+ StringUtils.capitalize(material.name().replace('_',' ').toLowerCase())+" $"+moneyValue));
            icon.onClick(clickEvent -> {
                int amount = 0;
                for (ItemStack itemStack : player.getInventory()) {
                    if (itemStack != null && itemStack.getType() == material){
                        amount += itemStack.getAmount();
                        player.getInventory().removeItem(itemStack); //https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/inventory/Inventory.html#removeItem(org.bukkit.inventory.ItemStack...):~:text=(ItemStack%C2%A0item)-,Removes%20all%20stacks%20in%20the%20inventory%20matching%20the%20given%20stack,-.
                    }
                }
                PlayerInfo.getPlayersBalance().put(
                        player,
                        PlayerInfo.getPlayersBalance().get(player)+amount*moneyValue
                );
                if (amount > 0){
                    player.playSound(player.getLocation(), Sound.LEVEL_UP,1,1);
                }
            });
            addItem(icon);
        }
        addItem(grayGlass);
        addItem(grayGlass);
    }
}
