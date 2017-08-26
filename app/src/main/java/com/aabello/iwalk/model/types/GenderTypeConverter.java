package com.aabello.iwalk.model.types;

import org.greenrobot.greendao.converter.PropertyConverter;


public class GenderTypeConverter implements PropertyConverter<GenderType, String> {
    @Override
    public GenderType convertToEntityProperty(String databaseValue) {
        return GenderType.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(GenderType entityProperty) {
        return entityProperty.name();
    }
}
