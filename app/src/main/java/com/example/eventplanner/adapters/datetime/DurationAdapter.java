package com.example.eventplanner.adapters.datetime;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Duration;

public class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {

    @Override
    public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }
        return new JsonPrimitive(src.toMinutes());
    }

    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }
        try {
            return Duration.ofMinutes(json.getAsLong());
        } catch (NumberFormatException e) {
            throw new JsonParseException("Neuspešno parsiranje duration-a. Očekivan je broj, a dobijena vrednost: " + json.getAsString(), e);
        }
    }
}