package be.ugent.zeus.hydra.recyclerview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import su.j2e.rvjoiner.JoinableLayout;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class TextCallback implements JoinableLayout.Callback {

    private String text;

    public TextCallback(String text) {
        this.text = text;
    }

    @Override
    public void onInflateComplete(View view, ViewGroup parent) {
        TextView v = $(view, R.id.text_header);
        v.setText(text);
    }
}