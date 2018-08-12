package be.ugent.zeus.hydra.resto.meta.selectable;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.meta.MetaRequest;
import be.ugent.zeus.hydra.resto.meta.RestoMeta;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SelectableMetaRequest implements Request<List<RestoChoice>> {

    private final Request<RestoMeta> resultRequest;

    public SelectableMetaRequest(Context context) {
        this.resultRequest = new MetaRequest(context);
    }

    @NonNull
    @Override
    public Result<List<RestoChoice>> performRequest(@NonNull Bundle args) {
        return resultRequest.performRequest(args).map(restoMeta -> StreamSupport.stream(restoMeta.locations)
                .filter(resto -> !TextUtils.isEmpty(resto.getEndpoint()))
                .map(resto -> new RestoChoice(resto.getName(), resto.getEndpoint()))
                .collect(Collectors.toCollection(ArrayList::new)));
    }
}