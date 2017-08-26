package com.aabello.iwalk.model.types;

import org.greenrobot.greendao.converter.PropertyConverter;


public class WeightUnitTypeConverter implements PropertyConverter<WeightUnitType, String> {
    @Override
    public WeightUnitType convertToEntityProperty(String databaseValue) {
        return WeightUnitType.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(WeightUnitType entityProperty) {
        return entityProperty.name();
    }
}
