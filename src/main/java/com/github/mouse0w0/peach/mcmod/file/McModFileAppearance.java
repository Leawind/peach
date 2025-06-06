package com.github.mouse0w0.peach.mcmod.file;

import com.github.mouse0w0.peach.file.FileAppearance;
import com.github.mouse0w0.peach.file.FileCell;
import com.github.mouse0w0.peach.icon.AppIcon;
import com.github.mouse0w0.peach.l10n.AppL10n;
import com.github.mouse0w0.peach.mcmod.element.ElementRegistry;
import com.github.mouse0w0.peach.mcmod.element.provider.ElementProvider;
import com.github.mouse0w0.peach.mcmod.project.ModProjectMetadata;
import com.github.mouse0w0.peach.project.Project;
import com.github.mouse0w0.peach.util.FileUtils;
import com.github.mouse0w0.peach.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class McModFileAppearance implements FileAppearance {
    private final Map<Path, String> localizablePathMap = new HashMap<>();

    public McModFileAppearance() {
        localizablePathMap.put(Paths.get("build"), "mod.folder.build");
        localizablePathMap.put(Paths.get("elements"), "mod.folder.element");
        localizablePathMap.put(Paths.get("resources"), "mod.folder.resource");
        localizablePathMap.put(Paths.get("resources/models"), "mod.folder.model");
        localizablePathMap.put(Paths.get("resources/models/block"), "mod.folder.model.block");
        localizablePathMap.put(Paths.get("resources/models/item"), "mod.folder.model.item");
        localizablePathMap.put(Paths.get("resources/textures"), "mod.folder.texture");
        localizablePathMap.put(Paths.get("resources/textures/armor"), "mod.folder.texture.armor");
        localizablePathMap.put(Paths.get("resources/textures/armors"), "mod.folder.texture.armor");
        localizablePathMap.put(Paths.get("resources/textures/block"), "mod.folder.texture.block");
        localizablePathMap.put(Paths.get("resources/textures/blocks"), "mod.folder.texture.block");
        localizablePathMap.put(Paths.get("resources/textures/item"), "mod.folder.texture.item");
        localizablePathMap.put(Paths.get("resources/textures/items"), "mod.folder.texture.item");
        localizablePathMap.put(Paths.get("resources/textures/gui"), "mod.folder.texture.gui");
    }

    @Override
    public boolean apply(Project project, Path file, FileCell cell) {
        final String fileName = FileUtils.getFileName(file);
        if (Files.isDirectory(file)) {
            final String localizationKey = localizablePathMap.get(project.getPath().relativize(file));
            if (localizationKey != null) {
                cell.setText(AppL10n.localize(localizationKey, fileName));
                cell.setIcon(AppIcon.File.Folder);
                return true;
            }
        } else {
            if (ModProjectMetadata.FILE_NAME.equals(fileName)) {
                cell.setText(AppL10n.localize("mod.file.metadata"));
                cell.setIcon(AppIcon.File.Forge);
                return true;
            } else {
                ElementProvider<?> provider = ElementRegistry.getInstance().getElementProvider(file);
                if (provider != null) {
                    cell.setText(StringUtils.substringBefore(fileName, '.') + " (" + AppL10n.localize(provider.getTranslationKey()) + ")");
                    cell.setIcon(AppIcon.File.ModElement);
                    return true;
                }
            }
        }
        return false;
    }
}
