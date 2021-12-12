package com.github.superslowjelly.jndidetector.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;

@ConfigSerializable
public class Config {

    public Config() {
        this.COMMANDS.add("kick %player% Attempted JNDI exploitation.");
        this.PLAYER_MESSAGES.add("&c&lNOPE!");
        this.LOGGER_MESSAGES.add("%player% attempted to use the Log4J JNDI exploit!");
    }

    @Setting(value = "commands", comment = "Commands to run on JNDI exploit detection.\n\"%player%\": The player's IGN.\nLeave blank for no commands.")
    public final ArrayList<String> COMMANDS = new ArrayList<>();

    @Setting(value = "player-messages", comment = "Messages to send to the player on JNDI explot detection.\nUse standard Minecraft formatting, i.e. \"&c&lNOPE!\".\n\"%player%\": The player's IGN.\nLeave blank for no messages.")
    public final ArrayList<String> PLAYER_MESSAGES = new ArrayList<>();

    @Setting(value = "logger-messages", comment = "Messages to log to the console on JNDI explot detection.\nLogger will use the WARN level.\n\"%player%\": The player's IGN.\nLeave blank for no messages.")
    public final ArrayList<String> LOGGER_MESSAGES = new ArrayList<>();
}