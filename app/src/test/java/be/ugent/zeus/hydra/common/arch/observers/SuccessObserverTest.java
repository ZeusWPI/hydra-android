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

package be.ugent.zeus.hydra.common.arch.observers;

import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
public class SuccessObserverTest {

    @Test
    public void testSuccess() {
        TestSuccessObserver observer = new TestSuccessObserver();
        Result<Integer> result = Result.Builder.fromData(1);
        observer.onChanged(result);
        assertTrue(observer.hasCalledSuccess);
        assertEquals(Integer.valueOf(1), observer.successData);
        assertFalse(observer.hasCalledEmpty);
    }

    @Test
    public void testEmpty() {
        TestSuccessObserver observer = new TestSuccessObserver();
        Result<Integer> result = Result.Builder.fromException(new RequestException());
        observer.onChanged(result);
        assertFalse(observer.hasCalledSuccess);
        assertTrue(observer.hasCalledEmpty);
    }

    @Test
    public void testNull() {
        TestSuccessObserver observer = new TestSuccessObserver();
        observer.onChanged(null);
        assertFalse(observer.hasCalledSuccess);
        assertTrue(observer.hasCalledEmpty);
    }

    @Test
    public void testConsumerSuccess() {
        MutableInt mutableInt = new MutableInt();
        MutableBoolean called = new MutableBoolean(false);
        SuccessObserver<Integer> observer = SuccessObserver.with(integer -> {
            mutableInt.setValue(integer);
            called.setTrue();
        });
        Result<Integer> result = Result.Builder.fromData(1);
        observer.onChanged(result);
        assertTrue(called.booleanValue());
        assertEquals(Integer.valueOf(1), mutableInt.getValue());
    }

    @Test
    public void testConsumerEmptyAndNull() {
        MutableBoolean called = new MutableBoolean(false);
        SuccessObserver<Integer> observer = SuccessObserver.with(i -> called.setTrue());
        Result<Integer> result = Result.Builder.fromException(new RequestException());
        observer.onChanged(result);
        assertFalse(called.booleanValue());
        observer.onChanged(null);
        assertFalse(called.booleanValue());
    }

    private static class TestSuccessObserver extends SuccessObserver<Integer> {

        private Integer successData;
        private boolean hasCalledEmpty;
        private boolean hasCalledSuccess;

        @Override
        protected void onSuccess(@NonNull Integer data) {
            successData = data;
            hasCalledSuccess = true;
        }

        @Override
        protected void onEmpty() {
            super.onEmpty();
            hasCalledEmpty = true;
        }
    }
}