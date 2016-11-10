package be.ugent.zeus.hydra.plugins.common;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * @author Niko Strijbol
 */
public final class ContextProvider {

    private ContextProvider() {}

    static FragmentProvider getProvider(Fragment fragment) {
        return new FragmentProvider(fragment);
    }

    static ActivityProvider getProvider(AppCompatActivity activity) {
        return new ActivityProvider(activity);
    }

    public interface Provider {

        LoaderManager getLoaderManager();

        FragmentManager getFragmentManager();

        Context getContext();

        View getRoot();
    }

    public static class FragmentProvider implements Provider {

        private final Fragment fragment;

        private FragmentProvider(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public LoaderManager getLoaderManager() {
            return fragment.getLoaderManager();
        }

        @Override
        public FragmentManager getFragmentManager() {
            return fragment.getFragmentManager();
        }

        @Override
        public Context getContext() {
            return fragment.getContext();
        }

        @Override
        public View getRoot() {
            return fragment.getView();
        }

        public Fragment getFragment() {
            return fragment;
        }
    }

    public static class ActivityProvider implements Provider {

        private final AppCompatActivity activity;

        private ActivityProvider(AppCompatActivity activity) {
            this.activity = activity;
        }

        @Override
        public LoaderManager getLoaderManager() {
            return activity.getSupportLoaderManager();
        }

        @Override
        public FragmentManager getFragmentManager() {
            return activity.getSupportFragmentManager();
        }

        @Override
        public Context getContext() {
            return activity;
        }

        @Override
        public View getRoot() {
            return activity.findViewById(android.R.id.content);
        }

        public AppCompatActivity getActivity() {
            return activity;
        }
    }
}
