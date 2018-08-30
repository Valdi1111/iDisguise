package org.valdi.entities.management.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.valdi.entities.iDisguise;

public class ScoreboardHooks {
	
	public static boolean nametagEdit = false;
	public static boolean coloredTags = false;
	
	public static void setup() {
		nametagEdit = Bukkit.getPluginManager().getPlugin("NametagEdit") != null;
		coloredTags = Bukkit.getPluginManager().getPlugin("ColoredTags") != null;
	}
	
	public static void updatePlayer(final Player player) {
		if(nametagEdit) {
			final com.nametagedit.plugin.NametagEdit plugin = (com.nametagedit.plugin.NametagEdit)Bukkit.getPluginManager().getPlugin("NametagEdit");
			plugin.getHandler().getNametagManager().reset(player.getName());
			Bukkit.getScheduler().runTaskLaterAsynchronously(iDisguise.getInstance(), new Runnable() {
				
				public void run() {
					plugin.getHandler().applyTagToPlayer(player, false);
				}
				
			}, 5L);
		}
		
		if(coloredTags) {
			Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), new Runnable() {
				
				public void run() {
					com.gmail.filoghost.coloredtags.ColoredTags.updateNametag(player);
					com.gmail.filoghost.coloredtags.ColoredTags.updateTab(player);
				}
				
			}, 5L);
		}
	}
	
}