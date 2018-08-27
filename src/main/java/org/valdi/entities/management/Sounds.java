package org.valdi.entities.management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.Disguise;
import org.valdi.entities.disguise.DisguiseType;

public class Sounds {
	
	private static Map<DisguiseType, Sounds> entitySounds = new ConcurrentHashMap<DisguiseType, Sounds>();
	private static Sounds stepSounds;
	
	public static Sounds getSoundsForEntity(DisguiseType type) {
		return entitySounds.get(type);
	}
	
	public static boolean setSoundsForEntity(DisguiseType type, Sounds sounds) {
		entitySounds.put(type, sounds);
		return true;
	}
	
	public static String replaceSoundEffect(DisguiseType source, String soundEffect, Disguise target) {
		Sounds sourceSounds = getSoundsForEntity(source);
		Sounds targetSounds = getSoundsForEntity(target.getType());
		if(sourceSounds != null) {
			SoundEffectType sourceType = sourceSounds.matchSoundEffect(soundEffect);
			if(sourceType == null) {
				sourceType = stepSounds.matchSoundEffect(soundEffect);
			}
			if(sourceType != null) {
				if(targetSounds != null) {
					String targetSoundEffect;
					SoundEffectType targetType = sourceType;
					while((targetSoundEffect = targetSounds.getSoundEffect(targetType, target)) == null) {
						targetType = targetType.fallback;
						if(targetType == null) break;
					}
					return targetSoundEffect != null && !targetSoundEffect.isEmpty() ? targetSoundEffect : stepSounds.getSoundEffect(sourceType) != null && !stepSounds.getSoundEffect(sourceType).isEmpty() ? stepSounds.getSoundEffect(sourceType) : null;
				}
				return null;
			}
		}
		return soundEffect;
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
	
	private Map<SoundEffectType, String> typeToSoundEffect = new ConcurrentHashMap<SoundEffectType, String>();
	private Map<String, SoundEffectType> soundEffectToType = new ConcurrentHashMap<String, SoundEffectType>();
	
	public Sounds(ConfigurationSection section) {
		for(SoundEffectType type : SoundEffectType.values()) {
			if(section.isList(type.name())) {
				List<String> soundEffects = section.getStringList(type.name());
				typeToSoundEffect.put(type, soundEffects.get(0));
				for(String soundEffect : soundEffects) {
					soundEffectToType.put(soundEffect, type);
				}
			}
		}
	}
	
	public SoundEffectType matchSoundEffect(String soundEffect) {
		return soundEffectToType.get(soundEffect);
	}
	
	public String getSoundEffect(SoundEffectType type) {
		return typeToSoundEffect.get(type);
	}
	
	public String getSoundEffect(SoundEffectType type, Disguise target) {
		return getSoundEffect(type);
	}
	
	public enum SoundEffectType {
		
		HURT(null),
		DEATH(HURT),
		SMALL_FALL(null),
		BIG_FALL(SMALL_FALL),
		SPLASH(null),
		SWIM(null),
		STEP(null), STEP_ANVIL(STEP), STEP_CLOTH(STEP), STEP_GLASS(STEP), STEP_GRASS(STEP), STEP_GRAVEL(STEP), STEP_LADDER(STEP), STEP_METAL(STEP), STEP_SAND(STEP), STEP_SLIME(STEP), STEP_SNOW(STEP), STEP_STONE(STEP), STEP_WOOD(STEP),
		AMBIENT(null),
		EAT(AMBIENT),
		ANGRY(AMBIENT);
		
		public final SoundEffectType fallback;
		
		private SoundEffectType(SoundEffectType fallback) {
			this.fallback = fallback;
		}
		
	}
	
}