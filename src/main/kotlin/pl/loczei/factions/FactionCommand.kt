package pl.loczei.factions

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.NumberFormatException

class FactionCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, name: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return false
        }

        if (sender is Player) {
            val player: Player = sender

            when (args[0]) {
                "create" -> {
                    if (args.size == 2) {
                        try {
                            Faction.create(player, args[1])
                        } catch (err: Throwable) {
                            player.sendMessage(ChatColor.RED.toString() + err.message)
                        }
                    } else {
                        player.sendMessage(ChatColor.RED.toString() + "Correct use: /factions create [name of faction]")
                    }
                }

                "invite" -> {
                    if (args.size == 2) {
                        if (Bukkit.getPlayer(args[1]) is Player) {
                            val factionPlayer = FactionPlayer.load(player.uniqueId)

                            if (factionPlayer.getFaction() != "Lonely") {
                                val faction = Faction.load(factionPlayer.getFaction())

                                try {
                                    faction.invite(player, Bukkit.getPlayer(args[1])!!)
                                } catch (err: Throwable) {
                                    player.sendMessage(ChatColor.RED.toString() + err.message)
                                }
                            } else {
                                player.sendMessage(ChatColor.RED.toString() + "You must be in the faction!")
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
                    try {
                        factionPlayer.accept()
                    } catch (err: Throwable) {
                        player.sendMessage(ChatColor.RED.toString() + err.message)
                    }
                }

                "reject" -> {
                    FactionPlayer.load(player.uniqueId).setPendingFaction("")
                }

                "kick" -> {
                    if (args.size == 2) {
                        val factionPlayer = FactionPlayer.load(player.uniqueId)

                        if (factionPlayer.getFaction() != "Lonely") {
                            try {
                                val faction = Faction.load(factionPlayer.getFaction())

                                if (Bukkit.getPlayer(args[1]) is Player) {
                                    faction.kick(player, faction.getMember(Bukkit.getPlayer(args[1])!!.uniqueId))
                                } else {
                                    faction.kick(player, faction.getMemberByName(args[1]))
                                }
                            } catch (err: Throwable) {
                                player.sendMessage(ChatColor.RED.toString() + err.message)
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED.toString() + "Correct use: /faction kick [name of kicked player]")
                    }
                }

                "setrank" -> {
                    if (args.size == 3) {
                        if (FactionPlayer.load(player.uniqueId).getFaction() != "Lonely") {
                            try {
                                val faction = Faction.load(FactionPlayer.load(player.uniqueId).getFaction())

                                if (Bukkit.getPlayer(args[1]) is Player) {
                                    faction.setRank(
                                        faction.getMember(player.uniqueId),
                                        faction.getMember(Bukkit.getPlayer(args[1])!!.uniqueId),
                                        args[2].toInt()
                                    )
                                } else {
                                    faction.setRank(
                                        faction.getMember(player.uniqueId),
                                        faction.getMemberByName(args[1]),
                                        args[2].toInt()
                                    )
                                }
                            } catch (err: Throwable) {
                                player.sendMessage(ChatColor.RED.toString() + err.message)
                            } catch (err: NumberFormatException) {
                                player.sendMessage(ChatColor.RED.toString() + "Correct use: /factions setrank [name] [rank]")
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED.toString() + "Correct use: /factions setrank [name] [rank]")
                    }
                }

                "leave" -> {
                    val factionPlayer = FactionPlayer.load(player.uniqueId)

                    try {
                        factionPlayer.leave()
                    } catch (err: Throwable) {
                        player.sendMessage(ChatColor.RED.toString() + err.message)
                    }
                }

                "transferownership" -> {
                    if (args.size == 2) {
                        val factionPlayer = FactionPlayer.load(player.uniqueId)

                        if (factionPlayer.getFaction() != "Lonely") {
                            try {
                                val faction = Faction.load(factionPlayer.getFaction())

                                if (Bukkit.getPlayer(args[1]) is Player) {
                                    faction.transferownership(faction.getMember(player.uniqueId), faction.getMember(Bukkit.getPlayer(args[1])!!.uniqueId))
                                } else {
                                    faction.transferownership(faction.getMember(player.uniqueId), faction.getMemberByName(args[1]))
                                }
                            } catch (err: Throwable) {
                                player.sendMessage(ChatColor.RED.toString() + err.message)
                            }
                        } else {
                            player.sendMessage(ChatColor.RED.toString() + "You don't have faction!")
                        }
                    } else {
                        player.sendMessage(ChatColor.RED.toString() + "Correct use: /faction transferownership [new owner]")
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

        if (args[0] == "info") {
            if (args.size == 2) {
                try {
                    val faction = Faction.load(args[1])

                    faction.info(sender)
                } catch (err: Throwable) {
                    sender.sendMessage(ChatColor.RED.toString() + err.message)
                }
            } else {
                sender.sendMessage(ChatColor.RED.toString() + "Correct use: /factions info [name of faction]")
            }
        }
        return true
    }
}