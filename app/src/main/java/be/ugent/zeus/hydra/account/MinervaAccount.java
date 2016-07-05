package be.ugent.zeus.hydra.account;

/**
 * Contains some constants for a Minerva account.
 *
 * @author Niko Strijbol
 */
public class MinervaAccount {

    /**
     * Internal account type.
     */
    public static final String ACCOUNT_TYPE = "be.ugent.minerva.oauth";

    public static final String ACCOUNT_NAME = "Minerva";

    /**
     * Scope of the account access. The Minerva API does currently not implement this.
     *
     * @see <a href="https://github.ugent.be/Onderwijstechnologie/ugent-android-sdk/wiki/Registering-Your-Application">Ugent SDK wiki</a>
     */
    public static final String TOKEN_TYPE = "MINERVA_NOTIFICATION_COUNT";
    public static final String TOKEN_TYPE_LABEL = "Toegang tot het Minerva-account";

    public static final ServerAuthenticate sServerAuthenticate = new ServerAuthenticate() {
        @Override
        public String userSignIn(String user, String pass, String authType) throws Exception {
            return null;
        }
    };
}
