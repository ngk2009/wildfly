/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.naming;

import org.wildfly.security.manager.GetContextClassLoaderAction;
import org.wildfly.security.manager.ReadPropertyAction;
import org.wildfly.security.manager.SetContextClassLoaderAction;
import org.wildfly.security.manager.WildFlySecurityManager;
import org.wildfly.security.manager.WritePropertyAction;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static java.lang.Thread.currentThread;
import static java.security.AccessController.doPrivileged;

final class SecurityActions {

    private SecurityActions() {
        // forbidden inheritance
    }

    static String getSystemProperty(final String property) {
        return ! WildFlySecurityManager.isChecking() ? getProperty(property) : doPrivileged(new ReadPropertyAction(property));
    }

    static void setSystemProperty(final String property, final String value) {
        if (! WildFlySecurityManager.isChecking()) {
            setProperty(property, value);
        } else {
            doPrivileged(new WritePropertyAction(property, value));
        }
    }

    /**
     * Gets context classloader.
     *
     * @return the current context classloader
     */
    static ClassLoader getContextClassLoader() {
        return ! WildFlySecurityManager.isChecking() ? currentThread().getContextClassLoader() : doPrivileged(GetContextClassLoaderAction.getInstance());
    }

    /**
     * Sets context classloader.
     *
     * @param classLoader
     *            the classloader
     */
    static void setContextClassLoader(final ClassLoader classLoader) {
        if (! WildFlySecurityManager.isChecking()) {
            currentThread().setContextClassLoader(classLoader);
        } else {
            doPrivileged(new SetContextClassLoaderAction(classLoader));
        }
    }
}
