package transfarmer.deterministicloot.util;

import java.lang.reflect.Method;

public class ReflectionHelper {
    public static Method getNewestMethod(final Object object, final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        try {
            return cls.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException exception) {
            return getNewestMethod(object, cls.getSuperclass(), methodName, parameterTypes);
        }
    }

    public static Method getNewestMethod(final Object object, final String methodName, final Class<?> parameterTypes) {
        return getNewestMethod(object, object.getClass(), methodName, parameterTypes);
    }
}
