package tech.secretgarden.ranks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventListener implements Listener {

    RanksPermissions ranksPermissions = new RanksPermissions();
    Database database = new Database();

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM ranks WHERE uuid = ?")) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet rs = statement.executeQuery();
            System.out.println("finding player in local");
            if (!rs.next()) {
                System.out.println("did not find local");
                //insert the new user into the table
                initLocalUser(player);
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }

        try (Connection connection = database.getDropletPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM ranks WHERE uuid = ?")) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet rs = statement.executeQuery();
            System.out.println("finding player in droplet");
            if (!rs.next()) {
                System.out.println("did not find droplet");
                //insert the new user into the table
                initDropletUser(player);
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }
        // finally, update the player with their rank
        ranksPermissions.checkRank(player);
    }

    private void initLocalUser(Player player) {

        try (Connection connection = database.getPool().getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO ranks (uuid, gamertag, rank_name) VALUES (?,?,?);")) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.setString(3, "null");
            statement.executeUpdate();
            System.out.println("inserted into local");

        } catch (SQLException x) {
            x.printStackTrace();
        }
    }
    private void initDropletUser(Player player) {

        try (Connection connection = database.getDropletPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO ranks (uuid, gamertag, rank_name) VALUES (?,?,?);")) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.setString(3, "null");
            statement.executeUpdate();
            System.out.println("inserted into Droplet");

        } catch (SQLException x) {
            x.printStackTrace();
        }
    }
}
