package com.nc.uetmail.mail.session.components;

import java.lang.reflect.Field;

public class MailUtils {
    public static <T> T getPrivateAttr(Object o, String attrName, Class<T> type) {
        Field field = null;
        T val = null;
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
}
