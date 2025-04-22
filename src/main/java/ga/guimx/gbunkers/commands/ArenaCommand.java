package ga.guimx.gbunkers.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import ga.guimx.gbunkers.config.ArenasConfig;
import ga.guimx.gbunkers.utils.Arena;
import ga.guimx.gbunkers.utils.prompts.ArenaPrompt;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

@Command(name = "arena")
@Permission("gbunkers.admin")
public class ArenaCommand {

    @Execute(name="set")
    void setArena(@Context Player sender) {
        new ArenaPrompt().generateSetArenaPrompt(sender).begin();
    }
    @Execute(name="delete")
    void deleteArena(@Context CommandSender sender, @Arg Arena arena){
        try{
            ArenasConfig.getInstance().deleteArena(arena);
            sender.sendMessage("done");
        }catch (IOException ex){
            sender.sendMessage("an error occurred, check console");
            ex.printStackTrace();
        }
    }
    @Execute(name="teleport")
    void teleportToArena(@Context Player sender, @Arg Arena arena){
        sender.teleport(arena.getSpectatorSpawn());
    }
}
