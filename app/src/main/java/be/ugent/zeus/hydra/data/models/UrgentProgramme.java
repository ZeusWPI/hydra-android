package be.ugent.zeus.hydra.data.models;

/**
 * @author Niko Strijbol
 */
public class UrgentProgramme {

    private String name;
    private String validUntil; //TODO: this should be Instant

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}