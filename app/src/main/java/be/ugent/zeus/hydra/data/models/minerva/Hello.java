package be.ugent.zeus.hydra.data.models.minerva;

import java.io.Serializable;

/**
 * Created by feliciaan on 20/06/16.
 */
public class Hello implements Serializable {
    private String hello;

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }
}
