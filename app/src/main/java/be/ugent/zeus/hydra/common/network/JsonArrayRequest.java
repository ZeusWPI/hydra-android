package be.ugent.zeus.hydra.common.network;

import android.content.Context;

import java.util.List;

import com.squareup.moshi.Types;

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