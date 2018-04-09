package be.ugent.zeus.hydra.minerva.announcement.sync;

import be.ugent.zeus.hydra.minerva.course.Module;
import com.squareup.moshi.Json;
import java8.util.Optional;

/**
 * A tool, as defined by the Minerva API.
 *
 * @author Niko Strijbol
 */
class ApiTool {

    public String tool;
    public String icon;
    public String link;
    @Json(name = "visibilty")
    public boolean visibility;

    /**
     * Get it as an enum.
     *
     * @return The enum representation, or null if not known.
     */
    public Optional<Module> asModule() {
        if (tool == null) {
            return Optional.empty();
        }
        switch (tool) {
            case "agenda":
                return Optional.of(Module.CALENDAR);
            case "announcement":
                return Optional.of(Module.ANNOUNCEMENTS);
            default:
                return Optional.empty();
        }
    }
}