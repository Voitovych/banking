package rest.transformer;

import java.lang.reflect.Type;

public interface Transformer {

    <T> T render(String model, Class<T> classOfT);

    <T> T render(String model, Type type);

    String render(Object model);
}
