package giphouse.nl.proprapp.dagger;

import android.util.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Serializer to encode byte[] as base64 string (taken from https://gist.github.com/orip/3635246)
 * @author haye
 */
public class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

	public byte[] deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {

		return Base64.decode(json.getAsString(), Base64.NO_WRAP);
	}

	public JsonElement serialize(final byte[] src, final Type typeOfSrc, final JsonSerializationContext context) {
		return new JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP));
	}
}
