package com.github.mouse0w0.peach.mcmod.wizard.step;

import com.github.mouse0w0.peach.mcmod.content.ContentManager;
import com.github.mouse0w0.peach.mcmod.contentPack.data.ItemGroupData;
import com.github.mouse0w0.peach.mcmod.element.ElementFile;
import com.github.mouse0w0.peach.mcmod.element.ItemElement;
import com.github.mouse0w0.peach.mcmod.ui.cell.ItemGroupCell;
import com.github.mouse0w0.peach.mcmod.util.ModUtils;
import com.github.mouse0w0.peach.project.Project;
import com.github.mouse0w0.peach.ui.project.WindowManager;
import com.github.mouse0w0.peach.ui.util.FXUtils;
import com.github.mouse0w0.peach.ui.util.FXValidator;
import com.github.mouse0w0.peach.ui.wizard.WizardStep;
import com.github.mouse0w0.peach.util.FileUtils;
import com.google.common.base.Strings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

public class ItemStep extends FlowPane implements WizardStep {

    private final ElementFile<ItemElement> file;

    @FXML
    private TextField registerName;
    @FXML
    private TextField displayName;
    @FXML
    private ComboBox<ItemGroupData> itemGroup;
    @FXML
    private Spinner<Integer> maxStackSize;
    @FXML
    private RadioButton effect;
    @FXML
    private TextArea information;

    public ItemStep(ElementFile<ItemElement> file) {
        this.file = file;

        FXUtils.loadFXML(this, "ui/mcmod/ItemElement.fxml");

        maxStackSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 64, 64));

        Project project = WindowManager.getInstance().getFocusedWindow().getProject();
        ContentManager contentManager = ContentManager.getInstance(project);

        itemGroup.setCellFactory(listView -> new ItemGroupCell());
        itemGroup.getItems().addAll(contentManager.getItemGroupMap().values());
        itemGroup.setButtonCell(new ItemGroupCell());
        itemGroup.getSelectionModel().selectFirst();
    }

    @Override
    public Node getContent() {
        return this;
    }

    @Override
    public void init() {
        Project project = WindowManager.getInstance().getFocusedWindow().getProject();
        ContentManager contentManager = ContentManager.getInstance(project);

        ItemElement item = file.get();

        registerName.setText(Strings.isNullOrEmpty(item.getRegisterName()) ?
                ModUtils.fileNameToRegisterName(file.getFile()) : item.getRegisterName());
        displayName.setText(Strings.isNullOrEmpty(item.getDisplayName()) ?
                FileUtils.getFileNameWithoutExtensionName(file.getFile()) : item.getDisplayName());
        ItemGroupData itemGroupData = contentManager.getItemGroup(item.getItemGroup());
        if (itemGroupData != null) itemGroup.getSelectionModel().select(itemGroupData);
        else itemGroup.getSelectionModel().selectFirst();
        maxStackSize.getValueFactory().setValue(item.getMaxStackSize());
        effect.setSelected(item.isEffect());
        information.setText(item.getInformation());
    }

    @Override
    public boolean validate() {
        if (!FXValidator.validate(registerName, "ui.item_element.error.register_name", ModUtils::validRegisterName))
            return false;
        return true;
    }

    @Override
    public void updateDataModel() {
        ItemElement item = file.get();

        item.setRegisterName(registerName.getText());
        item.setDisplayName(displayName.getText());
        item.setItemGroup(itemGroup.getValue().getId());
        item.setMaxStackSize(maxStackSize.getValue());
        item.setEffect(effect.isSelected());
        item.setInformation(information.getText());
    }

    @Override
    public void dispose() {
        // Nothing to do
    }
}
