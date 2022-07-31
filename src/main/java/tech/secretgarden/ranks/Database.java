package tech.secretgarden.ranks;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.ArrayList;

public class Database {
    private static final ArrayList<String> list = Ranks.dbList;
    public static HikariDataSource pool;

    public static void connect() throws SQLException {

        pool = new HikariDataSource();
        pool.setDriverClassName("com.mysql.jdbc.Driver");
        pool.setJdbcUrl("jdbc:mysql://" + list.get(0) + ":" + list.get(1) + "/" + list.get(2) + "?useSSL=true&autoReconnect=true");
        pool.setUsername(list.get(3));
        pool.setPassword(list.get(4));
    }

    public static boolean isConnected() {
        return pool != null;
    }

    public HikariDataSource getPool() {
        return pool;
    }

    public void disconnect() {
        if (isConnected()) {
            pool.close();
        }
    }

    private static final ArrayList<String> droplet = Ranks.dropletList;
    public static HikariDataSource dropletPool;

    public static void dropletConnect() throws SQLException {

        dropletPool = new HikariDataSource();
        dropletPool.setDriverClassName("com.mysql.jdbc.Driver");
        dropletPool.setJdbcUrl("jdbc:mysql://" + droplet.get(0) + ":" + droplet.get(1) + "/" + droplet.get(2) + "?useSSL=true&autoReconnect=true");
        dropletPool.setUsername(droplet.get(3));
        dropletPool.setPassword(droplet.get(4));
    }

    public static boolean dropletIsConnected() { return dropletPool != null; }

    public HikariDataSource getDropletPool() { return dropletPool; }

    public void dropletDisconnect() {
        if (dropletIsConnected()) {
            dropletPool.close();
        }
    }
}
