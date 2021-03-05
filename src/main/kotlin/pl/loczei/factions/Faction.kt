package pl.loczei.factions

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class Faction {
    private val members: Vector<FactionMember>
    private val name: String

    constructor (name: String, owner: UUID) {
        this.name = name
        this.members = Vector()
        members.add(FactionMember(5, owner))

        this.save()
    }

    constructor (name: String, members: Vector<FactionMember>) {
        this.name = name
        this.members = members
    }

    private fun save() {
        val factionFile = File(FactionsPlugin.instance.dataFolder.toString() + File.separator + "factions" + File.separator + this.name + ".yml")

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

    fun setRank(uuid: UUID, rank: Int) : Int {
        try {
            members[members.indexOf(getMember(uuid))].setRank(rank.toByte())
            save()

            return members[members.indexOf(getMember(uuid))].getRank().toInt()
        } catch (err: Throwable) {
            throw err
        }
    }

    companion object {
        fun exists (name: String) : Boolean {
            return if (name != "") {
                val factionFile = File(FactionsPlugin.instance.dataFolder.toString() + File.separator + "factions" + File.separator + name + ".yml")

                factionFile.exists()
            } else {
                false
            }
        }

        fun load(name: String): Faction {
            val factionFile =
                File(FactionsPlugin.instance.dataFolder.toString() + File.separator + "factions" + File.separator + name + ".yml")

            if (!factionFile.exists()) throw Throwable("Faction doesn't exist!")

            val yml: FileConfiguration = YamlConfiguration.loadConfiguration(factionFile)

            val members: Vector<FactionMember> = Vector()

            yml.getList("members")?.forEach {
                members.add(FactionMember.fromString(it.toString()))
            }

            return Faction(
                yml.getString("name").toString(),
                members
            )
        }
    }
}