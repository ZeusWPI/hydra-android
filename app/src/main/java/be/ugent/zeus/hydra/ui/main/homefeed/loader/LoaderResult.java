package be.ugent.zeus.hydra.ui.main.homefeed.loader;

import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;

import java.util.List;
import java.util.Set;

/**
 * Contains the results of the {@link HomeFeedLoader}.
 *
 * @author Niko Strijbol
 */
@Deprecated
public class LoaderResult {

    private final List<HomeCard> data;

    private final Set<Integer> errors;

    private final boolean completed;

    public LoaderResult(List<HomeCard> data, Set<Integer> errors, boolean completed) {
        this.data = data;
        this.errors = errors;
        this.completed = completed;
    }

    public List<HomeCard> getData() {
        return data;
    }

    public Set<Integer> getErrors() {
        return errors;
    }

    public boolean isCompleted() {
        return completed;
    }
}