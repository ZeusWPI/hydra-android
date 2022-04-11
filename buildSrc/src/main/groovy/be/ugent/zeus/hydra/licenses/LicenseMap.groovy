/**
 * Copyright 2016 Jared Burrows
 * Copyright 2020 Niko Strijbol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed : in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.ugent.zeus.hydra.licenses

/**
 * Maps URLs : a license.
 *
 * Based on https://github.com/jaredsburrows/gradle-license-plugin/blob/master/src/main/kotlin/com/jaredsburrows/license/internal/LicenseHelper.kt
 *
 * @author Niko Strijbol
 */
@Singleton
class LicenseMap {

    def mapping = [
            // Apache License 2.0
            // https://github.com/github/choosealicense.com/blob/gh-pages/_licenses/apache-2.0.txt
            "Apache 2.0"                                           : "apache-2.0.txt",
            "Apache License 2.0"                                   : "apache-2.0.txt",
            "The Apache Software License"                          : "apache-2.0.txt",
            "The Apache Software License, Version 2.0"             : "apache-2.0.txt",
            "http://www.apache.org/licenses/LICENSE-2.0.txt"       : "apache-2.0.txt",
            "https://www.apache.org/licenses/LICENSE-2.0.txt"      : "apache-2.0.txt",
            "http://opensource.org/licenses/Apache-2.0"            : "apache-2.0.txt",
            "https://opensource.org/licenses/Apache-2.0"           : "apache-2.0.txt",
            "https://api.github.com/licenses/apache-2.0"           : "apache-2.0.txt",

            // MIT License
            // https://github.com/github/choosealicense.com/blob/gh-pages/_licenses/mit.txt
            "MIT License"                                          : "mit.txt",
            "http://opensource.org/licenses/MIT"                   : "mit.txt",
            "https://opensource.org/licenses/MIT"                  : "mit.txt",
            "http://www.opensource.org/licenses/mit-license.php"   : "mit.txt",

            // GPLv2 + Classpath Exception
            // https://github.com/github/choosealicense.com/blob/gh-pages/_licenses/mpl-2.0.txt
            "http://openjdk.java.net/legal/gplv2+ce.html"          : "gplv2ce.txt",

            // Not a license -- skip these
            "https://developer.android.com/studio/terms.html"      : null,
            "http://developer.android.com/studio/terms.html"       : null,
            "https://answers.io/terms"                             : null,
            "http://answers.io/terms"                              : null,
            "https://fabric.io/terms"                              : null,
            "https://fabric.io/term"                               : null,
            "http://try.crashlytics.com/terms/terms-of-service.pdf": null,

            // Library specific licenses
            "org.threeten:threetenbp"                              : "threeten.txt",
            "com.heinrichreimersoftware:material-intro"            : "material-intro.txt"
    ]
}
