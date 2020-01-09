package be.ugent.zeus.hydra.schamper.list;

import android.app.Application;

import androidx.annotation.NonNull;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;
import be.ugent.zeus.hydra.schamper.Article;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SchamperViewModel extends RequestViewModel<List<Article>> {

    public SchamperViewModel(Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected Request<List<Article>> getRequest() {
        return new SchamperArticlesRequest(getApplication());
    }
}