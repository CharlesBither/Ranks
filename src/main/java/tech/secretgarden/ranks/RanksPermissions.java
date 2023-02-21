package tech.secretgarden.ranks;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class RanksPermissions {

    Rewards rewards = new Rewards();

    public void checkRank(Player player) {
        int stat = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        int second = stat / 20;
        int minute = second / 60;
        //System.out.println(minute + " minutes played");

        if (player.hasPermission("group.pro")) {
            //if the user has the highest free rank, this is not applicable.
            return;
        }
        if (minute > 60 * 168) {
            rewards.giveRewards("pro", player);
        }
        else if (minute > 60 * 24) {
            rewards.giveRewards("ender", player);
        }
        else if (minute > 600) {
            rewards.giveRewards("gold", player);
        }
        else if (minute > 120) {
            rewards.giveRewards("iron", player);
        }
    }
}
