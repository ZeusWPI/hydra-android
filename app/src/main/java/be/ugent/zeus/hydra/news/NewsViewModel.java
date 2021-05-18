package be.ugent.zeus.hydra.news;

import android.app.Application;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class NewsViewModel extends RequestViewModel<List<NewsArticle>> {

    public NewsViewModel(Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected Request<List<NewsArticle>> getRequest() {
        return new NewsRequest(getApplication()).map(NewsStream::getEntries);
    }
}
