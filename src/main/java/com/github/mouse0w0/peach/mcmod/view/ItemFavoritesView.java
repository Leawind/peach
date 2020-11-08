package com.github.mouse0w0.peach.mcmod.view;

import com.github.mouse0w0.gridview.GridView;
import com.github.mouse0w0.gridview.cell.GridCell;
import com.github.mouse0w0.peach.component.PersistentComponent;
import com.github.mouse0w0.peach.mcmod.Item;
import com.github.mouse0w0.peach.mcmod.ui.control.ItemView;
import com.github.mouse0w0.peach.project.Project;
import com.github.mouse0w0.peach.util.JsonUtils;
import com.github.mouse0w0.peach.view.ViewFactory;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.TransferMode;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemFavoritesView implements PersistentComponent {

    private static ItemFavoritesView getInstance(Project project) {
        return project.getService(ItemFavoritesView.class);
    }

    private final ObservableList<Item> items = FXCollections.observableArrayList();
    private final Set<Item> itemSet = new HashSet<>();

    private GridView<Item> content;

    public Node initViewContent() {
        content = new GridView<>();
        content.setId("item-favorites");
        content.setCellSize(32, 32);
        content.setCellSpacing(0, 0);
        content.setCellFactory(view -> new Cell());
        content.setItems(items);
        content.setOnDragOver(event -> {
            event.consume();
            if (event.getGestureSource() == event.getTarget()) return;

            Item item = (Item) event.getDragboard().getContent(ItemView.ITEM);
            if (item == null) return;

            event.acceptTransferModes(TransferMode.LINK);
        });
        content.setOnDragDropped(event -> {
            event.consume();
            Item item = (Item) event.getDragboard().getContent(ItemView.ITEM);
            if (itemSet.add(item)) {
                items.add(item);
            }
            event.setDropCompleted(true);
        });
        return content;
    }

    @Nonnull
    @Override
    public String getStorageFile() {
        return "itemFavorites.json";
    }

    @Override
    public JsonElement serialize() {
        return JsonUtils.toJson(items);
    }

    @Override
    public void deserialize(JsonElement jsonElement) {
        items.addAll(JsonUtils.fromJson(jsonElement, new TypeToken<List<Item>>() {
        }));
        itemSet.addAll(items);
    }

    private class Cell extends GridCell<Item> {
        private final ItemView itemView = new ItemView(32, 32);

        public Cell() {
            setGraphic(itemView);
        }

        @Override
        protected void updateItem(Item item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                itemView.setItem(null);
            } else {
                itemView.setItem(item);
            }
        }
    }

    public static class Factory implements ViewFactory {
        @Override
        public Node createViewContent(Project project) {
            return ItemFavoritesView.getInstance(project).initViewContent();
        }
    }
}
