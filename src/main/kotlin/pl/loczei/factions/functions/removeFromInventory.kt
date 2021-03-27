package pl.loczei.factions.functions

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun removeFromInventory(player: Player, itemStack: ItemStack, amount: Int = 1): Boolean {
    var toGet = amount
    for ((i, item) in player.inventory.withIndex()) {
        if (item is ItemStack) {
            if (item.isSimilar(itemStack)) {
                if (item.amount == toGet) {
                    player.inventory.setItem(i, ItemStack(Material.AIR))
                    return true
                } else if (item.amount > toGet) {
                    player.inventory.getItem(i)!!.amount = player.inventory.getItem(i)!!.amount - toGet
                    return true
                } else if (item.amount < toGet) {
                    toGet -= item.amount
                    player.inventory.setItem(i, ItemStack(Material.AIR))
                }
            }
        }
    }
    return false
}