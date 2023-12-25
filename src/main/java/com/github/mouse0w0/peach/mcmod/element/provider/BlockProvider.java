package com.github.mouse0w0.peach.mcmod.element.provider;

import com.github.mouse0w0.peach.fileEditor.FileEditor;
import com.github.mouse0w0.peach.mcmod.IdMetadata;
import com.github.mouse0w0.peach.mcmod.Identifier;
import com.github.mouse0w0.peach.mcmod.Item;
import com.github.mouse0w0.peach.mcmod.element.editor.BlockEditor;
import com.github.mouse0w0.peach.mcmod.element.impl.BlockElement;
import com.github.mouse0w0.peach.mcmod.index.IndexManager;
import com.github.mouse0w0.peach.mcmod.index.IndexProvider;
import com.github.mouse0w0.peach.mcmod.index.IndexTypes;
import com.github.mouse0w0.peach.mcmod.model.ModelManager;
import com.github.mouse0w0.peach.mcmod.project.ModProjectService;
import com.github.mouse0w0.peach.mcmod.util.ResourceUtils;
import com.github.mouse0w0.peach.project.Project;
import com.google.common.collect.Iterables;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BlockProvider extends ElementProvider<BlockElement> {
    public BlockProvider() {
        super(BlockElement.class, "block");
    }

    @Override
    public BlockElement newElement(Project project, Path file, String identifier, String name) {
        final IndexManager indexManager = IndexManager.getInstance(project);
        final BlockElement block = new BlockElement();
        block.setFile(file);
        block.setIdentifier(identifier);
        block.setDisplayName(name);
        block.setItemGroup(Iterables.getFirst(indexManager.getIndex(IndexTypes.ITEM_GROUP).keySet(), null));
        block.setMaterial("minecraft:rock");
        block.setSoundType("minecraft:stone");
        block.setMapColor("inherit");
        block.setModel(new Identifier("minecraft:cube_all"));
        block.setItemModel(ModelManager.INHERIT);
        return block;
    }

    @Override
    public FileEditor newEditor(Project project, BlockElement element) {
        return new BlockEditor(project, element);
    }

    @Override
    public Object[] addIndex(Project project, IndexProvider provider, BlockElement element) {
        if (element.isDoNotRegisterItem()) return null;

        String modId = ModProjectService.getInstance(project).getModId();

        Item item = new Item(element.getIdentifier(), 0, null, false);
        item.setLocalizedText(element.getDisplayName());
        item.setImage(ResourceUtils.CUBE_TEXTURE);

        Map<IdMetadata, List<Item>> items = provider.getIndex(IndexTypes.ITEM);
        IdMetadata item1 = IdMetadata.of(modId + ":" + item.getId(), item.getMetadata());
        items.put(item1, Collections.singletonList(item));
        IdMetadata item2 = IdMetadata.ignoreMetadata(modId + ":" + item.getId());
        items.put(item2, Collections.singletonList(item));
        return new Object[]{item1, item2};
    }

    @Override
    public void removeIndex(Project project, IndexProvider provider, Object[] objects) {
        Map<IdMetadata, List<Item>> items = provider.getIndex(IndexTypes.ITEM);
        items.remove(objects[0]);
        items.remove(objects[1]);
    }
}
