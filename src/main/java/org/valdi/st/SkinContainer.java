package org.valdi.st;

import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SkinContainer {
	private UUID uuid;
	private String value;
	private String signature;

	public SkinContainer(JsonObject jsonObject) {
		this.uuid = UUID.fromString(jsonObject.get("id").getAsString());
		for(JsonElement element : jsonObject.get("properties").getAsJsonArray()) {
			JsonObject object = element.getAsJsonObject();
			if(object.get("name").getAsString().equals("textures")) {
				this.value = object.get("value").getAsString();
				this.signature = object.get("signature").getAsString();
			}
		}
	}

	public UUID getId() {
		return uuid;
	}

	public String getValue() {
		return value;
	}

	public String getSignature() {
		return signature;
	}

}
