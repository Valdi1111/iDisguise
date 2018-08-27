package org.valdi.entities.packets.handlers;

import static org.valdi.entities.management.Reflection.CraftChatMessage_fromComponent;
import static org.valdi.entities.management.Reflection.CraftChatMessage_fromString;
import static org.valdi.entities.management.Reflection.CraftOfflinePlayer_getProfile;
import static org.valdi.entities.management.Reflection.CraftPlayer_getProfile;
import static org.valdi.entities.management.Reflection.EnumChatFormat_WHITE;
import static org.valdi.entities.management.Reflection.PlayerInfoData_new;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.valdi.entities.iDisguise;
import org.valdi.entities.disguise.AgeableDisguise;
import org.valdi.entities.disguise.AreaEffectCloudDisguise;
import org.valdi.entities.disguise.ArmorStandDisguise;
import org.valdi.entities.disguise.BoatDisguise;
import org.valdi.entities.disguise.ChestedHorseDisguise;
import org.valdi.entities.disguise.CreeperDisguise;
import org.valdi.entities.disguise.Disguise;
import org.valdi.entities.disguise.DisguiseType;
import org.valdi.entities.disguise.EndermanDisguise;
import org.valdi.entities.disguise.FallingBlockDisguise;
import org.valdi.entities.disguise.HorseDisguise;
import org.valdi.entities.disguise.ItemDisguise;
import org.valdi.entities.disguise.LlamaDisguise;
import org.valdi.entities.disguise.MinecartDisguise;
import org.valdi.entities.disguise.MobDisguise;
import org.valdi.entities.disguise.ObjectDisguise;
import org.valdi.entities.disguise.OcelotDisguise;
import org.valdi.entities.disguise.ParrotDisguise;
import org.valdi.entities.disguise.PigDisguise;
import org.valdi.entities.disguise.PlayerDisguise;
import org.valdi.entities.disguise.RabbitDisguise;
import org.valdi.entities.disguise.SheepDisguise;
import org.valdi.entities.disguise.SizedDisguise;
import org.valdi.entities.disguise.StyledHorseDisguise;
import org.valdi.entities.disguise.VillagerDisguise;
import org.valdi.entities.disguise.WolfDisguise;
import org.valdi.entities.disguise.ZombieVillagerDisguise;
import org.valdi.entities.disguise.LlamaDisguise.SaddleColor;
import org.valdi.entities.management.DisguiseManager;
import org.valdi.entities.management.PacketHandler;
import org.valdi.entities.management.ProfileHelper;
import org.valdi.entities.management.VersionHelper;
import org.valdi.entities.management.profile.GameProfileHelper;
import org.valdi.entities.management.reflection.EntityHumanNonAbstract;
import org.valdi.entities.packets.ProtocolLibPacketListener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import net.minecraft.server.v1_12_R1.Block;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityAgeable;
import net.minecraft.server.v1_12_R1.EntityAreaEffectCloud;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EntityBat;
import net.minecraft.server.v1_12_R1.EntityBoat;
import net.minecraft.server.v1_12_R1.EntityBoat.EnumBoatType;
import net.minecraft.server.v1_12_R1.EntityCreeper;
import net.minecraft.server.v1_12_R1.EntityEnderman;
import net.minecraft.server.v1_12_R1.EntityFallingBlock;
import net.minecraft.server.v1_12_R1.EntityHorse;
import net.minecraft.server.v1_12_R1.EntityHorseChestedAbstract;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityItem;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EntityLlama;
import net.minecraft.server.v1_12_R1.EntityMinecartAbstract;
import net.minecraft.server.v1_12_R1.EntityOcelot;
import net.minecraft.server.v1_12_R1.EntityParrot;
import net.minecraft.server.v1_12_R1.EntityPig;
import net.minecraft.server.v1_12_R1.EntityRabbit;
import net.minecraft.server.v1_12_R1.EntitySheep;
import net.minecraft.server.v1_12_R1.EntitySlime;
import net.minecraft.server.v1_12_R1.EntityTameableAnimal;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.EntityVillager;
import net.minecraft.server.v1_12_R1.EntityWolf;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.EntityZombieVillager;
import net.minecraft.server.v1_12_R1.EnumColor;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.InventorySubcontainer;
import net.minecraft.server.v1_12_R1.MathHelper;
import net.minecraft.server.v1_12_R1.World;

