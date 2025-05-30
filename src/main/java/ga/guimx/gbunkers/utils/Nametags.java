package ga.guimx.gbunkers.utils;

import com.google.common.collect.Maps;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import com.lunarclient.apollo.recipients.Recipients;
import ga.guimx.gbunkers.game.ArenaInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Nametags {
    @Getter
    @Setter
    private static Map<Player,List<Component>> playersLunarNametag = Maps.newHashMap();
    public static void apply(Player player){
        //if (playersLunarNametag.get(player).isEmpty()){
        //    Apollo.getModuleManager().getModule(NametagModule.class).resetNametag(Recipients.ofEveryone(),player.getUniqueId());
        //    return;
        //}
        Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(Recipients.ofEveryone(),player.getUniqueId(),
                Nametag.builder()
                .lines(playersLunarNametag.get(player))
                .build()
        );
        ArenaInfo.getArenasInUse().forEach((arena,map) -> {
            map.values().stream().filter(t -> t.getMembers().contains(player)).findFirst().ifPresent(t -> {
                List<UUID> memberUUIDs = t.getMembers().stream().map(Player::getUniqueId).collect(Collectors.toList());
                Recipients recipients = Recipients.of(
                        Apollo.getPlayerManager().getPlayers().stream()
                                .filter(apolloPlayer -> memberUUIDs.contains(apolloPlayer.getUniqueId()))
                                .collect(Collectors.toList())
                );
                var cloned = new ArrayList<>(playersLunarNametag.get(player));
                cloned.add(Chat.toComponent("&a[TEAM]"));
                Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(recipients,player.getUniqueId(),
                        Nametag.builder()
                                .lines(cloned)
                                .build()
                );
            });
        });
    }
    public static void applyAll(){
        playersLunarNametag.keySet().forEach(Nametags::apply);
    }
}
