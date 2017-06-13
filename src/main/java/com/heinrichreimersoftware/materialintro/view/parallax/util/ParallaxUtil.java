package com.heinrichreimersoftware.materialintro.view.parallax.util;

import android.view.View;
import android.view.ViewGroup;

import com.heinrichreimersoftware.materialintro.view.parallax.Parallaxable;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by meier on 13.06.17.
 */

public class ParallaxUtil {
    private ParallaxUtil() {
        //no instance
    }

    public static List<Parallaxable> findParallaxableChildren(View root) {
        List<Parallaxable> parallaxableChildrenFound = new LinkedList<>();
        Queue<View> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            View child = queue.remove();
            if (child instanceof Parallaxable) {
                parallaxableChildrenFound.add((Parallaxable) child);
            } else if (child instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) child;
                for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
                    queue.add(viewGroup.getChildAt(i));
                }
            }
        }
        return parallaxableChildrenFound;
    }

}
