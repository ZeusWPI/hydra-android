package be.ugent.zeus.hydra.common.database.migrations;

import com.guness.robolectric.sqlite.library.SQLiteLibraryLoader;
import org.junit.Before;

/**
 * @author Niko Strijbol
 */
public abstract class MigrationTest {
    @Before
    public void setUp() {
        SQLiteLibraryLoader.load();
    }
}
