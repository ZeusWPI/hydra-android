package be.ugent.zeus.hydra.ui.main.minerva;

import android.arch.lifecycle.ViewModel;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import java8.util.Objects;

/**
 * Contains a {@link ResultStarter}. You must call {@link #setResultStarter(ResultStarter)} before calling
 * {@link #getResultStarter()}.
 *
 * @author Niko Strijbol
 */
public class ResultViewModel extends ViewModel {

    private ResultStarter resultStarter;

    public void setResultStarter(ResultStarter resultStarter) {
        this.resultStarter = resultStarter;
    }

    public ResultStarter getResultStarter() {
        return Objects.requireNonNull(resultStarter);
    }
}