public abstract class AbstractPlayOutEntitySpawn extends ProtocolLibPacketListener {

	protected AbstractPlayOutEntitySpawn(iDisguise addon, ListenerPriority listenerPriority,
			PacketType... arrpacketType) {
		super(addon, listenerPriority, arrpacketType);
	}
	
	protected List<PacketContainer> getSpawnPackets(LivingEntity livingEntity) {
		try {
			Disguise disguise = DisguiseManager.getDisguise(livingEntity);
			if(disguise == null) return null;
			
			EntityLiving entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
			DisguiseType type = disguise.getType();
			List<PacketContainer> packets = new ArrayList<>();
			
			if(disguise instanceof MobDisguise) {
				MobDisguise mobDisguise = (MobDisguise) disguise;
				EntityLiving entity = (EntityLiving) Class.forName(VersionHelper.getNMSPackage() + "." + type.getNMSClass()).getConstructor(World.class).newInstance(entityLiving.getWorld());
				
				if(PacketHandler.showOriginalPlayerName) {
					entity.setCustomName(livingEntity.getName());
					entity.setCustomNameVisible(true);
				} else if(mobDisguise.getCustomName() != null && !mobDisguise.getCustomName().isEmpty()) {
					entity.setCustomName(mobDisguise.getCustomName());
					entity.setCustomNameVisible(mobDisguise.isCustomNameVisible());
				}
				
				if(mobDisguise instanceof AgeableDisguise) {
					if(!((AgeableDisguise) mobDisguise).isAdult()) {	
						if(entity instanceof EntityAgeable) {
							((EntityAgeable) entity).setAge(-24000);
						} else if(entity instanceof EntityZombie) {
							((EntityZombie) entity).setBaby(true);
						}
					}
					
					if(mobDisguise instanceof HorseDisguise) {
						HorseDisguise horseDisguise = (HorseDisguise)mobDisguise;
						InventorySubcontainer inventoryChest = ((EntityLlama) entity).inventoryChest;
						inventoryChest.setItem(0, CraftItemStack.asNMSCopy(horseDisguise.isSaddled() ? new ItemStack(Material.SADDLE) : null));
						inventoryChest.setItem(1, CraftItemStack.asNMSCopy(horseDisguise.getArmor().getItem()));
						if(horseDisguise instanceof StyledHorseDisguise) {
							((EntityHorse) entity).setVariant(((StyledHorseDisguise)horseDisguise).getColor().ordinal() & 0xFF | ((StyledHorseDisguise)horseDisguise).getStyle().ordinal() << 8);
						} else if(horseDisguise instanceof ChestedHorseDisguise) {
							((EntityHorseChestedAbstract) entity).setCarryingChest(((ChestedHorseDisguise)horseDisguise).hasChest());
						}
					} else if(mobDisguise instanceof LlamaDisguise) {
						LlamaDisguise llamaDisguise = (LlamaDisguise)mobDisguise;
						((EntityLlama) entity).setVariant(llamaDisguise.getColor().ordinal());
						InventorySubcontainer inventoryChest = ((EntityLlama) entity).inventoryChest;
						inventoryChest.setItem(1, CraftItemStack.asNMSCopy(llamaDisguise.getSaddle().equals(SaddleColor.NOT_SADDLED) ? null : new ItemStack(Material.CARPET, 1, (short)llamaDisguise.getSaddle().ordinal())));
						((EntityLlama) entity).setCarryingChest(llamaDisguise.hasChest());
					} else if(mobDisguise instanceof OcelotDisguise) {
						OcelotDisguise ocelotDisguise = (OcelotDisguise)mobDisguise;
						((EntityOcelot) entity).setCatType(ocelotDisguise.getCatType().getId());
						((EntityTameableAnimal) entity).setSitting(ocelotDisguise.isSitting());
					} else if(mobDisguise instanceof PigDisguise) {
						((EntityPig) entity).setSaddle(((PigDisguise)mobDisguise).isSaddled());
					} else if(mobDisguise instanceof RabbitDisguise) {
						((EntityRabbit) entity).setRabbitType(((RabbitDisguise)mobDisguise).getRabbitType().getId());
					} else if(mobDisguise instanceof SheepDisguise) {
						((EntitySheep) entity).setColor(EnumColor.fromColorIndex(((SheepDisguise)mobDisguise).getColor().getWoolData()));
					} else if(mobDisguise instanceof VillagerDisguise) {
						((EntityVillager) entity).setProfession(((VillagerDisguise)mobDisguise).getProfession().ordinal());
					} else if(mobDisguise instanceof WolfDisguise) {
						WolfDisguise wolfDisguise = (WolfDisguise)mobDisguise;
						((EntityWolf) entity).setCollarColor(EnumColor.fromColorIndex(wolfDisguise.getCollarColor().getWoolData()));
						((EntityWolf) entity).setTamed(wolfDisguise.isTamed());
						((EntityWolf) entity).setAngry(wolfDisguise.isAngry());
						((EntityWolf) entity).setSitting(wolfDisguise.isSitting());
					} else if(mobDisguise instanceof ZombieVillagerDisguise) {
						((EntityZombieVillager) entity).setProfession(((ZombieVillagerDisguise)mobDisguise).getProfession().ordinal());
					}
				} else if(mobDisguise instanceof CreeperDisguise) {
					((EntityCreeper) entity).setPowered(((CreeperDisguise)mobDisguise).isPowered());
				} else if(mobDisguise instanceof EndermanDisguise) {
					EndermanDisguise endermanDisguise = (EndermanDisguise)mobDisguise;
					((EntityEnderman) entity).setCarried(Block.getById(endermanDisguise.getBlockInHand().getId()).fromLegacyData(endermanDisguise.getBlockInHandData()));
				} else if(mobDisguise instanceof ParrotDisguise) {
					ParrotDisguise parrotDisguise = (ParrotDisguise)mobDisguise;
					((EntityParrot) entity).setVariant(parrotDisguise.getVariant().ordinal());
					((EntityParrot) entity).setSitting(parrotDisguise.isSitting());
				} else if(mobDisguise instanceof SizedDisguise) {
					((EntitySlime) entity).setSize(((SizedDisguise)mobDisguise).getSize(), false);
				}
				
				if(entity instanceof EntityBat) {
					((EntityBat) entity).setAsleep(false);
				}
				
				Location loc = livingEntity.getLocation();
				entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
				entity.h(livingEntity.getEntityId()); // TODO change for 1.13 entity.setId(int id) -> f(int id)
				
				PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
				packet.getIntegers().write(0, entity.getId());
				packet.getUUIDs().write(0, entity.getUniqueID());
				packet.getIntegers().write(1, EntityTypes.b.a(entity.getClass()));
				packet.getDoubles().write(0, entity.locX);
				packet.getDoubles().write(1, entity.locY);
				packet.getDoubles().write(2, entity.locZ);
				packet.getBytes().write(0, (byte)(int)(entity.yaw * 256.0F / 360.0F));
				packet.getBytes().write(1, (byte)(int)(entity.pitch * 256.0F / 360.0F));
				packet.getBytes().write(2, (byte)(int)(entity.aP * 256.0F / 360.0F));
				double d2 = entity.motX;
				double d3 = entity.motY;
				double d4 = entity.motZ;
				if (d2 < -3.9D) {
					d2 = -3.9D;
				}
				if (d3 < -3.9D) {
					d3 = -3.9D;
				}
				if (d4 < -3.9D) {
					d4 = -3.9D;
				}
				if (d2 > 3.9D) {
					d2 = 3.9D;
				}
				if (d3 > 3.9D) {
					d3 = 3.9D;
				}
				if (d4 > 3.9D) {
					d4 = 3.9D;
				}
				packet.getIntegers().write(2, ((int)(d2 * 8000.0D)));
				packet.getIntegers().write(3, ((int)(d3 * 8000.0D)));
				packet.getIntegers().write(4, ((int)(d4 * 8000.0D)));
				packet.getDataWatcherModifier().write(0, WrappedDataWatcher.getEntityWatcher(entity.getBukkitEntity()));
				
				packets.add(packet);
			} else if(disguise instanceof PlayerDisguise) {
				if(livingEntity instanceof Player) {
					EntityHuman human = (EntityHuman) entityLiving;
					
					PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
					spawnPacket.getIntegers().write(0, human.getId());
					spawnPacket.getUUIDs().write(0, human.getProfile().getId());
					spawnPacket.getDoubles().write(0, human.locX);
					spawnPacket.getDoubles().write(1, human.locY);
					spawnPacket.getDoubles().write(2, human.locZ);
					spawnPacket.getBytes().write(0, (byte)(int)(human.yaw * 256.0F / 360.0F));
					spawnPacket.getBytes().write(1, (byte)(int)(human.pitch * 256.0F / 360.0F));
					spawnPacket.getDataWatcherModifier().write(0, WrappedDataWatcher.getEntityWatcher(human.getBukkitEntity()));
					
					// don't modify anything else here, skin is applied via player list item packet
					packets.add(spawnPacket);
				} else {
					PlayerDisguise playerDisguise = (PlayerDisguise) disguise;
					WrappedGameProfile gameProfile = GameProfileHelper.getInstance().getGameProfile(livingEntity.getUniqueId(), playerDisguise.getSkinName(), playerDisguise.getDisplayName());
					
					// send game profile to client
					PacketContainer playerInfoPacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
					playerInfoPacket.getPlayerInfoAction().write(0, PlayerInfoAction.ADD_PLAYER);
					List<PlayerInfoData> playerInfoList = new ArrayList<>();
					playerInfoList.add(new PlayerInfoData(gameProfile, 35, NativeGameMode.SURVIVAL, WrappedChatComponent.fromChatMessage(playerDisguise.getDisplayName())[0]));
					playerInfoPacket.getPlayerInfoDataLists().write(0, playerInfoList);
					packets.add(playerInfoPacket);
					
					// send entity to client
					EntityHuman human = new EntityHumanNonAbstract(entityLiving.getWorld(), gameProfile);
					Location loc = livingEntity.getLocation();
					human.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
					human.h(livingEntity.getEntityId()); // TODO change for 1.13 entity.setId(int id) -> f(int id)
					
					PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
					spawnPacket.getIntegers().write(0, human.getId());
					spawnPacket.getUUIDs().write(0, human.getProfile().getId());
					spawnPacket.getDoubles().write(0, human.locX);
					spawnPacket.getDoubles().write(1, human.locY);
					spawnPacket.getDoubles().write(2, human.locZ);
					spawnPacket.getBytes().write(0, (byte)(int)(human.yaw * 256.0F / 360.0F));
					spawnPacket.getBytes().write(1, (byte)(int)(human.pitch * 256.0F / 360.0F));
					spawnPacket.getDataWatcherModifier().write(0, WrappedDataWatcher.getEntityWatcher(human.getBukkitEntity()));

					packets.add(spawnPacket);
					
					if(!PacketHandler.modifyPlayerListEntry) {
						// remove player list entry
						PacketContainer pInfoPacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
						pInfoPacket.getPlayerInfoAction().write(0, PlayerInfoAction.REMOVE_PLAYER);
						List<PlayerInfoData> pInfoList = new ArrayList<>();
						pInfoList.add(new PlayerInfoData(gameProfile, 35, NativeGameMode.SURVIVAL, null));
						pInfoPacket.getPlayerInfoDataLists().write(0, pInfoList);
						packets.add(pInfoPacket);
					}
				}
			} else if(disguise instanceof ObjectDisguise) {
				ObjectDisguise objectDisguise = (ObjectDisguise) disguise;
				Entity entity = (Entity) Class.forName(VersionHelper.getNMSPackage() + "." + type.getNMSClass()).getConstructor(World.class).newInstance(entityLiving.getWorld());
				Location loc = livingEntity.getLocation();
				entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
				entity.h(livingEntity.getEntityId()); // TODO change for 1.13 entity.setId(int id) -> f(int id)

				if(PacketHandler.showOriginalPlayerName) {
					entity.setCustomName(livingEntity.getName());
					entity.setCustomNameVisible(true);
				} else if(objectDisguise.getCustomName() != null && !objectDisguise.getCustomName().isEmpty()) {
					entity.setCustomName(objectDisguise.getCustomName());
					entity.setCustomNameVisible(objectDisguise.isCustomNameVisible());
				}
				
				if(entity instanceof EntityBoat) {
					((EntityBoat) entity).setType(EnumBoatType.a(((BoatDisguise) objectDisguise).getBoatType().name().toLowerCase(Locale.ENGLISH)));
					packets.add(getEntitySpawnPacket(entity, objectDisguise.getTypeId(), 0));
					packets.add(getEntityMetaPacket(entity));
				} else if(entity instanceof EntityFallingBlock) {
					packets.add(getEntitySpawnPacket(entity, objectDisguise.getTypeId(), objectDisguise instanceof FallingBlockDisguise ? ((FallingBlockDisguise)objectDisguise).getMaterial().getId() | (((FallingBlockDisguise)objectDisguise).getData() << 12) : 1));
				} else if(entity instanceof EntityItem) {
					if(objectDisguise instanceof ItemDisguise) {
						ItemDisguise itemDisguise = (ItemDisguise) objectDisguise;
						((EntityItem) entity).setItemStack(CraftItemStack.asNMSCopy(itemDisguise.getItemStack()));
					}
					packets.add(getEntitySpawnPacket(entity, objectDisguise.getTypeId(), 0));
					packets.add(getEntityMetaPacket(entity));
				} else if(entity instanceof EntityMinecartAbstract) {
					if(objectDisguise instanceof MinecartDisguise) {
						MinecartDisguise minecartDisguise = (MinecartDisguise) objectDisguise;
						((EntityMinecartAbstract) entity).setDisplayBlock(Block.getById(minecartDisguise.getDisplayedBlock().getId()).fromLegacyData(minecartDisguise.getDisplayedBlockData()));
					}
					packets.add(getEntitySpawnPacket(entity, objectDisguise.getTypeId(), 0));
					packets.add(getEntityMetaPacket(entity));
				} else if(entity instanceof EntityArmorStand) {
					if(objectDisguise instanceof ArmorStandDisguise) {
						((EntityArmorStand) entity).setArms(((ArmorStandDisguise) objectDisguise).getShowArms());
					}
					packets.add(getEntitySpawnPacket(entity, objectDisguise.getTypeId(), 0));
					packets.add(getEntityMetaPacket(entity));
				} else if(entity instanceof EntityAreaEffectCloud) {
					if(objectDisguise instanceof AreaEffectCloudDisguise) {
						AreaEffectCloudDisguise aecDisguise = (AreaEffectCloudDisguise) objectDisguise;
						((EntityAreaEffectCloud) entity).setRadius(aecDisguise.getRadius());
						((EntityAreaEffectCloud) entity).setColor(aecDisguise.getColor().asRGB());
						((EntityAreaEffectCloud) entity).setParticle(EnumParticle.valueOf(aecDisguise.getParticle().name()));
					}
					packets.add(getEntitySpawnPacket(entity, objectDisguise.getTypeId(), 0));
					packets.add(getEntityMetaPacket(entity));
				} else {
					packets.add(getEntitySpawnPacket(entity, objectDisguise.getTypeId(), 0));
				}
			}
			return packets;
		} catch(Exception e) {
			iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot construct the required packet.", e);
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	private UUID formatUniqueId(UUID origin) {
		return PacketHandler.bungeeCord ? new UUID(origin.getMostSignificantBits() & 0xFFFFFFFFFFFF0FFFL | 0x0000000000005000, origin.getLeastSignificantBits()) : origin;
	}
	
	private PacketContainer getEntitySpawnPacket(Entity entity, int type, int data) {
		PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
		spawnPacket.getIntegers().write(0, entity.getId());
		spawnPacket.getUUIDs().write(0, entity.getUniqueID());
		spawnPacket.getDoubles().write(0, entity.locX);
		spawnPacket.getDoubles().write(1, entity.locY);
		spawnPacket.getDoubles().write(2, entity.locZ);
		spawnPacket.getIntegers().write(1, MathHelper.d(entity.pitch * 256.0F / 360.0F));
		spawnPacket.getIntegers().write(2, MathHelper.d(entity.yaw * 256.0F / 360.0F));
		spawnPacket.getIntegers().write(3, type);
		spawnPacket.getIntegers().write(4, data);
		spawnPacket.getIntegers().write(5, (int) (MathHelper.a(entity.motX, -3.9D, 3.9D) * 8000.0D));
		spawnPacket.getIntegers().write(6, (int) (MathHelper.a(entity.motY, -3.9D, 3.9D) * 8000.0D));
		spawnPacket.getIntegers().write(7, (int) (MathHelper.a(entity.motZ, -3.9D, 3.9D) * 8000.0D));
		
		return spawnPacket;
	}
	
	private PacketContainer getEntityMetaPacket(Entity entity) {
		PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
		metaPacket.getIntegers().write(0, entity.getId());
		WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity.getBukkitEntity());
		metaPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
		
		return metaPacket;
	}
	
	private Object getPlayerInfo(OfflinePlayer offlinePlayer, Object context, int ping, Object gamemode, Object displayName) {
		Disguise disguise = DisguiseManager.getDisguise(offlinePlayer);
		try {
			if(disguise == null) {
				return PlayerInfoData_new.newInstance(context, offlinePlayer.isOnline() ? CraftPlayer_getProfile.invoke(offlinePlayer) : CraftOfflinePlayer_getProfile.invoke(offlinePlayer), ping, gamemode, displayName);
			} else if(disguise instanceof PlayerDisguise) {
				if(PacketHandler.modifyPlayerListEntry) {
					return PlayerInfoData_new.newInstance(context,
							ProfileHelper.getInstance().getGameProfile(formatUniqueId(offlinePlayer.getUniqueId()), ((PlayerDisguise)disguise).getSkinName(), ((PlayerDisguise)disguise).getDisplayName()),
							ping, gamemode, displayName != null ?
									Array.get(CraftChatMessage_fromString.invoke(null, ((String)CraftChatMessage_fromComponent.invoke(null, displayName, EnumChatFormat_WHITE.get(null))).replace(offlinePlayer.getName(), ((PlayerDisguise)disguise).getDisplayName())), 0) :
									null);
				} else {
					return PlayerInfoData_new.newInstance(context,
							ProfileHelper.getInstance().getGameProfile(formatUniqueId(offlinePlayer.getUniqueId()), ((PlayerDisguise)disguise).getSkinName(), ((PlayerDisguise)disguise).getDisplayName()),
							ping, gamemode, displayName != null ? displayName : Array.get(CraftChatMessage_fromString.invoke(null, offlinePlayer.isOnline() ? offlinePlayer.getPlayer().getPlayerListName() : offlinePlayer.getName()), 0));
				}
			} else if(!PacketHandler.modifyPlayerListEntry) {
				return PlayerInfoData_new.newInstance(context, offlinePlayer.isOnline() ? CraftPlayer_getProfile.invoke(offlinePlayer) : CraftOfflinePlayer_getProfile.invoke(offlinePlayer), ping, gamemode, displayName);
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot construct the required player info.", e);
			}
		}
		return null;
	}

}
