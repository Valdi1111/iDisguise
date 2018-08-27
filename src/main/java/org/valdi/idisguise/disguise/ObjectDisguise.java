package org.valdi.idisguise.disguise;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Represents a disguise as an object.
 * 
 * @since 5.1.1
 * @author RobinGrether
 */
public class ObjectDisguise extends Disguise {
	
	private final int typeId;
	private String customName = "";
	private boolean customNameVisible = true;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.1.1
	 * @throws IllegalArgumentException if the given {@linkplain DisguiseType} is not an object
	 * @param type the disguise type
	 */
	public ObjectDisguise(DisguiseType type) {
		super(type);
		if(!type.isObject()) {
			throw new IllegalArgumentException("DisguiseType must be an object");
		}
		typeId = getTypeId(type);
	}
	
	/**
	 * Gets the custom name of this entity.<br>
	 * The default value is <code>""</code>.
	 * 
	 * @since 5.6.1
	 * @return the custom name
	 */
	public String getCustomName() {
		return customName;
	}
	
	/**
	 * Sets the custom name of this entity.<br>
	 * The default value is <code>""</code>.
	 * 
	 * @since 5.6.1
	 * @param customName the custom name
	 */
	public void setCustomName(String customName) {
		if(customName == null) {
			customName = "";
		} else if(customName.length() > 64) {
			customName = customName.substring(0, 64);
		}
		this.customName = customName;
	}
	
	/**
	 * Indicates whether the custom name of this entity is visible all the time.<br>
	 * The default value is <code>true</code>.
	 * 
	 * @since 5.6.3
	 * @return <code>true</code>, if the custom name is visible all the time
	 */
	public boolean isCustomNameVisible() {
		return customNameVisible;
	}
	
	/**
	 * Sets whether the custom name of this entity is visible all the time.<br>
	 * The default value is <code>true</code>.
	 * This value has no effect if the custom name is empty.
	 * 
	 * @since 5.6.3
	 * @param customNameVisible <code>true</code>, if the custom name shall be visible all the time
	 */
	public void setCustomNameVisible(boolean customNameVisible) {
		this.customNameVisible = customNameVisible;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; custom-name=%s; %s", super.toString(), customName, customNameVisible ? "custom-name-visible" : "custom-name-invisible");
	}
	
	/**
	 * Gets the mob type id.<br>
	 * This id is used in the client/server communication.
	 * 
	 * @since 5.1.1
	 * @return the type id for this disguise
	 */
	public int getTypeId() {
		return typeId;
	}
	
	/**
	 * Gets the mob type id for a given {@linkplain DisguiseType}.<br>
	 * This id is used in the client/server communication.
	 * 
	 * @since 5.1.1
	 * @throws IllegalArgumentException if the given {@linkplain DisguiseType} is not an object
	 * @param type the disguise type
	 * @return the type id for the given disguise type
	 */
	public static int getTypeId(DisguiseType type) {
		if(!type.isObject()) {
			throw new IllegalArgumentException("DisguiseType must be an object");
		}
		switch(type) {
			case AREA_EFFECT_CLOUD:
				return 3;
			case ARMOR_STAND:
				return 78;
			case BOAT:
				return 1;
			case ENDER_CRYSTAL:
				return 51;
			case FALLING_BLOCK:
				return 70;
			case ITEM:
				return 2;
			case MINECART:
				return 10;
			default:
				return 0;
		}
	}
	
	static {
		Subtypes.registerParameterizedSubtype(ObjectDisguise.class, "setCustomName", "custom-name", String.class, Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("Hello\\sWorld!", "Notch", "I'm\\syour\\sfather"))));
		Subtypes.registerSubtype(ObjectDisguise.class, "setCustomNameVisible", true, "custom-name-visible");
		Subtypes.registerSubtype(ObjectDisguise.class, "setCustomNameVisible", false, "custom-name-invisible");
	}
	
}