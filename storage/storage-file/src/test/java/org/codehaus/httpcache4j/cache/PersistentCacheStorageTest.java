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

package org.codehaus.httpcache4j.cache;

import static junit.framework.Assert.assertEquals;
import junit.framework.Assert;

import java.io.File;
import java.net.URI;

import org.codehaus.httpcache4j.HTTPResponse;
import org.codehaus.httpcache4j.Headers;
import org.codehaus.httpcache4j.MIMEType;
import org.codehaus.httpcache4j.Status;
import org.codehaus.httpcache4j.payload.InputStreamPayload;
import org.codehaus.httpcache4j.util.DeletingFileFilter;
import org.codehaus.httpcache4j.util.TestUtil;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.apache.commons.io.input.NullInputStream;

/** @author <a href="mailto:hamnis@codehaus.org">Erlend Hamnaberg</a> */
public class PersistentCacheStorageTest extends CacheStorageAbstractTest {
    private File baseDirectory;
    
    @Override
	protected CacheStorage createCacheStorage() {
        baseDirectory = TestUtil.getTestFile("target/test/");
        baseDirectory.mkdirs();
        return new PersistentCacheStorage(baseDirectory);
    }

    @Test
    public void testPUTWithRealPayload() throws Exception {
        HTTPResponse response = createRealResponse();
        storage.insert(REQUEST, response);
        Assert.assertEquals(1, storage.size());
    }

    @Test
    public void testMultipleInserts() {
        Key key = new Key(URI.create("foo"), new Vary());
        HTTPResponse res = null;
        for (int i = 0; i < 100; i++) {
            HTTPResponse response = createRealResponse();
            res = storage.insert(REQUEST, response);
        }
        assertNotNull("Result may not be null", res);
        if (res.hasPayload()) {
            CleanableFilePayload payload = (CleanableFilePayload) res.getPayload();
            final File parent = payload.getFile().getParentFile();
            final File file = new FileResolver(parent.getParentFile()).resolve(key);
            assertEquals(file.toString(), payload.getFile().toString());
            assertTrue(parent.isDirectory());
            assertEquals(1, parent.list().length);
        }

    }

    private HTTPResponse createRealResponse() {
        return new HTTPResponse(new InputStreamPayload(new NullInputStream(10), MIMEType.APPLICATION_OCTET_STREAM), Status.OK, new Headers());
    }

    @Override
	public void afterTest() {
        baseDirectory.listFiles(new DeletingFileFilter());
    }
}