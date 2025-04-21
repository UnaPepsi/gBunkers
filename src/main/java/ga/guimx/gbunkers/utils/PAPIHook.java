package ga.guimx.gbunkers.utils;

import ga.guimx.gbunkers.GBunkers;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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
        switch (params.toLowerCase()){
            case "players_queued":
                return PlayerInfo.getPlayersQueued().size()+"";
        }
        return null;
    }

    public static void registerHook(){
        new PAPIHook().register();
    }
}
