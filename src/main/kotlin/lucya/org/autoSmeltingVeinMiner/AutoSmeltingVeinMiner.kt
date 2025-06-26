package lucya.org.autoSmeltingVeinMiner
import lucya.org.autoSmeltingVeinMiner.commands.CommandController
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import lucya.org.autoSmeltingVeinMiner.config.ConfigRepositoryImpl
import lucya.org.autoSmeltingVeinMiner.config.IConfigRepository
import lucya.org.autoSmeltingVeinMiner.controlers.AnvilController
import lucya.org.autoSmeltingVeinMiner.controlers.DungeonLootController
import lucya.org.autoSmeltingVeinMiner.controlers.ItemInteractController
import lucya.org.autoSmeltingVeinMiner.data.CustomEnchantment
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import kotlin.io.path.exists

class AutoSmeltingVeinMiner : JavaPlugin() {
    private val customEnchantment = CustomEnchantment()
    private val configPath: Path = dataFolder.toPath().resolve("config.yml")
    private lateinit var config: ConfigRepositoryImpl

    override fun onEnable() {
        loadConfig()
        if (config.isEnabled) {
            Bukkit.getPluginManager().registerEvents(ItemInteractController(customEnchantment, config, this), this)
            Bukkit.getPluginManager().registerEvents(AnvilController(customEnchantment, config, this), this)
            Bukkit.getPluginManager().registerEvents(DungeonLootController(customEnchantment, config, this), this)
            registerCommands()

            logger().info("AutoSmeltingVeinMiner enabled!")
        }
    }

    private fun loadConfig(): IConfigRepository {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        if (!Files.exists(configPath)) {
            val defaultConfig = ConfigRepositoryImpl()
            val loader = YamlConfigurationLoader.builder().path(configPath).build()
            loader.save(loader.createNode().set(ConfigRepositoryImpl::class.java, defaultConfig))
        }

        val loader = YamlConfigurationLoader.builder().path(configPath).build()
        val rootNode = loader.load()
        config = rootNode.get(ConfigRepositoryImpl::class.java) ?: ConfigRepositoryImpl()

        logger().info("Config loaded: $config")
        return config
    }

    private fun registerCommands() {
        val commandController = CommandController(customEnchantment, config, this)
        getCommand("rune")?.setExecutor(commandController)
    }

    override fun onDisable() {
        logger.info("AutoSmeltingVeinMiner disabled!")
    }
}
