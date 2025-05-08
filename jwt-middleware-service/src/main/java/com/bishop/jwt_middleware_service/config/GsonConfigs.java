package com.bishop.jwt_middleware_service.config;

import com.google.gson.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

@Configuration
public class GsonConfigs {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                // Custom serializer for LocalDateTime to ensure it's rendered as ISO-8601 string (e.g., 2025-05-07T17:30:00)
                .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                    @Override
                    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.toString());
                    }
                })
                .disableHtmlEscaping() // Prevent escaping of characters like '=' in Base64
                .setPrettyPrinting()
                .create();
    }
}
