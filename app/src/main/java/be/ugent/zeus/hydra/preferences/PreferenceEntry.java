package be.ugent.zeus.hydra.preferences;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import java.util.function.Supplier;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.preference.AssociationSelectionPreferenceFragment;
import be.ugent.zeus.hydra.feed.preferences.HomeFragment;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;

/**
 * Indicates a fragment that can be shown in a {@link PreferenceActivity}.
 *
 * @author Niko Strijbol
 */
public enum PreferenceEntry implements Parcelable {
    HOME(
            R.string.pref_overview_feed_title,
            R.string.pref_overview_feed_desc,
            R.drawable.ic_home,
            HomeFragment::new
    ),
    ACTIVITIES(
            R.string.pref_overview_activities_title,
            R.string.pref_overview_activities_desc,
            R.drawable.ic_event,
            AssociationSelectionPreferenceFragment::new
    ),
    RESTO(
            R.string.pref_overview_resto_title,
            R.string.pref_overview_resto_desc,
            R.drawable.ic_restaurant,
            RestoPreferenceFragment::new
    ),
    THEME(
            R.string.pref_overview_theme_title,
            R.string.pref_overview_theme_desc,
            R.drawable.ic_theme_light_dark,
            ThemeFragment::new
    ),
    DATA(
            R.string.onboarding_reporting_title,
            R.string.onboarding_reporting_desc,
            R.drawable.ic_multiline_chart,
            ReportingFragment::new
    ),
    ABOUT(
            R.string.pref_overview_about_title,
            R.string.pref_overview_about_desc,
            R.drawable.ic_info_outline,
            AboutFragment::new
    );
    public static final Parcelable.Creator<PreferenceEntry> CREATOR = new Parcelable.Creator<PreferenceEntry>() {
        @Override
        public PreferenceEntry createFromParcel(Parcel in) {
            return PreferenceEntry.values()[in.readInt()];
        }

        @Override
        public PreferenceEntry[] newArray(int size) {
            return new PreferenceEntry[size];
        }
    };
    @StringRes
    private final int name;
    @StringRes
    private final int description;
    @DrawableRes
    private final int icon;
    private final Supplier<Fragment> fragmentSupplier;

    PreferenceEntry(@StringRes int name, @StringRes int description, @DrawableRes int icon, Supplier<Fragment> fragmentSupplier) {
        this.name = name;
        this.description = description;
        this.fragmentSupplier = fragmentSupplier;
        this.icon = icon;
    }

    @StringRes
    public int getName() {
        return name;
    }

    @StringRes
    public int getDescription() {
        return description;
    }

    public Fragment getFragment() {
        return fragmentSupplier.get();
    }

    public int getIcon() {
        return icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ordinal());
    }
}
