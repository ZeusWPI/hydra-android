package be.ugent.zeus.hydra.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import be.ugent.zeus.hydra.R;

/**
 * Created by Mitch on 25/01/2016.
 */
public class RestoMenu extends View {

    String soep;
    String groenten;
    String hoofdgerecht;

    public RestoMenu(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attrsValues = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RestoMenu,0,0);

        try {
            soep = attrsValues.getString(R.styleable.RestoMenu_soep);
            groenten = attrsValues.getString(R.styleable.RestoMenu_groenten);
            hoofdgerecht = attrsValues.getString(R.styleable.RestoMenu_hoofdgerecht);
        } finally {
            attrsValues.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public String getSoep() {
        return soep;
    }

    public void setSoep(String soep) {
        this.soep = soep;
        invalidate();
        requestLayout();
    }

    public String getGroenten() {
        return groenten;
    }

    public void setGroenten(String groenten) {
        this.groenten = groenten;
        invalidate();
        requestLayout();
    }

    public String getHoofdgerecht() {
        return hoofdgerecht;
    }

    public void setHoofdgerecht(String hoofdgerecht) {
        this.hoofdgerecht = hoofdgerecht;
        invalidate();
        requestLayout();
    }
}
