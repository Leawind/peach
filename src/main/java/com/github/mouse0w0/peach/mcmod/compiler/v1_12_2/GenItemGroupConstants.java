package com.github.mouse0w0.peach.mcmod.compiler.v1_12_2;

import com.github.mouse0w0.peach.mcmod.compiler.Context;
import com.github.mouse0w0.peach.mcmod.compiler.task.Task;
import com.github.mouse0w0.peach.mcmod.compiler.v1_12_2.bytecode.ItemGroupsClassGenerator;
import com.github.mouse0w0.peach.mcmod.element.ElementTypes;
import com.github.mouse0w0.peach.mcmod.element.impl.MEBlock;
import com.github.mouse0w0.peach.mcmod.element.impl.MEItem;

public class GenItemGroupConstants implements Task {
    @Override
    public void run(Context context) throws Exception {
        ItemGroupsClassGenerator groups = new ItemGroupsClassGenerator(context.getInternalName("init/ItemGroups"));

        for (MEBlock block : context.getElements(ElementTypes.BLOCK)) {
            groups.visitItemGroup(block.getItemGroup().getId());
        }

        for (MEItem item : context.getElements(ElementTypes.ITEM)) {
            groups.visitItemGroup(item.getItemGroup().getId());
        }

        context.getClassesFiler().write(groups.getThisName() + ".class", groups.toByteArray());
    }
}
