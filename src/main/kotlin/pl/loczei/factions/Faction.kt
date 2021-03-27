package pl.loczei.factions

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import pl.loczei.factions.functions.removeFromInventory
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

    fun getMemberByName(name: String): FactionMember {
        members.forEach {
            if (Bukkit.getOfflinePlayer(it.getUUID()).name == name) {
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

    fun deleteMember(uuid: UUID) {
        try {
            members.remove(getMember(uuid))

            save()
        } catch (err: Throwable) {
            throw err
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

    fun delete() {
        if (members.size != 1) throw Throwable("You must be alone in faction!")

        val factionFile =
                File(FactionsPlugin.instance.dataFolder.toString() + File.separator + "factions" + File.separator + name + ".yml")

        factionFile.delete()

        FactionPlayer.load(members[0].getUUID()).setFaction("Lonely")
        Bukkit.getPlayer(members[0].getUUID())!!.sendMessage(ChatColor.AQUA.toString() + "You have successfully deleted faction " + ChatColor.GREEN.toString() + name)
    }

    fun invite(inviting: Player, invited: Player) {
        if (getMember(inviting.uniqueId).getRank() !in 3..5) throw Throwable("Your rank in faction is too small!")

        try {
            val invitedFP = FactionPlayer.load(invited.uniqueId)
            if (invitedFP.getFaction() != "Lonely") throw Throwable("Invited is in faction!")
            if (invitedFP.getPendingFaction() != "") throw Throwable("Invited has a pending invitation!")

            invitedFP.setPendingFaction(name)

            invited.sendMessage(ChatColor.GREEN.toString() + inviting.name + ChatColor.BLUE.toString() + " invites you to faction " + ChatColor.GREEN.toString() + getName())
            invited.sendMessage(ChatColor.AQUA.toString() + "Write /factions accept to accept")
            invited.sendMessage(ChatColor.DARK_PURPLE.toString() + "Write /factions reject to reject")


        } catch (err: Throwable) {
            throw err
        }
    }

    fun info(player: Player) {
        player.sendMessage(ChatColor.AQUA.toString() + "Faction: " + ChatColor.GREEN.toString() + getName())

        getMembers().forEachIndexed { index, it ->
            var message: String = (index + 1).toString() + ". "

            when(it.getRank().toInt()) {
                1 -> {
                    message += ChatColor.GRAY.toString() + "Recruit"
                }

                2 -> {
                    message += ChatColor.BLUE.toString() + "Member"
                }

                3 -> {
                    message += ChatColor.AQUA.toString() + "Mod" //temp
                }

                4 -> {
                    message += ChatColor.GREEN.toString() + "Admin" //temp
                }

                5 -> {
                    message += ChatColor.LIGHT_PURPLE.toString() + "Owner"
                }
            }

            message += ChatColor.GRAY.toString()

            message += if (Bukkit.getPlayer(it.getUUID()) is Player) {
                " " + Bukkit.getPlayer(it.getUUID())!!.name
            } else {
                " " + Bukkit.getOfflinePlayer(it.getUUID()).name
            }
            player.sendMessage(message)
        }
    }

    fun kick(kicker: Player, kicked: FactionMember) {
        try {
            if (getMember(kicker.uniqueId).getRank() !in 3..5) throw Throwable("Your rank in faction is too small!")
        } catch (err: Throwable) { throw err }

        if (getMember(kicker.uniqueId).getRank() < kicked.getRank()) throw Throwable("Your rank in faction is too small!")

        members.remove(kicked)

        FactionPlayer.load(kicked.getUUID()).setFaction("Lonely")

        if (Bukkit.getPlayer(kicked.getUUID()) is Player) {
            Bukkit.getPlayer(kicked.getUUID())!!.sendMessage(ChatColor.DARK_PURPLE.toString() + "You are kicked from faction " + ChatColor.GREEN.toString() + getName())
        }
    }

    fun transferownership(old: FactionMember, new: FactionMember) {
        if (old.getRank().toInt() != 5) throw Throwable("You aren't owner!")

        setRank(new.getUUID(), 5)
        setRank(old.getUUID(), 4)
    }

    fun setRank(giver: FactionMember, getter: FactionMember, rank: Int) {
        if (giver.getRank() < getter.getRank() || giver.getRank() !in 3..5 || giver.getRank() < rank)
            throw Throwable("Your rank is too small!")

        members[members.indexOf(getter)].setRank(rank.toByte())
    }

    companion object {
        fun exist (name: String) : Boolean {
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

        fun create(player: Player, name: String): Faction {
            if (name == "Lonely") throw Throwable("D:")
            if (exist(name)) throw Throwable("Faction with this name exist!")
            if (name.length !in 4..12) throw Throwable("Name of faction must be between 4 and 12 chars!")
            if (!player.inventory.containsAtLeast(ItemStack(Material.DIAMOND), 32))
                throw Throwable("You don't have 32 diamonds!")

            val factionPlayer = FactionPlayer.load(player.uniqueId)

            if (factionPlayer.getFaction() != "Lonely") throw Throwable("You have faction!")

            removeFromInventory(player, ItemStack(Material.DIAMOND), 32)
            factionPlayer.setFaction(name)

            player.sendMessage(ChatColor.AQUA.toString() + "Successfully created faction " + ChatColor.GREEN.toString() + name + ChatColor.AQUA.toString() +"!")

            Bukkit.broadcastMessage(ChatColor.AQUA.toString() + "Player " + ChatColor.GREEN.toString() + player.name + ChatColor.AQUA.toString() + " created faction " + ChatColor.GREEN.toString() + name)

            return Faction(
                name,
                player.uniqueId
            )
        }
    }
}