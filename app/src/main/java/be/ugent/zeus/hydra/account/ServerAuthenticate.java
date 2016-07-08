package be.ugent.zeus.hydra.account;

/**
 * @author Niko Strijbol
 * @version 4/07/2016
 */
public interface ServerAuthenticate {

    String userSignIn(final String user, final String pass, String authType) throws Exception;

}
