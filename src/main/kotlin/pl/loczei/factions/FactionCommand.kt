package pl.loczei.factions

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class FactionCommand(private val plugin: Plugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, name: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender

            when (args[0]) {
                "create" -> {
                    if (player.inventory.contains(ItemStack(Material.DIAMOND, 32))) {
                        val factionPlayer = FactionPlayer.load(player.uniqueId, plugin)

                        if (factionPlayer.getFaction() == "Lonely") {
                            if (Faction.exists(args[1], plugin)) {
                                if (args[1].length in 4..16) {
                                    player.inventory.remove(ItemStack(Material.DIAMOND, 32))

                                    Faction(args[1], player.uniqueId, plugin)
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
                }

                "add" -> {
                    if (Bukkit.getPlayer(args[1])?.let { FactionPlayer.exists(it.uniqueId, plugin) } == true) {
                        val inviting: FactionPlayer = FactionPlayer.load(player.uniqueId, plugin)

                        if (inviting.getFaction() != "Lonely") {
                            val faction = Faction.load(inviting.getFaction(), plugin)

                            if (faction.getMember(player.uniqueId).getRank() in 2..6) {
                                val invited: FactionPlayer = FactionPlayer.load(Bukkit.getPlayer(args[1])!!.uniqueId, plugin)
                                val invitedPlayer: Player = Bukkit.getPlayer(args[1])!!

                                if (invited.getPendingFaction() == "") {
                                    invited.setPendingFaction(inviting.getFaction())

                                    //here
                                }
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED.toString() + "Player doesn't exist!")
                    }
                }
            }
        }
        return true
    }
}