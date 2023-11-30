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

package be.ugent.zeus.hydra.common;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Size;
import android.util.SizeF;

import java.io.FileDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mockito.exceptions.base.MockitoException;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

/**
 * Mock implementation for {@link Parcel}. This emulates saving/restoring from a parcel, to enable testing of the
 * implementations in models.
 * <p>
 * When a method is used that is not stubbed, an exception is thrown.
 *
 * @author Niko Strijbol
 * @see <a href="https://gist.github.com/Sloy/d59a36e6c51214d0b131">here</a>
 */
@SuppressLint("NewApi")
public class MockParcel {

    private final Parcel mockedParcel;
    private final List<Object> objects = new ArrayList<>();
    private int position;

    private MockParcel() {
        mockedParcel = mock(Parcel.class, invocation -> {
            throw new MockitoException("Method not mocked: " + invocation.getMethod().getName());
        });
        setupMock();
    }

    /**
     * @return A parcel.
     */
    private static Parcel obtain() {
        return new MockParcel().mockedParcel;
    }

    /**
     * Writes a parcelable to a parcel, and sets the position of the parcel so it is ready to read from.
     *
     * @param parcelable Object to write.
     * @return Parcel with position 0.
     */
    public static Parcel writeToParcelable(Parcelable parcelable) {
        Parcel parcel = obtain();
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        return parcel;
    }

    /**
     * Set up the mocks.
     */
    private void setupMock() {
        setupWrites();
        setupReads();
        setupPosition();
    }

    /**
     * Mock write operations.
     */
    private void setupWrites() {
        Answer<Void> write = invocation -> {
            Object parameter = invocation.getArguments()[0];
            objects.add(parameter);
            return null;
        };
        doAnswer(write).when(mockedParcel).writeInt(anyInt());
        doAnswer(write).when(mockedParcel).writeLong(anyLong());
        doAnswer(write).when(mockedParcel).writeFloat(anyFloat());
        doAnswer(write).when(mockedParcel).writeDouble(anyDouble());
        doAnswer(write).when(mockedParcel).writeString(anyString());
        doAnswer(write).when(mockedParcel).writeFileDescriptor(any(FileDescriptor.class));
        doAnswer(write).when(mockedParcel).writeByte(anyByte());
        doAnswer(write).when(mockedParcel).writeSize(any(Size.class));
        doAnswer(write).when(mockedParcel).writeSizeF(any(SizeF.class));
        doAnswer(write).when(mockedParcel).writeSerializable(any(Serializable.class));
        doAnswer(write).when(mockedParcel).writeStringList(anyList());
        doAnswer(write).when(mockedParcel).writeStringArray(any(String[].class));
        doAnswer(write).when(mockedParcel).writeBooleanArray(any(boolean[].class));
        doAnswer(write).when(mockedParcel).writeTypedList(anyList());
        doAnswer(write).when(mockedParcel).writeParcelable(any(Parcelable.class), anyInt());
        doAnswer(write).when(mockedParcel).writeList(anyList());
    }

    /**
     * Mock position.
     */
    private void setupPosition() {
        Answer<Void> pos = invocation -> {
            position = (int) invocation.getArguments()[0];
            return null;
        };
        doAnswer(pos).when(mockedParcel).setDataPosition(anyInt());
    }

    /**
     * Mock read operations.
     * @noinspection resource, deprecation
     */
    private void setupReads() {
        Answer<Object> answer = i -> objects.get(position++);
        doAnswer(answer).when(mockedParcel).readInt();
        doAnswer(answer).when(mockedParcel).readLong();
        doAnswer(answer).when(mockedParcel).readFloat();
        doAnswer(answer).when(mockedParcel).readDouble();
        doAnswer(answer).when(mockedParcel).readString();
        doAnswer(answer).when(mockedParcel).readFileDescriptor();
        doAnswer(answer).when(mockedParcel).readByte();
        doAnswer(answer).when(mockedParcel).readSize();
        doAnswer(answer).when(mockedParcel).readSizeF();
        doAnswer(answer).when(mockedParcel).readSerializable();
        doAnswer(answer).when(mockedParcel).createStringArrayList();
        doAnswer(answer).when(mockedParcel).createStringArray();
        doAnswer(answer).when(mockedParcel).createBooleanArray();
        doAnswer(answer).when(mockedParcel).createTypedArrayList(any());
        doAnswer(answer).when(mockedParcel).readParcelable(any(ClassLoader.class));
    }
}
