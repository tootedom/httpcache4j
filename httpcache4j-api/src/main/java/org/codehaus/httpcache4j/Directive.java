/*
 * Copyright (c) 2010. The Codehaus. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.codehaus.httpcache4j;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Collections;
import java.util.List;


/**
 * @author <a href="mailto:hamnis@codehaus.org">Erlend Hamnaberg</a>
 * @version $Revision: $
 */
public class Directive extends NameValue {
    private final List<Parameter> parameters;
    
    public Directive(final String name, String value) {
        this(name, HeaderUtils.fixQuotedString(value), Collections.<Parameter>emptyList());
    }

    public Directive(final String name, String value, List<Parameter> parameters) {
        super(name, HeaderUtils.fixQuotedString(value));
        Validate.notNull(parameters, "Parameters may not be null");
        Validate.noNullElements(parameters, "Parameters may not contain any null elements");
        this.parameters = ImmutableList.copyOf(parameters);
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public int getValueAsInteger() {
        return NumberUtils.toInt(getValue(), -1);
    }

    @Override
    public String toString() {
        String output = name + "=" + value;
        if (!parameters.isEmpty()) {
            output = output + "; " + Joiner.on("; ").join(parameters);
        }
        return output;
    }
}
