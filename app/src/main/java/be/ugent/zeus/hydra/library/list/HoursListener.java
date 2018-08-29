package be.ugent.zeus.hydra.library.list;

import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.library.details.OpeningHours;

import java.util.Optional;

/**
 * @author Niko Strijbol
 */
interface HoursListener {
    void receiveHours(Result<Optional<OpeningHours>> hoursResult);
}
