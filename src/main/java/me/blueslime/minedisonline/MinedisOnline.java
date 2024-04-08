package me.blueslime.minedisonline;

import me.blueslime.minedis.api.extension.MinedisExtension;
import me.blueslime.minedis.utils.consumer.PluginConsumer;
import me.blueslime.minedisonline.listener.DiscordCommandListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;

public final class MinedisOnline extends MinedisExtension {

    private final ArrayList<String> staffCommands = new ArrayList<>();

    private final ArrayList<String> onlineCommands = new ArrayList<>();

    @Override
    public String getIdentifier() {
        return "MOnlineList";
    }

    @Override
    public String getName() {
        return "Minedis Online";
    }

    @Override
    public void onEnabled() {
        String embedPath = "settings.commands.staff-online.";
        if (!getConfiguration().contains("settings.staff-permission")) {
            getConfiguration().set("settings.staff-permission", "minedis.online.staff");
        }
        if (!getConfiguration().contains(embedPath + "command")) {
            getConfiguration().set(embedPath + "command", "os");
            getConfiguration().set(embedPath + "command-description", "Check your staff list");
            getConfiguration().set(embedPath + "guild-id", "NOT_SET");
            getConfiguration().set(embedPath + "title", "Server Online Staff");
            getConfiguration().set(embedPath + "description", "Currently **%staff% staff(s) online and %users% user(s) online.");
            getConfiguration().set(embedPath + "color", "YELLOW");
            getConfiguration().set(embedPath + "footer", "mc.spigotmc.org");
        }

        embedPath = "settings.commands.default-online.";
        if (!getConfiguration().contains(embedPath + "command")) {
            getConfiguration().set(embedPath + "command", "online");
            getConfiguration().set(embedPath + "command-description", "Check your online list");
            getConfiguration().set(embedPath + "guild-id", "NOT_SET");
            getConfiguration().set(embedPath + "title", "Server Online");
            getConfiguration().set(embedPath + "description", "Currently %users% user(s) online.");
            getConfiguration().set(embedPath + "color", "BLUE");
            getConfiguration().set(embedPath + "footer", "mc.spigotmc.org");
        }

        saveConfiguration();

        registerEventListeners(
            new DiscordCommandListener(this)
        );

        registerOnlineCommand(embedPath);

        embedPath = "settings.commands.staff-online.";

        registerStaffOnlineCommand(embedPath);
    }

    public void registerOnlineCommand(String embedPath) {
        PluginConsumer.process(
            () -> {
                String guildID = getConfiguration().getString(embedPath + "guild-id", "NOT_SET");

                if (guildID.isEmpty() || guildID.equalsIgnoreCase("NOT_SET")) {
                    getLogger().info("Can't register online command because discord guild id was not set yet.");
                    getLogger().info("Path: " + embedPath + "guild-id, value: " + guildID);
                    return;
                }

                Guild guild = getJDA().getGuildById(guildID);

                if (guild == null) {
                    getLogger().info("Discord GUILD was not found for online command.");
                    return;
                }

                getLogger().info("Discord guild-id for online command is: " + guild.getId());

                String description = getConfiguration().getString(embedPath + "command-description", "Check your online list");

                String command = getConfiguration().getString(embedPath + "command", "online");

                onlineCommands.add(command);

                registerCommand(
                        guild,
                        Commands.slash(
                                command,
                                description
                        )
                );

                getLogger().info("Registered command: /" + command + " at guild-id: " + guild.getId());

                if (getConfiguration().contains(embedPath + "command-aliases")) {
                    for (String cmd : getConfiguration().getStringList(embedPath + "command-aliases")) {
                        getLogger().info("Registered command: /" + cmd + " at guild-id: " + guild.getId());
                        onlineCommands.add(cmd);
                        registerCommand(
                                guild,
                                Commands.slash(
                                        cmd,
                                        description
                                )
                        );
                    }
                }
            },
            e -> getLogger().info("Can't register online command due to issues with bot connection!")
        );
    }

    public void registerStaffOnlineCommand(String embedPath) {
        PluginConsumer.process(
            () -> {
                String description = getConfiguration().getString(embedPath + "command-description", "Check your staff list");

                String guildID = getConfiguration().getString(embedPath + "guild-id", "NOT_SET");

                if (guildID.isEmpty() || guildID.equalsIgnoreCase("NOT_SET")) {
                    getLogger().info("Can't register staff online command because discord guild id was not set yet.");
                    getLogger().info("Path: " + embedPath + "guild-id, value: " + guildID);
                    return;
                }

                Guild guild = getJDA().getGuildById(guildID);

                if (guild == null) {
                    getLogger().info("Discord GUILD was not found for staff online command.");
                    return;
                }

                String cmd = getConfiguration().getString(embedPath + "command", "os");
                getLogger().info("Registered command: /" + cmd + " at guild-id: " + guild.getId());

                staffCommands.add(cmd);

                registerCommand(
                        guild,
                        Commands.slash(
                                cmd,
                                description
                        )
                );

                if (getConfiguration().contains(embedPath + "command-aliases")) {
                    for (String command : getConfiguration().getStringList(embedPath + "command-aliases")) {
                        getLogger().info("Registered command: /" + command + " at guild-id: " + guild.getId());
                        staffCommands.add(command);
                        registerCommand(
                                guild,
                                Commands.slash(
                                        command,
                                        description
                                )
                        );
                    }
                }
            },
            e -> getLogger().info("Can't register staff online command due to bot connection issues.")
        );
    }

    @Override
    public void onDisable() {
        getLogger().info("All listeners are unloaded from Minedis Online");

        staffCommands.clear();
        onlineCommands.clear();
    }

    public ArrayList<String> getOnlineCommands() {
        return onlineCommands;
    }

    public ArrayList<String> getStaffCommands() {
        return staffCommands;
    }
}
