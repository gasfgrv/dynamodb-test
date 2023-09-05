package com.github.gasfgrv.dynamodbtest.commons.utils;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DynamoDBUtils {

    public void setConditions(String keyValue, List<String> filterConditions) {
        switch (keyValue) {
            case "Album", "ReleasedIn" -> filterConditions.add("%s = :%s".formatted(keyValue, toSnakeCase(keyValue)));
            case "ProducedBy", "WrittenBy" ->
                    filterConditions.add("contains(%s, :%s)".formatted(keyValue, toSnakeCase(keyValue)));
            default -> throw new NullPointerException();
        }
    }

    public void setAttributes(String keyValue, String valueToSave, Map<String, AttributeValue> attributesMap) {
        switch (keyValue) {
            case "Album", "ProducedBy", "WrittenBy" ->
                    attributesMap.put(":%s".formatted(toSnakeCase(keyValue)), new AttributeValue().withS(valueToSave));
            case "ReleasedIn" ->
                    attributesMap.put(":%s".formatted(toSnakeCase(keyValue)), new AttributeValue().withN(valueToSave));
            default -> throw new NullPointerException();
        }
    }

    private String toSnakeCase(String string) {
        return string
                .replaceAll("([a-z])([A-Z]+)", "$1_$2")
                .toLowerCase();
    }

}
