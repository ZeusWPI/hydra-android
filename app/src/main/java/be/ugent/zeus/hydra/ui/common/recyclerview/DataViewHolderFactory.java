package be.ugent.zeus.hydra.ui.common.recyclerview;

import android.view.View;

public interface DataViewHolderFactory<E> {
    DataViewHolder<E> newInstance(View itemView);
}
