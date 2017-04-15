package be.ugent.zeus.hydra.ui.common.recyclerview.viewholders;

import android.view.View;

import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;

public interface DataViewHolderFactory<E> {
    DataViewHolder<E> newInstance(View itemView);
}
