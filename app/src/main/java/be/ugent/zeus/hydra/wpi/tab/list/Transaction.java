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

package be.ugent.zeus.hydra.wpi.tab.list;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Transaction on Tab.
 * 
 * @author Niko Strijbol
 * @noinspection unused
 */
public class Transaction {
    private int id;
    private String debtor;
    private String creditor;
    private OffsetDateTime time;
    private int amount;
    private String issuer;
    private String message;

    public int id() {
        return id;
    }

    public String debtor() {
        return debtor;
    }

    public String creditor() {
        return creditor;
    }

    public OffsetDateTime time() {
        return time;
    }

    public int amount() {
        return amount;
    }

    public String issuer() {
        return issuer;
    }

    public String message() {
        return message;
    }
    
    public BigDecimal bigAmount() {
        return new BigDecimal(amount()).movePointLeft(2);
    }

    public BigDecimal adjustedAmount(String fromPerspectiveOf) {
        BigDecimal raw = bigAmount();
        if (fromPerspectiveOf.equals(debtor())) {
            return raw.negate();
        } else {
            return raw;
        }
    }
    
    public String displayOther(String fromPerspectiveOf) {
        String other = otherParty(fromPerspectiveOf);
        if (other.equals("Tab")) {
            other = "Zeus";
        }
        return other;
    }

    public String otherParty(String fromPerspectiveOf) {
        String other;
        if (fromPerspectiveOf.equals(debtor())) {
            other = creditor();
        } else {
            other = debtor();
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
