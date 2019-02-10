/*
 * Copyright (C) 2016 The Android Open Source Project
 * Copyright 2018 Niko Strijbol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

/**
 * Extended version of {@link androidx.recyclerview.widget.ListUpdateCallback}.
 *
 * @author Niko Strijbol
 */
public interface ListUpdateCallback extends androidx.recyclerview.widget.ListUpdateCallback {

    /**
     * Called when the data has changed, but no information about the change is known.
     */
    void onDataSetChanged();
}