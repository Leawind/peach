package com.github.mouse0w0.peach.mcmod.model.json;

import com.google.gson.JsonElement;

public class Override {
    private JsonElement predicate;
    private String model;

    public JsonElement getPredicate() {
        return predicate;
    }

    public void setPredicate(JsonElement predicate) {
        this.predicate = predicate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
