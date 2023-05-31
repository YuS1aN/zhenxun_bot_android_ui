package me.kbai.zhenxunui.api;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * @author sean 2020/9/8
 */
public class ErrorHandleAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new ErrorHandleTypeAdapter<>(delegate, type);
    }

    private static class ErrorHandleTypeAdapter<T> extends TypeAdapter<T> {
        TypeAdapter<T> delegate;
        TypeToken<T> type;

        ErrorHandleTypeAdapter(TypeAdapter<T> delegate, TypeToken<T> type) {
            this.delegate = delegate;
            this.type = type;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            delegate.write(out, value);
        }

        @Override
        public T read(JsonReader in) {
            try {
                return delegate.read(in);
            } catch (Exception e) {
                e.printStackTrace();
                consumeAll(in);
            }
            return null;
        }
    }

    private static void consumeAll(JsonReader in) {
        try {
            if (in.hasNext()) {
                JsonToken peek = in.peek();
                if (peek == JsonToken.STRING) {
                    in.nextString();
                } else if (peek == JsonToken.BEGIN_ARRAY) {
                    in.beginArray();
                    consumeAll(in);
                    in.endArray();
                } else if (peek == JsonToken.BEGIN_OBJECT) {
                    in.beginObject();
                    while (in.peek() != JsonToken.END_OBJECT) {
                        consumeAll(in);
                    }
                    in.endObject();
                } else if (peek == JsonToken.END_ARRAY) {
                    in.endArray();
                } else if (peek == JsonToken.END_OBJECT) {
                    in.endObject();
                } else if (peek == JsonToken.NUMBER) {
                    in.nextString();
                } else if (peek == JsonToken.BOOLEAN) {
                    in.nextBoolean();
                } else if (peek == JsonToken.NAME) {
                    in.nextName();
                    consumeAll(in);
                } else if (peek == JsonToken.NULL) {
                    in.nextNull();
                }
            }
        } catch (IOException e) {
            //
        }
    }
}
