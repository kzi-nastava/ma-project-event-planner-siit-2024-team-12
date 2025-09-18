package com.example.eventplanner.adapters.datetime;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.format.DateTimeParseException;

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
        String durationString = json.getAsString();
        try {
            return Duration.parse(durationString);
        } catch (DateTimeParseException e) {
            try {
                return Duration.ofMinutes(Long.parseLong(durationString));
            } catch (NumberFormatException ex) {
                throw new JsonParseException("Neuspešno parsiranje duration-a. Očekivan je ISO 8601 format (PTxHxM) ili broj, a dobijena vrednost: " + durationString, ex);
            }
        }
    }
}