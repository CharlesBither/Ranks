package tech.secretgarden.ranks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RanksPermissions {

    Database database = new Database();
    ConsoleCommandSender console = Bukkit.getConsoleSender();

    public void checkRank(Player player) {
        int stat = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        int second = stat / 20;
        int minute = second / 60;
        System.out.println(minute + " minutes played");

        if (player.hasPermission("group.pro")) {
            //if the user has a paid rank, or has the highest free rank, this is not applicable.
            return;
        }
        if (minute > 60 * 168) {
            //user does not have pro rank but needs it.
            if (!player.hasPermission("group.iron")) {
                //player needs all other rewards as well
                giveRank(player, "pro");
                giveMoney(player, "33500");
                giveKey(player, "seasonal", "20");
                return;
            }
            giveRank(player, "pro");
            giveMoney(player, "25000");
            giveKey(player, "seasonal", "10");
            return;
        }
        if (minute > 60 * 24) {
            if (!player.hasPermission("group.iron")) {
                //player needs all other rewards as well
                giveRank(player, "ender");
                giveMoney(player, "8500");
                giveKey(player, "seasonal", "10");
                return;
            }
            if (!player.hasPermission("group.ender")) {
                giveRank(player, "ender");
                giveMoney(player, "5000");
                giveKey(player, "seasonal", "5");
                return;
            }
            Bukkit.getLogger().warning("Err setting ender rank");
            return;
        }
        if (minute > 600) {
            if (!player.hasPermission("group.iron")) {
                //player needs all other rewards as well
                giveRank(player, "gold");
                giveMoney(player, "3500");
                giveKey(player, "seasonal", "10");
                return;
            }
            if (!player.hasPermission("group.gold")) {
                giveRank(player, "gold");
                giveMoney(player, "2500");
                giveKey(player, "seasonal", "3");
                return;
            }
            Bukkit.getLogger().warning("Err setting gold rank");
            return;
        }
        if (minute > 120) {
            if (!player.hasPermission("group.iron")) {
                //make them iron rank at 2 hours.
                giveRank(player, "iron");
                giveMoney(player, "1000");
                giveKey(player, "seasonal", "2");
                return;
            }
            Bukkit.getLogger().warning("Err setting iron rank");
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
