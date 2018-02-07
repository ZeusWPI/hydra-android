package be.ugent.zeus.hydra.data.dto.minerva;

import be.ugent.zeus.hydra.domain.models.minerva.Module;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.EnumSet;
import java.util.List;

/**
 * The response for the tools, as defined by the Minerva API.
 * <p>
 * Note: the response contains additional data that is not present in this class, as we don't need it.
 *
 * @author Niko Strijbol
 */
public class Tools {

    private List<Tool> tools;

    public List<Tool> getTools() {
        return tools;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public EnumSet<Module> asModules() {
        return StreamSupport.stream(getTools())
                .flatMap(o -> o.asModule().stream())
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Module.class)));
    }
}