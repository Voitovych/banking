package application;

import application.module.DomainModule;
import application.module.RestModule;
import application.module.StorageModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import rest.RestInitializer;

public class Application {
    public static final Injector INJECTOR = Guice.createInjector(
            new DomainModule(),
            new RestModule(),
            new StorageModule()
    );

    public static void main(String[] args) {
        final RestInitializer restInitializer = INJECTOR.getInstance(RestInitializer.class);
        restInitializer.initialize();
    }
}
