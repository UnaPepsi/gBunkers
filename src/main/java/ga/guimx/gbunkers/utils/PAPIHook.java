package ga.guimx.gbunkers.utils;

import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.game.ArenaInfo;
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
        switch (params.toLowerCase()){
            case "players_queued":
                return PlayerInfo.getPlayersQueued().size()+"";
            case "players_in_game":
                return PlayerInfo.getPlayersInGame().size()+"";
            case "balance":
                if (onlinePlayer == null){
                    return "0";
                }
                return PlayerInfo.getPlayersBalance().getOrDefault(onlinePlayer,0)+"";
            case "dtr":
                if (onlinePlayer == null){
                    return "NaN";
                }
                AtomicReference<String> result = new AtomicReference<>("NaN");
                ArenaInfo.getArenasInUse().forEach((arena,map) -> {
                    map.values().forEach(team -> {
                        if (team.getMembers().contains(onlinePlayer)){
                            result.set(team.getDtr() + "");
                        }
                    });
                });
                return result.get();
        }
        return null;
    }

    public static void registerHook(){
        new PAPIHook().register();
    }
}
