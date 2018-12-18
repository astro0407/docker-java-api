/**
 * Copyright (c) 2018, Mihai Emil Andronache
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1)Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2)Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3)Neither the name of docker-java-api nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.amihaiemil.docker;

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Condition;
import com.amihaiemil.docker.mock.Response;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ListedNetworks}.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 */
public final class ListedNetworksTestCase {

    /**
     * Mock docker.
     */
    private static final Docker DOCKER = Mockito.mock(Docker.class);

    /**
     * ListedNetworks can filter according filters.
     * @throws IOException if something goes wrong
     */
    @Test
    @Ignore
    public void filterResults() throws IOException {
        final Map<String, Iterable<String>> filters = new HashMap<>();
        filters.put(
            "scope",
            Arrays.asList(
                "local"
            )
        );
        MatcherAssert.assertThat(
            "Could not filter networks",
            new ListedNetworks(
                //@checkstyle LineLength (50 lines)
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_OK,
                        "[{\"Id\": \"id1\", \"scope\":\"local\"}, {\"Id\":\"cde2\"}]"
                    ),
                    new Condition(
                        // @checkstyle LineLength (11 lines)
                        "iterate() query parameters must include the filters provided",
                        req -> {
                            final List<NameValuePair> params = new UncheckedUriBuilder(
                                req.getRequestLine().getUri()
                            ).getQueryParams();
                            // @checkstyle BooleanExpressionComplexity (5 lines)
                            return params.size() == 1
                                && "filters".equals(params.get(0).getName())
                                && params.get(0).getValue().contains("scope")
                                && params.get(0).getValue().contains("local");
                        }
                    )
                ),
                URI.create("http://localhost/networks/id1"),
                DOCKER,
                filters
            ).iterator().next().inspect().getString("id"),
            new IsEqual<>("id1")
        );
    }
}
