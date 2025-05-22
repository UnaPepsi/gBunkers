package ga.guimx.gbunkers.commands;

import com.google.common.collect.Lists;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import com.lunarclient.apollo.recipients.Recipients;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import ga.guimx.gbunkers.game.ArenaInfo;
import ga.guimx.gbunkers.utils.Chat;
import ga.guimx.gbunkers.utils.PlayerInfo;
import ga.guimx.gbunkers.utils.Task;
import ga.guimx.gbunkers.utils.Time;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Command(name = "faction",aliases = {"f","t","team","equipoxdxdxd"})
public class FactionCommand {

    @Execute(name="who",aliases = {"info","i","quienxdxd"})
    void executeFWho(@Context Player sender, @OptionalArg String lookUp) {
        sender.sendMessage("lookUp: "+lookUp);
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
                    .filter(team -> !team.getMembers().isEmpty() && team.getMembers().getFirst().getWorld().equals(sender.getWorld()) &&
                            (team.getMembers().stream().map(Player::getName).collect(Collectors.toList()).contains(finalLookUp) || team.getColor().name().equalsIgnoreCase(finalLookUp)))
                    .findAny().ifPresent(team -> {
                        found.set(true);
                        Location home = arena.getTeams().get(team.getColor().name().toLowerCase()).getHome();
                        sender.sendMessage(Chat.trans("&9%team%:\n&aHome: %home%\n&aDTR: %dtr%\n&aMembers: %members%"
                                .replace("%team%", team.getColor()+team.getColor().name())
                                .replace("%home%",String.format("%d, %d, %d",home.getBlockZ(),home.getBlockY(),home.getBlockZ()))
                                .replace("%dtr%",(team.getDtr() > 0 ? team.getDtr() > 1 ? ChatColor.GREEN : ChatColor.YELLOW : ChatColor.RED) + (team.getDtr()+""))
                                .replace("%members%",team.getMembers().stream().map(Player::getDisplayName).collect(Collectors.joining(", ")))));
                    });
        });
        if (!found.get()){
            sender.sendMessage(Chat.trans("&cNothing found"));
        }
    }

    @Execute(name="hq",aliases = {"home","casa"}) //this is ugly as hell idc
    void executeFHome(@Context Player sender){
        ArenaInfo.getArenasInUse().forEach((arena,map) -> {
            map.values().stream().filter(team -> team.getMembers().contains(sender)).findAny().ifPresent(team -> {
                long startedAt = System.currentTimeMillis();
                PlayerInfo.getPlayersFHomming().add(sender.getUniqueId());
                Task.runTimer(task -> {
                    List<UUID> memberUUIDs = team.getMembers().stream().map(Player::getUniqueId).collect(Collectors.toList());
                    Recipients recipients = Recipients.of(
                            Apollo.getPlayerManager().getPlayers().stream()
                                    .filter(apolloPlayer -> memberUUIDs.contains(apolloPlayer.getUniqueId()))
                                    .collect(Collectors.toList())
                    );
                    if (!PlayerInfo.getPlayersFHomming().contains(sender.getUniqueId())){
                        sender.sendMessage(Chat.transPrefix("&cYou've moved or got hit! cancelling teleport"));
                        for (Player player : team.getMembers()){
                            Apollo.getModuleManager().getModule(NametagModule.class).resetNametag(Recipients.ofEveryone(),player.getUniqueId());
                        }
                        for (Player player : team.getMembers()){
                            Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(recipients,player.getUniqueId(), Nametag.builder()
                                    .lines(Lists.newArrayList(
                                            Component.text()
                                                    .content(player.getDisplayName())
                                                    .color(NamedTextColor.NAMES.value(team.getColor().name().toLowerCase()))
                                                    .build(),
                                            Chat.toComponent("&a[TEAM]")
                                    ))
                                    .build());
                        }
                        task.cancel();
                        return;
                    }
                    if (Time.timePassedSecs(startedAt,System.currentTimeMillis()) >= 10){
                        sender.teleport(arena.getTeams().get(team.getColor().name().toLowerCase()).getHome());
                        PlayerInfo.getPlayersFHomming().remove(sender.getUniqueId());
                    }
                    for (Player player : team.getMembers()){
                        Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(Recipients.ofEveryone(),player.getUniqueId(), Nametag.builder()
                                .lines(Lists.newArrayList(
                                        Component.text()
                                                .content(player.getDisplayName())
                                                .color(NamedTextColor.NAMES.value(team.getColor().name().toLowerCase()))
                                                .build(),
                                        Chat.toComponent("&9F Home: "+(10-Time.timePassedSecs(startedAt,System.currentTimeMillis()))+"s")
                                ))
                                .build());
                    }
                    for (Player player : team.getMembers()){
                        Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(recipients,player.getUniqueId(), Nametag.builder()
                                .lines(Lists.newArrayList(
                                        Component.text()
                                                .content(player.getDisplayName())
                                                .color(NamedTextColor.NAMES.value(team.getColor().name().toLowerCase()))
                                                .build(),
                                        Chat.toComponent("&a[TEAM]"),
                                        Chat.toComponent("&9F Home: "+(10-Time.timePassedSecs(startedAt,System.currentTimeMillis()))+"s")
                                ))
                                .build());
                    }

                },0,1);
            });
        });
    }

}
