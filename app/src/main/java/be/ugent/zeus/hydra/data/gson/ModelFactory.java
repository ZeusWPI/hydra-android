package be.ugent.zeus.hydra.data.gson;

import com.google.gson.TypeAdapterFactory;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;

/**
 * Factory for Gson models.
 *
 * @author Niko Strijbol
 */
@GsonTypeAdapterFactory
public abstract class ModelFactory implements TypeAdapterFactory {

    public static ModelFactory create() {
        return new AutoValueGson_ModelFactory();
    }
}