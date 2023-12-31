package me.blueslime.minedisonline;

import me.blueslime.minedis.api.extension.MinedisExtension;
import me.blueslime.minedisonline.listener.DiscordCommandListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;

public final class MinedisOnline extends MinedisExtension {

    private final ArrayList<String> staffList = new ArrayList<>();
    private final ArrayList<String> onlineList = new ArrayList<>();

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

        String guildID = getConfiguration().getString(embedPath + "guild-id", "NOT_SET");

        if (guildID.isEmpty() || guildID.equalsIgnoreCase("NOT_SET")) {
            getLogger().info("Can't register link command because discord guild id was not set yet.");
            return;
        }

        Guild guild = getJDA().getGuildById(guildID);

        if (guild == null) {
            getLogger().info("Discord GUILD was not found for link command.");
            return;
        }

        String description = getConfiguration().getString(embedPath + "command-description", "Check your online list");

        guild.upsertCommand(
                Commands.slash(
                    getConfiguration().getString(embedPath + "command", "online"),
                    description
                )
        ).queue(cmd -> onlineList.add(cmd.getId()));

        if (getConfiguration().contains(embedPath + "command-aliases")) {
            for (String command : getConfiguration().getStringList(embedPath + "command-aliases")) {
                guild.upsertCommand(
                        Commands.slash(
                            command,
                            description
                        )
                ).queue(cmd -> onlineList.add(cmd.getId()));
            }
        }

        embedPath = "settings.commands.staff-online.";

        description = getConfiguration().getString(embedPath + "command-description", "Check your staff list");

        guildID = getConfiguration().getString(embedPath + "guild-id", "NOT_SET");

        if (guildID.isEmpty() || guildID.equalsIgnoreCase("NOT_SET")) {
            getLogger().info("Can't register link command because discord guild id was not set yet.");
            return;
        }

        guild = getJDA().getGuildById(guildID);

        if (guild == null) {
            getLogger().info("Discord GUILD was not found for link command.");
            return;
        }

        guild.upsertCommand(
                Commands.slash(
                        getConfiguration().getString(embedPath + "command", "os"),
                        description
                )
        ).queue(cmd -> staffList.add(cmd.getId()));

        if (getConfiguration().contains(embedPath + "command-aliases")) {
            for (String command : getConfiguration().getStringList(embedPath + "command-aliases")) {
                guild.upsertCommand(
                        Commands.slash(
                                command,
                                description
                        )
                ).queue(cmd -> staffList.add(cmd.getId()));
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("All listeners are unloaded from Minedis Online");

        String guildID = getConfiguration().getString("settings.commands.staff-online.guild-id", "NOT_SET");

        if (guildID.isEmpty() || guildID.equalsIgnoreCase("NOT_SET")) {
            getLogger().info("Can't register link command because discord guild id was not set yet.");
            return;
        }

        Guild guild = getJDA().getGuildById(guildID);

        if (guild == null) {
            getLogger().info("Discord GUILD was not found for online command.");
            return;
        }

        Guild finalGuild1 = guild;
        onlineList.forEach(command -> finalGuild1.deleteCommandById(command).queue());

        guildID = getConfiguration().getString("settings.commands.default-online.guild-id", "NOT_SET");

        if (guildID.isEmpty() || guildID.equalsIgnoreCase("NOT_SET")) {
            getLogger().info("Can't register link command because discord guild id was not set yet.");
            return;
        }

        guild = getJDA().getGuildById(guildID);

        if (guild == null) {
            getLogger().info("Discord GUILD was not found for staff online command.");
            return;
        }

        Guild finalGuild = guild;
        staffList.forEach(command -> finalGuild.deleteCommandById(command).queue());

        onlineList.clear();
        staffList.clear();
    }

    public ArrayList<String> getOnlineList() {
        return onlineList;
    }

    public ArrayList<String> getStaffList() {
        return staffList;
    }
}
