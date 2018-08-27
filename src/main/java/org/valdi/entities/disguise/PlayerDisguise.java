package org.valdi.entities.disguise;

import java.util.Locale;

import org.valdi.entities.management.ProfileHelper;
import org.valdi.st.SkinContainer;
import org.valdi.st.WrappedProfileHelper;

import de.robingrether.util.Validate;

/**
 * Represents a disguise as a player.
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public class PlayerDisguise extends Disguise {
	
	private final String skinName;
	private String displayName;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 2.1.3
	 * @param skinName the player skin
	 * @throws IllegalArgumentException the given skin name is not valid
	 */
	public PlayerDisguise(String skinName) {
		this(skinName, skinName);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.6.3
	 * @param skinName the player skin
	 * @param displayName the display name (above player's head and player list)
	 * @throws IllegalArgumentException the given skin name is not valid
	 */
	public PlayerDisguise(String skinName, String displayName) {
		this(skinName, displayName, null);
	}
	
	public PlayerDisguise(String skinName, String displayName, SkinContainer profile) {
		super(DisguiseType.PLAYER);
		if(!Validate.minecraftUsername(skinName)) {
			throw new IllegalArgumentException("The given skin name is invalid!");
		}
		this.skinName = skinName.toLowerCase(Locale.ENGLISH);
		this.displayName = displayName;
		
		if(profile == null) {
			ProfileHelper.getInstance().loadGameProfileAsynchronously(this.skinName);
		} else {
			WrappedProfileHelper.getInstance().loadGameProfileAsynchronously(skinName, profile);
		}
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @deprecated ghost disguise is no longer available
	 * @since 2.3.1
	 * @param skinName the player skin
	 * @param ghost <code>false</code> for a normal player, otherwise {@linkplain UnsupportedOperationException} will be thrown
	 * @throws IllegalArgumentException the given skin name is not valid
	 */
	@Deprecated
	public PlayerDisguise(String skinName, boolean ghost) {
		this(skinName, skinName, ghost);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @deprecated ghost disguise is no longer available
	 * @since 5.2.2
	 * @param skinName the player skin
	 * @param displayName the display name (above player's head and player list)
	 * @param ghost <code>false</code> for a normal player, otherwise {@linkplain UnsupportedOperationException} will be thrown
	 * @throws IllegalArgumentException the given skin name is not valid
	 */
	@Deprecated
	public PlayerDisguise(String skinName, String displayName, boolean ghost) {
		this(skinName, displayName);
		if(ghost) {
			throw new UnsupportedOperationException("Ghost disguise is no longer available!");
		}
	}
	
	/**
	 * Returns the name.
	 * 
	 * @deprecated replaced by <code>getSkinName()</code>
	 * @since 2.1.3
	 * @return the skin name
	 */
	@Deprecated
	public String getName() {
		return this.skinName;
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
	 * Checks whether this disguise is a ghost.
	 * 
	 * @deprecated ghost disguise is no longer available
	 * @since 2.3.1
	 * @return <code>false</code>
	 */
	@Deprecated
	public boolean isGhost() {
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; %s; %s", super.toString(), skinName, displayName);
	}
	
}