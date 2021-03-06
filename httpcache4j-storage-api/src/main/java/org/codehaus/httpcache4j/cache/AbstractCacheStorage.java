/*
 * Copyright (c) 2009. The Codehaus. All Rights Reserved.
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

package org.codehaus.httpcache4j.cache;

import org.codehaus.httpcache4j.HTTPResponse;
import org.codehaus.httpcache4j.HTTPRequest;

/**
 * @author <a href="mailto:erlend@codehaus.org">Erlend Hamnaberg</a>
 * @version $Revision: $
 */
public abstract class AbstractCacheStorage implements CacheStorage {

    public final HTTPResponse insert(HTTPRequest request, HTTPResponse response) {
        Key key = Key.create(request, response);
        invalidate(key);
        HTTPResponse cacheableResponse = rewriteResponse(key, response);
        return putImpl(key, cacheableResponse);
    }

    protected abstract HTTPResponse rewriteResponse(Key key, HTTPResponse response);

    protected abstract HTTPResponse putImpl(Key key, HTTPResponse response);

    protected abstract void invalidate(Key key);
  
    protected abstract HTTPResponse get(Key key);
}
