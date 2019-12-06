package rest.transformer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.exception.RequestMalformedException;

import java.lang.reflect.Type;

public class JsonTransformer implements Transformer {

    private static final Logger log = LoggerFactory.getLogger(JsonTransformer.class);
    private final Gson gson = new Gson();

    @Override
    public <T> T render(String model, Class<T> classOfT) {
        try {
            return gson.fromJson(model, classOfT);
        } catch (JsonSyntaxException e) {
            log.error(e.getLocalizedMessage());
            throw new RequestMalformedException(e.getLocalizedMessage());
        }
    }

    @Override
    public <T> T render(String model, Type type) {
        return gson.fromJson(model, type);
    }

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }
}
