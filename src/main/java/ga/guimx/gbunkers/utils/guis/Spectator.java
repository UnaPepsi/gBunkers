package ga.guimx.gbunkers.utils.guis;

import com.google.common.collect.Lists;
import ga.guimx.gbunkers.game.ArenaInfo;
import ga.guimx.gbunkers.utils.Chat;
import ga.guimx.gbunkers.utils.PlayerInfo;
import ga.guimx.gbunkers.utils.Task;
import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Spectator extends Gui {
    private final PaginationManager pagination = new PaginationManager(this);
    ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)5);
    private boolean isOpen = true;
    public Spectator(Player player){
        super(player,"spectator-gui","Games Active",3);
        pagination.registerPageSlotsBetween(10,16);
        Task.runTimer(dontusejava8 -> {
            if (!isOpen){
                dontusejava8.cancel();
                return;
            }
            updatePaginator();
        },0,20);
    }
    @Override
    public void onOpen(InventoryOpenEvent event){
        fillRow(new Icon(glassPane),0);
        fillRow(new Icon(glassPane),2);
        addItem(new Icon(Material.ARROW).onClick(e -> pagination.goNextPage()),9);
        addItem(new Icon(Material.ARROW).onClick(e -> pagination.goPreviousPage()),17);
    }
    @Override
    public void onClose(InventoryCloseEvent event){
        isOpen = false;
    }
    void updatePaginator(){
        pagination.getItems().clear();
        ArenaInfo.getArenasInUse().keySet().forEach(arena -> {
            ItemStack game = new ItemStack(Material.WATCH);
            ItemMeta meta = game.getItemMeta();
            meta.setDisplayName(Chat.trans(arena.getName()));
            meta.setLore(Lists.newArrayList(Chat.trans(arena.getKoth().getName() + "&e: " + PlayerInfo.getArenaKothCapTime().getOrDefault(arena, "06:00")), "&e&lClick to spectate!"));
            game.setItemMeta(meta);
            pagination.addItem(new Icon(game).onClick(e -> {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().teleport(arena.getSpectatorSpawn());
                e.getWhoClicked().setGameMode(GameMode.SPECTATOR);
                PlayerInfo.getPlayersSpectating().put(e.getWhoClicked().getUniqueId(), arena);
                e.getWhoClicked().sendMessage(Chat.trans("&cTo leave, run /lobby"));
            }));
        });
        pagination.update();
    }
}
