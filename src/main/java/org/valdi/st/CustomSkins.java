package org.valdi.st;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.inventivetalent.mcwrapper.auth.GameProfileWrapper;
import org.mineskin.MineskinClient;
import org.mineskin.SkinOptions;
import org.mineskin.data.Skin;
import org.mineskin.data.SkinCallback;
import org.valdi.idisguise.iDisguise;
import org.valdi.idisguise.disguise.PlayerDisguise;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CustomSkins implements Listener {
	private final iDisguise plugin;
	private final Set<String> loadedSkins = new HashSet<>();
	private final File skinFolder;

	private static CustomSkins instance;
	private MineskinClient skinClient;
	
	public CustomSkins(final iDisguise plugin) {
		this.plugin = plugin;
		this.skinFolder = new File(plugin.getDataFolder(), "skins");
		
		instance = this;
	}
	
	public static CustomSkins getInstance() {
		return instance;
	}

	public void onEnable() {
		if (!skinFolder.exists()) {
			skinFolder.mkdirs();
		}

		skinClient = new MineskinClient();
	}
	
	public Logger getLogger() {
		return this.plugin.getLogger();
	}

	public void createSkin(final CommandSender sender, String name, String urlString, String privateUploadString) {
		try {
			URL url = new URL(urlString);
			final File skinFile = new File(skinFolder, name + ".cs");
			boolean privateUpload = "true".equalsIgnoreCase(privateUploadString) || "yes".equalsIgnoreCase(privateUploadString) || "private".equalsIgnoreCase(privateUploadString);

			if (skinFile.exists()) {
				sender.sendMessage("�cCustom skin '" + name + "' already exists. Please choose a different name.");
				return;
			} else {
				skinFile.createNewFile();
			}


			skinClient.generateUrl(url.toString(), SkinOptions.name(name), new SkinCallback() {

				@Override
				public void waiting(long l) {
					sender.sendMessage("�7Waiting " + (l / 1000D) + "s to upload skin...");
				}

				@Override
				public void uploading() {
					sender.sendMessage("�eUploading skin...");
				}

				@Override
				public void error(String s) {
					sender.sendMessage("�cError while generating skin: " + s);
					sender.sendMessage("�cPlease make sure the image is a valid skin texture and try again.");

					skinFile.delete();
				}

				@Override
				public void exception(Exception exception) {
					sender.sendMessage("�cException while generating skin, see console for details: " + exception.getMessage());
					sender.sendMessage("�cPlease make sure the image is a valid skin texture and try again.");

					skinFile.delete();

					getLogger().log(Level.WARNING, "Exception while generating skin", exception);
				}

				@Override
				public void done(Skin skin) {
					sender.sendMessage("�aSkin data generated.");
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("id", skin.data.uuid.toString());
					jsonObject.addProperty("name", "");

					JsonObject property = new JsonObject();
					property.addProperty("name", "textures");
					property.addProperty("value", skin.data.texture.value);
					property.addProperty("signature", skin.data.texture.signature);

					JsonArray propertiesArray = new JsonArray();
					propertiesArray.add(property);

					jsonObject.add("properties", propertiesArray);

					try (Writer writer = new FileWriter(skinFile)) {
						new Gson().toJson(jsonObject, writer);
					} catch (IOException e) {
						sender.sendMessage("�cFailed to save skin to file: " + e.getMessage());
						getLogger().log(Level.SEVERE, "Failed to save skin", e);
					}
				}
			});
		} catch (MalformedURLException e) {
			sender.sendMessage("�cInvalid URL");
			return;
		} catch (IOException e) {
			sender.sendMessage("�cUnexpected IOException: " + e.getMessage());
			getLogger().log(Level.SEVERE, "Unexpected IOException while creating skin '" + name + "' with source '" + urlString + "'", e);
		}
	}

	public void applySkin(CommandSender sender, String name, String targetPlayer) {
		Player target;
		if (targetPlayer == null) {
			return;
		}
		
		if (!sender.hasPermission("customskins.apply.other")) {
			sender.sendMessage("�cYou don't have permission to change other player's skins");
			return;
		}
		target = Bukkit.getPlayer(targetPlayer);
		if (target == null || !target.isOnline()) {
			sender.sendMessage("�cPlayer not found");
			return;
		}
		
		this.setSkin(target, name);
	}

	public void applySkin(CommandSender sender, String name) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("�cPlease specify the target player");
			return;
		}
		
		this.setSkin((Player) sender, name);
	}
	
	private void setSkin(Player player, String name) {
		File skinFile = new File(skinFolder, name + ".cs");
		if (!skinFile.exists()) {
			player.sendMessage("�cSkin '" + name + "' does not exist");
			if (player.hasPermission("customskins.create")) {
				player.sendMessage("�cPlease use /createCustomSkin first");
			}
			return;
		}
		
		JsonObject skinData;
		try {
			skinData = new JsonParser().parse(new FileReader(skinFile)).getAsJsonObject();
		} catch (IOException e) {
			player.sendMessage("�cFailed to load skin from file: " + e.getMessage());
			getLogger().log(Level.SEVERE, "Failed to load skin", e);
			return;
		}

		if (!loadedSkins.contains(name)) {
			SkinAPI.getSkinManager().loadCustomSkin("cs_" + name, skinData);
			loadedSkins.add(name);
			this.getLogger().info("Loading skin....");
		}

		SkinAPI.getSkinManager().setCustomSkin(player.getUniqueId(), "cs_" + name);
		player.sendMessage("�aCustom skin changed to " + name);
	}

	public void applySkin(CommandSender sender, String name, LivingEntity entity) {
		if (entity == null) {
			return;
		}

		File skinFile = new File(skinFolder, name + ".cs");
		if (!skinFile.exists()) {
			sender.sendMessage("�cSkin '" + name + "' does not exist");
			if (sender.hasPermission("customskins.create")) {
				sender.sendMessage("�cPlease use /createCustomSkin first");
			}
			return;
		}
		JsonObject skinData;
		try {
			skinData = new JsonParser().parse(new FileReader(skinFile)).getAsJsonObject();
		} catch (IOException e) {
			sender.sendMessage("�cFailed to load skin from file: " + e.getMessage());
			getLogger().log(Level.SEVERE, "Failed to load skin", e);
			return;
		}

		if (!loadedSkins.contains(name)) {
			SkinAPI.getSkinManager().loadCustomSkin("cs_" + name, skinData);
			loadedSkins.add(name);
			this.getLogger().info("Loading skin....");
		}

		SkinAPI.getSkinManager().setCustomSkin(entity, "cs_" + name);
		sender.sendMessage("�aCustom skin changed to " + name + " for entity " + entity.getUniqueId().toString());
	}

	/*public void applySkin(List<String> completions, CommandSender sender, String name, String targetName) {
		if (sender.hasPermission("customskins.apply")) {
			if (name == null || name.isEmpty() || !new File(skinFolder, name + ".cs").exists()) {
				for (String s : skinFolder.list()) {
					if (s.endsWith(".cs")) {
						completions.add(s.substring(0, s.length() - 3));
					}
				}
			} else {
				if (sender.hasPermission("customskins.apply.other")) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						completions.add(player.getName());
					}
				}
			}
		}
	}*/

}
