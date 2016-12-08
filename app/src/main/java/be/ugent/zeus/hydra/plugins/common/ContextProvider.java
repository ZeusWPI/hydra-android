package be.ugent.zeus.hydra.plugins.common;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Support class for {@link Plugin}s. This class provides common contexts to the Plugins.
 *
 * Since plugins can be used with both Activities and Fragments, this encapsulate the difference and tries to unify
 * the access to contexts.
 *
 * @author Niko Strijbol
 */
final class ContextProvider {

    private ContextProvider() {
    }

    /**
     * Create a provider from a Fragment.
     *
     * @param fragment The fragment.
     *
     * @return A provider.
     */
    static FragmentProvider getProvider(Fragment fragment) {
        return new FragmentProvider(fragment);
    }

    /**
     * Create a provider from an Activity.
     *
     * @param activity The activity.
     *
     * @return A provider.
     */
    static ActivityProvider getProvider(AppCompatActivity activity) {
        return new ActivityProvider(activity);
    }

    /**
     * The actual interface with the provide contexts.
     */
    public interface Provider {

        /**
         * Get the loader manager.
         *
         * @return The loader manager.
         */
        LoaderManager getLoaderManager();

        /**
         * The fragment manager. This is not the child fragment manager.
         *
         * @return The fragment manager.
         */
        FragmentManager getFragmentManager();

        /**
         * @return The context.
         */
        Context getContext();

        /**
         * @return The root view.
         */
        View getRoot();
    }

    static class FragmentProvider implements Provider {

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

    static class ActivityProvider implements Provider {

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