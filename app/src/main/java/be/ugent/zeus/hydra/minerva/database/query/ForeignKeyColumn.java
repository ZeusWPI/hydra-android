package be.ugent.zeus.hydra.minerva.database.query;

/**
 * @author Niko Strijbol
 */
public class ForeignKeyColumn extends SimpleColumn {

    private final String key;
    private final String foreignTable;
    private final String foreignKey;
    private boolean cascade;

    public ForeignKeyColumn(String key, String foreignTable, String foreignKey) {
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