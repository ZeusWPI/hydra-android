/*
 * Copyright (c) 2022 Niko Strijbol
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

package be.ugent.zeus.hydra.wpi.tab.create;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Model for creating a new transaction.
 * 
 * @author Niko Strijbol
 */
public class TransactionForm implements Parcelable {
    
    private String destination;
    private int amount;
    private String description;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getAmount() {
        return amount;
    }

    public BigDecimal getAdjustedAmount() {
        return new BigDecimal(getAmount()).movePointLeft(2);
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<String, Object> asApiObject(String me) {
        Map<String, Object> m = new HashMap<>();
        m.put("debtor", me);
        m.put("creditor", getDestination());
        m.put("cents", getAmount());
        m.put("message", getDescription());
        return m;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionForm that = (TransactionForm) o;
        return amount == that.amount && Objects.equals(destination, that.destination) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination, amount, description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.destination);
        dest.writeInt(this.amount);
        dest.writeString(this.description);
    }

    public void readFromParcel(Parcel source) {
        this.destination = source.readString();
        this.amount = source.readInt();
        this.description = source.readString();
    }

    public TransactionForm() {
    }

    protected TransactionForm(Parcel in) {
        this.destination = in.readString();
        this.amount = in.readInt();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<TransactionForm> CREATOR = new Parcelable.Creator<TransactionForm>() {
        @Override
        public TransactionForm createFromParcel(Parcel source) {
            return new TransactionForm(source);
        }

        @Override
        public TransactionForm[] newArray(int size) {
            return new TransactionForm[size];
        }
    };
}
