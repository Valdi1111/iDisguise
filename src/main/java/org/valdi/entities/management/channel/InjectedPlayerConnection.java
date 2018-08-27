package org.valdi.entities.management.channel;

@Deprecated
public interface InjectedPlayerConnection {

	@Deprecated
	public void resetToDefaultConnection() throws Exception;

	@Deprecated
	public void sendPacket(Object packet);

	@Deprecated
	public void sendPacketDirectly(Object packet);
	
}