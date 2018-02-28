package be.ugent.zeus.hydra.resto.meta.selectable;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.meta.RestoMeta;
import be.ugent.zeus.hydra.resto.meta.MetaRequest;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SelectableMetaRequest implements Request<List<RestoChoice>> {

    private final Request<RestoMeta> resultRequest;

    public SelectableMetaRequest(Context context) {
        this.resultRequest = Requests.cache(context.getApplicationContext(), new MetaRequest());
    }

    @NonNull
    @Override
    public Result<List<RestoChoice>> performRequest(@Nullable Bundle args) {
        return resultRequest.performRequest(args).map(restoMeta -> StreamSupport.stream(restoMeta.locations)
                .filter(resto -> !TextUtils.isEmpty(resto.getEndpoint()))
                .map(resto -> new RestoChoice(resto.getName(), resto.getEndpoint()))
                .collect(Collectors.toCollection(ArrayList::new)));
    }
}