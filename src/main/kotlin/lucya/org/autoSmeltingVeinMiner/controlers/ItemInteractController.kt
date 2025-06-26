package lucya.org.autoSmeltingVeinMiner.controlers

import lucya.org.autoSmeltingVeinMiner.config.IConfigRepository
import lucya.org.autoSmeltingVeinMiner.data.CustomEnchantment
import lucya.org.autoSmeltingVeinMiner.managers.enchantItem
import lucya.org.autoSmeltingVeinMiner.managers.hasCustomEnchantment
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import kotlin.math.absoluteValue

class ItemInteractController(
    private val customEnchantment: CustomEnchantment,
    private val config: IConfigRepository,
    private val plugin: Plugin
) : Listener {
//    @EventHandler
//    fun onBlockBreak(event: BlockBreakEvent) {
//        val player = event.player
//        val block = event.block
//        val item = player.inventory.itemInMainHand
//        if (player.gameMode != GameMode.SURVIVAL)
//            return
//        if (!hasCustomEnchantment(item))
//            return
//
//        val eyeLocation = player.eyeLocation
//        val lookingBasis: Vector = roundDirectionToBasis(eyeLocation.direction)
//        val (u,v) = getOrthogonalVectors(lookingBasis)
//        for (i in -1..1) {
//            for (j in -1..1) {
//                val offsetX = i * u.x + j * v.x
//                val offsetY = i * u.y + j * v.y
//                val offsetZ = i * u.z + j * v.z
//
//                val targetBlockLocation = block.location.clone().add(offsetX, offsetY, offsetZ)
//                spawnParticlesAtLocation(targetBlockLocation)
//                targetBlockLocation.block.breakNaturally(item)
//            }
//        }
//    }
//
//    private fun getOrthogonalVectors(basis: Vector): Pair<Vector, Vector> {
//        val arbitrary = Vector(basis.y, basis.z, basis.x).normalize()
//        val u = basis.clone().crossProduct(arbitrary).normalize()
//        return Pair(u, arbitrary)
//    }
//
//    private fun roundDirectionToBasis(direction: Vector): Vector {
//        val x = direction.x
//        val y = direction.y
//        val z = direction.z
//
//        val absX = abs(x)
//        val absY = abs(y)
//        val absZ = abs(z)
//
//        return when {
//            absX >= absY && absX >= absZ -> Vector(absX, 0.0, 0.0)
//            absY >= absX && absY >= absZ -> Vector(0.0, absY, 0.0)
//            else -> Vector(0.0, 0.0, absZ)
//        }
//    }

    private val diggingRadius = (config.diggingAreaSize - 1) / 2

    private val maxBlockCount = config.blockDiggingLimit

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val item = player.inventory.itemInMainHand
        if (item.type == Material.ENCHANTED_BOOK)
            return
        val key = NamespacedKey(plugin, config.enchantmentKey)
        if (!hasCustomEnchantment(item, key) || !isOre(block)) return

        if (config.isAutoDiggingEnabled) {
            handleAutoDigging(block, item, player)
        } else {
            handleSingleBlockDigging(block, item, player)
        }
    }

    private fun handleAutoDigging(startBlock: Block, item: ItemStack, player: Player) {
        val visited = mutableSetOf<Block>()
        val blocksToBreak = findOreCluster(startBlock, visited)
            .take(maxBlockCount)

        for (oreBlock in blocksToBreak) {
            if (config.isSmeltingEnabled) {
                smeltAndBreak(oreBlock, item, player)
            } else {
                breakNaturally(oreBlock, item)
            }
        }
    }

    private fun handleSingleBlockDigging(block: Block, item: ItemStack, player: Player) {
        if (config.isSmeltingEnabled) {
            smeltAndBreak(block, item, player)
        } else {
            breakNaturally(block, item)
        }
    }

    private fun smeltAndBreak(block: Block, item: ItemStack, player: Player) {
        val drop = getSmeltedDrop(block.type)
        if (drop != null) {
            val fortuneLevel = item.enchantments[Enchantment.LOOT_BONUS_BLOCKS] ?: 0
            val dropsCount = calculateFortuneDrops(fortuneLevel)

            val inventory = player.inventory
            for (i in 1..dropsCount) {
                val leftover = inventory.addItem(ItemStack(drop))
                leftover.values.forEach {
                    block.world.dropItemNaturally(block.location, it)
                }
            }

            block.type = Material.AIR
        } else {
            breakNaturally(block, item)
        }
        spawnParticlesAtLocation(block.location)
    }

    private fun breakNaturally(block: Block, item: ItemStack) {
        block.breakNaturally(item)
        spawnParticlesAtLocation(block.location)
    }

    private fun calculateFortuneDrops(fortuneLevel: Int): Int {
        if (fortuneLevel == 0) return 1

        val multiplier = (1..fortuneLevel + 1).random()
        return multiplier
    }

    fun isOre(block: Block): Boolean {
        return block.type in listOf(
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.NETHER_QUARTZ_ORE
        )
    }

    fun getSmeltedDrop(oreType: Material): Material? {
        return when (oreType) {
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE -> Material.IRON_INGOT
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE -> Material.GOLD_INGOT
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE -> Material.COPPER_INGOT
            else -> null
        }
    }

    fun findOreCluster(startBlock: Block, visited: MutableSet<Block>): List<Block> {
        val blocksToBreak = mutableListOf<Block>()

        val queue = ArrayDeque<Block>()
        queue.add(startBlock)
        val startLocation = startBlock.location

        while (queue.isNotEmpty()) {
            val currentBlock = queue.removeFirst()
            if (currentBlock in visited) continue

            visited.add(currentBlock)
            blocksToBreak.add(currentBlock)

            val neighbors = getNeighbors(currentBlock)
            for (neighbor in neighbors) {
                if (neighbor.type == startBlock.type && neighbor !in visited && isWithinDistance(startLocation, neighbor.location)) {
                    queue.add(neighbor)
                }
            }
        }
        return blocksToBreak
    }

    fun getNeighbors(block: Block): List<Block> {
        val neighbors = mutableListOf<Block>()
        val baseLocation = block.location

        val offsets = listOf(
            Triple(1, 0, 0),
            Triple(-1, 0, 0),
            Triple(0, 1, 0),
            Triple(0, -1, 0),
            Triple(0, 0, 1),
            Triple(0, 0, -1)
        )

        for ((dx, dy, dz) in offsets) {
            val neighborLocation = baseLocation.clone().add(dx.toDouble(), dy.toDouble(), dz.toDouble())
            neighbors.add(neighborLocation.block)
        }

        return neighbors
    }

    fun isWithinDistance(startLocation: Location, targetLocation: Location): Boolean {
        return (startLocation.blockX - targetLocation.blockX).absoluteValue <= diggingRadius &&
                (startLocation.blockY - targetLocation.blockY).absoluteValue <= diggingRadius &&
                (startLocation.blockZ - targetLocation.blockZ).absoluteValue <= diggingRadius
    }

    private fun spawnParticlesAtLocation(location: Location) {
        location.world?.spawnParticle(
            Particle.FLAME,
            location,
            10,
            0.0, 0.0, 0.0,
            0.01
        )
    }

}