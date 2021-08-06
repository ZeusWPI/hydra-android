/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package be.ugent.zeus.hydra.common.reporting;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * These classes use the initialization-on-demand holder idiom.
 *
 * @author Niko Strijbol
 */
final class FirebaseEvents implements BaseEvents {

    static FirebaseEvents getInstance() {
        return LazyHolder.EVENTS;
    }

    @Override
    public Params params() {
        return LazyHolder.PARAMS;
    }

    @Override
    public String login() {
        return FirebaseAnalytics.Event.LOGIN;
    }

    @Override
    public String search() {
        return FirebaseAnalytics.Event.SEARCH;
    }

    @Override
    public String selectContent() {
        return FirebaseAnalytics.Event.SELECT_CONTENT;
    }

    @Override
    public String share() {
        return FirebaseAnalytics.Event.SHARE;
    }

    @Override
    public String tutorialBegin() {
        return FirebaseAnalytics.Event.TUTORIAL_BEGIN;
    }

    @Override
    public String tutorialComplete() {
        return FirebaseAnalytics.Event.TUTORIAL_COMPLETE;
    }

    @Override
    public String viewItem() {
        return FirebaseAnalytics.Event.VIEW_ITEM;
    }

    @Override
    public String cardDismissal() {
        return "card_dismissal";
    }

    private static class LazyHolder {
        static final FirebaseEvents EVENTS = new FirebaseEvents();
        static final FirebaseParams PARAMS = new FirebaseParams();
    }

    private final static class FirebaseParams implements BaseEvents.Params {

        @Override
        public String method() {
            return FirebaseAnalytics.Param.METHOD;
        }

        @Override
        public String searchTerm() {
            return FirebaseAnalytics.Param.SEARCH_TERM;
        }

        @Override
        public String contentType() {
            return FirebaseAnalytics.Param.CONTENT_TYPE;
        }

        @Override
        public String itemId() {
            return FirebaseAnalytics.Param.ITEM_ID;
        }

        @Override
        public String itemName() {
            return FirebaseAnalytics.Param.ITEM_NAME;
        }

        @Override
        public String itemCategory() {
            return FirebaseAnalytics.Param.ITEM_CATEGORY;
        }

        @Override
        public String dismissalType() {
            return "dismissal_type";
        }

        @Override
        public String cardType() {
            return "card_type";
        }

        @Override
        public String cardIdentifier() {
            return "card_identifier";
        }
    }
}