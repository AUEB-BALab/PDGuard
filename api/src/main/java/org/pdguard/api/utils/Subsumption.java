/*-
 * Copyright 2014 Diomidis Spinellis
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.pdguard.api.utils;

/**
 * This interface defines classes (specifically enumerations) whose objects
 * can be tested for a subsumption relation.
 *
 * Under this relation a hyponym (subtype, subclass) has a type-of (is-a)
 * relationship with its hypernym (supertype, superclass).  This is specified
 * through the parent type of an object, and is tested with the is(x) method.
 *
 * Here are some examples. A shark is a shark. A shark is a fish.
 * A shark is not a tuna. A fish is not a tuna.
 *
 * Note that this relationship is separate from the Java type hierarchy.
 *
 * @author Diomidis Spinellis
 */
public interface Subsumption {
    /**
     * Obtain the parent type class of a given object.
     * @return The object's parent class in the subsumption relationship.
     */
    Subsumption getParent();

    /**
     * Return true if the passed object is of the type of the object for
     * which the method is being called.
     *
     * @param obj An object to test
     * @return True if both objects are of the same type, or if the object
     *     for which the method is called is a subtype of obj.
     */
    default boolean is(Subsumption obj) {
        if (this == obj)
            return true;
        if (this.getParent() != null)
            return this.getParent().is(obj);
        return false;
    }
}
