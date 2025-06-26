package lucya.org.autoSmeltingVeinMiner.controlers

import lucya.org.autoSmeltingVeinMiner.config.IConfigRepository
import lucya.org.autoSmeltingVeinMiner.data.CustomEnchantment
import lucya.org.autoSmeltingVeinMiner.managers.enchantItem
import lucya.org.autoSmeltingVeinMiner.managers.getEnchantedBook
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.LootTables
import org.bukkit.plugin.Plugin

class DungeonLootController(
    private val customEnchantment: CustomEnchantment,
    private val config: IConfigRepository,
    private val plugin: Plugin,
) : Listener {
    private val enchantmentKey = NamespacedKey(plugin, config.enchantmentKey)
    private val customBook = getEnchantedBook(customEnchantment, enchantmentKey)

    @EventHandler
    fun onLootGenerate(event: LootGenerateEvent) {
        val lootTableKey = event.lootTable.key
        if (lootTableKey != LootTables.NETHER_BRIDGE.key && lootTableKey != LootTables.SIMPLE_DUNGEON.key) {
            return
        }

        if (Math.random() < config.spawnProbability) {
            event.loot.add(customBook)
        }
    }
}