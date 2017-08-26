package com.aabello.iwalk.model.types;

import org.greenrobot.greendao.converter.PropertyConverter;


public class HeightUnitTypeConverter implements PropertyConverter<HeightUnitType, String> {
    @Override
    public HeightUnitType convertToEntityProperty(String databaseValue) {
        return HeightUnitType.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(HeightUnitType entityProperty) {
        return entityProperty.name();
    }
}
