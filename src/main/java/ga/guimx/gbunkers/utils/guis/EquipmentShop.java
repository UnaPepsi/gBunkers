package ga.guimx.gbunkers.utils.guis;

import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.game.ArenaInfo;
import ga.guimx.gbunkers.game.Team;
import ga.guimx.gbunkers.utils.PlayerInfo;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Map;
import java.util.Optional;

public class EquipmentShop extends ConfigurableGui {
    public EquipmentShop(Player player){
        super(player,"equipment-shop");
    }
    @Override
    public void onOpen(InventoryOpenEvent event){
        putDysfunctionalIcons();
        addConfigIcon("d-helmet").onClick(e -> handleArmor(e,"d-helmet"));
        addConfigIcon("d-chestplate").onClick(e -> handleArmor(e,"d-chestplate"));
        addConfigIcon("d-leggings").onClick(e -> handleArmor(e,"d-leggings"));
        addConfigIcon("d-boots").onClick(e -> handleArmor(e,"d-boots"));
        addConfigIcon("b-helmet").onClick(e -> handleArmor(e,"b-helmet"));
        addConfigIcon("b-chestplate").onClick(e -> handleArmor(e,"b-chestplate"));
        addConfigIcon("b-leggings").onClick(e -> handleArmor(e,"b-leggings"));
        addConfigIcon("b-boots").onClick(e -> handleArmor(e,"b-boots"));
        addConfigIcon("a-helmet").onClick(e -> handleArmor(e,"a-helmet"));
        addConfigIcon("a-chestplate").onClick(e -> handleArmor(e,"a-chestplate"));
        addConfigIcon("a-leggings").onClick(e -> handleArmor(e,"a-leggings"));
        addConfigIcon("a-boots").onClick(e -> handleArmor(e,"a-boots"));
        addConfigIcon("pick").onClick(e -> handleItem(e,"pick"));
        addConfigIcon("axe").onClick(e -> handleItem(e,"axe"));
        addConfigIcon("speed",item -> handlePotion(item,PotionType.SPEED ,2,false,false)).onClick(e -> handleItem(e,"speed"));
        addConfigIcon("fres",item -> handlePotion(item,PotionType.FIRE_RESISTANCE,1,true,false)).onClick(e -> handleItem(e,"fres"));
        addConfigIcon("antidote",item -> handlePotion(item,PotionType.POISON, 1,false,false)).onClick(e -> handleItem(e,"antidote"));
        addConfigIcon("heal",item -> handlePotion(item,PotionType.INSTANT_HEAL, 1,false,true)).onClick(e -> {
            if (e.isRightClick()) {
                for (ItemStack itemStack : player.getInventory()) {
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        handleItem(e, "heal");
                    }
                }
            }else{
                handleItem(e, "heal");
            }
        });
        addConfigIcon("slow",item -> handlePotion(item,PotionType.SLOWNESS, 1,false,true)).onClick(e -> handleItem(e,"slow"));
        addConfigIcon("poison",item -> handlePotion(item,PotionType.POISON, 1,false, true)).onClick(e -> handleItem(e,"poison"));
        addConfigIcon("invis",item -> handlePotion(item,PotionType.INVISIBILITY, 1,true, false)).onClick(e -> handleItem(e,"invis"));
        addConfigIcon("pearl").onClick(e -> {
            if (e.isRightClick()){
                for (int i = 0; i < 16; i++){
                    handleItem(e,"pearl");
                }
            }else{
                handleItem(e,"pearl");
            }
        });
        addConfigIcon("bow").onClick(e -> {
            if (handleItem(e,"bow")){
                player.getInventory().addItem(new ItemStack(Material.ARROW,64));
            }
        });
        addConfigIcon("sword").onClick(e -> handleItem(e,"sword"));
        addConfigIcon("archer-effects").onClick(e -> {
            if (handleItem(e,"archer-effects")){
                player.getInventory().addItem(new ItemStack(Material.FEATHER,15));
            }
        });
        //15 cuz iron ingot gives 30, 15*30=450 and buying this costs 500
        addConfigIcon("bard-effects").onClick(e -> {
            if (handleItem(e,"bard-effects")){
                player.getInventory().addItem(new ItemStack(Material.GHAST_TEAR,15));
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT,15));
                player.getInventory().addItem(new ItemStack(Material.MAGMA_CREAM,15));
            }
        });
    }
    void handleArmor(InventoryClickEvent event, String name){
        int moneyValue = PluginConfig.getShopPrices().get(name);
        ItemStack item = event.getCurrentItem().clone();
        if (PlayerInfo.getPlayersBalance().get(player) < moneyValue){
            player.playSound(player.getLocation(), Sound.VILLAGER_NO,1,1);
            return;
        }
        if (name.startsWith("a-")){
            Optional<ChatColor> team = null;
            for (Map<ChatColor, Team> map : ArenaInfo.getArenasInUse().values()) {
                team = map.keySet().stream().filter(color -> map.get(color).getMembers().contains(player)).findFirst();
                if (team.isPresent()){
                    break;
                }
            }
            if (team == null || !team.isPresent()){
                getPlugin().getLogger().warning(player.getName()+" opened the equipment shop without being on a team");
                return;
            }
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            NamedTextColor color = NamedTextColor.NAMES.value(team.get().name().toLowerCase());
            System.out.println(team.get().name().toLowerCase());
            System.out.println(color);
            meta.setColor(Color.fromRGB(color.red(),color.green(),color.blue()));
            item.setItemMeta(meta);
        }
        if (name.endsWith("helmet")){
            player.getEquipment().setHelmet(item);
        }
        else if (name.endsWith("chestplate")){
            player.getEquipment().setChestplate(item);
        }
        else if (name.endsWith("leggings")){
            player.getEquipment().setLeggings(item);
        }
        else if (name.endsWith("boots")){
            player.getEquipment().setBoots(item);
        }
        PlayerInfo.getPlayersBalance().put(player,
                PlayerInfo.getPlayersBalance().get(player)-moneyValue);
        player.playSound(player.getLocation(), Sound.LEVEL_UP,1,1);
    }
    ItemStack handlePotion(ItemStack item, PotionType effect, int level, boolean isExtended, boolean isSplash){
        Potion pot = Potion.fromItemStack(item);
        pot.setType(effect);
        pot.setSplash(isSplash);
        pot.setLevel(level);
        //for some God knows why reason, even if I set isExtended to false, pot.setHasExtendedDuration(isExtended) throws Caused by: java.lang.IllegalArgumentException: Instant potions cannot be extended
        //for healing pots
        //oh... https://github.com/SpigotMC/Spigot-API/blob/b3e065a2b11a6dd2590606600ae58ba08e8763f8/src/main/java/org/bukkit/potion/Potion.java#L262C41-L262C45
        //bruh
        if (isExtended) {
            pot.setHasExtendedDuration(true);
        }
        ItemStack itemStack = pot.toItemStack(1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(item.getItemMeta().getDisplayName());
        meta.addItemFlags(item.getItemMeta().getItemFlags().toArray(new ItemFlag[0]));
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    boolean handleItem(InventoryClickEvent event, String name){
        System.out.println(event.getCurrentItem().getItemMeta().getDisplayName());
        int moneyValue = PluginConfig.getShopPrices().get(name);
        if (PlayerInfo.getPlayersBalance().get(player) < moneyValue){
            player.playSound(player.getLocation(), Sound.VILLAGER_NO,1,1);
            return false;
        }
        PlayerInfo.getPlayersBalance().put(player,
                PlayerInfo.getPlayersBalance().get(player)-moneyValue);
        player.playSound(player.getLocation(), Sound.LEVEL_UP,1,1);
        player.getInventory().addItem(event.getCurrentItem());
        return true;
    }
}
