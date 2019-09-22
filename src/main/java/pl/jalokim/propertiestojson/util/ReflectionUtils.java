package pl.jalokim.propertiestojson.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ReflectionUtils {

    public static void setValue(Object target, String fieldName, Object value) {
        Field field = null;
        Class<?> currentClass = target.getClass();
        List<String> noSuchFieldExceptions = new ArrayList<>();
        while(currentClass != null) {
            try {
                field = currentClass.getDeclaredField(fieldName);
            } catch(NoSuchFieldException e) {
                noSuchFieldExceptions.add(e.getMessage());
            }
            if(field != null) {
                break;
            }
            currentClass = currentClass.getSuperclass();
        }

        if(field == null) {
            throw new RuntimeException(new NoSuchFieldException(noSuchFieldExceptions.toString()));
        }

        try {
            field.setAccessible(true);
            field.set(target, value);
            field.setAccessible(false);
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T invokeMethod(Object target, String methodName,
                                     List<Class<?>> argClasses, List<Object> args) {
        return invokeMethod(target, target.getClass(), methodName, argClasses, args);
    }

    public static <T> T invokeMethod(Object target, Class<?> targetClass, String methodName,
                                     List<Class<?>> argClasses, List<Object> args) {
        Method method = null;
        Class<?> currentClass = targetClass;
        List<String> noSuchMethodExceptions = new ArrayList<>();
        while(currentClass != null) {
            try {
                method = currentClass.getDeclaredMethod(methodName, argClasses.toArray(new Class[0]));
            } catch(NoSuchMethodException e) {
                noSuchMethodExceptions.add(e.getMessage());
            }
            if(method != null) {
                break;
            }
            currentClass = currentClass.getSuperclass();
        }

        if(method == null) {
            throw new RuntimeException(new NoSuchMethodException(noSuchMethodExceptions.toString()));
        }

        try {
            method.setAccessible(true);
            T result = (T) method.invoke(target, args.toArray(new Object[0]));
            method.setAccessible(false);
            return result;
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
