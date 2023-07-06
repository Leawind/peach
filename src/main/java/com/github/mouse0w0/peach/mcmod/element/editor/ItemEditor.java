package com.github.mouse0w0.peach.mcmod.element.editor;

import com.github.mouse0w0.peach.form.ColSpan;
import com.github.mouse0w0.peach.form.Form;
import com.github.mouse0w0.peach.form.FormView;
import com.github.mouse0w0.peach.form.Section;
import com.github.mouse0w0.peach.form.field.*;
import com.github.mouse0w0.peach.javafx.util.Check;
import com.github.mouse0w0.peach.l10n.AppL10n;
import com.github.mouse0w0.peach.mcmod.*;
import com.github.mouse0w0.peach.mcmod.element.impl.MEItem;
import com.github.mouse0w0.peach.mcmod.index.IndexManager;
import com.github.mouse0w0.peach.mcmod.index.Indexes;
import com.github.mouse0w0.peach.mcmod.ui.LocalizableConverter;
import com.github.mouse0w0.peach.mcmod.ui.cell.LocalizableCell;
import com.github.mouse0w0.peach.mcmod.ui.cell.LocalizableWithItemIconCell;
import com.github.mouse0w0.peach.mcmod.ui.form.*;
import com.github.mouse0w0.peach.mcmod.util.ModUtils;
import com.github.mouse0w0.peach.mcmod.util.ResourceStore;
import com.github.mouse0w0.peach.mcmod.util.ResourceUtils;
import com.github.mouse0w0.peach.project.Project;
import com.github.mouse0w0.peach.util.StringUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

public class ItemEditor extends ElementEditor<MEItem> {

    private final IndexManager indexManager;

    private Form form;

    // Properties
    private TextFieldField identifier;
    private TextFieldField displayName;
    private ComboBoxField<ItemType> type;
    private ComboBoxField<ItemGroup> itemGroup;
    private SpinnerField<Integer> maxStackSize;
    private SpinnerField<Integer> durability;
    private ChoiceBoxField<EquipmentSlot> equipmentSlot;
    private ToolAttributesField toolAttributes;
    private SpinnerField<Double> destroySpeed;
    private RadioButtonField canDestroyAnyBlock;
    private SpinnerField<Double> attackDamage;
    private SpinnerField<Double> attackSpeed;
    private SpinnerField<Integer> enchantability;
    private CheckComboBoxField<EnchantmentType> acceptableEnchantments;
    private AttributeModifiersField attributeModifiers;
    private ItemPickerField repairItem;
    private ItemPickerField recipeRemain;
    private ComboBoxField<UseAnimation> useAnimation;
    private SpinnerField<Integer> useDuration;
    private SpinnerField<Integer> hitEntityLoss;
    private SpinnerField<Integer> destroyBlockLoss;
    private TextAreaField information;

    // Appearance
    private ModelField model;
    private ModelTextureField textures;
    private RadioButtonField hasEffect;
    private TextureField armorTexture;

    // Extra
    private SpinnerField<Integer> fuelBurnTime;
    private ChoiceBoxField<SoundEvent> equipSound;
    private SpinnerField<Integer> hunger;
    private SpinnerField<Double> saturation;
    private RadioButtonField isWolfFood;
    private RadioButtonField alwaysEdible;
    private ItemPickerField foodContainer;

    public ItemEditor(@NotNull Project project, @NotNull MEItem element) {
        super(project, element);
        indexManager = IndexManager.getInstance(project);
    }

