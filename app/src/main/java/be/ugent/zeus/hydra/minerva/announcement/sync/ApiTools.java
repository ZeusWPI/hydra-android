package be.ugent.zeus.hydra.minerva.announcement.sync;

import be.ugent.zeus.hydra.minerva.course.Module;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

import java.util.EnumSet;
import java.util.List;

/**
 * The response for the tools, as defined by the Minerva API.
 * <p>
 * Note: the response contains additional data that is not present in this class, as we don't need it.
 *
 * @author Niko Strijbol
 */
class ApiTools {

    public List<ApiTool> tools;

    public EnumSet<Module> asModules() {
        return StreamSupport.stream(tools)
                .flatMap(o -> o.asModule().stream())
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Module.class)));
    }
}