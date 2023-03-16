package com.github.mouse0w0.peach.extension;

import com.github.mouse0w0.peach.plugin.ExtensionDescriptor;
import com.github.mouse0w0.peach.plugin.ExtensionPointDescriptor;
import com.github.mouse0w0.peach.plugin.Plugin;
import com.github.mouse0w0.peach.plugin.PluginManagerCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Extensions {
    private static final Logger LOGGER = LoggerFactory.getLogger("Extension");

    private static final Map<String, ExtensionPointImpl<?>> POINTS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> ExtensionPoint<T> getPoint(String name) {
        return (ExtensionPoint<T>) POINTS.get(name);
    }

    public static void loadExtensions() {
        for (Plugin plugin : PluginManagerCore.getEnabledPlugins()) {
            String pluginId = plugin.getId();
            ClassLoader classLoader = plugin.getClassLoader();
            for (ExtensionPointDescriptor extensionPoint : plugin.getExtensionPoints()) {
                String pointName = pluginId + "." + extensionPoint.getName();
                String extensionClassName = extensionPoint.getClassName();
                Class<?> extensionClass = null;
                try {
                    extensionClass = Class.forName(extensionClassName, false, classLoader);
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Not found class {} when registering extension point {}, plugin={}.", extensionClassName, pointName, pluginId);
                }
                POINTS.put(pointName, new ExtensionPointImpl<>(plugin, pointName, extensionClass, extensionPoint.isBean()));
            }

            for (Map.Entry<String, List<ExtensionDescriptor>> entry : plugin.getExtensions().entrySet()) {
                ExtensionPointImpl<?> point = POINTS.get(entry.getKey());
                if (point != null) {
                    for (ExtensionDescriptor extension : entry.getValue()) {
                        point.register(plugin, extension);
                    }
                } else {
                    LOGGER.error("Not found extension point {} when registering extension, plugin={}.", entry.getKey(), pluginId);
                }
            }
        }
    }
}
