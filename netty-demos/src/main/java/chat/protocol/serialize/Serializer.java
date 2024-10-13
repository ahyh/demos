package chat.protocol.serialize;

import com.google.gson.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public interface Serializer {

    /**
     * 反序列化方法
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    /**
     * 序列化方法
     */
    <T> byte[] serialize(T object);

    enum Algorithm implements Serializer {

        JDK {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                     ObjectInputStream ois = new ObjectInputStream(bais);) {
                    T obj = (T) ois.readObject();
                    return obj;
                } catch (Exception e) {
                    throw new RuntimeException("deserialize error");
                }
            }

            @Override
            public <T> byte[] serialize(T object) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                     ObjectOutputStream oos = new ObjectOutputStream(baos);) {
                    oos.writeObject(object);
                    return baos.toByteArray();
                } catch (Exception e) {
                    throw new RuntimeException("serialize error");
                }
            }
        },

        JSON {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                String jsonStr = new String(bytes, StandardCharsets.UTF_8);
                return new Gson().fromJson(jsonStr, clazz);
            }

            @Override
            public <T> byte[] serialize(T object) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassJsonConverter()).create();
                String jsonStr = gson.toJson(object);
                return jsonStr.getBytes(StandardCharsets.UTF_8);
            }
        }

    }

    static class ClassJsonConverter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                String str = jsonElement.getAsString();
                return Class.forName(str);
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> src, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(src.getName());
        }
    }
}
