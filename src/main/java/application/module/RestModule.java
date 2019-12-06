package application.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import rest.transformer.JsonTransformer;
import rest.transformer.Transformer;

public class RestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Transformer.class).to(JsonTransformer.class).in(Singleton.class);
    }
}
