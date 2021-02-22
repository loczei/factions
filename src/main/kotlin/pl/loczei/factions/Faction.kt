package pl.loczei.factions

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class Faction {
    private val members: Vector<FactionMember>
    private val name: String
    private val plugin: Plugin

    constructor (name: String, owner: UUID, plugin: Plugin) {
        this.name = name
        this.members = Vector()
        this.plugin = plugin
        members.add(FactionMember(5, owner))

        this.save()
    }

    constructor (name: String, members: Vector<FactionMember>, plugin: Plugin) {
        this.name = name
        this.members = members
        this.plugin = plugin
    }

    private fun save() {
        val factionFile = File(plugin.dataFolder.toString() + File.separator + "factions" + File.separator + this.name + ".yml")

        val yml: FileConfiguration = YamlConfiguration.loadConfiguration(factionFile)

        val membersArray: Vector<String> = Vector()

        members.forEach( fun (e: FactionMember) {
            membersArray.add(e.toString())
        })

        yml.set("name", name)
        yml.set("members", membersArray.toList())

        yml.save(factionFile)
    }

    fun getMember(uuid: UUID) : FactionMember {

        members.forEach {
            if (it.getUUID() == uuid) {
                return it
            }
        }

        throw Throwable("Member doesn't exist!")
    }

    fun getMembers() : Vector<FactionMember> {
        return members
    }

    fun addMember(uuid: UUID) {
        members.add(FactionMember(1, uuid))
        this.save()
    }

    fun deleteMember(uuid: UUID) : Boolean {
        return try {
            members.remove(getMember(uuid))
            save()

            true
        } catch (err: Throwable) {
            false
        }

    }

    fun getName () : String { return name }

    companion object {
        fun exists (name: String, plugin: Plugin) : Boolean {
            val factionFile = File(plugin.dataFolder.toString() + File.separator + "factions" + File.separator + name + ".yml")

            return factionFile.exists()
        }

        fun load(name: String, plugin: Plugin): Faction {
            val factionFile =
                File(plugin.dataFolder.toString() + File.separator + "factions" + File.separator + name + ".yml")

            if (!factionFile.exists()) throw Throwable("Faction doesn't exist!")

            val yml: FileConfiguration = YamlConfiguration.loadConfiguration(factionFile)

            val members: Vector<FactionMember> = Vector()

            yml.getList("members")?.forEach {
                members.add(FactionMember.fromString(it.toString()))
            }

            return Faction(
                yml.getString("name").toString(),
                members,
                plugin
            )
        }
    }
}