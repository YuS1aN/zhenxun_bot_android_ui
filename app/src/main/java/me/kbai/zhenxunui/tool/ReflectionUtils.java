package me.kbai.zhenxunui.tool;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author sean
 */
public class ReflectionUtils {
    private static final String TAG = ReflectionUtils.class.getSimpleName();

    @Nullable
    public static Field getAccessibleField(Object object, String fieldName) {
        try {
            Class<?> clazz = object.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException | NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    @Nullable
    public static Method getAccessibleMethod(Object object, String methodName, Class<?>... parameterTypes) {
        try {
            Class<?> clazz = object.getClass();
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException | NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    @Nullable
    public static Method getAccessibleMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException | NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object object, Field field) {
        T result = null;
        try {
            result = (T) field.get(object);
        } catch (IllegalAccessException | NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object object, String fieldName) {
        T result = null;
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                result = (T) field.get(object);
                break;
            } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
                Log.e(TAG, e.getMessage());
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T, R> R getFieldValue(Class<?> clazz, T object, String fieldName) {
        R result = null;
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            result = (R) field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    public static <T> boolean setFieldValue(Object object, Field field, T fieldValue) {
        try {
            field.set(object, fieldValue);
            return true;
        } catch (IllegalAccessException | NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    /**
     * @noinspection CallToPrintStackTrace
     */
    public static <T> boolean setFieldValue(Object object, String fieldName, T fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return true;
            } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
                String log = e.getMessage();
                if (TextUtils.isEmpty(log)) {
                    e.printStackTrace();
                } else {
                    Log.e(TAG, log);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }
}
