package be.ugent.zeus.hydra.common.network;

import android.content.Context;

import com.squareup.moshi.Types;

import java.util.List;

/**
 * This is very small class whose only purpose is making the constructor more type safe.
 *
 * @author Niko Strijbol
 */
public abstract class JsonArrayRequest<T> extends JsonOkHttpRequest<List<T>> {

    public JsonArrayRequest(Context context, Class<T> clazz) {
        super(context, Types.newParameterizedType(List.class, clazz));
    }
}