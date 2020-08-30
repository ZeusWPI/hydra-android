package be.ugent.zeus.hydra.common.converter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.ZoneId;

import com.squareup.moshi.JsonQualifier;

/**
 * Annotation for dates that do not include timezone data. This annotation assumes they use the "Europe\Brussels"
 * timezone.
 *
 * @author Niko Strijbol
 */
@Retention(RetentionPolicy.RUNTIME)
@JsonQualifier
public @interface LocalZonedDateTime {

    ZoneId BRUSSELS = ZoneId.of("Europe/Brussels");

}
