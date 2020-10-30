package com.github.mouse0w0.peach.ui.project;

import com.github.mouse0w0.peach.project.Project;
import com.sun.istack.internal.NotNull;

public interface StatusBar {
    enum Position {
        LEFT, CENTER, RIGHT
    }

    enum Anchor {
        BEFORE, AFTER
    }

    Project getProject();

    StatusBarWidget getWidget(String id);

    boolean hasWidget(String id);

    void addWidget(@NotNull StatusBarWidget widget);

    void addWidget(@NotNull StatusBarWidget widget, @NotNull Position position);

    void addWidget(@NotNull StatusBarWidget widget, Anchor anchor, String anchorId);

    void addWidget(@NotNull StatusBarWidget widget, Position position, Anchor anchor, String anchorId);

    boolean removeWidget(String id);
}
