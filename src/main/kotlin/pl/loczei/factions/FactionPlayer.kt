package pl.loczei.factions

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class FactionPlayer(private val uuid: UUID, private var faction: String, private var pendingFaction: String, private val plugin: Plugin) {

    init {
        this.save()
    }

    fun save () {
        val playerFile = File(plugin.dataFolder.toString() + File.separator + "players" + File.separator + uuid.toString() + ".yml")

        if (!playerFile.exists()) playerFile.createNewFile()

        val yml: FileConfiguration = YamlConfiguration.loadConfiguration(playerFile)

        yml.set("uuid", uuid.toString())
        yml.set("faction", faction)
        yml.set("pendingFaction", pendingFaction)

        yml.save(playerFile)
    }

    fun getFaction() : String {
        return faction
    }

    fun setFaction(faction: String) {
        this.faction = faction
        this.save()
    }

    fun getPendingFaction() : String {
        return pendingFaction
    }

    fun setPendingFaction(faction: String) {
        this.pendingFaction = faction
        this.save()
    }

    companion object {
        fun exists(uuid: UUID, plugin: Plugin) : Boolean {
            val playerFile = File(plugin.dataFolder.toString() + File.separator + "players" + File.separator + uuid.toString() + ".yml")

            return playerFile.exists()
        }

        fun load(uuid: UUID, plugin: Plugin) : FactionPlayer {
            val playerFile = File(plugin.dataFolder.toString() + File.separator + "players" + File.separator + uuid.toString() + ".yml")

            if (!playerFile.exists()) throw Throwable("Player doesn't exist!")

            val yml: FileConfiguration = YamlConfiguration.loadConfiguration(playerFile)

            yml.load(playerFile)

            return FactionPlayer(
                UUID.fromString(yml.getString("uuid")),
                yml.getString("faction").toString(),
                yml.getString("pendingFaction").toString(),
                plugin
            )
        }
    }
}