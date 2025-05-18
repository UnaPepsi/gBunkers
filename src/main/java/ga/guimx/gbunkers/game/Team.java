package ga.guimx.gbunkers.game;

import com.google.common.collect.Lists;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import com.lunarclient.apollo.recipients.Recipients;
import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.utils.Task;
import ga.guimx.gbunkers.utils.TeamManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Team {
    @Getter
    private final List<Player> members;
    @Getter
    private int dtr;
    @Getter
    private final ChatColor color;
    private boolean check = true;
    private TeamManager.Team team;
    public Team(List<Player> members, ChatColor color){
        this.members = members;
        this.color = color;
    }
    public Team setDtr(int dtr){
        this.dtr = dtr;
        return this;
    }
    public Team setTeamViewToMembers(){
        NamedTextColor displayNameColor = NamedTextColor.NAMES.value(color.name().toLowerCase());
        Color markerColor = new Color(displayNameColor.red(), displayNameColor.green(), displayNameColor.blue());
        this.team = GBunkers.getTeamManager().createTeam();
        members.forEach(p -> {
            team.addMember(new TeamManager.TeamPlayerWaypoint(p,displayNameColor,markerColor));
        });
        Task.runTimer(c -> {
            if(!check){
                c.cancel();
                return;
            }
            team.refresh();
        },0,20);
        return this;
    }
    public void setBard(){ //TODO: waypoint
        throw new NotImplementedException();
    }
    public void setArcher(){
        throw new NotImplementedException();
    }
    public void removeTeamViewFromMembers(){
        check = false;
        GBunkers.getTeamManager().deleteTeam(team.getTeamId());
    }
    public Team setLunarNametags(){
        List<UUID> memberUUIDs = members.stream().map(Player::getUniqueId).collect(Collectors.toList());
        Recipients recipients = Recipients.of(
                Apollo.getPlayerManager().getPlayers().stream()
                        .filter(apolloPlayer -> memberUUIDs.contains(apolloPlayer.getUniqueId()))
                        .collect(Collectors.toList())
        );
        //using scoreboards makes the player nametag visible with invis so imma use lunar api
        //opponents
        //for (Player player : members){
        //    Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(Recipients.ofEveryone(),player.getUniqueId(), Nametag.builder()
        //            .lines(Lists.newArrayList(
        //                    Component.text()
        //                            .content(player.getDisplayName())
        //                            .color(NamedTextColor.NAMES.value(color.name().toLowerCase()))
        //                            .build()
        //            ))
        //            .build());
        //}
        //teammates
        for (Player player : members){
            Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(recipients,player.getUniqueId(), Nametag.builder()
                            .lines(Lists.newArrayList(
                                    Component.text()
                                            .content(player.getDisplayName())
                                            .color(NamedTextColor.NAMES.value(color.name().toLowerCase()))
                                            .build(),
                                    Component.text()
                                            .content("[TEAM]")
                                            .color(NamedTextColor.GREEN)
                                            .build()
                            ))
                    .build());
        }
        return this;
    }
}
