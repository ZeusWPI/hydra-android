package be.ugent.zeus.hydra.testing.matcher;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * Takes care of selecting which fields need to be considered.
 *
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.N)
class Fields<T> {

    private final Class<T> clazz;

    private final Collection<String> ignoredFields;
    private List<Field> results;

    Fields(Class<T> clazz) {
        this.clazz = clazz;
        this.ignoredFields = new HashSet<>();
    }

    public void ignoreFields(String... fields) {
        Collections.addAll(this.ignoredFields, fields);
    }

    public List<Field> getFields() {
        return FieldUtils.getAllFieldsList(clazz).stream()
                .filter(f -> !Modifier.isTransient(f.getModifiers()))
                .filter(f -> !ignoredFields.contains(f.getName()))
                .collect(Collectors.toList());
    }
}