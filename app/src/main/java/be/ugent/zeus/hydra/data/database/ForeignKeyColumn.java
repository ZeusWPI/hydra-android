package be.ugent.zeus.hydra.data.database;

import com.alexfu.sqlitequerybuilder.api.Column;

/**
 * @author Niko Strijbol
 */
public class ForeignKeyColumn extends Column {

    private final String key;
    private final String foreignTable;
    private final String foreignKey;
    private boolean cascade;

    public ForeignKeyColumn(String key, String foreignTable, String foreignKey) {
        super(null, null);
        this.foreignKey = foreignKey;
        this.foreignTable = foreignTable;
        this.key = key;
    }

    public ForeignKeyColumn cascade(boolean cascade) {
        this.cascade = cascade;
        return this;
    }

    @Override
    public String build() {
        return "FOREIGN KEY(" + key + ") REFERENCES " + foreignTable + "(" + foreignKey + ")" + (this.cascade ? " ON UPDATE CASCADE" : "");
    }
}