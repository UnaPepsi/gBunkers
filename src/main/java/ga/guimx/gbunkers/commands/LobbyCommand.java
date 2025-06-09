package ga.guimx.gbunkers.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.utils.Chat;
import ga.guimx.gbunkers.utils.PlayerInfo;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Command(name = "lobby")
public class LobbyCommand {

    @Execute
    void teleortLobby(@Context Player sender){
        if (PlayerInfo.getPlayersInGame().contains(sender.getUniqueId())){
            sender.sendMessage(Chat.trans(PluginConfig.getMessages().get("in_game_cant")));
            return;
        }
        sender.teleport(PluginConfig.getLobbyLocation());
        PlayerInfo.getPlayersSpectating().remove(sender.getUniqueId());
        sender.setGameMode(GameMode.ADVENTURE);
    }
    @Execute(name="set")
    @Permission("gbunkers.admin")
    void setLobby(@Context Player sender) {
        PluginConfig.setLobbyLocation(sender.getLocation());
        sender.sendMessage("done");
    }
}
