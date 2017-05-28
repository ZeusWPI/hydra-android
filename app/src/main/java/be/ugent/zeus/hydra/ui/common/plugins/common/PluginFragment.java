package be.ugent.zeus.hydra.ui.common.plugins.common;

import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that hosts plugins.
 *
 * To use plugins with fragments, extend this class. Then insert the needed plugins in {@link #onAddPlugins(List)}.
 *
 * @author Niko Strijbol
 */
public class PluginFragment extends LifecycleFragment {

    private List<FragmentPlugin> fragmentPlugins = new ArrayList<>();
    private List<Plugin> otherPlugins = new ArrayList<>();

    /**
     * This method allows to register plugins. The plugins should be added to the given list. This method is also
     * suitable for initialising the plugin, as it will be called before anything else.
     *
     * @param plugins The list.
     */
    @CallSuper
    protected void onAddPlugins(List<Plugin> plugins) {
        //Nothing
    }

    @Override
    public void onAttach(Context context) {
        onAddPlugins(otherPlugins);
        List<Plugin> recursive = new ArrayList<>();
        for (Plugin plugin : otherPlugins) {
            plugin.onAddPlugins(recursive);
        }
        otherPlugins.addAll(recursive);
        for (Plugin plugin : otherPlugins) {
            if (plugin instanceof FragmentPlugin) {
                fragmentPlugins.add((FragmentPlugin) plugin);
            }
            plugin.setProvider(ContextProvider.getProvider(this));
        }
        super.onAttach(context);
        for (FragmentPlugin plugin : fragmentPlugins) {
            plugin.onAttach(context);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (Plugin plugin : otherPlugins) {
            plugin.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        for (Plugin plugin : otherPlugins) {
            plugin.onViewCreated(view, savedInstanceState);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        for (FragmentPlugin plugin : fragmentPlugins) {
            plugin.onActivityCreated(savedInstanceState);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        for (FragmentPlugin plugin : fragmentPlugins) {
            plugin.onViewStateRestored(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        for (Plugin plugin : otherPlugins) {
            plugin.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (Plugin plugin : otherPlugins) {
            plugin.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (Plugin plugin : otherPlugins) {
            plugin.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        for (Plugin plugin : otherPlugins) {
            plugin.onStop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (FragmentPlugin plugin : fragmentPlugins) {
            plugin.onDestroyView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Plugin plugin : otherPlugins) {
            plugin.onDestroy();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        for (FragmentPlugin plugin : fragmentPlugins) {
            plugin.onDetach();
        }
    }
}