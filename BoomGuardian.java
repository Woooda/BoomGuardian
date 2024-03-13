package com.example.obsidianexplosion;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ObsidianExplosionPlugin extends JavaPlugin implements Listener {

    private int obsidianStrength;
    private Random random = new Random();

    @Override
    public void onEnable() {
        getLogger().info("ObsidianExplosionPlugin has been enabled!");

        // Загрузка конфигурации при запуске плагина
        loadConfig();

        // Регистрация слушателя событий
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("ObsidianExplosionPlugin has been disabled!");
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (event.getBlock().getType() == Material.OBSIDIAN) {
            // Проверка прочности обсидиана и уменьшение её
            if (obsidianStrength > 0) {
                obsidianStrength--;
                saveConfig();
            } else {
                event.setCancelled(true); // Отмена взрыва, если прочность обсидиана исчерпана
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().getType() == EntityType.PRIMED_TNT) {
            World world = event.getLocation().getWorld();
            for (int x = event.getLocation().getBlockX() - 2; x <= event.getLocation().getBlockX() + 2; x++) {
                for (int y = event.getLocation().getBlockY() - 2; y <= event.getLocation().getBlockY() + 2; y++) {
                    for (int z = event.getLocation().getBlockZ() - 2; z <= event.getLocation().getBlockZ() + 2; z++) {
                        if (world.getBlockAt(x, y, z).getType() == Material.OBSIDIAN) {
                            // Проверка прочности обсидиана и уменьшение её
                            if (obsidianStrength > 0) {
                                obsidianStrength--;
                                saveConfig();
                            } else {
                                event.setCancelled(true); // Отмена взрыва, если прочность обсидиана исчерпана
                            }
                        }
                    }
                }
            }
        }
    }

    private void loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getLogger().info("Creating default config.yml...");
            getConfig().options().copyDefaults(true);
            getConfig().set("obsidian_strength", 30); // Прочность обсидиана по умолчанию
            saveConfig();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        obsidianStrength = config.getInt("obsidian_strength");
    }

    private void saveConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("obsidian_strength", obsidianStrength);
        try {
            config.save(configFile);
        } catch (IOException e) {
            getLogger().warning("Unable to save config.yml!");
            e.printStackTrace();
        }
    }

    // Добавляем новую функцию для увеличения прочности обсидиана
    public void increaseObsidianStrength(int amount) {
        obsidianStrength += amount;
        saveConfig();
    }

    // Добавляем новую функцию для уменьшения прочности обсидиана
    public void decreaseObsidianStrength(int amount) {
        if (obsidianStrength >= amount) {
            obsidianStrength -= amount;
            saveConfig();
        } else {
            getLogger().warning("Obsidian strength cannot be decreased below 0!");
        }
    }

    // Добавляем новую функцию для случайного увеличения прочности обсидиана
    public void randomizeObsidianStrength(int min, int max) {
        obsidianStrength += random.nextInt(max - min + 1) + min;
        saveConfig();
    }
}
