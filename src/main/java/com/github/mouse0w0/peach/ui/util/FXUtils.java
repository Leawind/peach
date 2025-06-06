package com.github.mouse0w0.peach.ui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.function.Function;

public final class FXUtils {
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static void loadFXML(Object root, String location, ResourceBundle resources) {
        loadFXML(root, root, STACK_WALKER.getCallerClass().getClassLoader(), location, resources);
    }

    public static <T> T loadFXML(Object root, Object controller, String location, ResourceBundle resourceBundle) {
        return loadFXML(root, controller, STACK_WALKER.getCallerClass().getClassLoader(), location, resourceBundle);
    }

    public static <T> T loadFXML(Object root, Object controller, ClassLoader classLoader, String location, ResourceBundle resourceBundle) {
        URL resource = classLoader.getResource(location);
        if (resource == null) {
            throw new IllegalArgumentException("Not found resource, location=" + location);
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setRoot(root);
        loader.setController(controller);
        loader.setClassLoader(classLoader);
        loader.setLocation(resource);
        loader.setResources(resourceBundle);
        loader.setCharset(StandardCharsets.UTF_8);
        try {
            return loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load fxml", e);
        }
    }

    public static void addStylesheet(Scene scene, String location) {
        ClassLoader classLoader = STACK_WALKER.getCallerClass().getClassLoader();
        URL resource = classLoader.getResource(location);
        if (resource == null) {
            throw new IllegalArgumentException("Not found resource, location=" + location);
        }
        scene.getStylesheets().add(resource.toExternalForm());
    }

    public static void addStylesheet(Parent parent, String location) {
        ClassLoader classLoader = STACK_WALKER.getCallerClass().getClassLoader();
        URL resource = classLoader.getResource(location);
        if (resource == null) {
            throw new IllegalArgumentException("Not found resource, location=" + location);
        }
        parent.getStylesheets().add(resource.toExternalForm());
    }

    public static Window getOwner(Window window) {
        if (window instanceof Stage) {
            return ((Stage) window).getOwner();
        } else if (window instanceof PopupWindow) {
            return ((PopupWindow) window).getOwnerWindow();
        } else {
            throw new UnsupportedOperationException("getOwner does not support " + window.getClass().getName());
        }
    }

    public static void hideWindow(Node node) {
        node.getScene().getWindow().hide();
    }

    public static void hideWindow(Scene scene) {
        scene.getWindow().hide();
    }

    public static void setFixedSize(Region region, double width, double height) {
        region.setMinSize(width, height);
        region.setPrefSize(width, height);
        region.setMaxSize(width, height);
    }

    public static void setFixedWidth(Region region, double width) {
        region.setMinWidth(width);
        region.setPrefWidth(width);
        region.setMaxWidth(width);
    }

    public static void setFixedHeight(Region region, double height) {
        region.setMinHeight(height);
        region.setPrefHeight(height);
        region.setMaxHeight(height);
    }

    public static void setFitToAnchorPane(Node node) {
        AnchorPane.setTopAnchor(node, 0D);
        AnchorPane.setLeftAnchor(node, 0D);
        AnchorPane.setBottomAnchor(node, 0D);
        AnchorPane.setRightAnchor(node, 0D);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProperty(Node node, Object key) {
        return node.hasProperties() ? (T) node.getProperties().get(key) : null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T computeProperty(Node node, Object key, Function<Object, Object> mappingFunction) {
        return (T) node.getProperties().computeIfAbsent(key, mappingFunction);
    }

    public static void setProperty(Node node, Object key, Object value) {
        if (value == null) {
            if (node.hasProperties()) {
                node.getProperties().remove(key);
            }
        } else {
            node.getProperties().put(key, value);
        }
    }

    private FXUtils() {
    }
}
