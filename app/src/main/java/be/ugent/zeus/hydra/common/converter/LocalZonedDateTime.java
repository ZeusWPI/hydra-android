package be.ugent.zeus.hydra.common.converter;

import com.squareup.moshi.JsonQualifier;
import org.threeten.bp.ZoneId;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
