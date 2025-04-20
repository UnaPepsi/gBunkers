package ga.guimx.gbunkers.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.utils.Chat;
import ga.guimx.gbunkers.utils.PlayerInfo;
import org.bukkit.entity.Player;

@Command(name = "queue")
public class QueueCommand {

    @Execute(name="join",aliases = {"enter"})
    void joinQueue(@Context Player player) {
        if (PlayerInfo.getPlayersQueued().contains(player.getUniqueId())){
            player.sendMessage(Chat.trans(PluginConfig.getMessages().get("already_queued")));
            return;
        }
        PlayerInfo.getPlayersQueued().add(player.getUniqueId());
        player.sendMessage(Chat.trans(PluginConfig.getMessages().get("joined_queue")));
    }

    @Execute(name="leave",aliases = {"exit"})
    void leaveQueue(@Context Player player){
        if (!PlayerInfo.getPlayersQueued().contains(player.getUniqueId())){
            player.sendMessage(Chat.trans(PluginConfig.getMessages().get("not_queued")));
            return;
        }
        PlayerInfo.getPlayersQueued().remove(player.getUniqueId());
        player.sendMessage(Chat.trans(PluginConfig.getMessages().get("left_queue")));
    }

}
