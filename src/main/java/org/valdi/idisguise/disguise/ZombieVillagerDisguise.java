package org.valdi.idisguise.disguise;

import java.util.Locale;

import org.valdi.idisguise.disguise.VillagerDisguise.Profession;
import org.valdi.idisguise.management.VersionHelper;

/**
 * Represents a disguise as an infected villager.
 * 
 * @since 5.5.1
 * @author RobinGrether
 */
public class ZombieVillagerDisguise extends AgeableDisguise {
	
	private Profession profession;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 */
	public ZombieVillagerDisguise() {
		this(true, Profession.FARMER);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 */
	public ZombieVillagerDisguise(boolean adult, Profession profession) {
		super(DisguiseType.ZOMBIE_VILLAGER, adult);
		this.profession = profession;
	}
	
	/**
	 * @since 5.5.1
	 */
	public Profession getProfession() {
		return profession;
	}
	
	/**
	 * @since 5.5.1
	 */
	public void setProfession(Profession profession) {
		this.profession = profession;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; %s", super.toString(), profession.name().toLowerCase(Locale.ENGLISH));
	}
	
	static {
		if(VersionHelper.require1_9()) {
			for(Profession profession : Profession.values()) {
				Subtypes.registerSubtype(ZombieVillagerDisguise.class, "setProfession", profession, profession.name().toLowerCase(Locale.ENGLISH));
			}
		}
	}
	
}