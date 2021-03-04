package pl.loczei.factions

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class FactionsPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: FactionsPlugin
            private set
    }

    override fun onEnable() {
        Bukkit.getServer().pluginManager.registerEvents(JoinEvent(this), this)

        getCommand("factions")?.setExecutor(FactionCommand())

        instance = this

        //creating folders if doesn't exists

        if (!dataFolder.exists()) dataFolder.mkdir()


        val dir1 = File(dataFolder.toString() + File.separator + "players"  + File.separator)

        if (!dir1.exists()) dir1.mkdir()


        val dir2 = File(dataFolder.toString() + File.separator + "factions"  + File.separator)

        if (!dir2.exists()) dir2.mkdir()

    }
}