package me.RareHyperIon.AntiTrample;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Main extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

    private Material parsedFarmland;

    @Override
    public void onLoad() {
        this.parsedFarmland = XMaterial.FARMLAND.get();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(this.getCommand("antitrample")).setExecutor(this);
        Objects.requireNonNull(this.getCommand("antitrample")).setTabCompleter(this);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL) return;

        final Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null) return;

        if(clickedBlock.getType() != this.parsedFarmland) {
            return;
        }

        final Player player = event.getPlayer();
        if(this.shouldIgnore(player)) return;

        event.setCancelled(true);

        final FileConfiguration config = this.getConfig();
        final String message = config.getString("Message");

        if(message != null && !message.trim().isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        if(!config.getBoolean("Sound.Enabled", true)) {
            return;
        }

        final String soundName = config.getString("Sound.Type", "BLOCK_NOTE_BLOCK_BASS");

        if(soundName != null && !soundName.trim().isEmpty()) {
            final Optional<XSound> sound = XSound.of(soundName);

            if(sound.isPresent()) {
                final float volume = (float) config.getDouble("Sound.Volume", 1.0D);
                final float pitch  = (float) config.getDouble("Sound.Pitch",  1.0D);

                sound.get().play(player.getLocation(), volume, pitch);
            } else {
                this.getServer().getLogger().warning("Invalid sound '" + soundName + "'.");
            }
        }

        // Silently ignoring if a sound wasn't actually provided.
        // May or may not be a better idea to add a warning, but I'm not sure so I'll leave it like this.
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityInteract(final EntityInteractEvent event) {
        if(!this.getConfig().getBoolean("PreventMobs", true)) return;
        if(event.getBlock().getType() != this.parsedFarmland) return;

        event.setCancelled(true);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if(cmd.getName().equalsIgnoreCase("antitrample")) {
            if(!sender.hasPermission("antitrample.reload")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to run this command."));
                return true;
            }

            if(args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /antitrample reload"));
                return true;
            }

            this.reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reloaded."));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        return Collections.singletonList("reload");
    }

    private boolean shouldIgnore(final Player player) {
        final String mode = Objects.requireNonNull(
                this.getConfig().getString("PermissionMode", "BYPASS")
        ).toUpperCase(Locale.ROOT);

        return ("BYPASS".equals(mode) && player.hasPermission("antitrample.ignored")) ||
                ("WHITELIST".equals(mode) && !player.hasPermission("antitrample.use"));
    }

}
