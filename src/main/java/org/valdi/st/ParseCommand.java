package org.valdi.st;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.valdi.idisguise.iDisguise;

public class ParseCommand implements CommandExecutor, TabCompleter {
	private final iDisguise plugin;
	
	public ParseCommand(final iDisguise plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nope. Players only.");
			return false;
		}
		
		Player player = (Player) sender;
		// String link =  "http://2.224.170.54/test.png";
		String alieno = "http://2.224.170.54/alieno.png";
		CustomSkins.getInstance().createSkin(player, "testSkin", alieno, "private");
		
		Bukkit.getScheduler().runTaskLater(plugin, () -> CustomSkins.getInstance().applySkin(player, "testSkin"), 60L);
		
		for(Entity e : player.getNearbyEntities(5, 5, 5)) {
			if(e instanceof LivingEntity) {
				Bukkit.getScheduler().runTaskLater(plugin, () -> CustomSkins.getInstance().applySkin(player, "testSkin", (LivingEntity) e), 60L);
				CraftLivingEntity entity = (CraftLivingEntity) e;
				
				Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getLogger().info(e.toString() + " -> " + entity.getHandle().getId()), 100L);
			}
		}
		/*Bukkit.getScheduler().runTaskLater(plugin, () -> {
			for(Entity e : player.getNearbyEntities(5, 5, 5)) {
				plugin.getLogger().info(e.toString());
			}
		}, 100L);*/
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
