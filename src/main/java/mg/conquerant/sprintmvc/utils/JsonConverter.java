package mg.conquerant.sprintmvc.utils;

import java.io.InputStream;

import java.lang.reflect.Type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonConverter {
    private final ObjectMapper objectMapper;

    public JsonConverter() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String toJson(Object toConvert) throws JsonProcessingException {
        return this.objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(toConvert);
    }

    public Object toObject(InputStream jsonData, Type targetType) {
        try {
            JavaType type = objectMapper.getTypeFactory().constructType(targetType);
            return objectMapper.readValue(jsonData, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
