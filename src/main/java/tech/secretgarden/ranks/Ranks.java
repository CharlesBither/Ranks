package tech.secretgarden.ranks;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;

public final class Ranks extends JavaPlugin {

    Database database = new Database();
    RanksPermissions ranksPermissions = new RanksPermissions();

    public static ArrayList<String> dbList = new ArrayList();
    public ArrayList<String> getDbList() {
        dbList.add(getConfig().getString("HOST"));
        dbList.add(getConfig().getString("PORT"));
        dbList.add(getConfig().getString("DATABASE"));
        dbList.add(getConfig().getString("USERNAME"));
        dbList.add(getConfig().getString("PASSWORD"));
        return dbList;
    }

    public static ArrayList<String> dropletList = new ArrayList();
    public ArrayList<String> getDropletList() {
        dropletList.add(getConfig().getString("DROPLET_HOST"));
        dropletList.add(getConfig().getString("DROPLET_PORT"));
        dropletList.add(getConfig().getString("DROPLET_DATABASE"));
        dropletList.add(getConfig().getString("DROPLET_USERNAME"));
        dropletList.add(getConfig().getString("DROPLET_PASSWORD"));
        return dropletList;
    }

    public static ArrayList<String> possibleGroups = new ArrayList<>();

    public static ArrayList<String> getPossibleGroups() {
        possibleGroups.add("owner");
        possibleGroups.add("admin");
        possibleGroups.add("mod");
        possibleGroups.add("diamond");
        possibleGroups.add("mvp");
        possibleGroups.add("vip");
        possibleGroups.add("pro");
        possibleGroups.add("ender");
        possibleGroups.add("gold");
        possibleGroups.add("iron");
        possibleGroups.add("default");
        return possibleGroups;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("Ranks has loaded");
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        getConfig().options().copyDefaults();
        saveDefaultConfig();

//        if (luckPerms == null) {
//            System.out.println("LP not found, shutting down");
//            Bukkit.getPluginManager().disablePlugin(this);
//            return;
//        }
        try {
            getDbList();
            getDropletList();
            Database.connect();
            Database.dropletConnect();
            getPossibleGroups();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dbList.get(0).isEmpty() || dropletList.get(0).isEmpty()) {
            System.out.println("config is empty, shutting down");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (Database.isConnected()) {
            System.out.println("Connected to local db");
        } else {
            System.out.println("not connected to local db, shutting down");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (Database.dropletIsConnected()) {
            System.out.println("Connected to Droplet");
        } else {
            System.out.println("not connected to Droplet db, shutting down");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        updateRanks.runTaskTimer(this, 20, 20 * 60);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Ranks has been disabled");
        database.dropletDisconnect();
        database.disconnect();
    }

    private final BukkitRunnable updateRanks = new BukkitRunnable() {
        @Override
        public void run() {
            ImmutableList<Player> onlinePlayers = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
            for (Player p : onlinePlayers) {
                ranksPermissions.checkRank(p);
            }
            System.out.println("updating ranks");
        }
    };
}
