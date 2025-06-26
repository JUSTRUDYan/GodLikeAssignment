package lucya.org.autoSmeltingVeinMiner.controlers

import lucya.org.autoSmeltingVeinMiner.config.IConfigRepository
import lucya.org.autoSmeltingVeinMiner.data.CustomEnchantment
import lucya.org.autoSmeltingVeinMiner.managers.enchantItem
import lucya.org.autoSmeltingVeinMiner.managers.hasCustomEnchantment
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.plugin.Plugin

class AnvilController(
    private val customEnchantment: CustomEnchantment,
    private val config: IConfigRepository,
    private val plugin: Plugin,
): Listener {

    @EventHandler
    fun onPrepareAnvil(event: PrepareAnvilEvent) {
        val inventory = event.inventory
        val firstItem = inventory.firstItem
        val secondItem = inventory.secondItem
        val enchantmentKey = NamespacedKey(plugin, config.enchantmentKey)

        if (firstItem == null || secondItem == null) return
        if (secondItem.type != Material.ENCHANTED_BOOK || !hasCustomEnchantment(secondItem, enchantmentKey)) return
        if (hasCustomEnchantment(firstItem, enchantmentKey)) return

        var cost = config.baseRepairCost
        val resultItem = firstItem.clone()
        val meta = resultItem.itemMeta

        if (meta != null && !inventory.renameText.isNullOrEmpty()) {
            meta.displayName(Component.text(inventory.renameText!!).color(NamedTextColor.AQUA))
            resultItem.itemMeta = meta
            cost++
        }

        event.result = enchantItem(resultItem, customEnchantment, enchantmentKey)
        plugin.server.scheduler.runTask(plugin, Runnable {
            inventory.repairCost = cost
        })
    }

}