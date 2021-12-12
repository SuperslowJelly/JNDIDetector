package com.github.superslowjelly.jndidetector;

import com.github.superslowjelly.jndidetector.configuration.Config;
import com.github.superslowjelly.jndidetector.configuration.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class JNDIDetector extends JavaPlugin implements Listener {

    private static JNDIDetector instance;

    private final Pattern PATTERN = Pattern.compile("${jndi", Pattern.LITERAL);

    private ConfigManager configManager;

    private final Logger LOGGER = this.getLogger();

    public static JNDIDetector get() { return JNDIDetector.instance; }

    public static File getConfigDir() { return JNDIDetector.get().getDataFolder(); }

    @Override
    public void onEnable() {
        JNDIDetector.instance = this;
        this.getServer().getPluginManager().registerEvents(this, this);
        this.LOGGER.info("Loading config...");
        configManager = new ConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpigotMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().toLowerCase();

        if (PATTERN.matcher(message).find()) {
            event.setCancelled(true);

            Config config = this.configManager.getConfig();

            if (!config.LOGGER_MESSAGES.isEmpty()) {
                for (String loggerMessage : config.LOGGER_MESSAGES) {
                    this.LOGGER.warning(loggerMessage.replace("%player%", player.getName()));
                }
            }

            if (!config.PLAYER_MESSAGES.isEmpty()) {
                for (String playerMessage : config.PLAYER_MESSAGES) {
                    player.sendMessage(
                        ChatColor.translateAlternateColorCodes('&', playerMessage.replace("%player%", player.getName())));
                    player.sendRawMessage("");
                }
            }

            if (!config.COMMANDS.isEmpty()) {
                ConsoleCommandSender console = this.getServer().getConsoleSender();
                for (String command : config.COMMANDS) {
                    Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(console, command.replace("%player%", player.getName())));
                }
            }
        }
    }
}
