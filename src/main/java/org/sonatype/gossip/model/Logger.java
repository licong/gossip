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

import org.sonatype.gossip.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Logger node.
 *
 * @since 1.0
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Logger
    extends ContainerNode
{
    private String name;

    private Level level;

    private List<Logger> loggers;

    public Logger() {}

    public Logger(final String name) {
        assert name != null;

        setName(name);
    }

    public Logger(final String name, final String level) {
        assert name != null;
        assert level != null;

        setName(name);
        setLevel(level);
    }

    public String toString() {
        return "Logger{" +
                "name='" + name + '\'' +
                ", level=" + level +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(final Level level) {
        this.level = level;
    }

    public void setLevel(final String label) {
        setLevel(Level.valueOf(label.toUpperCase()));
    }

    public List<Logger> loggers() {
        if (loggers == null) {
            loggers = new ArrayList<Logger>();
        }
        
        return loggers;
    }

    public void addLogger(final Logger node) {
        assert node != null;

        loggers().add(node);
        node.setParent(this);
    }
}