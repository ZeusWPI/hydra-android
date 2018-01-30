package be.ugent.zeus.hydra.minerva.calendar.itemdetails;

import be.ugent.zeus.hydra.common.request.RequestException;

/**
 * Exception when a calendar item could not be found.
 *
 * @author Niko Strijbol
 */
class CalendarItemNotFoundException extends RequestException {
    CalendarItemNotFoundException(String message) {
        super(message);
    }
}