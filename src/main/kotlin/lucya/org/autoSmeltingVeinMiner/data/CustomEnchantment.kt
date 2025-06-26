package lucya.org.autoSmeltingVeinMiner.data

import io.papermc.paper.enchantments.EnchantmentRarity
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.EntityCategory
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class CustomEnchantment() : Enchantment() {

    @Deprecated("Deprecated in Java")
    override fun getName(): String = "Rune: VeinSmelt"

    override fun getMaxLevel(): Int = 1

    override fun getStartLevel(): Int = 1

    override fun getItemTarget(): EnchantmentTarget = EnchantmentTarget.TOOL

    override fun isTreasure(): Boolean = false

    override fun isCursed(): Boolean = false

    override fun conflictsWith(other: Enchantment): Boolean {
        return false
    }

    override fun canEnchantItem(item: ItemStack): Boolean {
        return item.type.toString().endsWith("_PICKAXE")
    }

    override fun displayName(level: Int): Component {
        return Component.text("$name Level $level")
    }

    override fun isTradeable(): Boolean = true

    override fun isDiscoverable(): Boolean = true

    override fun getMinModifiedCost(level: Int): Int = level * 5

    override fun getMaxModifiedCost(level: Int): Int = level * 10

    override fun getRarity(): EnchantmentRarity = EnchantmentRarity.COMMON

    override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = 0.0f

    override fun getActiveSlots(): Set<EquipmentSlot> = setOf(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND)

    override fun getKey(): NamespacedKey = NamespacedKey.minecraft("auto_smelt_vein_miner")

    override fun translationKey(): String = "enchantment.custom.auto_smelt_vein_miner"

    @Suppress("DEPRECATION")
    override fun getTranslationKey(): String = translationKey()
}
