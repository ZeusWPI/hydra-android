package be.ugent.zeus.hydra.ui.main.homefeed;

import be.ugent.zeus.hydra.repository.requests.RequestException;

import java.util.Arrays;
import java.util.Set;

/**
 * @author Niko Strijbol
 */
public class FeedException extends RequestException {

    private final Set<Integer> failedTypes;

    public FeedException(Set<Integer> failedTypes) {
        super("Exception for card types" + Arrays.toString(failedTypes.toArray()));
        this.failedTypes = failedTypes;
    }

    public Set<Integer> getFailedTypes() {
        return failedTypes;
    }
}
