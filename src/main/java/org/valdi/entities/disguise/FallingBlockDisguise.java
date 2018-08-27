package org.valdi.entities.disguise;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Material;
import org.valdi.entities.management.VersionHelper;

/**
 * Represents a disguise as a falling block.
 * 
 * @since 5.1.1
 * @author RobinGrether
 */
public class FallingBlockDisguise extends ObjectDisguise {
	
	private Material material;
	private int data;
	private boolean onlyBlockCoordinates;
	
	/**
	 * Creates an instance.<br>
	 * The default material is {@link Material#STONE}
	 * 
	 * @since 5.1.1
	 */
	public FallingBlockDisguise() {
		this(Material.STONE);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.1.1
	 * @param material the material
	 * @throws IllegalArgumentException if the material is not a block
	 */
	public FallingBlockDisguise(Material material) {
		this(material, 0);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.2.2
	 * @param material the material
	 * @param data the block data
	 * @throws IllegalArgumentException if the material is not a block, or if the data is negative
	 */
	public FallingBlockDisguise(Material material, int data) {
		this(material, data, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.4.1
	 * @param material the material
	 * @param data the block data
	 * @param onlyBlockCoordinates makes the disguise appear on block coordinates only, so it looks like an actual block that you can't target
	 * @throws IllegalArgumentException if the material is not a block, or if the data is negative
	 */
	public FallingBlockDisguise(Material material, int data, boolean onlyBlockCoordinates) {
		super(DisguiseType.FALLING_BLOCK);
		if(!material.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		if(INVALID_MATERIALS.contains(material)) {
			throw new IllegalArgumentException("Material is invalid! Disguise would be invisible.");
		}
		if(data < 0) {
			throw new IllegalArgumentException("Data must be positive");
		}
		this.material = material;
		this.data = data;
		this.onlyBlockCoordinates = onlyBlockCoordinates;
	}
	
	/**
	 * Gets the material.
	 * 
	 * @since 5.1.1
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * Sets the material.<br>
	 * This also resets the data to 0.
	 * 
	 * @since 5.1.1
	 * @param material the material
	 * @throws IllegalArgumentException if the material is not a block
	 */
	public void setMaterial(Material material) {
		if(!material.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		if(INVALID_MATERIALS.contains(material)) {
			throw new IllegalArgumentException("Material is invalid! Disguise would be invisible.");
		}
		this.material = material;
		this.data = 0;
	}
	
	/**
	 * Gets the block data.
	 * 
	 * @since 5.2.2
	 * @return the block data
	 */
	public int getData() {
		return data;
	}
	
	/**
	 * Sets the block data.
	 * 
	 * @since 5.2.2
	 * @param data the block data
	 */
	public void setData(int data) {
		if(data < 0) {
			throw new IllegalArgumentException("Data must be positive");
		}
		this.data = data;
	}
	
	/**
	 * Indicates whether this disguise may appear only on block coordinates.
	 * 
	 * @since 5.4.1
	 * @return <code>true</code>, if this disguise may appear only on block coordinates
	 */
	public boolean onlyBlockCoordinates() {
		return onlyBlockCoordinates;
	}
	
	/**
	 * Sets whether this disguise may appear only on block coordinates.
	 * 
	 * @since 5.4.1
	 * @param onlyBlockCoordinates makes this disguise appear on block coordinates only
	 */
	public void setOnlyBlockCoordinates(boolean onlyBlockCoordinates) {
		this.onlyBlockCoordinates = onlyBlockCoordinates;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; material=%s; material-data=%s; %s", super.toString(), material.name().toLowerCase(Locale.ENGLISH).replace('_', '-'), data, onlyBlockCoordinates ? "block-coordinates" : "all-coordinates");
	}
	
	/**
	 * A set containing all invalid materials.<br>
	 * These materials are <em>invalid</em> because the associated disguise would be invisible.
	 * 
	 * @since 5.7.1
	 */
	public static final Set<Material> INVALID_MATERIALS;
	
	static {
		Set<Material> tempSet = new HashSet<Material>(Arrays.asList(Material.AIR, Material.BARRIER, Material.BED_BLOCK, Material.CHEST,
				Material.COBBLE_WALL, Material.ENDER_CHEST, Material.ENDER_PORTAL, Material.LAVA, Material.MELON_STEM, Material.PISTON_MOVING_PIECE, Material.PORTAL,
				Material.PUMPKIN_STEM, Material.SIGN_POST, Material.SKULL, Material.STANDING_BANNER, Material.STATIONARY_LAVA, Material.STATIONARY_WATER, Material.TRAPPED_CHEST,
				Material.WALL_BANNER, Material.WALL_SIGN, Material.WATER));
		if(VersionHelper.require1_9()) {
			tempSet.add(Material.END_GATEWAY);
		}
		if(VersionHelper.require1_10()) {
			tempSet.add(Material.STRUCTURE_VOID);
		}
		if(VersionHelper.require1_11()) {
			tempSet.addAll(Arrays.asList(Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.CYAN_SHULKER_BOX,
					Material.GRAY_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.ORANGE_SHULKER_BOX,
					Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.SILVER_SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX));
		}
		INVALID_MATERIALS = Collections.unmodifiableSet(tempSet);
		
		Set<String> parameterSuggestions = new HashSet<String>();
		for(Material material : Material.values()) {
			if(material.isBlock() && !INVALID_MATERIALS.contains(material)) {
				parameterSuggestions.add(material.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
			}
		}
		Subtypes.registerParameterizedSubtype(FallingBlockDisguise.class, "setMaterial", "material", Material.class, Collections.unmodifiableSet(parameterSuggestions));
		
		Subtypes.registerParameterizedSubtype(FallingBlockDisguise.class, "setData", "material-data", int.class);
		Subtypes.registerSubtype(FallingBlockDisguise.class, "setOnlyBlockCoordinates", true, "block-coordinates");
		Subtypes.registerSubtype(FallingBlockDisguise.class, "setOnlyBlockCoordinates", false, "all-coordinates");
	}
	
}