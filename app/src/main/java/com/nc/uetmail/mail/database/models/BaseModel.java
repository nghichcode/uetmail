package com.nc.uetmail.mail.database.models;

import java.lang.reflect.Field;
import java.util.HashSet;

class BaseModel {
    protected String[] requireFields() {
        return new String[]{};
    }

    protected HashSet<String> validateRequireFields(Object subClass) {
        HashSet<String> errors = new HashSet<>();
        String[] rq = requireFields();
        for (int i = 0; i < rq.length; i++) {
            try {
                Field field = subClass.getClass().getDeclaredField(rq[i]);
                field.setAccessible(true);
                if (field.get(subClass) == null) {
                    errors.add(rq[i]);
                } else if (field.getType().equals(String.class) && ((String) field.get(subClass)).isEmpty()) {
                    errors.add(rq[i]);
                }
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return errors;
    }

    protected HashSet<String> validate() {
        return validateRequireFields(this);
    }
}
