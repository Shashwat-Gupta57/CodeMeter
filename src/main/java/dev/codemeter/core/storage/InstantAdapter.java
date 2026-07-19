package dev.codemeter.core.storage;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

/**
 * Gson adapter for java.time.Instant serialization/deserialization.
 */
public class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

    @Override
    public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return Instant.parse(json.getAsString());
        } catch (Exception e) {
            // Try parsing as epoch millis
            try {
                return Instant.ofEpochMilli(json.getAsLong());
            } catch (Exception e2) {
                throw new JsonParseException("Cannot parse Instant: " + json, e2);
            }
        }
    }
}
