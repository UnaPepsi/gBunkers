package ga.guimx.gbunkers.utils.guis;

import com.google.common.collect.Maps;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.utils.Chat;
import ga.guimx.gbunkers.utils.PlayerInfo;
import ga.guimx.gbunkers.utils.Task;
import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.advancedslot.AdvancedSlotManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class Enchanting extends Gui {
    AdvancedSlotManager manager;
    private final Map<Enchantment, Integer> enchantments = new HashMap<Enchantment,Integer>(){{
        put(Enchantment.PROTECTION_ENVIRONMENTAL,1);
        put(Enchantment.DURABILITY,3);
        put(Enchantment.PROTECTION_FALL,4);
        put(Enchantment.DAMAGE_ALL,1);
        put(Enchantment.ARROW_DAMAGE,5);
        put(Enchantment.ARROW_INFINITE,1);
        put(Enchantment.DIG_SPEED,5);
    }};
    private final Map<Enchantment,ItemStack> enchantmentsItem = Maps.newHashMap();
    private final Map<Material, Enchantment[]> validItems = new HashMap<Material, Enchantment[]>(){{
        put(Material.DIAMOND_SWORD,new Enchantment[]{Enchantment.DAMAGE_ALL,Enchantment.DURABILITY});
        put(Material.DIAMOND_HELMET,new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.DURABILITY});
        put(Material.DIAMOND_CHESTPLATE,new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.DURABILITY});
        put(Material.DIAMOND_LEGGINGS,new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.DURABILITY});
        put(Material.DIAMOND_BOOTS,new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.DURABILITY,Enchantment.PROTECTION_FALL});
        put(Material.GOLD_HELMET,new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.DURABILITY});
        put(Material.GOLD_CHESTPLATE,new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.DURABILITY});
        put(Material.GOLD_LEGGINGS,new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.DURABILITY});
        put(Material.GOLD_BOOTS,new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.DURABILITY,Enchantment.PROTECTION_FALL});
        put(Material.LEATHER_HELMET,new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.DURABILITY});
        put(Material.LEATHER_CHESTPLATE,new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.DURABILITY});
        put(Material.LEATHER_LEGGINGS,new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.DURABILITY});
        put(Material.LEATHER_BOOTS,new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.DURABILITY,Enchantment.PROTECTION_FALL});
        put(Material.BOW,new Enchantment[]{Enchantment.ARROW_DAMAGE,Enchantment.DURABILITY,Enchantment.ARROW_INFINITE});
        put(Material.DIAMOND_PICKAXE,new Enchantment[]{Enchantment.DIG_SPEED,Enchantment.DURABILITY});
        put(Material.DIAMOND_AXE,new Enchantment[]{Enchantment.DIG_SPEED,Enchantment.DURABILITY});
    }};
    public Enchanting(Player player){
        super(player,"enchanting-gui","Enchant",3);
        manager = new AdvancedSlotManager(this);
        enchantments.forEach((enc,lvl) -> {
            ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            meta.addStoredEnchant(enc,lvl,true);
            item.setItemMeta(meta);
            enchantmentsItem.put(enc,item);
        });
    }
    @Override
    public void onOpen(InventoryOpenEvent event){
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierMeta = barrier.getItemMeta();
        barrierMeta.setDisplayName(Chat.trans("&7"));
        barrier.setItemMeta(barrierMeta);
        ItemStack grayGlass = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)7);
        ItemMeta grayGlassMeta = grayGlass.getItemMeta();
        grayGlassMeta.setDisplayName(Chat.trans("&7"));
        grayGlass.setItemMeta(grayGlassMeta);
        ItemStack enchantingTable = new ItemStack(Material.ENCHANTMENT_TABLE);
        ItemMeta enchantingTableMeta = enchantingTable.getItemMeta();
        enchantingTableMeta.setDisplayName(Chat.trans("&aEnchant!"));
        enchantingTable.setItemMeta(enchantingTableMeta);
        ItemStack greenGlass = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)5);
        ItemMeta greenGlassMeta = greenGlass.getItemMeta();
        greenGlassMeta.setDisplayName(Chat.trans("&7"));
        greenGlass.setItemMeta(greenGlassMeta);
        fillGui(new Icon(grayGlass));
        addItem(10,enchantingTable);
        manager.addAdvancedIcon(11,new Icon(greenGlass))
                .onPrePutClick((clickEvent, itemStack) -> {
                    if (clickEvent.isShiftClick()){
                        return true; //enchanting breaks if the player shift clicks it for some reason
                    }
                    if (!validItems.containsKey(itemStack.getType())){
                        player.playSound(player.getLocation(), Sound.VILLAGER_NO,1,1);
                        return true;
                    }
                    return false;
                })
                .onPut((clickEvent, itemStack) -> {
                    int moneyValue = PluginConfig.getShopPrices().get("enchant");
                    Task.runLater(VIVALAGRASAVIVANLOSMOMOSLAGRASAESCLAVELAGRASAESDIOS -> {
                        int slotPos = 14;
                        for (Enchantment enchantment : validItems.get(itemStack.getType())) {
                            final int finalSlotPos = slotPos;
                            if (itemStack.containsEnchantment(enchantment)){
                                addItem(slotPos,barrier);
                            }else{
                                addItem(new Icon(enchantmentsItem.get(enchantment)).onClick(e -> {
                                    if (PlayerInfo.getPlayersBalance().get(player.getUniqueId()) < moneyValue){
                                        player.playSound(player.getLocation(),Sound.VILLAGER_NO,1,1);
                                        return;
                                    }
                                    player.playSound(player.getLocation(),Sound.LEVEL_UP,1,1);
                                    PlayerInfo.getPlayersBalance().put(player.getUniqueId(),PlayerInfo.getPlayersBalance().get(player.getUniqueId())-moneyValue);
                                    itemStack.addEnchantment(enchantment,enchantments.get(enchantment));
                                    addItem(finalSlotPos,barrier);
                                }),slotPos);
                            }
                            slotPos++;
                        }
                    },2);
                }).onPickup((clickEvent,itemStack) -> Task.runLater(ajsjdkasdkashkjdjasdkasjkdjqwukdyiashdukywu7qyhdu12th7uk6t678tds6utas6utxc7a65sd67ad6asd3as56d465123r56r56aser56ecxdsrdyatsdaghsfdghasfdghgwjkdhqjle -> {
                    addItem(14, barrier);
                    addItem(15, barrier);
                    addItem(16, barrier);
                },1));
        addItem(14,barrier);
        addItem(15,barrier);
        addItem(16,barrier);
    }
}
