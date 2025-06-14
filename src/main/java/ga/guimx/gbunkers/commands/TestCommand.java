package ga.guimx.gbunkers.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.game.ArenaInfo;
import ga.guimx.gbunkers.game.Game;
import ga.guimx.gbunkers.utils.*;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Command(name = "test")
@Permission("gbunkers.admin")
public class TestCommand {

    @Execute(name="add")
    void addPlayerToTeam(@Context Player sender, @Arg Player target, @Arg String displayNameColor, @Arg String markerColor) {
        NamedTextColor namedTextColor = NamedTextColor.NAMES.value(displayNameColor.toLowerCase());
        NamedTextColor tempTextColor = NamedTextColor.NAMES.value(markerColor.toLowerCase());
        Color color = new Color(tempTextColor.red(),tempTextColor.green(),tempTextColor.blue());
        TeamManager.Team senderTeam = GBunkers.getTeamManager().getByPlayerUuid(sender.getUniqueId()).orElseGet(() -> {
            TeamManager.Team team = GBunkers.getTeamManager().createTeam();
            team.addMember(new TeamManager.TeamPlayerWaypoint(sender,namedTextColor,color));
            return team;
        });
        GBunkers.getTeamManager().getByPlayerUuid(target.getUniqueId()).ifPresent(team -> {
                GBunkers.getTeamManager().deleteTeam(team.getTeamId());
            }
        );
        senderTeam.addMember(new TeamManager.TeamPlayerWaypoint(target,namedTextColor,color));
        Task.runTimer(c -> {
            if (!GBunkers.getTeamManager().getByTeamId(senderTeam.getTeamId()).isPresent()){
                c.cancel();
                return;
            }
            //sender.sendMessage(senderTeam.getMembers().stream().map(Player::getDisplayName).collect(Collectors.joining()));
            senderTeam.refresh();
        },0,20);
    }
    @Execute(name="remove")
    void removeMemberFromTeam(@Context Player sender, @Arg Player target){
        GBunkers.getTeamManager().getByPlayerUuid(target.getUniqueId()).ifPresent(team -> {
            team.removeMember(team.getMembers().stream().filter(m -> m.getPlayer().getUniqueId().equals(target.getUniqueId())).findFirst().get());
        });
    }

    @Execute(name="startgame")
    void startGame(@Context Player sender, @Arg Arena arena){
        try{
            Game.startGame(arena);
        } catch (IllegalStateException e){
            sender.sendMessage(Chat.trans("&cArena in use"));
        }
    }
    @Execute(name="hide")
    void toggleHide(@Context Player sender, @Arg Player target){
        if (target.canSee(sender)){
            target.hidePlayer(sender);
            sender.sendMessage(Chat.trans("hid"));
        }else{
            target.showPlayer(sender);
            sender.sendMessage(Chat.trans("shown"));
        }
        ItemStack item = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        NamedTextColor color = NamedTextColor.NAMES.value("red");
        meta.setColor(org.bukkit.Color.fromRGB(color.red(),color.green(),color.blue()));
        item.setItemMeta(meta);
        sender.getInventory().addItem(item);
    }
    @Execute(name="fstop")
    void dumbStop(@Context Player sender){
        Bukkit.getWorld("world").getEntities().forEach(Entity::remove);
        //we're removing items while iterating
        new ArrayList<>(ArenaInfo.getArenasInUse().keySet()).forEach(arena -> Game.endGame(arena,sender));
        Task.runLater(a -> Bukkit.shutdown(),20*3);
    }
    @Execute(name="cappers")
    void cappersCheck(@Context Player sender){
        PlayerInfo.getPlayersCappingKoth().forEach((arena,uuid)->{
            sender.sendMessage(arena.getName()+"|"+Bukkit.getPlayer(uuid).getDisplayName());
        });
        Chat.bukkitSend("asdasd"+sender.getWorld().getPlayers().stream().map(Player::getDisplayName).collect(Collectors.joining("', ")));
    }
    @Execute(name="tp")
    void teleportWorld(@Context Player sender, @Arg World world, @Arg Optional<Float> x, @Arg Optional<Float> y, @Arg Optional<Float> z){
        sender.teleport(new Location(world,x.orElse(0f),y.orElse(100f),z.orElse(0f)));
    }
    @Execute(name="loadworld")
    void loadWorld(@Context Player sender, @Arg String world){
        if (Bukkit.getWorld(world) != null){
            sender.sendMessage("World alr loaded");
            return;
        }
        WorldCreator creator = new WorldCreator(world);
        World wrld = Bukkit.createWorld(creator);
        sender.sendMessage("is null: "+(wrld == null));
    }
}
