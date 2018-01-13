package be.ugent.zeus.hydra.ui.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import be.ugent.zeus.hydra.R;

/**
 *
 * @author Rien Maertens
 * @since 03/03/2016.
 */
public class ComingSoonFragment extends Fragment {

    private static final String HYDRA_FACEBOOK_URL = "https://www.facebook.com/hydra.ugent";
    private static final String HYDRA_FACEBOOK_ID = "1689280941334844";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comingsoon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton button = view.findViewById(R.id.fbButton);

        button.setOnClickListener(v -> {
            try {
                int versionCode = getContext()
                        .getPackageManager()
                        .getPackageInfo("com.facebook.katana", 0)
                        .versionCode;
                if (versionCode >= 3002850) {
                    Uri uri = Uri.parse("fb://facewebmodal/f?href=" + HYDRA_FACEBOOK_URL);
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } else {
                    // open the Facebook app using the old method (fb://page/id)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + HYDRA_FACEBOOK_ID)));
                }
            } catch (PackageManager.NameNotFoundException e) {
                // Facebook is not installed. Open the browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(HYDRA_FACEBOOK_URL)));
            }
        });
    }
}