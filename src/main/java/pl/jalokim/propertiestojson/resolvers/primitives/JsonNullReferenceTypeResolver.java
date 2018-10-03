/*-
 * #%L
 * IGT Common Data Model
 * %%
 * Copyright (C) 2016 - 2017 IGT
 * %%
 * This software and all information contained therein is confidential and proprietary
 * and shall not be duplicated, used, disclosed or disseminated in any way except
 * as authorized by the applicable license agreement, without the express written permission of IGT.
 * All authorized reproductions must be marked with this language.
 * #L%
 */
package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.JsonNullReferenceType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_OBJECT;
import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_VALUE;

public class JsonNullReferenceTypeResolver extends PrimitiveJsonTypeResolver<JsonNullReferenceType> {

    @Override
    public JsonNullReferenceType returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        if (propertyValue == null || isStringType(propertyValue) && propertyValue.equals(NULL_VALUE)) {
            return NULL_OBJECT;
        }
        return null;
    }

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, JsonNullReferenceType propertyValue) {
        return NULL_OBJECT;
    }

    private Boolean isStringType(Object value) {
        return String.class.isAssignableFrom(value.getClass());
    }
}
