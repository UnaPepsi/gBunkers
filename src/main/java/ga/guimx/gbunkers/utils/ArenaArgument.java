package ga.guimx.gbunkers.utils;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import ga.guimx.gbunkers.config.ArenasConfig;
import org.bukkit.command.CommandSender;

public class ArenaArgument extends ArgumentResolver<CommandSender, Arena> {
    @Override
    protected ParseResult<Arena> parse(
            Invocation<CommandSender> invocation,
            Argument<Arena> argument,
            String string
    ){
        Arena arena;
        try {
            arena = ArenasConfig.getArenas().stream().filter(a -> a.getName().equalsIgnoreCase(string)).findFirst().orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException e){
            return ParseResult.failure("Invalid arena");
        }
        return ParseResult.success(arena);
    }

    @Override
    public SuggestionResult suggest(
            Invocation<CommandSender> invocation,
            Argument<Arena> argument,
            SuggestionContext context
    ){
        return ArenasConfig.getArenas().stream().map(Arena::getName).collect(SuggestionResult.collector());
    }
}
