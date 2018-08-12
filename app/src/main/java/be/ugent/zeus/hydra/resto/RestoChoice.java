package be.ugent.zeus.hydra.resto;

import java9.util.Objects;

/**
 * @author Niko Strijbol
 */
public final class RestoChoice {

    private final String name;
    private final String endpoint;

    public RestoChoice(String name, String endpoint) {
        this.name = name;
        this.endpoint = endpoint;
    }

    public String getName() {
        return name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestoChoice that = (RestoChoice) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(endpoint, that.endpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, endpoint);
    }
}
