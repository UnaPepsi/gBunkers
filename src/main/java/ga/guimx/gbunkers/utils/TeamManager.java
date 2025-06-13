package ga.guimx.gbunkers.utils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloLocation;
import com.lunarclient.apollo.module.team.TeamMember;
import com.lunarclient.apollo.module.team.TeamModule;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class TeamManager {
    private final Map<UUID, Team> teamsByTeamId = Maps.newHashMap();
    private final Map<UUID, Team> teamsByPlayerUuid = Maps.newHashMap();

    public Optional<Team> getByPlayerUuid(UUID playerUuid) {
        return Optional.ofNullable(teamsByPlayerUuid.get(playerUuid));
    }

    public Optional<Team> getByTeamId(UUID teamId) {
        return Optional.ofNullable(teamsByTeamId.get(teamId));
    }

    public Team createTeam() {
        Team team = new Team();
        teamsByTeamId.put(team.getTeamId(), team);
        return team;
    }

    public void deleteTeam(UUID teamId) {
        Team team = teamsByTeamId.remove(teamId);

        if (team != null) {
            new ArrayList<>(team.getMembers()).forEach(team::removeMember);
        }
    }
    @Getter
    public class Team{
        private final UUID teamId;
        private final Set<TeamPlayerWaypoint> members;
        public Team(){
            this.teamId = UUID.randomUUID();
            this.members = Sets.newHashSet();
        }
        public void addMember(TeamPlayerWaypoint player){
            members.add(player);
            teamsByPlayerUuid.put(player.getPlayer().getUniqueId(),this);
        }
        public void removeMember(TeamPlayerWaypoint member){
            members.remove(member);
            teamsByPlayerUuid.remove(member.getPlayer().getUniqueId());
            Apollo.getPlayerManager().getPlayer(member.getPlayer().getUniqueId())
                    .ifPresent(Apollo.getModuleManager().getModule(TeamModule.class)::resetTeamMembers);
            if (members.isEmpty()){
                deleteTeam(teamId);
            }
        }
        private TeamMember createTeamMember(TeamPlayerWaypoint member){
            Location loc = member.getPlayer().getLocation();
            return TeamMember.builder()
                    .playerUuid(member.getPlayer().getUniqueId())
                    .displayName(Component.text()
                            .color(member.getDisplayNameColor())
                            .content(member.getPlayer().getName())
                            .build())
                    .markerColor(member.getMarkerColor())
                    .location(ApolloLocation.builder()
                            .world(loc.getWorld().getName())
                            .x(loc.getX())
                            .y(loc.getY())
                            .z(loc.getZ())
                            .build())
                    .build();
        }
        public void refresh(){
            List<TeamMember> teammates = members.stream().filter(m -> m.getPlayer().isOnline())
                    .map(this::createTeamMember)
                    .collect(Collectors.toList());
            members.forEach(member -> Apollo.getPlayerManager().getPlayer(member.getPlayer().getUniqueId())
                        .ifPresent(
                                apolloPlayer -> Apollo.getModuleManager().getModule(TeamModule.class).updateTeamMembers(apolloPlayer,teammates)
                        )
            );
        }
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (other == null || other.getClass() != this.getClass()) {
                return false;
            }

            Team team = (Team) other;
            return this.teamId.equals(team.getTeamId());
        }

        @Override
        public int hashCode() {
            return this.teamId.hashCode();
        }
    }
    @Getter
    public static class TeamPlayerWaypoint {
        private final Player player;
        private final NamedTextColor displayNameColor;
        private final Color markerColor;
        public TeamPlayerWaypoint(Player player, NamedTextColor displayNameColor, Color markerColor){
            this.player = player;
            this.displayNameColor = displayNameColor;
            this.markerColor = markerColor;
        }
    }
}