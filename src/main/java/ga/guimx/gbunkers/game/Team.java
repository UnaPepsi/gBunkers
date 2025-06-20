package ga.guimx.gbunkers.game;

import com.google.common.collect.Lists;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.recipients.Recipients;
import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.utils.Nametags;
import ga.guimx.gbunkers.utils.Task;
import ga.guimx.gbunkers.utils.TeamManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Team {
    @Getter
    private final List<UUID> members;
    @Getter
    private int dtr;
    @Getter
    private final ChatColor color;
    private boolean check = true;
    private TeamManager.Team team;
    public Team(List<UUID> members, ChatColor color){
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
        members.forEach(uuid -> {
            team.addMember(new TeamManager.TeamPlayerWaypoint(Bukkit.getPlayer(uuid),displayNameColor,markerColor));
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
    public void removeWaypoints(){
        Recipients recipients = Recipients.of(
                Apollo.getPlayerManager().getPlayers().stream()
                        .filter(apolloPlayer -> members.contains(apolloPlayer.getUniqueId()))
                        .collect(Collectors.toList())
        );
        Apollo.getModuleManager().getModule(WaypointModule.class).resetWaypoints(recipients);
    }
    public Team setLunarNametags(){
        members.forEach(uuid -> {
            Nametags.getPlayersLunarNametag().put(uuid, Lists.newArrayList(
                    Component.text()
                            .content(Bukkit.getPlayer(uuid).getDisplayName())
                            .color(NamedTextColor.NAMES.value(color.name().toLowerCase()))
                            .build()
            ));
            Nametags.apply(Bukkit.getPlayer(uuid));
        });
        return this;
    }
}
