package pl.loczei.factions

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinEvent : Listener {

    @EventHandler fun onPlayerJoin (event: PlayerJoinEvent) {
        if (!FactionPlayer.exists(event.player.uniqueId))
            FactionPlayer(event.player.uniqueId, "Lonely", "")

        val player = FactionPlayer.load(event.player.uniqueId)

        if (player.getPendingFaction() != "") {
            event.player.sendMessage(ChatColor.BLUE.toString() + "You have been invited to faction " + ChatColor.GREEN.toString() + player.getPendingFaction())
            event.player.sendMessage(ChatColor.AQUA.toString() + "Write /factions accept to accept")
            event.player.sendMessage(ChatColor.DARK_PURPLE.toString() + "Write /factions reject to reject")
        }
    }
}