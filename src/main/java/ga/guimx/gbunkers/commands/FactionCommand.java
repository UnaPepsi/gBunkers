package ga.guimx.gbunkers.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.game.ArenaInfo;
import ga.guimx.gbunkers.utils.Chat;
import ga.guimx.gbunkers.utils.Nametags;
import ga.guimx.gbunkers.utils.PlayerInfo;
import ga.guimx.gbunkers.utils.Task;
import lombok.var;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Command(name = "faction",aliases = {"f","t","team","equipoxdxdxd"})
public class FactionCommand {

    @Execute
    void executeHelp(@Context Player sender){
        sender.sendMessage(Chat.trans("&c/f who and /f hq das it"));
    }
    @Execute(name="who",aliases = {"info","i","quienxdxd"})
    void executeFWho(@Context Player sender, @OptionalArg String lookUp) {
        if (!PlayerInfo.getPlayersInGame().contains(sender.getUniqueId())){
            sender.sendMessage(Chat.trans("&cYou're not in a game!"));
            return;
        }
        if (lookUp == null){
            lookUp = sender.getName();
        }
        String finalLookUp = lookUp;
        AtomicBoolean found = new AtomicBoolean(false);
        ArenaInfo.getArenasInUse().forEach((arena, map) -> {
            map.values().stream()
                    .filter(team -> !team.getMembers().isEmpty() && team.getMembers().get(0).getWorld().equals(sender.getWorld()) &&
                            (team.getMembers().stream().map(p -> p.getName().toLowerCase()).collect(Collectors.toList()).contains(finalLookUp.toLowerCase()) || team.getColor().name().equalsIgnoreCase(finalLookUp)))
                    .findAny().ifPresent(team -> {
                        found.set(true);
                        Location home = arena.getTeams().get(team.getColor().name().toLowerCase()).getHome();
                        sender.sendMessage(Chat.trans("&9%team%:\n&aHome: %home%\n&aDTR: %dtr%\n&aMembers: %members%"
                                .replace("%team%", team.getColor()+team.getColor().name())
                                .replace("%home%",String.format("%d, %d, %d",home.getBlockZ(),home.getBlockY(),home.getBlockZ()))
                                .replace("%dtr%",(team.getDtr() > 0 ? team.getDtr() > 1 ? ChatColor.GREEN : ChatColor.YELLOW : ChatColor.RED) + (team.getDtr()+""))
                                .replace("%members%",team.getColor()+team.getMembers().stream().map(Player::getDisplayName).collect(Collectors.joining(", ")))));
                    });
        });
        if (!found.get()){
            sender.sendMessage(Chat.trans("&cNothing found"));
        }
    }

    @Execute(name="hq",aliases = {"home","casa"}) //this is ugly as hell idc
    void executeFHome(@Context Player sender){
        if (!PlayerInfo.getPlayersInGame().contains(sender.getUniqueId())){
            sender.sendMessage(Chat.trans(PluginConfig.getMessages().get("in_game_cant")));
            return;
        }
        if (PlayerInfo.getPlayersFHomming().contains(sender.getUniqueId())){
            sender.sendMessage(Chat.trans("&cYou're already teleporting to your faction's HQ"));
            return;
        }
        var nametag = Nametags.getPlayersLunarNametag().get(sender);
        sender.sendMessage(Chat.trans(PluginConfig.getMessages().get("f_home")));
        ArenaInfo.getArenasInUse().forEach((arena,map) -> {
            map.values().stream().filter(team -> team.getMembers().contains(sender)).findAny().ifPresent(team -> {
                AtomicInteger timer = new AtomicInteger(11);
                PlayerInfo.getPlayersFHomming().add(sender.getUniqueId());
                Task.runTimer(task -> {
                    timer.decrementAndGet();
                    if (!PlayerInfo.getPlayersFHomming().contains(sender.getUniqueId())){
                        if (!arena.getTeams().get(team.getColor().name().toLowerCase()).getHome().equals(sender.getLocation())){
                            sender.sendMessage(Chat.trans("&cYou've moved or got hit! cancelling teleport"));
                        }else{
                            sender.sendMessage(Chat.trans("&aYou've teleported to your faction's HQ"));
                        }
                        nametag.remove(Chat.toComponent("&9F Home: " + (timer.get() + 1) + "s"));
                        Nametags.apply(sender);
                        task.cancel();
                        return;
                    }
                    if (timer.get() <= 0){
                        sender.teleport(arena.getTeams().get(team.getColor().name().toLowerCase()).getHome());
                        PlayerInfo.getPlayersFHomming().remove(sender.getUniqueId());
                    }
                    var opFHomeComponent = nametag.stream().filter(comp -> Chat.toPlainString(comp).startsWith("F Home")).findFirst();
                    if (opFHomeComponent.isPresent()){
                        int index = nametag.indexOf(opFHomeComponent.get());
                        nametag.set(index,Chat.toComponent("&9F Home: "+timer.get()+"s"));
                    }else{
                        nametag.add(Chat.toComponent("&9F Home: "+timer.get()+"s"));
                    }
                    Nametags.apply(sender);

                },0,20);
            });
        });
    }

}
