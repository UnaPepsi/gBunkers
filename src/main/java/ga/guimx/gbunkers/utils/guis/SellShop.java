package ga.guimx.gbunkers.utils.guis;

import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.utils.PlayerInfo;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class SellShop extends ConfigurableGui {
    public SellShop(Player player){
        super(player,"sell-shop");
    }
    @Override
    public void onOpen(InventoryOpenEvent event){
        putDysfunctionalIcons();
        addConfigIcon("coal").onClick(this::handleSellEvent);
        addConfigIcon("iron").onClick(this::handleSellEvent);
        addConfigIcon("gold").onClick(this::handleSellEvent);
        addConfigIcon("diamond").onClick(this::handleSellEvent);
        addConfigIcon("emerald").onClick(this::handleSellEvent);
    }
    void handleSellEvent(InventoryClickEvent e){
        Material material = e.getCurrentItem().getType();
        int moneyValue = PluginConfig.getMoneyFromOres().get(material);
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
    }
}
