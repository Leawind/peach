package com.github.mouse0w0.peach;

import com.github.mouse0w0.eventbus.EventBus;
import com.github.mouse0w0.eventbus.SimpleEventBus;
import com.github.mouse0w0.eventbus.asm.AsmEventListenerFactory;
import com.github.mouse0w0.i18n.I18n;
import com.github.mouse0w0.i18n.Translator;
import com.github.mouse0w0.i18n.source.ClasspathFileTranslationSource;
import com.github.mouse0w0.peach.event.AppEvent;
import com.github.mouse0w0.peach.forge.ForgeProjectService;
import com.github.mouse0w0.peach.project.ProjectManager;
import com.github.mouse0w0.peach.service.ServiceManagerImpl;
import com.github.mouse0w0.peach.ui.FXApplication;
import com.github.mouse0w0.peach.ui.project.WindowManager;
import com.github.mouse0w0.version.Version;
import javafx.application.Application;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class Peach extends ServiceManagerImpl {

    private static final EventBus EVENT_BUS = SimpleEventBus.builder()
            .eventListenerFactory(AsmEventListenerFactory.create()).build();

    private static Peach INSTANCE;

    private final Version version = new Version(getImplementationVersion());

    private final Path userPropertiesPath = Paths.get(SystemUtils.USER_HOME, ".peach");

    public static EventBus getEventBus() {
        return EVENT_BUS;
    }

    public static Peach getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        INSTANCE = new Peach();
        I18n.setTranslator(Translator.builder()
                .locale(Locale.getDefault())
                .source(new ClasspathFileTranslationSource("lang"))
                .build());
        INSTANCE.initServices();
        Application.launch(FXApplication.class, args);
    }

    public Version getVersion() {
        return version;
    }

    public Path getUserPropertiesPath() {
        return userPropertiesPath;
    }

    public void exit() {
        exit(false);
    }

    public void exit(boolean force) {
        getEventBus().post(new AppEvent.Closing());

        if (!force && getEventBus().post(new AppEvent.CanClose())) return;

        ProjectManager.getInstance().closeAllProjects();
        getEventBus().post(new AppEvent.WillBeClosed());
        System.exit(0);
    }

    private void initServices() {
        registerService(RecentProjectsManager.class, new RecentProjectsManager());
        registerService(WindowManager.class, new WindowManager());
        registerService(ProjectManager.class, ProjectManager::new, false);
        registerService(ForgeProjectService.class, new ForgeProjectService());
    }

    private String getImplementationVersion() {
        String version = Peach.class.getPackage().getImplementationVersion();
        return version != null && !version.isEmpty() ? version : "99.99.999.999999-Indev";
    }
}
