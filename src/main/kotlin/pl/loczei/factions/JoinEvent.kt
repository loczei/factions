package pl.loczei.factions

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinEvent(private val plugin: Plugin) : Listener {

    @EventHandler fun onPlayerJoin (event: PlayerJoinEvent) {
        if (!FactionPlayer.exists(event.player.uniqueId, plugin))
            FactionPlayer(event.player.uniqueId, "Lonely", "", plugin)
    }
}