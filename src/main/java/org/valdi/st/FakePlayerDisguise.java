package org.valdi.st;

import java.util.UUID;

import org.valdi.idisguise.disguise.Disguise;
import org.valdi.idisguise.disguise.DisguiseType;

/**
 * Represents a disguise as a player.
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public class FakePlayerDisguise extends Disguise {

	private final String skinName;
	private final String skinValue;
	private final String skinSignature;
	private String displayName;

	/**
	 * Creates an instance.
	 * 
	 * @since 0.1-BETA
	 * @param uuid the unique identifier for game profile
	 * @param skinName the player skin
	 * @param value the skin value
	 * @param signature the skin signature
	 * @throws IllegalArgumentException the given skin name is not valid
	 */
	protected FakePlayerDisguise(UUID uuid, String skinName, String value, String signature) {
		this(uuid, skinName, value, signature, skinName);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 0.1-BETA
	 * @param uuid the unique identifier for game profile
	 * @param skinName the player skin
	 * @param value the skin value
	 * @param signature the skin signature
	 * @param displayName the display name (above player's head and player list)
	 * @throws IllegalArgumentException the given skin name is not valid
	 */
	protected FakePlayerDisguise(UUID uuid, String skinName, String value, String signature, String displayName) {
		super(DisguiseType.PLAYER);
		this.skinName = skinName;
		this.skinValue = value;
		this.skinSignature = signature;
		this.displayName = displayName;
		//WrappedProfileHelper.getInstance().loadGameProfileAsynchronously(uuid, skinName, this.skinValue, this.skinSignature);
	}
	
	/**
	 * Returns the skin name.<br>
	 * This is always lower case as of 5.5.2.
	 * 
	 * @since 5.2.2
	 * @return the skin name
	 */
	public String getSkinName() {
		return skinName;
	}
	
	public String getSkinValue() {
		return skinValue;
	}

	public String getSignature() {
		return skinSignature;
	}
	
	/**
	 * Returns the display name.
	 * 
	 * @since 5.2.2
	 * @return the display name (above player's head and player list)
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Sets the display name.
	 * 
	 * @since 5.2.2
	 * @param displayName the display name (above player's head and player list)
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; %s; %s; %s", super.toString(), skinValue, skinSignature, displayName);
	}
	
}