    @Override
    protected Node getContent() {
        form = new Form();

        Section properties = new Section();
        properties.setText(AppL10n.localize("item.properties.title"));

        identifier = new TextFieldField();
        identifier.getChecks().add(Check.of(AppL10n.localize("validate.invalidIdentifier"), ModUtils::validateIdentifier));
        identifier.setText(AppL10n.localize("item.properties.identifier"));
        identifier.setColSpan(ColSpan.HALF);

        displayName = new TextFieldField();
        displayName.setText(AppL10n.localize("item.properties.displayName"));
        displayName.setColSpan(ColSpan.HALF);

        type = new ComboBoxField<>();
        type.setText(AppL10n.localize("item.properties.type"));
        type.setCellFactory(LocalizableCell.factory());
        type.setButtonCell(new LocalizableCell<>());
        type.getItems().setAll(ItemType.values());
        type.setColSpan(ColSpan.HALF);
        BooleanBinding isFood = type.valueProperty().isEqualTo(ItemType.FOOD);
        BooleanBinding isNotFood = type.valueProperty().isNotEqualTo(ItemType.FOOD);
        BooleanBinding isArmor = type.valueProperty().isEqualTo(ItemType.ARMOR);
        BooleanBinding isNotArmor = type.valueProperty().isNotEqualTo(ItemType.ARMOR);
        BooleanBinding isArmorOrFood = isArmor.or(isFood);

        isFood.addListener(observable -> {
            if (isFood.get()) {
                durability.setValue(0);
                useAnimation.setValue(UseAnimation.EAT);
                useDuration.setValue(32);
            } else {
                useAnimation.setValue(UseAnimation.NONE);
                useDuration.setValue(0);
            }
        });

        itemGroup = new ComboBoxField<>();
        itemGroup.setText(AppL10n.localize("item.properties.itemGroup"));
        itemGroup.setCellFactory(LocalizableWithItemIconCell.factory());
        itemGroup.setButtonCell(LocalizableWithItemIconCell.create());
        itemGroup.getItems().setAll(indexManager.getIndex(Indexes.ITEM_GROUPS).values());
        itemGroup.setColSpan(ColSpan.HALF);

        maxStackSize = new SpinnerField<>(1, 64, 64);
        maxStackSize.setText(AppL10n.localize("item.properties.maxStackSize"));
        maxStackSize.setColSpan(ColSpan.HALF);
        maxStackSize.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            ItemType type = this.type.getValue();
            if (type == ItemType.NORMAL || type == ItemType.FOOD) {
                maxStackSize.setValue(64);
                return false;
            } else {
                maxStackSize.setValue(1);
                return true;
            }
        }, type.valueProperty()));

        durability = new SpinnerField<>(0, Integer.MAX_VALUE, 0);
        durability.setText(AppL10n.localize("item.properties.durability"));
        durability.setColSpan(ColSpan.HALF);
        durability.disableProperty().bind(isFood);

        equipmentSlot = new ChoiceBoxField<>();
        equipmentSlot.setText(AppL10n.localize("item.properties.equipmentSlot"));
        equipmentSlot.setConverter(LocalizableConverter.instance());
        equipmentSlot.getItems().setAll(EquipmentSlot.values());
        equipmentSlot.setColSpan(ColSpan.HALF);
        equipmentSlot.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            final ItemType type = this.type.getValue();
            if (type == ItemType.FOOD) {
                equipmentSlot.getItems().setAll(EquipmentSlot.values());
                equipmentSlot.setValue(EquipmentSlot.NONE);
                return true;
            } else if (type == ItemType.NORMAL) {
                equipmentSlot.getItems().setAll(EquipmentSlot.values());
                equipmentSlot.setValue(EquipmentSlot.NONE);
            } else if (type == ItemType.SWORD || type == ItemType.TOOL) {
                equipmentSlot.getItems().setAll(EquipmentSlot.HAND_SLOTS);
                equipmentSlot.setValue(EquipmentSlot.MAINHAND);
            } else if (type == ItemType.ARMOR) {
                equipmentSlot.getItems().setAll(EquipmentSlot.ARMOR_SLOTS);
                equipmentSlot.setValue(EquipmentSlot.HEAD);
            }
            return false;
        }, type.valueProperty()));

        toolAttributes = new ToolAttributesField();
        toolAttributes.setText(AppL10n.localize("item.properties.toolAttributes"));
        toolAttributes.setColSpan(ColSpan.HALF);
        toolAttributes.disableProperty().bind(isArmorOrFood);

        destroySpeed = new SpinnerField<>(0.0, Double.MAX_VALUE, 0D);
        destroySpeed.setText(AppL10n.localize("item.properties.destroySpeed"));
        destroySpeed.setColSpan(ColSpan.HALF);
        destroySpeed.disableProperty().bind(isArmorOrFood);

        canDestroyAnyBlock = new RadioButtonField();
        canDestroyAnyBlock.setText(AppL10n.localize("item.properties.canDestroyAnyBlock"));
        canDestroyAnyBlock.setColSpan(ColSpan.HALF);
        canDestroyAnyBlock.disableProperty().bind(isArmorOrFood);

        attackDamage = new SpinnerField<>(-Double.MAX_VALUE, Double.MAX_VALUE, 1D);
        attackDamage.setText(AppL10n.localize("item.properties.attackDamage"));
        attackDamage.setColSpan(ColSpan.HALF);

        attackSpeed = new SpinnerField<>(-Double.MAX_VALUE, Double.MAX_VALUE, 4D);
        attackSpeed.setText(AppL10n.localize("item.properties.attackSpeed"));
        attackSpeed.setColSpan(ColSpan.HALF);
        type.valueProperty().addListener(observable -> {
            ItemType type = this.type.getValue();
            if (type == ItemType.SWORD) {
                attackSpeed.setValue(1.6D);
            } else if (type == ItemType.TOOL) {
                attackSpeed.setValue(1D);
            } else {
                attackSpeed.setValue(4D);
            }
        });

        enchantability = new SpinnerField<>(0, Integer.MAX_VALUE, 0);
        enchantability.setText(AppL10n.localize("item.properties.enchantability"));
        enchantability.setColSpan(ColSpan.HALF);
        enchantability.disableProperty().bind(isFood);

        acceptableEnchantments = new CheckComboBoxField<>();
        acceptableEnchantments.setText(AppL10n.localize("item.properties.acceptableEnchantments"));
        acceptableEnchantments.setConverter(LocalizableConverter.instance());
        acceptableEnchantments.getItems().setAll(EnchantmentType.values());
        acceptableEnchantments.setColSpan(ColSpan.HALF);
        acceptableEnchantments.disableProperty().bind(isFood);

        attributeModifiers = new AttributeModifiersField();
        attributeModifiers.setText(AppL10n.localize("item.properties.attributeModifiers"));
        attributeModifiers.disableProperty().bind(equipmentSlot.valueProperty().isEqualTo(EquipmentSlot.NONE));

        repairItem = new ItemPickerField();
        repairItem.setText(AppL10n.localize("item.properties.repairItem"));
        repairItem.setFitSize(32, 32);
        repairItem.setColSpan(ColSpan.HALF);
        repairItem.disableProperty().bind(isFood);

        recipeRemain = new ItemPickerField();
        recipeRemain.setText(AppL10n.localize("item.properties.recipeRemain"));
        recipeRemain.setFitSize(32, 32);
        recipeRemain.setColSpan(ColSpan.HALF);

        useAnimation = new ComboBoxField<>();
        useAnimation.setText(AppL10n.localize("item.properties.useAnimation"));
        useAnimation.setCellFactory(LocalizableCell.factory());
        useAnimation.setButtonCell(new LocalizableCell<>());
        useAnimation.getItems().setAll(UseAnimation.values());
        useAnimation.setColSpan(ColSpan.HALF);

        useDuration = new SpinnerField<>(0, Integer.MAX_VALUE, 0);
        useDuration.setText(AppL10n.localize("item.properties.useDuration"));
        useDuration.setColSpan(ColSpan.HALF);

        hitEntityLoss = new SpinnerField<>(0, Integer.MAX_VALUE, 0);
        hitEntityLoss.setText(AppL10n.localize("item.properties.hitEntityLoss"));
        hitEntityLoss.setColSpan(ColSpan.HALF);
        hitEntityLoss.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            if (durability.getValue() == 0) return true;
            ItemType type = this.type.getValue();
            if (type == ItemType.NORMAL) {
                hitEntityLoss.setValue(0);
                return false;
            } else if (type == ItemType.TOOL) {
                hitEntityLoss.setValue(2);
                return false;
            } else if (type == ItemType.SWORD) {
                hitEntityLoss.setValue(1);
                return false;
            } else {
                hitEntityLoss.setValue(0);
                return true;
            }
        }, type.valueProperty(), durability.valueProperty()));

        destroyBlockLoss = new SpinnerField<>(0, Integer.MAX_VALUE, 0);
        destroyBlockLoss.setText(AppL10n.localize("item.properties.destroyBlockLoss"));
        destroyBlockLoss.setColSpan(ColSpan.HALF);
        destroyBlockLoss.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            if (durability.getValue() == 0) return true;
            ItemType type = this.type.getValue();
            if (type == ItemType.NORMAL) {
                destroyBlockLoss.setValue(0);
                return false;
            } else if (type == ItemType.TOOL) {
                destroyBlockLoss.setValue(1);
                return false;
            } else if (type == ItemType.SWORD) {
                destroyBlockLoss.setValue(2);
                return false;
            } else {
                destroyBlockLoss.setValue(0);
                return true;
            }
        }, type.valueProperty(), durability.valueProperty()));

        information = new TextAreaField();
        information.setText(AppL10n.localize("item.properties.information"));

        properties.getElements().addAll(
                identifier, displayName,
                type, itemGroup,
                maxStackSize, durability,
                equipmentSlot, toolAttributes,
                destroySpeed, canDestroyAnyBlock,
                attackDamage, attackSpeed,
                enchantability, acceptableEnchantments,
                attributeModifiers,
                repairItem, recipeRemain,
                useAnimation, useDuration,
                hitEntityLoss, destroyBlockLoss,
                information);

        Section appearance = new Section();
        appearance.setText(AppL10n.localize("item.appearance.title"));

        model = new ModelField(getProject(), new ResourceStore(
                ResourceUtils.getResourcePath(getProject(), ResourceUtils.MODELS),
                ResourceUtils.getResourcePath(getProject(), ResourceUtils.ITEM_MODELS), ".json"));
        model.setText(AppL10n.localize("item.appearance.model"));
        model.setBlockstate("item");

        textures = new ModelTextureField(new ResourceStore(
                ResourceUtils.getResourcePath(getProject(), ResourceUtils.TEXTURES),
                ResourceUtils.getResourcePath(getProject(), ResourceUtils.ITEM_TEXTURES), ".png"));
        textures.setText(AppL10n.localize("item.appearance.texture"));
        model.getTextures().addListener((InvalidationListener) observable -> textures.setTextureKeys(model.getTextures()));

        hasEffect = new RadioButtonField();
        hasEffect.setText(AppL10n.localize("item.appearance.hasEffect"));
        hasEffect.setColSpan(ColSpan.HALF);

        armorTexture = new TextureField(new ResourceStore(
                ResourceUtils.getResourcePath(getProject(), ResourceUtils.TEXTURES),
                ResourceUtils.getResourcePath(getProject(), ResourceUtils.ARMOR_TEXTURES)));
        armorTexture.setFitSize(128, 64);
        armorTexture.setText(AppL10n.localize("item.appearance.armorTexture"));
        armorTexture.visibleProperty().bind(isArmor);

        appearance.getElements().addAll(model, textures, hasEffect, armorTexture);

        Section extra = new Section();
        extra.setText(AppL10n.localize("item.extra.title"));

        fuelBurnTime = new SpinnerField<>(0, Integer.MAX_VALUE, 0);
        fuelBurnTime.setText(AppL10n.localize("item.fuel.fuelBurnTime"));
        fuelBurnTime.setColSpan(ColSpan.HALF);

        equipSound = new ChoiceBoxField<>();
        equipSound.setText(AppL10n.localize("item.armor.equipSound"));
        equipSound.setColSpan(ColSpan.HALF);
        equipSound.setConverter(LocalizableConverter.instance());
        equipSound.getItems().addAll(IndexManager.getInstance(getProject()).getIndex(Indexes.SOUND_EVENTS).values());
        equipSound.disableProperty().bind(isNotArmor);

        hunger = new SpinnerField<>(0, Integer.MAX_VALUE, 0);
        hunger.setText(AppL10n.localize("item.food.hunger"));
        hunger.setColSpan(ColSpan.HALF);
        hunger.disableProperty().bind(isNotFood);

        saturation = new SpinnerField<>(0.0, Double.MAX_VALUE, 0.6);
        saturation.setText(AppL10n.localize("item.food.saturation"));
        saturation.setColSpan(ColSpan.HALF);
        saturation.disableProperty().bind(isNotFood);

        isWolfFood = new RadioButtonField();
        isWolfFood.setText(AppL10n.localize("item.food.isWolfFood"));
        isWolfFood.setColSpan(ColSpan.HALF);
        isWolfFood.disableProperty().bind(isNotFood);

        alwaysEdible = new RadioButtonField();
        alwaysEdible.setText(AppL10n.localize("item.food.alwaysEdible"));
        alwaysEdible.setColSpan(ColSpan.HALF);
        alwaysEdible.disableProperty().bind(isNotFood);

        foodContainer = new ItemPickerField();
        foodContainer.setText(AppL10n.localize("item.food.foodContainer"));
        foodContainer.setFitSize(32, 32);
        foodContainer.setColSpan(ColSpan.HALF);
        foodContainer.disableProperty().bind(isNotFood);

        extra.getElements().addAll(fuelBurnTime, equipSound, hunger, saturation, isWolfFood, alwaysEdible, foodContainer);

        form.getGroups().addAll(properties, appearance, extra);

        return new FormView(form);
    }

    @Override
    protected void initialize(MEItem item) {
        identifier.setValue(item.getIdentifier());
        displayName.setValue(item.getDisplayName());
        type.setValue(item.getType());
        itemGroup.setValue(item.getItemGroup());
        maxStackSize.setValue(item.getMaxStackSize());
        durability.setValue(item.getDurability());
        equipmentSlot.setValue(item.getEquipmentSlot());
        toolAttributes.setValue(item.getToolAttributes());
        destroySpeed.setValue(item.getDestroySpeed());
        canDestroyAnyBlock.setValue(item.isCanDestroyAnyBlock());
        attackDamage.setValue(item.getAttackDamage());
        attackSpeed.setValue(item.getAttackSpeed());
        attributeModifiers.setValue(item.getAttributeModifiers());
        enchantability.setValue(item.getEnchantability());
        acceptableEnchantments.setValue(item.getAcceptableEnchantments());
        repairItem.setValue(item.getRepairItem());
        recipeRemain.setValue(item.getRecipeRemain());
        useAnimation.setValue(item.getUseAnimation());
        useDuration.setValue(item.getUseDuration());
        hitEntityLoss.setValue(item.getHitEntityLoss());
        destroyBlockLoss.setValue(item.getDestroyBlockLoss());
        final String[] array = item.getInformation();
        information.setValue(StringUtils.join(array, System.lineSeparator()));

        model.setModel(item.getModel());
        model.setCustomModels(item.getCustomModels());
        textures.setTextures(item.getTextures());
        hasEffect.setValue(item.isHasEffect());
        armorTexture.setTexture(item.getArmorTexture());

        fuelBurnTime.setValue(item.getFuelBurnTime());
        equipSound.setValue(item.getEquipSound());
        hunger.setValue(item.getHunger());
        saturation.setValue(item.getSaturation());
        isWolfFood.setValue(item.isWolfFood());
        alwaysEdible.setValue(item.isAlwaysEdible());
        foodContainer.setValue(item.getFoodContainer());
    }

    @Override
    protected void updateDataModel(MEItem item) {
        item.setIdentifier(identifier.getValue().trim());
        item.setDisplayName(displayName.getValue().trim());
        item.setType(type.getValue());
        item.setItemGroup(itemGroup.getValue());
        item.setMaxStackSize(maxStackSize.getValue());
        item.setDurability(durability.getValue());
        item.setEquipmentSlot(equipmentSlot.getValue());
        item.setToolAttributes(toolAttributes.getValue());
        item.setDestroySpeed(destroySpeed.getValue());
        item.setCanDestroyAnyBlock(canDestroyAnyBlock.getValue());
        item.setAttackDamage(attackDamage.getValue());
        item.setAttackSpeed(attackSpeed.getValue());
        item.setAttributeModifiers(attributeModifiers.getValue());
        item.setEnchantability(enchantability.getValue());
        item.setAcceptableEnchantments(acceptableEnchantments.getValue(EnchantmentType[]::new));
        item.setRepairItem(repairItem.getValue());
        item.setRecipeRemain(recipeRemain.getValue());
        item.setUseAnimation(useAnimation.getValue());
        item.setUseDuration(useDuration.getValue());
        item.setHitEntityLoss(hitEntityLoss.getValue());
        item.setDestroyBlockLoss(destroyBlockLoss.getValue());
        final String str = information.getValue();
        item.setInformation(StringUtils.splitByLineSeparator(str));

        item.setModel(model.getModel());
        item.setCustomModels(model.getCustomModels());
        item.setTextures(textures.getTextures());
        item.setHasEffect(hasEffect.getValue());
        item.setArmorTexture(armorTexture.getTexture());

        item.setFuelBurnTime(fuelBurnTime.getValue());
        item.setEquipSound(equipSound.getValue());
        item.setHunger(hunger.getValue());
        item.setSaturation(saturation.getValue());
        item.setWolfFood(isWolfFood.getValue());
        item.setAlwaysEdible(alwaysEdible.getValue());
        item.setFoodContainer(foodContainer.getValue());
    }

    @Override
    protected boolean validate() {
        return form.validate();
    }
}
