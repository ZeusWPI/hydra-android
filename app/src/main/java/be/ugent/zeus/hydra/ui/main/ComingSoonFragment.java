package be.ugent.zeus.hydra.ui.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import be.ugent.zeus.hydra.R;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 *
 * @author Rien Maertens
 * @since 03/03/2016.
 */
public class ComingSoonFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comingsoon, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton button = $(view, R.id.fbButton);

        button.setOnClickListener(v -> {
            String facebookUrl = "https://www.facebook.com/hydra.ugent";
            try {
                int versionCode = getContext()
                        .getPackageManager()
                        .getPackageInfo("com.facebook.katana", 0)
                        .versionCode;
                if (versionCode >= 3002850) {
                    Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } else {
                    // open the Facebook app using the old method (fb://page/id)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1689280941334844")));
                }
            } catch (PackageManager.NameNotFoundException e) {
                // Facebook is not installed. Open the browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
            }
        });
    }
}