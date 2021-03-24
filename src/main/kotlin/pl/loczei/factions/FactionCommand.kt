package pl.loczei.factions

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.NumberFormatException

class FactionCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, name: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender

            when (args[0]) {
                "create" -> {
                    if (args.size == 2) {
                        if (player.inventory.containsAtLeast(ItemStack(Material.DIAMOND), 32)) {
                            val factionPlayer = FactionPlayer.load(player.uniqueId)

                            if (factionPlayer.getFaction() == "Lonely") {
                                if (!Faction.exists(args[1])) {
                                    if (args[1].length in 4..16) {
                                        var i = 0
                                        var toGet = 32
                                        for (item in player.inventory) {
                                            if (item is ItemStack) {
                                                if (item.isSimilar(ItemStack(Material.DIAMOND))) {
                                                    if (item.amount == toGet) {
                                                        player.inventory.setItem(i, ItemStack(Material.AIR))
                                                        break
                                                    } else if (item.amount > toGet){
                                                        player.inventory.setItem(i, ItemStack(Material.DIAMOND, item.amount - toGet))
                                                        break
                                                    } else if (item.amount < toGet) {
                                                        toGet -= item.amount
                                                        player.inventory.setItem(i, ItemStack(Material.AIR))
                                                    }
                                                }
                                            }
                                            i++
                                        }

                                        Faction(args[1], player.uniqueId)
                                        factionPlayer.setFaction(args[1])

                                        player.sendMessage(ChatColor.BLUE.toString() + "Successfully created faction " + ChatColor.GREEN.toString() + args[1] + ChatColor.BLUE.toString() +"!")

                                        Bukkit.broadcastMessage(ChatColor.BLUE.toString() + "Player " + ChatColor.GREEN.toString() + player.name + ChatColor.BLUE.toString() + " created faction " + ChatColor.GREEN.toString() + args[1])
                                    } else {
                                        player.sendMessage(ChatColor.RED.toString() + "Faction name length must be between 5 and 15")
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED.toString() + "Faction with this name exist!")
                                }
                            } else {
                                player.sendMessage(ChatColor.RED.toString() + "You have faction!")
                            }
                        } else {
                            player.sendMessage(ChatColor.RED.toString() + "You don't have 32 diamonds!")
                        }
                    } else {
                        player.sendMessage(ChatColor.RED.toString() + "Too few arguments")
                    }
                }

                "add" -> {
                    if (args.size == 2) {
                        if (Bukkit.getPlayer(args[1]) is Player) {
                            if (Bukkit.getPlayer(args[1])?.let { FactionPlayer.exists(it.uniqueId) } == true) {
                                val inviting: FactionPlayer = FactionPlayer.load(player.uniqueId)

                                if (inviting.getFaction() != "Lonely") {
                                    val faction = Faction.load(inviting.getFaction())

                                    if (faction.getMember(player.uniqueId).getRank() in 2..6) {
                                        val invited: FactionPlayer = FactionPlayer.load(Bukkit.getPlayer(args[1])!!.uniqueId)
                                        val invitedPlayer: Player = Bukkit.getPlayer(args[1])!!

                                        if (invited.getPendingFaction() == "") {
                                            if (invited.getFaction() == "Lonely") {
                                                invited.setPendingFaction(inviting.getFaction())

                                                if (invitedPlayer.isOnline) {
                                                    invitedPlayer.sendMessage(ChatColor.GREEN.toString() + player.name + ChatColor.BLUE.toString() + " invites you to faction " + ChatColor.GREEN.toString() + faction.getName())
                                                    invitedPlayer.sendMessage(ChatColor.AQUA.toString() + "Write /factions accept to accept")
                                                    invitedPlayer.sendMessage(ChatColor.DARK_PURPLE.toString() + "Write /factions reject to reject")
                                                }
                                            } else {
                                                player.sendMessage(ChatColor.RED.toString() + "Invited player have faction!")
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED.toString() + "Invited player have pending faction!")
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED.toString() + "Your rank in faction is to small!")
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED.toString() + "You must have faction!")
                                }
                            } else {
                                player.sendMessage(ChatColor.RED.toString() + "Player doesn't exist!")
                            }
                        } else {
                            player.sendMessage(ChatColor.RED.toString() + "Player must be online")
                        }
                    } else {
                        player.sendMessage(ChatColor.RED.toString() + "Too few arguments")
                    }
                }

                "accept" -> {
                    val factionPlayer = FactionPlayer.load(player.uniqueId)
                    if (factionPlayer.getPendingFaction() != "") {
                        try {
                            val faction = Faction.load(factionPlayer.getPendingFaction())

                            factionPlayer.setFaction(faction.getName())
                            factionPlayer.setPendingFaction("")

                            faction.addMember(player.uniqueId)
                            player.sendMessage(ChatColor.BLUE.toString() + "You successfully joined faction " + ChatColor.GREEN.toString() + faction.getName())
                        } catch (err: Throwable) {
                            factionPlayer.setPendingFaction("")

                            player.sendMessage(ChatColor.RED.toString() + "Faction doesn't exist!")
                        }
                    }
                }

                "reject" -> {
                    FactionPlayer.load(player.uniqueId).setPendingFaction("")
                }

                "info" -> {
                    if (args.size == 2 && Faction.exists(args[1])) {
                        val faction = Faction.load(args[1])

                        player.sendMessage(ChatColor.BLUE.toString() + "Faction: " + ChatColor.GREEN.toString() + faction.getName())

                        faction.getMembers().forEachIndexed { index, it ->
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

                            message += if (Bukkit.getPlayer(it.getUUID()) is Player) {
                                " " + Bukkit.getPlayer(it.getUUID())!!.name
                            } else {
                                " " + Bukkit.getOfflinePlayer(it.getUUID()).name
                            }
                            player.sendMessage(message)
                        }
                    } else {
                        player.sendMessage(ChatColor.RED.toString() + "Faction doesn't exist!")
                    }
                }

                "kick" -> {
                    if (args.size == 2) {
                        val factionPlayer = FactionPlayer.load(player.uniqueId)

                        if (factionPlayer.getFaction() != "Lonely") {
                            try {
                                val faction = Faction.load(factionPlayer.getFaction())

                                if (faction.getMember(player.uniqueId).getRank() in 2..6 ) {
                                    if (Bukkit.getPlayer(args[1]) is Player) {
                                        val kickedPlayer: Player = Bukkit.getPlayer(args[1])!!

                                        if (faction.getMember(kickedPlayer.uniqueId).getRank() < faction.getMember(player.uniqueId).getRank()) {
                                            if(faction.deleteMember(kickedPlayer.uniqueId)) {
                                                val kicked = FactionPlayer.load(kickedPlayer.uniqueId)

                                                kicked.setFaction("Lonely")
                                                if (kickedPlayer.isOnline) {
                                                    kickedPlayer.sendMessage(ChatColor.DARK_PURPLE.toString() + "You are kicked from faction " + ChatColor.GREEN.toString() + faction.getName())
                                                }
                                            } else {
                                                player.sendMessage(ChatColor.RED.toString() + "This player isn't in your faction!")
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED.toString() + "Your rank is to small to kick this member!")
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED.toString() + "Player doesn't exist!")
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED.toString() + "Your rank is to small to kick anybody from the faction!")
                                }
                            } catch (err: Throwable) {
                                player.sendMessage(ChatColor.RED.toString() + err.message)
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED.toString() + "Too few arguments")
                    }
                }

                "setrank" -> {
                    if (args.size == 3) {
                        if (Bukkit.getPlayer(args[1]) is Player) {
                            val sPlayer: Player = Bukkit.getPlayer(args[1])!!

                            if (FactionPlayer.load(player.uniqueId).getFaction() != "Lonely") {
                                val faction = Faction.load(FactionPlayer.load(player.uniqueId).getFaction())

                                try {
                                    if (faction.getMember(player.uniqueId).getRank() > faction.getMember(sPlayer.uniqueId).getRank()) {
                                        if (faction.getMember(player.uniqueId).getRank() in 3..6) {
                                            try {
                                                val rank = args[2].toInt()

                                                if (faction.getMember(player.uniqueId).getRank() > rank) {
                                                    player.sendMessage(
                                                        ChatColor.BLUE.toString() + "You have successfully set rank of member " + ChatColor.GREEN.toString() + sPlayer.name + ChatColor.BLUE.toString() + " to " + ChatColor.GREEN.toString() + faction.setRank(sPlayer.uniqueId, rank)
                                                    )
                                                } else {
                                                    player.sendMessage(ChatColor.RED.toString() + "You can only set rank of a member with lower rank!")
                                                }
                                            } catch (err: NumberFormatException) {
                                                player.sendMessage(ChatColor.RED.toString() + "Rank must be a Integer!")
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED.toString() + "Your rank is to small!")
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED.toString() + "Your rank is to small!")
                                    }
                                } catch (err: Throwable) {
                                    player.sendMessage(ChatColor.RED.toString() + err.message)
                                }
                            } else {
                                player.sendMessage(ChatColor.RED.toString() + "You must have a faction!")
                            }
                        } else {
                            player.sendMessage(ChatColor.RED.toString() + "Player doesn't exist!")
                        }
                    } else {
                        player.sendMessage(ChatColor.RED.toString() + "Too few arguments")
                    }
                }

                "leave" -> {
                    val factionPlayer = FactionPlayer.load(player.uniqueId)

                    if (factionPlayer.getFaction() != "Lonely") {
                        val faction = Faction.load(factionPlayer.getFaction())

                        if (faction.getMember(player.uniqueId).getRank() < 5) {
                            faction.deleteMember(player.uniqueId)
                            factionPlayer.setFaction("Lonely")
                        } else {
                            player.sendMessage(ChatColor.RED.toString() + "You must transfer ownership!")
                        }
                    } else {
                        player.sendMessage(ChatColor.RED.toString() + "You must be in faction!")
                    }
                }

                "transferownership" -> {
                    if (args.size == 2) {
                        val factionPlayer = FactionPlayer.load(player.uniqueId)

                        if (factionPlayer.getFaction() != "Lonely") {
                            val faction = Faction.load(factionPlayer.getFaction())

                            if (faction.getMember(player.uniqueId).getRank().toInt() == 5) {
                                if (Bukkit.getPlayer(args[1]) is Player) {
                                    try {
                                        faction.setRank(Bukkit.getPlayer(args[1])!!.uniqueId, 5)
                                        faction.setRank(player.uniqueId, 4)
                                    } catch (err: Throwable) {
                                        player.sendMessage(ChatColor.RED.toString() + err.message)
                                    }
                                }
                            }
                        }
                    }
                }

                "delete" -> {
                    val faction = Faction.load(FactionPlayer.load(player.uniqueId).getFaction())

                    try {
                        faction.delete()
                    } catch (err: Throwable) {
                        player.sendMessage(ChatColor.RED.toString() + err.message)
                    }
                }
            }
        }
        return true
    }
}