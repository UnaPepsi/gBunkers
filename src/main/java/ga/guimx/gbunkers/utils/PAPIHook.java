package ga.guimx.gbunkers.utils;

import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.game.ArenaInfo;
import lombok.var;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class PAPIHook extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "gbunkers";
    }

    @Override
    public @NotNull String getAuthor() {
        return "guimx";
    }

    @Override
    public @NotNull String getVersion() {
        return GBunkers.getInstance().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params){
        Player onlinePlayer = player.getPlayer();
        var result = new AtomicReference<>("NaN");
        switch (params.toLowerCase()){
            case "players_queued":
                return PlayerInfo.getPlayersQueued().size()+"";
            case "players_in_game":
                return PlayerInfo.getPlayersInGame().size()+"";
            case "balance":
                if (onlinePlayer == null){
                    return "0";
                }
                return PlayerInfo.getPlayersBalance().getOrDefault(onlinePlayer.getUniqueId(),0)+"";
            case "dtr":
                if (onlinePlayer == null){
                    return "NaN";
                }
                ArenaInfo.getArenasInUse().forEach((arena,map) -> {
                    map.values().forEach(team -> {
                        if (team.getMembers().contains(onlinePlayer.getUniqueId())){
                            result.set(team.getDtr() + "");
                        }
                    });
                });
                return result.get();
            case "bard_energy":
                if (onlinePlayer == null || !PlayerInfo.getBardEnergy().containsKey(onlinePlayer.getUniqueId())){
                    return "0";
                }
                return PlayerInfo.getBardEnergy().getOrDefault(onlinePlayer.getUniqueId(),(short)0)+"";
            case "bard_cooldown":
                if (onlinePlayer == null || !PlayerInfo.getBardCD().containsKey(onlinePlayer.getUniqueId())){
                    return "0";
                }
                return PlayerInfo.getBardCD().getOrDefault(onlinePlayer.getUniqueId(),0L)+"";
        }
        return null;
    }

    public static void registerHook(){
        new PAPIHook().register();
    }
}
