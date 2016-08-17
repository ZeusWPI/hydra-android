package be.ugent.zeus.hydra.minerva.database.query;

import com.alexfu.sqlitequerybuilder.api.Column;

/**
 * @author Niko Strijbol
 * @version 15/08/2016
 */
public abstract class SimpleColumn extends Column {

    public SimpleColumn() {
        super(null, null);
    }

    public abstract String build();
}