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

package be.ugent.zeus.hydra.wpi.tab.requests;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Currency;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

/**
 * Show "acceptable" requests.
 *
 * @author Niko Strijbol
 * @see TabRequestRequest#acceptableRequests(Context)
 */
class AcceptableRequestsViewHolder extends DataViewHolder<TabRequest> {

    private final TextView summary;
    private final TextView description;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    private final Button declineButton;
    private final Button acceptButton;

    AcceptableRequestsViewHolder(View v) {
        super(v);
        this.summary = v.findViewById(R.id.request_summary);
        this.description = v.findViewById(R.id.request_description);
        this.currencyFormatter.setCurrency(Currency.getInstance("EUR"));
        this.declineButton = v.findViewById(R.id.decline_button);
        this.acceptButton = v.findViewById(R.id.accept_button);
        
        acceptButton.setOnClickListener(v1 -> Toast.makeText(v1.getContext(), "Pressed OK!", Toast.LENGTH_SHORT).show());
        declineButton.setOnClickListener(v1 -> Toast.makeText(v1.getContext(), "Pressed NO!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void populate(final TabRequest tabrequest) {
        String amount = currencyFormatter.format(tabrequest.getBigAmount());
        String to = tabrequest.getDebtor();
        summary.setText(summary.getContext().getString(R.string.wpi_tab_transaction_summary, amount, to));
        description.setText(tabrequest.getMessage());
        
        acceptButton.setEnabled(tabrequest.getActions().contains("confirm"));
        declineButton.setEnabled(tabrequest.getActions().contains("decline"));
    }

}
