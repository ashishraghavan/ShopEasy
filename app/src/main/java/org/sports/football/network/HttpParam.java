package org.sports.football.network;

import android.support.annotation.NonNull;

import com.google.common.primitives.Primitives;

import java.io.InputStream;

/**
 * TODO Add further warnings for not being thread safe.
 * Is not thread safe.
 */
public class HttpParam {

    private static final String TAG = HttpParam.class.getSimpleName();
    private String name;
    private Object value;
    private boolean isBinary = false;
    private Class valueType;

    public HttpParam(@NonNull final String name,final Object value) {
        this.name = name;
        this.value = value;
        inferTypeFromValue(value);
    }

    public boolean isBinary() {
        return isBinary;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isPrimitive(Class<?> type) {
        return Enum.class.isAssignableFrom(type) || type.isPrimitive() ||
                Primitives.isWrapperType(type) || type.equals(String.class) ||
                type.equals(javax.xml.datatype.XMLGregorianCalendar.class);
    }

    private void inferTypeFromValue(final Object value) {
        if ( value instanceof byte[] ||
                value instanceof Byte[] ||
                value instanceof InputStream ) {
            isBinary = true;
        }

        valueType = value.getClass();
    }
}
