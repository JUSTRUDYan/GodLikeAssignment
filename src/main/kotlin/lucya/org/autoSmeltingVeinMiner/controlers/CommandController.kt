package lucya.org.autoSmeltingVeinMiner.commands

import lucya.org.autoSmeltingVeinMiner.config.IConfigRepository
import lucya.org.autoSmeltingVeinMiner.data.CustomEnchantment
import lucya.org.autoSmeltingVeinMiner.managers.getEnchantedBook
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class CommandController(
    private val customEnchantment: CustomEnchantment,
    private val config: IConfigRepository,
    private val plugin: Plugin
) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size != 2) {
            sender.sendMessage(Component.text("Usage: /rune give <player> <type>").color(NamedTextColor.RED))
            return true
        }

        val playerName = args[0]
        val type = args[1].lowercase()

        if (!sender.hasPermission("rune.give")) {
            sender.sendMessage(Component.text("You don't have permission to use this command.").color(NamedTextColor.RED))
            return true
        }

        val target = Bukkit.getPlayerExact(playerName)
        if (target == null || !target.isOnline) {
            sender.sendMessage(Component.text("Player $playerName not found or not online.").color(NamedTextColor.RED))
            return true
        }

        if (type != "veinsmelt") {
            sender.sendMessage(Component.text("Invalid type: $type. Valid types: veinsmelt").color(NamedTextColor.RED))
            return true
        }

        val enchantmentKey = NamespacedKey(plugin, config.enchantmentKey)
        val enchantedBook = getEnchantedBook(customEnchantment, enchantmentKey)

        target.inventory.addItem(enchantedBook)
        target.sendMessage(Component.text("You have received a rune of type: veinsmelt!").color(NamedTextColor.GREEN))
        sender.sendMessage(Component.text("Successfully gave rune to $playerName.").color(NamedTextColor.GREEN))
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        return when (args.size) {
            1 -> Bukkit.getOnlinePlayers()
                .map { it.name }
                .filter { it.startsWith(args[0], ignoreCase = true) }
                .toMutableList()
            2 -> listOf("veinsmelt")
                .filter { it.startsWith(args[1], ignoreCase = true) }
                .toMutableList()
            else -> emptyList<String>().toMutableList()
        }
    }
}
