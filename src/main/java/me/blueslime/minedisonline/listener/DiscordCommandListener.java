package me.blueslime.minedisonline.listener;

import me.blueslime.minedis.utils.text.TextReplacer;
import me.blueslime.minedisonline.MinedisOnline;
import me.blueslime.minedisonline.utils.EmbedSection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class DiscordCommandListener extends ListenerAdapter {
    private final MinedisOnline plugin;

    public DiscordCommandListener(MinedisOnline plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (plugin.getOnlineCommands().contains(event.getName()) || plugin.getStaffCommands().contains(event.getName())) {

            String permission = plugin.getConfiguration().getString("settings.staff-permission", "minedis.online.staff");

            int staff = 0;
            int users = 0;

            for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
                if (player.hasPermission(permission)) {
                    staff++;
                    continue;
                }
                users++;
            }

            String path = plugin.getOnlineCommands().contains(event.getName()) ?
                    "settings.commands.default-online" : "settings.commands.staff-online";

            event.deferReply(true).queue();

            TextReplacer replacer = TextReplacer.builder()
                .replace("%staff%", String.valueOf(staff))
                .replace("%users%", String.valueOf(users))
                .replace("%user%", String.valueOf(users))
                .replace("%staffs%", String.valueOf(staff))
                .replace("%players%", String.valueOf(users));

            event.getHook().setEphemeral(true).sendMessageEmbeds(
                new EmbedSection(
                    plugin.getConfiguration().getSection(path)
                ).build(replacer)
            ).queueAfter(1, TimeUnit.SECONDS);
        }
    }
}
