package tech.secretgarden.ranks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class Rewards {

    Database database = new Database();
    ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static HashMap<String, String[]> map = new HashMap<>();

    private static final String[] ranks = {"iron", "gold", "ender", "pro"};
    private static final String[] iron = {"group.iron", "1000", "seasonal", "2"};
    private static final String[] gold = {"group.gold", "2500", "seasonal", "3"};
    private static final String[] ender = {"group.ender", "5000", "seasonal", "5"};
    private static final String[] pro = {"group.pro", "25000", "seasonal", "10"};

    public static void initMap() {
        map.put(ranks[0], iron);
        map.put(ranks[1], gold);
        map.put(ranks[2], ender);
        map.put(ranks[3], pro);
    }

    public void giveRewards(String stop, Player player) {

        // if player already has the permission, return.
        if (player.hasPermission(map.get(stop)[0])) { return; }

        // Iterate through ranks and give rewards as needed.
        for (int i = 0; i < map.size(); i++) {
            String key = ranks[i];
            String[] context = map.get(key);

            // Give rewards if player does not have rank.
            if (!player.hasPermission(context[0])) {
                giveRank(player, key);
                giveMoney(player, context[1]);
                giveKey(player, context[2], context[3]);
            }

            // return if rank equals stop param
            if (key.equals(stop)) { return; }
        }
    }

    private void giveRank(Player player, String rank) {
        String uuid = player.getUniqueId().toString();

        Bukkit.getServer().dispatchCommand(console, "lp user " + player.getName() + " parent add " + rank);
        Bukkit.getServer().dispatchCommand(console, "broadcast Congratulations! " + player.getDisplayName() + ChatColor.GREEN + " has been promoted to " + rank + " rank!");
        Bukkit.getLogger().info("CONSOLE issued server command: lp user " + player.getName() + " parent add " + rank);
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE ranks SET rank_name = ? WHERE uuid = ?")) {
            statement.setString(1, rank);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Connection connection = database.getDropletPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE ranks SET rank_name = ? WHERE uuid = ?")) {
            statement.setString(1, rank);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void giveMoney(Player player, String amount) {
        Bukkit.getServer().dispatchCommand(console, "eco give " + player.getName() + " " + amount);
        Bukkit.getLogger().info("CONSOLE issued server command: eco give " + player.getName() + " " + amount);
    }

    private void giveKey(Player player, String keyName, String amount) {
        Bukkit.getServer().dispatchCommand(console, "stashkey give " + player.getName() + " " + keyName + " " + amount);
        Bukkit.getLogger().info("CONSOLE issued server command: stashkey give " + player.getName() + " " + keyName + " " + amount);
        player.sendMessage(ChatColor.GREEN + amount + " " + keyName + " keys were deposited into your stash");
    }

}
