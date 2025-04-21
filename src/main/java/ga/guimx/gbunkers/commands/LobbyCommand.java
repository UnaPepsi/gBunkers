package ga.guimx.gbunkers.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import ga.guimx.gbunkers.config.PluginConfig;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.Player;

@Command(name = "lobby")
public class LobbyCommand {

    @Execute
    void teleortLobby(@Context Player sender){
        throw new NotImplementedException("to be done");
    }
    @Execute(name="set")
    @Permission("gbunkers.admin")
    void setLobby(@Context Player sender) {
        PluginConfig.setLobbyLocation(sender.getLocation());
        sender.sendMessage("done");
    }
}
