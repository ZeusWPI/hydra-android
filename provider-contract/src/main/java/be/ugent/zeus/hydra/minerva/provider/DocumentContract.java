package be.ugent.zeus.hydra.minerva.provider;

/**
 * Contract for the Minerva Document Provider.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings("unused")
public interface DocumentContract {
    /**
     * The authority for the {@link android.provider.DocumentsProvider}.
     *
     * @implNote This value must is defined in the build file of the companion app as well.
     */
    String AUTHORITY = "be.ugent.zeus.hydra.minerva.provider.documents";

    /**
     * The ID of the root.
     */
    String ROOT_ID = "minerva.documents.root";
}