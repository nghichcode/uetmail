package com.nc.uetmail.mail.database.models;

import androidx.room.TypeConverters;

import com.nc.uetmail.mail.converters.DateConverter;

import java.util.Date;

class BaseTimeModel extends BaseModel {
    @TypeConverters({DateConverter.class})
    public Date created_at;
    @TypeConverters({DateConverter.class})
    public Date updated_at;

    public BaseTimeModel() {
        Date now = new Date();
        created_at = now;
        updated_at = now;
    }

    public void updateCreatedAt() {
        Date now = new Date();
        created_at = now;
    }
}
