package pl.loczei.factions

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class FactionPlayer(private val uuid: UUID, private var faction: String, private var pendingFaction: String) {

    init {
        this.save()
    }

    private fun save () {
        val playerFile = File(FactionsPlugin.instance.dataFolder.toString() + File.separator + "players" + File.separator + uuid.toString() + ".yml")

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

    fun getUUID(): UUID {
        return uuid
    }

    fun accept() {
        if (getPendingFaction() == "") throw Throwable("You don't have pending faction!")
        
        try {
            val faction = Faction.load(getPendingFaction())

            setFaction(faction.getName())
            setPendingFaction("")

            faction.addMember(uuid)
            Bukkit.getPlayer(uuid)!!.sendMessage(ChatColor.BLUE.toString() + "You successfully joined faction " + ChatColor.GREEN.toString() + faction.getName())
        } catch (err: Throwable) {
            setPendingFaction("")

            throw err
        }
    }

    fun leave() {
        if (getFaction() == "Lonely") throw Throwable("You don't have faction!")

        try {
            val faction = Faction.load(getFaction())

            if (faction.getMember(getUUID()).getRank().toInt() == 5) throw Throwable("You must transfer ownership before leave!")
            if (faction.getMembers().size == 1) throw Throwable("If you want leave faction with 1 member you must type /factions delete")

            faction.deleteMember(getUUID())
            setFaction("Lonely")
        } catch (err: Throwable) {
            throw err
        }
    }

    companion object {
        fun exists(uuid: UUID) : Boolean {
            val playerFile = File(FactionsPlugin.instance.dataFolder.toString() + File.separator + "players" + File.separator + uuid.toString() + ".yml")

            return playerFile.exists()
        }

        fun load(uuid: UUID) : FactionPlayer {
            val playerFile = File(FactionsPlugin.instance.dataFolder.toString() + File.separator + "players" + File.separator + uuid.toString() + ".yml")

            if (!playerFile.exists()) throw Throwable("Player doesn't exist!")

            val yml: FileConfiguration = YamlConfiguration.loadConfiguration(playerFile)

            yml.load(playerFile)

            return FactionPlayer(
                UUID.fromString(yml.getString("uuid")),
                yml.getString("faction").toString(),
                yml.getString("pendingFaction").toString()
            )
        }
    }
}