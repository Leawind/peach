package com.github.mouse0w0.peach.mcmod;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.regex.Pattern;

@JsonAdapter(Identifier.TypeAdapter.class)
public final class Identifier implements Comparable<Identifier> {
    public static final Pattern VALID_NAMESPACE = Pattern.compile("[a-z0-9._-]+");
    public static final Pattern VALID_PATH = Pattern.compile("[a-z0-9/._-]+");

    public static final String PROJECT_NAMESPACE = "project";

    private final String namespace;
    private final String path;

    public static Identifier of(String identifier) {
        int separatorIndex = identifier.indexOf(':');
        if (separatorIndex == -1) {
            return new Identifier("minecraft", identifier);
        } else {
            return new Identifier(identifier.substring(0, separatorIndex), identifier.substring(separatorIndex + 1));
        }
    }

    public static Identifier of(String namespace, String path) {
        return new Identifier(namespace, path);
    }

    public static Identifier project(String name) {
        return new Identifier(PROJECT_NAMESPACE, name);
    }

    private Identifier(String namespace, String path) {
        if (namespace == null) throw new NullPointerException("namespace");
        if (namespace.isEmpty()) throw new IllegalArgumentException("namespace is empty");
        if (path == null) throw new NullPointerException("path");
        if (path.isEmpty()) throw new IllegalArgumentException("path is empty");
        this.namespace = namespace;
        this.path = path;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return path;
    }

    public boolean isProjectNamespace() {
        return PROJECT_NAMESPACE.equals(namespace);
    }

    public boolean isValid() {
        return VALID_NAMESPACE.matcher(namespace).matches() && VALID_PATH.matcher(path).matches();
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Identifier that = (Identifier) o;

        if (!namespace.equals(that.namespace)) return false;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        int result = namespace.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }

    @Override
    public int compareTo(Identifier o) {
        int result = namespace.compareTo(o.namespace);
        return result == 0 ? path.compareTo(o.path) : result;
    }

    public static final class TypeAdapter extends com.google.gson.TypeAdapter<Identifier> {
        @Override
        public void write(JsonWriter out, Identifier value) throws IOException {
            if (value == null) out.nullValue();
            else out.value(value.toString());
        }

        @Override
        public Identifier read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else {
                return Identifier.of(in.nextString());
            }
        }
    }
}
