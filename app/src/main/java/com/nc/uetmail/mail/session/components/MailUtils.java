package com.nc.uetmail.mail.session.components;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.mail.Flags;

public class MailUtils {
    public static <T> T getPrivateAttr(Object o, String attrName, Class<T> type) {
        T val = null;
        if (o == null) return val;
        Field field = null;
        try {
            field = o.getClass().getDeclaredField(attrName);
            field.setAccessible(true);
            val = (T) field.get(o);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return val;
    }

    public static <T> T callPrivateConstructor(Class<T> type, int offset, Object... objects) {
        T val = null;
        if (type == null) return val;
        Constructor<T> constructor = (Constructor<T>) type.getDeclaredConstructors()[offset];
        constructor.setAccessible(true);
        try {
            val = constructor.newInstance(objects);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return val;
    }
}
