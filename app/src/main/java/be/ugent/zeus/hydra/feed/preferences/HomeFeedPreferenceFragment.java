package be.ugent.zeus.hydra.feed.preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import be.ugent.zeus.hydra.R;

/**
 * Allow the user to select which home feed things they wants. This fragment is basically a wrapper around
 * {@link HomeFeedSelectFragment}, with some additional text.
 *
 * @author Niko Strijbol
 */
public class HomeFeedPreferenceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences_homefeed, container, false);
    }
}