package ga.guimx.gbunkers.utils;

import ga.guimx.gbunkers.GBunkers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.awt.*;

public class Chat {
    public static String transPrefix(String string){
        return trans(GBunkers.getPrefix()+string);
    }
    public static String trans(String string){
        return ChatColor.translateAlternateColorCodes('&',string);
    }
    public static Component toComponent(String string){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
    }
    public static Component toComponentPrefix(String string){
        return toComponent(GBunkers.getPrefix()+string);
    }
    public static String toPlainString(Component component){
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
    public static void bukkitSend(String string){
        Bukkit.getConsoleSender().sendMessage(Chat.transPrefix(string));
    }
    public static Color getColor(String string) {
        TextColor a = toComponent(string).color();
        return new Color(a.red(),a.green(),a.blue());
    }
}
