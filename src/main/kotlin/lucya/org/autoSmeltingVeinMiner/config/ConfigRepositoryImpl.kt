package lucya.org.autoSmeltingVeinMiner.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.io.File

@ConfigSerializable
class ConfigRepositoryImpl() : IConfigRepository {
    override val isEnabled: Boolean = true
    override val isSmeltingEnabled: Boolean = true
    override val isAutoDiggingEnabled: Boolean = true
    override val spawnProbability: Double = 0.5
    override val blockDiggingLimit: Int = 27
    override val diggingAreaSize: Int = 3
        get() = if (field % 2 == 0) field + 1 else field
    override val enchantmentKey: String = "auto_smelt_vein_miner"
    override val baseRepairCost: Int = 5
}