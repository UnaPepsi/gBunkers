package ga.guimx.gbunkers.game;

import com.google.common.collect.Lists;
import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.utils.Nametags;
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
        members.forEach(member -> {
            Nametags.getPlayersLunarNametag().put(member, Lists.newArrayList(
                    Component.text()
                            .content(member.getDisplayName())
                            .color(NamedTextColor.NAMES.value(color.name().toLowerCase()))
                            .build()
            ));
            Nametags.apply(member);
        });
        return this;
    }
}
