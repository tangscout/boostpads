package net.scoutlink.boostpads;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener {
    double heightBoost = 0.201D;
    double dirMultiplier = 3.8D;
    double maxBoost = 10.0D;

    public Main() {
    }

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        if (!(new File(this.getDataFolder(), "config.yml")).exists()) {
            this.saveDefaultConfig();
        }

        this.heightBoost = this.getConfig().getDouble("HeightBoost", heightBoost);
        this.dirMultiplier = this.getConfig().getDouble("DirectionMultiplier", dirMultiplier);
        this.maxBoost = this.getConfig().getDouble("MaxBoost", maxBoost);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player p = event.getPlayer();
        if (event.getLine(0).equalsIgnoreCase("[Boost]")) {
            if (p.hasPermission("booster.place")) {
                if (!event.getLine(1).equalsIgnoreCase("") || !event.getLine(2).equalsIgnoreCase("") || !event.getLine(3).equalsIgnoreCase("")) {
                    try {
                        double x = Double.parseDouble(event.getLine(1));
                        double y = Double.parseDouble(event.getLine(2));
                        double z = Double.parseDouble(event.getLine(3));
                        if (x <= maxBoost && y <= maxBoost && z <= maxBoost) {
                            event.setLine(0, ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Boost" + ChatColor.GRAY + "]");
                        } else {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.DARK_RED + "Error" + ChatColor.GRAY + ": One of your entered values is too high! None of them should be higher than " + String.format("%d", maxBoost) + "!");
                        }
                    } catch (NumberFormatException var9) {
                        if (!event.getLine(1).equalsIgnoreCase("player")) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.DARK_RED + "Error" + ChatColor.GRAY + ": Invalid boost-sign.");
                        } else {
                            event.setLine(0, ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Boost" + ChatColor.GRAY + "]");
                        }
                    }
                }
            } else {
                event.setLine(0, "No!");
                event.setLine(1, "Not enough");
                event.setLine(2, "Permissions!");
                event.setLine(3, ":(");
            }
        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Block block = p.getLocation().add(0.0D, -2.0D, 0.0D).getBlock();
        if (p.getLocation().add(0.0D, -2.0D, 0.0D).getBlock().getType() == Material.SIGN || p.getLocation().add(0.0D, -2.0D, 0.0D).getBlock().getType() == Material.SIGN_POST || p.getLocation().add(0.0D, -2.0D, 0.0D).getBlock().getType() == Material.WALL_SIGN) {
            BlockState stateBlock = block.getState();
            Sign sign = (Sign) stateBlock;
            if (sign.getLine(0).equals(ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Boost" + ChatColor.GRAY + "]")) {
                try {
                    double x = Double.parseDouble(sign.getLine(1));
                    double y = Double.parseDouble(sign.getLine(2));
                    double z = Double.parseDouble(sign.getLine(3));
                    p.setVelocity(new Vector(x, y, z));
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 1.0F, 1.0F);
                } catch (NumberFormatException var12) {
                    if (sign.getLine(1).equalsIgnoreCase("player")) {
                        p.setVelocity(p.getLocation().getDirection().setY(this.heightBoost * 0.1D).multiply(this.dirMultiplier));
                    }
                }
            }
        }

    }

    public void onDisable() {
    }
}
