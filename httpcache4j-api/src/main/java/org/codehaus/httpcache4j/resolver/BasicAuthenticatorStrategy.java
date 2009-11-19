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

package org.codehaus.httpcache4j.resolver;

import org.codehaus.httpcache4j.HTTPRequest;
import org.codehaus.httpcache4j.Challenge;
import org.codehaus.httpcache4j.UsernamePasswordChallenge;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

/**
 * @author <a href="mailto:erlend@escenic.com">Erlend Hamnaberg</a>
 * @version $Revision: $
 */
public class BasicAuthenticatorStrategy implements AuthenticatorStrategy {
    public boolean supports(final AuthScheme scheme) {
        return "basic".equalsIgnoreCase(scheme.getType());
    }

    public HTTPRequest prepare(final HTTPRequest request, final AuthScheme scheme) {
        HTTPRequest req = request;
        Challenge challenge = request.getChallenge();
        if (challenge instanceof UsernamePasswordChallenge) {
            UsernamePasswordChallenge upc = (UsernamePasswordChallenge) challenge;
            String basicString = request.getChallenge().getIdentifier() + ":" + new String(upc.getPassword());
            try {
                basicString = new String(Base64.encodeBase64(basicString.getBytes("UTF-8")));
                req = request.addHeader("Authorization", "Basic" + " " + basicString);
            } catch (UnsupportedEncodingException e) {
                throw new Error("UTF-8 is not supported on this platform", e);
            }
        }
        return req;
    }
}
