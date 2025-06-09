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
    private static Map<UUID,List<Component>> playersLunarNametag = Maps.newHashMap();
    public static void apply(Player player){
        //if (playersLunarNametag.get(player).isEmpty()){
        //    Apollo.getModuleManager().getModule(NametagModule.class).resetNametag(Recipients.ofEveryone(),player.getUniqueId());
        //    return;
        //}
        Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(Recipients.ofEveryone(),player.getUniqueId(),
                Nametag.builder()
                .lines(playersLunarNametag.get(player.getUniqueId()))
                .build()
        );
        ArenaInfo.getArenasInUse().forEach((arena,map) -> {
            map.values().stream().filter(t -> t.getMembers().contains(player.getUniqueId())).findFirst().ifPresent(t -> {
                Recipients recipients = Recipients.of(
                        Apollo.getPlayerManager().getPlayers().stream()
                                .filter(apolloPlayer -> t.getMembers().contains(apolloPlayer.getUniqueId()))
                                .collect(Collectors.toList())
                );
                var cloned = new ArrayList<>(playersLunarNametag.get(player.getUniqueId()));
                cloned.add(Chat.toComponent("&a[TEAM]"));
                Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(recipients,player.getUniqueId(),
                        Nametag.builder()
                                .lines(cloned)
                                .build()
                );
            });
        });
    }
}
