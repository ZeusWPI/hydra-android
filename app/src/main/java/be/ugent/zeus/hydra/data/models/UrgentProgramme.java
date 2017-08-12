package be.ugent.zeus.hydra.data.models;

import org.threeten.bp.Instant;

/**
 * @author Niko Strijbol
 */
public class UrgentProgramme {

    private String name;
    private Instant validUntil;

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
