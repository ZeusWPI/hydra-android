/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University Ghent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in all copies or
 *      substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package be.ugent.zeus.hydra.minerva.auth;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Minerva user attributes.
 *
 * @author Niko Strijbol
 * @author UGent
 */
final class UserAttributes {

    @Json(name = "mail")
    private List<String> email;

    @Json(name = "objectClass")
    private List<String> userClass;

    @Json(name = "givenname")
    private List<String> givenName;

    private List<String> surname;

    private List<String> uid;

    private List<String> ugentStudentID;

    @Json(name = "lastenrolled")
    private List<String> lastEnrolled;

    public String getFullName() {
        return givenName + " " + surname;
    }

    public List<String> getEmail() {
        return email;
    }

    public List<String> getUserClass() {
        return userClass;
    }

    public List<String> getGivenName() {
        return givenName;
    }

    public List<String> getSurname() {
        return surname;
    }

    public List<String> getUid() {
        return uid;
    }

    public List<String> getUgentStudentID() {
        return ugentStudentID;
    }
}