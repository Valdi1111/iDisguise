package org.valdi.entities.management.reflection;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.World;

public class EntityHumanNonAbstract extends EntityHuman {
	
	public EntityHumanNonAbstract(World world, WrappedGameProfile gameProfile) {
		super(world, (GameProfile) gameProfile.getHandle());
	}
	
	public boolean isSpectator() { return false; }
	
	public boolean z() { return false; }
	
}