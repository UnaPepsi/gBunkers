package ga.guimx.gbunkers.utils.guis;

import mc.obliviate.inventory.configurable.ConfigurableGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class EquipmentShop extends ConfigurableGui {
    public EquipmentShop(Player player){
        super(player,"equipment-shop");
    }
    @Override
    public void onOpen(InventoryOpenEvent event){
        putDysfunctionalIcons();
    }
}
