package me.RareHyperIon.AntiTrample;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

import java.util.Collections;
import java.util.List;

public final class Main extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

    private Material parsedFarmland;

    @Override
    public void onLoad() {
        this.parsedFarmland = XMaterial.FARMLAND.parseMaterial();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("antitrample").setExecutor(this);
        this.getCommand("antitrample").setTabCompleter(this);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL || event.getClickedBlock().getType() != this.parsedFarmland) return;
        final FileConfiguration config = this.getConfig();

        final String mode = config.getString("PermissionMode").toUpperCase();
        final Player player = event.getPlayer();

        if("BYPASS".equals(mode) && player.hasPermission("antitrample.ignored") ||
            "WHITELIST".equals(mode) && !player.hasPermission("antitrample.use")) {
            return;
        }

        final String message = config.getString("Message");
        final String sound = config.getString("Sound");

        if(message != null && !message.trim().isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        if(sound != null && !sound.trim().isEmpty()) {
            player.playSound(player.getLocation(), XSound.valueOf(sound).parseSound(), 1, 1);
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityInteract(final EntityInteractEvent event) {
        if(!this.getConfig().getBoolean("PreventMobs")) return;
        if(event.getBlock() == null || event.getBlock().getType() != this.parsedFarmland) return;
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

}
