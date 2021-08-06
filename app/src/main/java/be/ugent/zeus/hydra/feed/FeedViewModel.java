/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.feed;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.arch.data.Event;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RefreshViewModel;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.commands.CommandResult;
import be.ugent.zeus.hydra.feed.commands.FeedCommand;

/**
 * @author Niko Strijbol
 */
public class FeedViewModel extends RefreshViewModel<List<Card>> {

    private MutableLiveData<Event<CommandResult>> commandLiveData;

    public FeedViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<Card>>> constructDataInstance() {
        return new FeedLiveData(getApplication());
    }

    LiveData<Event<CommandResult>> getCommandLiveData() {
        if (commandLiveData == null) {
            commandLiveData = new MutableLiveData<>();
        }
        return commandLiveData;
    }

    void execute(FeedCommand command) {
        AsyncTask.execute(() -> {
            int result = command.execute(getApplication());
            commandLiveData.postValue(new Event<>(CommandResult.forExecute(command, result)));
        });
    }

    void undo(FeedCommand command) {
        AsyncTask.execute(() -> {
            int result = command.undo(getApplication());
            commandLiveData.postValue(new Event<>(CommandResult.forUndo(result)));
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        commandLiveData = null;
    }
}
