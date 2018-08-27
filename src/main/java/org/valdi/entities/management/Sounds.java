package org.valdi.entities.management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.Disguise;
import org.valdi.entities.disguise.DisguiseType;

public class Sounds {
	private static Map<DisguiseType, Sounds> entitySounds = new ConcurrentHashMap<>();
	private static Sounds stepSounds;
	
	public static Sounds getSoundsForEntity(DisguiseType type) {
		return entitySounds.get(type);
	}
	
	public static boolean setSoundsForEntity(DisguiseType type, Sounds sounds) {
		entitySounds.put(type, sounds);
		return true;
	}
	
	public static Sound replaceSoundEffect(DisguiseType source, Sound sound, Disguise target) {
		Sounds sourceSounds = getSoundsForEntity(source);
		Sounds targetSounds = getSoundsForEntity(target.getType());
		if(sourceSounds == null) {
			return sound;
		}
		
		SoundEffectType sourceType = sourceSounds.matchSoundEffect(sound);
		if(sourceType == null) {
			sourceType = stepSounds.matchSoundEffect(sound);
		}
		if(sourceType == null) {
			return sound;
		}
		
		if(targetSounds == null) {
			return null;
		}
		
		Sound targetSound;
		SoundEffectType targetType = sourceType;
		while((targetSound = targetSounds.getSoundEffect(targetType, target)) == null) {
			targetType = targetType.getFallback();
			if(targetType == null) break;
		}
		
		if(targetSound != null) {
			return targetSound;
		}
		
		if(stepSounds.getSoundEffect(sourceType) != null) {
			return stepSounds.getSoundEffect(sourceType);
		}
		 
		return null;
	}
	
	public static void init(String file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(iDisguise.getInstance().getResource(file)));
			FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(reader);
			if(fileConfiguration.isConfigurationSection("STEP")) {
				stepSounds = new Sounds(fileConfiguration.getConfigurationSection("STEP"));
			}
			for(DisguiseType type : DisguiseType.values()) {
				if(fileConfiguration.isConfigurationSection(type.name())) {
					setSoundsForEntity(type, new Sounds(fileConfiguration.getConfigurationSection(type.name())));
				}
			}
			reader.close();
		} catch(IOException e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot load the required sound effect configuration.", e);
			}
		}
	}
	
	private Map<SoundEffectType, Sound> typeToSoundEffect = new ConcurrentHashMap<>();
	private Map<Sound, SoundEffectType> soundEffectToType = new ConcurrentHashMap<>();
	
	public Sounds(ConfigurationSection section) {
		for(SoundEffectType type : SoundEffectType.values()) {
			if(section.isList(type.name())) {
				List<Sound> soundEffects = new ArrayList<>();
				for(String soundRaw : section.getStringList(type.name())) {
					if(soundRaw == null || soundRaw.isEmpty()) {
						soundEffects.add(null);
						continue;
					}
					
					try {
						soundRaw = soundRaw.toUpperCase();
						Sound sound = Sound.valueOf(soundRaw);
						soundEffects.add(sound);
					} catch(IllegalArgumentException | NullPointerException e) {
						iDisguise.getInstance().getLogger().info("Cannot load sound " + soundRaw + " as it isn't a valid org.bukkit.Sound!");
					}
				}
				typeToSoundEffect.put(type, soundEffects.get(0));
				for(Sound soundEffect : soundEffects) {
					soundEffectToType.put(soundEffect, type);
				}
			}
		}
	}
	
	public SoundEffectType matchSoundEffect(Sound sound) {
		return soundEffectToType.get(sound);
	}
	
	public Sound getSoundEffect(SoundEffectType type) {
		return typeToSoundEffect.get(type);
	}
	
	public Sound getSoundEffect(SoundEffectType type, Disguise target) {
		return getSoundEffect(type);
	}
	
	public enum SoundEffectType {
		HURT(null),
		DEATH(HURT),
		SMALL_FALL(null),
		BIG_FALL(SMALL_FALL),
		SPLASH(null),
		SWIM(null),
		STEP(null), 
		STEP_ANVIL(STEP), 
		STEP_CLOTH(STEP), 
		STEP_GLASS(STEP), 
		STEP_GRASS(STEP), 
		STEP_GRAVEL(STEP), 
		STEP_LADDER(STEP), 
		STEP_METAL(STEP), 
		STEP_SAND(STEP), 
		STEP_SLIME(STEP), 
		STEP_SNOW(STEP), 
		STEP_STONE(STEP), 
		STEP_WOOD(STEP),
		AMBIENT(null),
		EAT(AMBIENT),
		ANGRY(AMBIENT);
		
		private final SoundEffectType fallback;
		
		private SoundEffectType(SoundEffectType fallback) {
			this.fallback = fallback;
		}
		
		public SoundEffectType getFallback() {
			return this.fallback;
		}
	}
	
}