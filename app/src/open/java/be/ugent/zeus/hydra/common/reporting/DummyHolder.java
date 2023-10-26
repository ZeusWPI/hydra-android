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

/**
 * @author Niko Strijbol
 */
class DummyHolder implements BaseEvents {

    @Override
    public Params params() {
        return new DummyParams();
    }

    @Override
    public String login() {
        return "null";
    }

    @Override
    public String selectContent() {
        return "null";
    }

    @Override
    public String share() {
        return "null";
    }

    @Override
    public String tutorialBegin() {
        return "null";
    }

    @Override
    public String tutorialComplete() {
        return "null";
    }

    @Override
    public String viewItem() {
        return "null";
    }

    @Override
    public String cardDismissal() {
        return "null";
    }

    private static class DummyParams implements Params {

        @Override
        public String method() {
            return "null";
        }

        @Override
        public String contentType() {
            return "null";
        }

        @Override
        public String itemId() {
            return "null";
        }

        @Override
        public String itemName() {
            return "null";
        }

        @Override
        public String itemCategory() {
            return "null";
        }

        @Override
        public String dismissalType() {
            return "null";
        }

        @Override
        public String cardType() {
            return "null";
        }

        @Override
        public String cardIdentifier() {
            return "null";
        }
    }
}
