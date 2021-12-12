package com.github.superslowjelly.jndidetector;

import com.github.superslowjelly.jndidetector.configuration.Config;
import com.github.superslowjelly.jndidetector.configuration.ConfigManager;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.util.regex.Pattern;

@Plugin(
    id = "jndidetector",
    name = "JNDIDetector",
    description = "Standalone detector/mitigator for the Log4J JNDI exploit, with configurable options for console logging, player messaging, and command execution on detection.",
    authors = {
        "SuperslowJelly"
    }
)
public class JNDIDetector {

    private static JNDIDetector instance;

    private final Pattern PATTERN = Pattern.compile("${jndi", Pattern.LITERAL);

    private ConfigManager configManager;

    @Inject private Logger logger;

    @Inject @ConfigDir(sharedRoot = true) private File configDir;

    public static JNDIDetector get() { return JNDIDetector.instance; }

    public static File getConfigDir() { return JNDIDetector.get().configDir; }

    @Listener
    public void onGameConstruction(GameConstructionEvent event) { JNDIDetector.instance = this; }

    @Listener public void onGamePreInit(GamePreInitializationEvent event) {
        this.logger.info("Loading config...");
        this.configManager = new ConfigManager();
    }

    @Listener(order = Order.FIRST)
    public void onSpongeMessage(MessageChannelEvent event, @Root Player player) {
        String message = event.getFormatter().getBody().toText().toPlain().toLowerCase();

        if (PATTERN.matcher(message).find()) {
            event.setMessageCancelled(true);

            Config config = this.configManager.getConfig();

            if (!config.LOGGER_MESSAGES.isEmpty()) {
                for (String loggerMessage : config.LOGGER_MESSAGES) {
                    this.logger.warn(loggerMessage.replace("%player%", player.getName()));
                }
            }

            if (!config.PLAYER_MESSAGES.isEmpty()) {
                for (String playerMessage : config.PLAYER_MESSAGES) {
                    player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(playerMessage.replace("%player%", player.getName())));
                }
            }

            if (!config.COMMANDS.isEmpty()) {
                CommandManager commandManager = Sponge.getCommandManager();
                CommandSource console = Sponge.getServer().getConsole();
                for (String command : config.COMMANDS) {
                    commandManager.process(console, command.replace("%player%", player.getName()));
                }
            }
        }
    }

    @Listener public void onGameReload(GameReloadEvent event) {
        this.configManager.reload();
    }
}
