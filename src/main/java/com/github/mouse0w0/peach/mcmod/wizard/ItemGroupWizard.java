package com.github.mouse0w0.peach.mcmod.wizard;

import com.github.mouse0w0.peach.mcmod.element.Element;
import com.github.mouse0w0.peach.mcmod.element.ItemGroup;
import com.github.mouse0w0.peach.mcmod.wizard.step.ItemGroupStep;
import com.github.mouse0w0.peach.project.Project;

public class ItemGroupWizard extends ElementWizard<ItemGroup> {

    public ItemGroupWizard(Project project, Element<ItemGroup> element) {
        super(project, element);
        addWizardStep(new ItemGroupStep(element));
    }
}
