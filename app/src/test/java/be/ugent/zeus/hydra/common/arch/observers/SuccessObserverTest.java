package be.ugent.zeus.hydra.common.arch.observers;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

import static org.junit.Assert.*;

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