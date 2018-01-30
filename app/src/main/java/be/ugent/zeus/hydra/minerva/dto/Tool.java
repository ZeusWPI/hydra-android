package be.ugent.zeus.hydra.minerva.dto;

import be.ugent.zeus.hydra.minerva.course.Module;
import java8.util.Optional;

/**
 * A tool, as defined by the Minerva API.
 *
 * @author Niko Strijbol
 */
public class Tool {

    private String tool;
    private String icon;
    private String link;
    private boolean visibility;

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

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
}