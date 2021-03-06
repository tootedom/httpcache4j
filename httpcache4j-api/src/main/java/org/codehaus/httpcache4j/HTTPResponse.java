/*
 * Copyright (c) 2008, The Codehaus. All Rights Reserved.
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
 *
 */

package org.codehaus.httpcache4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.codehaus.httpcache4j.payload.Payload;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.codehaus.httpcache4j.HeaderConstants.*;

/**
 * Represents a HTTP response delivered by the cache.
 * Constructions of this should not be done by clients, they should
 * rely on that the cache does its job.
 *
 * @author <a href="mailto:hamnis@codehaus.org">Erlend Hamnaberg</a>
 */
public final class HTTPResponse {
    
    private final StatusLine statusLine;
    private final Payload payload;
    private final Headers headers;
    private DateTime date;
    private DateTime expires;
    private DateTime lastModified;
    private Tag ETag;
    private Set<HTTPMethod> allowedMethods;
    private CacheControl cacheControl;
    private boolean cached;

    public HTTPResponse(Payload payload, Status status, Headers headers) {
        this(payload, new StatusLine(status), headers);
    }

    public HTTPResponse(Payload payload, StatusLine statusLine, Headers headers) {
        Validate.notNull(statusLine, "You must supply a Status");
        Validate.notNull(headers, "You must supply some Headers");
        this.statusLine = statusLine;
        this.payload = payload;
        this.headers = headers;

        if (headers.hasHeader(ETAG)) {
            ETag = Tag.parse(headers.getFirstHeader(ETAG).getValue());
        }
        if (headers.hasHeader(LAST_MODIFIED)) {
            lastModified = HeaderUtils.fromHttpDate(headers.getFirstHeader(LAST_MODIFIED));
        }
        if (headers.hasHeader(ALLOW)) {
            Directives value = headers.getFirstHeader(ALLOW).getDirectives();
            Set<HTTPMethod> allowedMethods = new HashSet<HTTPMethod>();
            for (Directive part : value) {
                allowedMethods.add(HTTPMethod.valueOf(part.getName()));
            }
            this.allowedMethods = Collections.unmodifiableSet(allowedMethods);
        }
        if (headers.hasHeader(CACHE_CONTROL)) {
            cacheControl = new CacheControl(headers.getFirstHeader(CACHE_CONTROL).getDirectives());
        }
        if (headers.hasHeader(DATE)) {
            date = HeaderUtils.fromHttpDate(headers.getFirstHeader(DATE));
        }
        if (headers.hasHeader(EXPIRES)) {
            expires = HeaderUtils.fromHttpDate(headers.getFirstHeader(EXPIRES));
        }
        if (headers.hasHeader(X_CACHE)) {
            Header cacheHeader = CacheHeaderBuilder.getBuilder().createHITXCacheHeader();
            List<Header> xcacheHeaders = headers.getHeaders(X_CACHE);
            cached = xcacheHeaders.contains(cacheHeader);
        }
    }

    public boolean hasPayload() {
        return payload != null;
    }

    public Status getStatus() {
        return statusLine.getStatus();
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Payload getPayload() {
        return payload;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Tag getETag() {
        return ETag;
    }

    public DateTime getDate() {
        return date;
    }

    public DateTime getExpires() {
        return expires;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public CacheControl getCacheControl() {
        return cacheControl;
    }

    public boolean isCached() {
        return cached;
    }

    public Set<HTTPMethod> getAllowedMethods() {
        return allowedMethods != null ? allowedMethods : Collections.<HTTPMethod>emptySet();
    }

    public void consume() {
        if (hasPayload()) {
            if (payload.isAvailable()) {
                IOUtils.closeQuietly(payload.getInputStream());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HTTPResponse response = (HTTPResponse) o;

        if (headers != null ? !headers.equals(response.headers) : response.headers != null) {
            return false;
        }
        if (payload != null ? !payload.equals(response.payload) : response.payload != null) {
            return false;
        }
        if (statusLine != response.statusLine) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = statusLine != null ? statusLine.hashCode() : 0;
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        return result;
    }
}
