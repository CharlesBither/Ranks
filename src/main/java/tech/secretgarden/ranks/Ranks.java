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
    public void getDbList() {
        dbList.add(getConfig().getString("HOST"));
        dbList.add(getConfig().getString("PORT"));
        dbList.add(getConfig().getString("DATABASE"));
        dbList.add(getConfig().getString("USERNAME"));
        dbList.add(getConfig().getString("PASSWORD"));
    }

    public static ArrayList<String> dropletList = new ArrayList();
    public void getDropletList() {
        dropletList.add(getConfig().getString("DROPLET_HOST"));
        dropletList.add(getConfig().getString("DROPLET_PORT"));
        dropletList.add(getConfig().getString("DROPLET_DATABASE"));
        dropletList.add(getConfig().getString("DROPLET_USERNAME"));
        dropletList.add(getConfig().getString("DROPLET_PASSWORD"));
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Ranks has loaded");
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        try {
            getDbList();
            getDropletList();
            Database.connect();
            Database.dropletConnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dbList.get(0).isEmpty() || dropletList.get(0).isEmpty()) {
            Bukkit.getLogger().warning("config is empty, shutting down");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (Database.isConnected()) {
            Bukkit.getLogger().info("Connected to local db");
        } else {
            Bukkit.getLogger().warning("not connected to local db, shutting down");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (Database.dropletIsConnected()) {
            Bukkit.getLogger().info("Connected to Droplet");
        } else {
            Bukkit.getLogger().warning("not connected to Droplet db, shutting down");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Rewards.initMap();
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
            Bukkit.getLogger().info("updating ranks");
        }
    };
}
