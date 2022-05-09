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

package be.ugent.zeus.hydra.wpi.tab.transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Transaction on Tab.
 * 
 * @author Niko Strijbol
 */
public class Transaction {
    private int id;
    private String debtor;
    private String creditor;
    private OffsetDateTime time;
    private int amount;
    private String issuer;
    private String message;

    public int getId() {
        return id;
    }

    public String getDebtor() {
        return debtor;
    }

    public String getCreditor() {
        return creditor;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public int getAmount() {
        return amount;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getMessage() {
        return message;
    }

    public BigDecimal getAdjustedAmount(String fromPerspectiveOf) {
        BigDecimal raw = new BigDecimal(getAmount()).movePointLeft(2);
        if (fromPerspectiveOf.equals(getDebtor())) {
            return raw.negate();
        } else {
            return raw;
        }
    }
    
    public String getDisplayOther(String fromPerspectiveOf) {
        String other = getOtherParty(fromPerspectiveOf);
        if (other.equals("Tab")) {
            other = "Zeus";
        }
        return other;
    }

    public String getOtherParty(String fromPerspectiveOf) {
        String other;
        if (fromPerspectiveOf.equals(getDebtor())) {
            other = getCreditor();
        } else {
            other = getDebtor();
        }
        return other;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
