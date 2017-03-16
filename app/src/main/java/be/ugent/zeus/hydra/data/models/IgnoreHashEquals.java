package be.ugent.zeus.hydra.data.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ignore certain fields for hash/equals when using auto value.
 *
 * @see <a href="https://github.com/REggar/auto-value-ignore-hash-equals">The extension</a>
 *
 * @author Niko Strijbol
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
public @interface IgnoreHashEquals {
}