package be.ugent.zeus.hydra.feed;

import be.ugent.zeus.hydra.common.request.RequestException;

import java.util.Arrays;
import java.util.Set;

/**
 * @author Niko Strijbol
 */
public class FeedException extends RequestException {

    private final Set<Integer> failedTypes;

    FeedException(Set<Integer> failedTypes) {
        super("Exception for card types" + Arrays.toString(failedTypes.toArray()));
        this.failedTypes = failedTypes;
    }

    public Set<Integer> getFailedTypes() {
        return failedTypes;
    }
}
