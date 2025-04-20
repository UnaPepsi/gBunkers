package ga.guimx.gbunkers.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.utils.Task;
import ga.guimx.gbunkers.utils.TeamManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.awt.*;

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
}
