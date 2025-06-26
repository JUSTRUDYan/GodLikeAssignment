package lucya.org.autoSmeltingVeinMiner.config

interface IConfigRepository {
    val isEnabled: Boolean
    val isSmeltingEnabled: Boolean
    val isAutoDiggingEnabled: Boolean
    val spawnProbability: Double
    val blockDiggingLimit: Int
    val diggingAreaSize: Int
    val enchantmentKey: String
    val baseRepairCost: Int
}