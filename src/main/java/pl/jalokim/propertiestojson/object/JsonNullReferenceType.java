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
package pl.jalokim.propertiestojson.object;

public class JsonNullReferenceType extends AbstractJsonType {

    public final static JsonNullReferenceType NULL_OBJECT = new JsonNullReferenceType();

    public final static String NULL_VALUE = "null";

    @Override
    public String toStringJson() {
        return NULL_VALUE;
    }
}
