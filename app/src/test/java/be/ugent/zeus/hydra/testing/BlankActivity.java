package be.ugent.zeus.hydra.testing;

import androidx.appcompat.app.AppCompatActivity;

/**
 * An activity that contains nothing, to test components that start other activities. We cannot just use the application
 * context, since that would require passing {@link android.content.Intent#FLAG_ACTIVITY_NEW_TASK}, which we don't want.
 *
 * @author Niko Strijbol
 */
public class BlankActivity extends AppCompatActivity {
}