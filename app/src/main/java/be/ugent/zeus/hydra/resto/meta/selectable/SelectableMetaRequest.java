package be.ugent.zeus.hydra.resto.meta.selectable;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.meta.MetaRequest;
import be.ugent.zeus.hydra.resto.meta.RestoMeta;

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
    public Result<List<RestoChoice>> execute(@NonNull Bundle args) {
        return resultRequest.execute(args).map(restoMeta -> restoMeta.locations.stream()
                .filter(resto -> !TextUtils.isEmpty(resto.getEndpoint()))
                .map(resto -> new RestoChoice(resto.getName(), resto.getEndpoint()))
                .collect(Collectors.toCollection(ArrayList::new)));
    }
}