/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*- */

/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is collection of utilities useful for Rhino code.
 *
 * The Initial Developer of the Original Code is
 * RUnit Software AS.
 * Portions created by the Initial Developer are Copyright (C) 2003
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Igor Bukanov, igor@fastmail.fm
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.mozilla.javascript;

import java.lang.reflect.*;

/**
 * Collection of utilities
 */

public class Kit
{

    static Class classOrNull(String className)
    {
        try {
            return Class.forName(className);
        } catch  (ClassNotFoundException ex) {
        } catch  (SecurityException ex) {
        } catch  (LinkageError ex) {
        } catch (IllegalArgumentException e) {
            // Can be thrown if name has characters that a class name
            // can not contain
        }
        return null;
    }

    static Class classOrNull(ClassLoader loader, String className)
    {
        try {
            return loader.loadClass(className);
        } catch (ClassNotFoundException ex) {
        } catch (SecurityException ex) {
        } catch  (LinkageError ex) {
        } catch (IllegalArgumentException e) {
            // Can be thrown if name has characters that a class name
            // can not contain
        }
        return null;
    }

    static Object newInstanceOrNull(Class cl)
    {
        try {
            return cl.newInstance();
        } catch (SecurityException x) {
        } catch  (LinkageError ex) {
        } catch (InstantiationException x) {
        } catch (IllegalAccessException x) {
        }
        return null;
    }

    /**
     * Split string into array of strings using semicolon as string terminator
     * (; after the last string is required).
     */
    public static String[] semicolonSplit(String s)
    {
        int count = 0;
        for (int cursor = 0; ;) {
            int next = s.indexOf(';', cursor) + 1;
            if (next <= 0) {
                // check for missing ;
                if (cursor + 1 < s.length())
                    throw new IllegalArgumentException();
                break;
            }
            ++count;
            cursor = next + 1;
        }
        String[] array = new String[count];
        count = 0;
        for (int cursor = 0; ;) {
            int next = s.indexOf(';', cursor);
            if (next < 0) { break; }
            array[count] = s.substring(cursor, next);
            ++count;
            cursor = next + 1;
        }
        return array;
    }

    public static Object addListener(Object bag, Object listener)
    {
        if (listener == null) throw new IllegalArgumentException();
        if (listener instanceof Object[]) throw new IllegalArgumentException();

        if (bag == null) {
            bag = listener;
        } else if (!(bag instanceof Object[])) {
            bag = new Object[] { bag, listener };
        } else {
            Object[] array = (Object[])bag;
            int L = array.length;
            // bag has at least 2 elements if it is array
            if (L < 2) throw new IllegalArgumentException();
            Object[] tmp = new Object[L + 1];
            System.arraycopy(array, 0, tmp, 0, L);
            tmp[L] = listener;
            bag = tmp;
        }

        return bag;
    }

    public static Object removeListener(Object bag, Object listener)
    {
        if (listener == null) throw new IllegalArgumentException();
        if (listener instanceof Object[]) throw new IllegalArgumentException();

        if (bag == listener) {
            bag = null;
        } else if (bag instanceof Object[]) {
            Object[] array = (Object[])bag;
            int L = array.length;
            // bag has at least 2 elements if it is array
            if (L < 2) throw new IllegalArgumentException();
            if (L == 2) {
                if (array[1] == listener) {
                    bag = array[0];
                } else if (array[0] == listener) {
                    bag = array[1];
                }
            } else {
                int i = L;
                do {
                    --i;
                    if (array[i] == listener) {
                        Object[] tmp = new Object[L - 1];
                        System.arraycopy(array, 0, tmp, 0, i);
                        System.arraycopy(array, i + 1, tmp, i, L - (i + 1));
                        bag = tmp;
                        break;
                    }
                } while (i != 0);
            }
        }

        return bag;
    }

    public static Object getListener(Object bag, int index)
    {
        if (index == 0) {
            if (bag == null)
                return null;
            if (!(bag instanceof Object[]))
                return bag;
            Object[] array = (Object[])bag;
            // bag has at least 2 elements if it is array
            if (array.length < 2) throw new IllegalArgumentException();
            return array[0];
        } else if (index == 1) {
            if (!(bag instanceof Object[])) {
                if (bag == null) throw new IllegalArgumentException();
                return null;
            }
            Object[] array = (Object[])bag;
            // the array access will check for index on its own
            return array[1];
        } else {
            // bag has to array
            Object[] array = (Object[])bag;
            int L = array.length;
            if (L < 2) throw new IllegalArgumentException();
            if (index == L)
                return null;
            return array[index];
        }
    }

    /**
     * Throws RuntimeException to indicate failed assertion.
     * The function never returns and its return type is RuntimeException
     * only to be able to write <tt>throw Kit.codeBug()</tt> if plain
     * <tt>Kit.codeBug()</tt> triggers unreachable code error.
     */
    public static RuntimeException codeBug()
        throws RuntimeException
    {
        throw new RuntimeException("FAILED ASSERTION");
    }

}
