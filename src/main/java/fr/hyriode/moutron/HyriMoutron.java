package fr.hyriode.moutron;

import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Created by AstFaster
 * on 17/12/2022 at 09:45
 */
public class HyriMoutron extends JavaPlugin implements IPluginProvider {

    public static final String NAME = "Moutron";
    public static final String ID = "moutron";
    private static final String[] HEADER_LINES = new String[] {
            "  __  __          _                ",
            " |  \\/  |___ _  _| |_ _ _ ___ _ _  ",
            " | |\\/| / _ \\ || |  _| '_/ _ \\ ' \\ ",
            " |_|  |_\\___/\\_,_|\\__|_| \\___/_||_|",
            "                                   "
    };
    private static final String PACKAGE = "fr.hyriode.moutron";

    private static HyriMoutron instance;

    private IHyrame hyrame;

    @Override
    public void onEnable() {
        instance = this;

        for (String line : HEADER_LINES) {
            log(line);
        }

        log("Starting " + NAME + "...");

        this.hyrame = HyrameLoader.load(this);
    }

    @Override
    public void onDisable() {
        log("Stopping " + NAME + "...");

        this.hyrame.getGameManager().unregisterGame(this.game);
    }

    public static void log(Level level, String message) {
        String prefix = ChatColor.GOLD + "[" + NAME + "] ";

        if (level == Level.SEVERE) {
            prefix += ChatColor.RED;
        } else if (level == Level.WARNING) {
            prefix += ChatColor.YELLOW;
        } else {
            prefix += ChatColor.RESET;
        }

        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

    public static void log(String msg) {
        log(Level.INFO, msg);
    }

    public static HyriMoutron get() {
        return instance;
    }

    public IHyrame getHyrame() {
        return this.hyrame;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public String getId() {
        return "moutron";
    }

    @Override
    public String[] getCommandsPackages() {
        return new String[] {PACKAGE};
    }

    @Override
    public String[] getListenersPackages() {
        return new String[] {PACKAGE};
    }

    @Override
    public String[] getItemsPackages() {
        return new String[] {PACKAGE};
    }

    @Override
    public String getLanguagesPath() {
        return "/lang/";
    }

}
