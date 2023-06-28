package com.github.mouse0w0.peach.mcmod.element.editor;

import com.github.mouse0w0.peach.javafx.util.FXUtils;
import com.github.mouse0w0.peach.javafx.util.Validator;
import com.github.mouse0w0.peach.l10n.AppL10n;
import com.github.mouse0w0.peach.mcmod.ItemRef;
import com.github.mouse0w0.peach.mcmod.element.impl.MECraftingRecipe;
import com.github.mouse0w0.peach.mcmod.ui.control.ItemPicker;
import com.github.mouse0w0.peach.mcmod.ui.control.ItemStackView;
import com.github.mouse0w0.peach.mcmod.ui.control.ItemView;
import com.github.mouse0w0.peach.mcmod.util.ModUtils;
import com.github.mouse0w0.peach.project.Project;
import com.github.mouse0w0.peach.util.ArrayUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

public class CraftingRecipeEditor extends ElementEditor<MECraftingRecipe> {

    private static final Image BACKGROUND = new Image("/image/mcmod/crafting_recipe.png", 560, 312, true, false, true);

    @FXML
    private TextField identifier;
    @FXML
    private ChoiceBox<String> namespace;
    @FXML
    private ComboBox<String> group;
    @FXML
    private RadioButton shapeless;
    @FXML
    private AnchorPane recipeView;
    private ItemPicker[] inputs = new ItemPicker[9];
    private ItemStackView output;

    public CraftingRecipeEditor(@NotNull Project project, @NotNull MECraftingRecipe element) {
        super(project, element);
    }

    @Override
    protected Node getContent() {
        FlowPane root = FXUtils.loadFXML(null, this, "ui/mcmod/CraftingRecipe.fxml", AppL10n.getResourceBundle());

        Validator.error(identifier, ModUtils::validateIdentifier, AppL10n.localize("validate.invalidIdentifier"));

        group.setEditable(true);

        FXUtils.setFixedSize(recipeView, 560, 312);
        recipeView.setBackground(new Background(new BackgroundImage(BACKGROUND, null, null, null, null)));

        GridPane inputGridPane = new GridPane();
        AnchorPane.setTopAnchor(inputGridPane, 52d);
        AnchorPane.setLeftAnchor(inputGridPane, 52d);
        inputGridPane.setHgap(8);
        inputGridPane.setVgap(8);
        recipeView.getChildren().add(inputGridPane);
        for (int i = 0; i < 9; i++) {
            ItemView itemViews = inputs[i] = new ItemPicker(64, 64, true, true);
            itemViews.setPlayAnimation(true);
            inputGridPane.add(itemViews, i % 3, i / 3);
        }

        output = new ItemStackView();
        output.setFitSize(64, 64);
        FXUtils.setFixedSize(output, 72, 72);
        AnchorPane.setTopAnchor(output, 121d);
        AnchorPane.setLeftAnchor(output, 424d);
        recipeView.getChildren().add(output);

        return root;
    }

    @Override
    protected void initialize(MECraftingRecipe element) {
        identifier.setText(element.getIdentifier());
        namespace.setValue(element.getNamespace());
        group.setValue(element.getGroup());
        shapeless.setSelected(element.isShapeless());
        ArrayUtils.biForEach(inputs, element.getInputs(), ItemView::setItem);
        output.setItemStack(element.getOutput());
    }

    @Override
    protected void updateDataModel(MECraftingRecipe element) {
        element.setIdentifier(identifier.getText().trim());
        element.setNamespace(namespace.getValue());
        element.setGroup(group.getValue());
        element.setShapeless(shapeless.isSelected());
        element.setInputs(ArrayUtils.map(inputs, ItemView::getItem, ItemRef[]::new));
        element.setOutput(output.getItemStack());
    }

    @Override
    protected boolean validate() {
        return Validator.test(identifier);
    }

    @Override
    public void dispose() {
        super.dispose();
        for (ItemView input : inputs) {
            input.setPlayAnimation(false);
        }
    }
}
