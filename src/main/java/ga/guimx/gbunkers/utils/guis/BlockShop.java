package ga.guimx.gbunkers.utils.guis;

import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.utils.PlayerInfo;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockShop extends ConfigurableGui {
    public BlockShop(Player player){
        super(player,"block-shop");
    }
    @Override
    public void onOpen(InventoryOpenEvent event){
        putDysfunctionalIcons();
        addConfigIcon("fence-gate").onClick(this::handleItem);
        addConfigIcon("spruce-fence-gate").onClick(this::handleItem);
        addConfigIcon("dark-oak-fence-gate").onClick(this::handleItem);
        addConfigIcon("cobblestone").onClick(this::handleItem);
        addConfigIcon("wood").onClick(this::handleItem);
        addConfigIcon("chest").onClick(this::handleItem);
        addConfigIcon("trapped-chest").onClick(this::handleItem);
    }
    void handleItem(InventoryClickEvent e){
        ItemStack item = e.getCurrentItem().clone();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(null);
        item.setItemMeta(meta);
        int moneyValue = PluginConfig.getShopPrices().get(e.getCurrentItem().getType().name().toLowerCase().replace('_','-'));
        int amount = e.isRightClick() ? 64 : 1;
        if (PlayerInfo.getPlayersBalance().get(player) < moneyValue * amount){
            player.playSound(player.getLocation(), Sound.VILLAGER_NO,1,1);
            return;
        }
        for (int i = 0; i < amount; i++){
            player.getInventory().addItem(item);
        }
        PlayerInfo.getPlayersBalance().put(player,PlayerInfo.getPlayersBalance().get(player)-moneyValue*amount);
        player.playSound(player.getLocation(), Sound.LEVEL_UP,1,1);

    }
}
