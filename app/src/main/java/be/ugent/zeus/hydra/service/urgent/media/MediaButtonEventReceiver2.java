/*
 * Copyright 2016 Allan Pichardo
 * Copyright 2016 Niko Strijbol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.ugent.zeus.hydra.service.urgent.media;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.media.session.MediaButtonReceiver;
import android.view.KeyEvent;

import be.ugent.zeus.hydra.service.urgent.MediaManager;
import be.ugent.zeus.hydra.service.urgent.MusicBinder2;
import be.ugent.zeus.hydra.service.urgent.MusicService2;

public class MediaButtonEventReceiver2 extends MediaButtonReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        IBinder binder = peekService(context, new Intent(context, MusicService2.class));
        if (binder == null) {
            return;
        }
        MediaManager manager = ((MusicBinder2) binder).getManager();

        if (manager == null) {
            return;
        }

        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    manager.play();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    manager.pause();
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    manager.destroy(); // TODO: do this or not?
                    break;
            }
        } else if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            manager.pause();
        }
    }
}