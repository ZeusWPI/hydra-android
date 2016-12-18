package com.google.firebase.crash;

import com.google.firebase.FirebaseApp;

/**
 * No-op class of Firebase Crash. This can enable you to not report crashes during debugging.
 *
 * This class implements the public API as operations that do nothing.
 *
 * @author Niko Strijbol
 * @version 10.0.1
 * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/crash/FirebaseCrash">The
 * API.</a>
 */
@SuppressWarnings("unused")
public class FirebaseCrash {

    @Deprecated
    public static FirebaseCrash getInstance(FirebaseApp firebaseApp) {
        return new FirebaseCrash();
    }

    public static void log(String message) {
        //noop
    }

    public static void logcat(int level, String tag, String message) {
        //noop
    }

    public static void report(Throwable throwable) {
        //noop
    }
}