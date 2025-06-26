package lucya.org.autoSmeltingVeinMiner.managers

import lucya.org.autoSmeltingVeinMiner.data.CustomEnchantment
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.persistence.PersistentDataType

fun hasCustomEnchantment(item: ItemStack, enchantmentKey: NamespacedKey): Boolean {
    val meta = item.itemMeta ?: return false
    return meta.persistentDataContainer.has(enchantmentKey, PersistentDataType.INTEGER)
}

fun enchantItem(item: ItemStack, customEnchantment: CustomEnchantment, enchantmentKey: NamespacedKey): ItemStack {
    val tempItem = item.clone()
    val meta = tempItem.itemMeta

    meta.persistentDataContainer.set(enchantmentKey, PersistentDataType.INTEGER, 1)

    val lore = meta.lore()?.toMutableList() ?: mutableListOf()
    lore.add(Component.text(customEnchantment.name).color(NamedTextColor.GOLD))
    meta.lore(lore)

    meta.addEnchant(customEnchantment, 1, true)
    tempItem.itemMeta = meta

    return tempItem
}

fun getEnchantedBook (customEnchantment: CustomEnchantment, enchantmentKey: NamespacedKey): ItemStack {
    val book = enchantItem(ItemStack(Material.ENCHANTED_BOOK), customEnchantment, enchantmentKey)
    val bookMeta = book.itemMeta

    if (bookMeta != null) {
        val lore = bookMeta.lore()?.toMutableList() ?: mutableListOf()
        lore.add(Component.text("Ancient Rune infused with smelting power...").color(NamedTextColor.GOLD))
        bookMeta.lore(lore)
        book.itemMeta = bookMeta
    }
    return book
}