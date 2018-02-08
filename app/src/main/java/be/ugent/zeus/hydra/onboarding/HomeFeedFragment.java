package be.ugent.zeus.hydra.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.preferences.HomeFeedSelectFragment;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;

/**
 * @author Niko Strijbol
 */
public class HomeFeedFragment extends SlideFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager manager = getChildFragmentManager();
        manager.beginTransaction().add(R.id.recycler_fragment, new HomeFeedSelectFragment()).commit();
    }
}