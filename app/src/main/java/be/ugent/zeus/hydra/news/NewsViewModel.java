package be.ugent.zeus.hydra.news;

import android.app.Application;
import androidx.annotation.NonNull;

import java.util.List;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

/**
 * @author Niko Strijbol
 */
public class NewsViewModel extends RequestViewModel<List<UgentNewsArticle>> {

    public NewsViewModel(Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected Request<List<UgentNewsArticle>> getRequest() {
        return new UgentNewsRequest(getApplication());
    }
}
