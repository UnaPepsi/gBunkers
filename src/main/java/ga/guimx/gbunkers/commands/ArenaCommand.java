package ga.guimx.gbunkers.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import ga.guimx.gbunkers.utils.prompts.ArenaPrompt;
import org.bukkit.entity.Player;

@Command(name = "arena")
@Permission("gbunkers.admin")
public class ArenaCommand {

    @Execute(name="set")
    void setArena(@Context Player sender) {
        new ArenaPrompt().generateSetArenaPrompt(sender).begin();
    }

}
