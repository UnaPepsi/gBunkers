package ga.guimx.gbunkers.utils;

import ga.guimx.gbunkers.GBunkers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Chat {
    public static String trans(String string){
        return ChatColor.translateAlternateColorCodes('&',GBunkers.getPrefix()+string);
    }
    public static Component toComponent(String string){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(GBunkers.getPrefix()+string);
    }
    public static void bukkitSend(String string){
        Bukkit.getConsoleSender().sendMessage(Chat.trans(string));
    }
}
