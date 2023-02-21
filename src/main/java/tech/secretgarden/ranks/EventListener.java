package tech.secretgarden.ranks;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class EventListener implements Listener {

    RanksPermissions ranksPermissions = new RanksPermissions();
    Database database = new Database();

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT uuid, gamertag FROM ranks WHERE uuid = ?")) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet rs = statement.executeQuery();
            System.out.println("finding player in local");
            if (!rs.next()) {
                System.out.println("did not find local");
                //insert the new user into the table
                initLocalUser(player);
                initDropletUser(player);
            } else {
                // this uuid already exists
                String gamertag = rs.getString("gamertag");
                String name = player.getName();
                // check if gamertag matches player's name
                if (!name.equals(gamertag)) {

                    // update gamertag with current name
                    Connection localConnection = database.getPool().getConnection();
                    Connection dropletConnection = database.getDropletPool().getConnection();
                    updateUsername(player, gamertag, localConnection).runTaskAsynchronously(Ranks.plugin);
                    updateUsername(player, gamertag, dropletConnection).runTaskAsynchronously(Ranks.plugin);
                    Bukkit.getLogger().info("Updated name '" + gamertag + "' to '" + name + "' for uuid '" + player.getUniqueId() + "'");
                }
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }

        //finally update the player with their rank
        ranksPermissions.checkRank(player);
    }

    private BukkitRunnable updateUsername(Player player, String oldName, Connection connection) {
        String name = player.getName();
        String uuid = player.getUniqueId().toString();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try (connection;
                     PreparedStatement statement = connection.prepareStatement("UPDATE ranks SET gamertag = ? WHERE uuid = ?;")) {

                     statement.setString(1, name);
                     statement.setString(2, uuid);
                     statement.executeUpdate();
                } catch (SQLException x) {
                    x.printStackTrace();
                }
            }
        };
        return runnable;
    }

    private void initLocalUser(Player player) {

        String rank = getPlayerGroup(player, Ranks.getPossibleGroups());
        try (Connection connection = database.getPool().getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO ranks (uuid, gamertag, rank_name) VALUES (?,?,?);")) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.setString(3, rank);
            statement.executeUpdate();
            System.out.println("inserted into local");

        } catch (SQLException x) {
            x.printStackTrace();
        }
    }
    private void initDropletUser(Player player) {

        String rank = getPlayerGroup(player, Ranks.getPossibleGroups());
        try (Connection connection = database.getDropletPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO ranks (uuid, gamertag, rank_name) VALUES (?,?,?);")) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.setString(3, rank);
            statement.executeUpdate();
            System.out.println("inserted into Droplet");

        } catch (SQLException x) {
            x.printStackTrace();
        }
    }
    public static String getPlayerGroup(Player player, Collection<String> possibleGroups) {
        for (String group : possibleGroups) {
            if (player.hasPermission("group." + group)) {
                System.out.println(group);
                return group;
            }
        }
        return null;
    }
}
