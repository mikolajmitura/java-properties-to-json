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

import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class EmptyStringJsonTypeResolver extends StringJsonTypeResolver {

    private final static String EMPTY_VALUE = "";

    @Override
    public String returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        return propertyValue.equals(EMPTY_VALUE) ? EMPTY_VALUE : null;
    }
}
