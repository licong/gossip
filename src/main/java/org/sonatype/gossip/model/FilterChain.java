/*
 * Copyright (C) 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonatype.gossip.model;

import org.sonatype.gossip.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter-chain node.
 *
 * @since 1.0
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class FilterChain
    extends AbstractNode
{
    private List<Filter> filters;

    private Filter[] chain;

    public List<Filter> filters() {
        if (filters == null) {
            filters = new ArrayList<Filter>();
        }

        return filters;
    }

    public void addFilter(final Filter node) {
        assert node != null;

        filters().add(node);
        node.setParent(this);
    }
    
    public void filter(final Event event) {
        assert event != null;

        // Else execute all filters until we get a stop
        if (chain == null) {
            chain = filters().toArray(new Filter[filters.size()]);
        }

        // log.debug("Applying {} filters to event: {}", String.valueOf(chain.length), event);
        
        for (int i=0; i<chain.length; i++) {
            // log.debug("Applying filter[{}]: {}", String.valueOf(i), chain[i]);

            Filter.Result r = chain[i].filter(event);

            // log.debug("Filter[{}] result: ", String.valueOf(i), r);

            if (r == Filter.Result.STOP) {
                break;
            }
        }
    }
}