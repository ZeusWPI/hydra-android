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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.KeyEvent;

import be.ugent.zeus.hydra.service.urgent.MusicBinder;
import be.ugent.zeus.hydra.service.urgent.MusicService;

@Deprecated
public class MediaButtonEventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            MusicService service = getMusicService(context);
            if (service != null) {
                switch(event.getKeyCode()){
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        service.play();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        service.pause();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_STOP:
                        service.stop();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        service.next();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        service.previous();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                        service.fastForward();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_REWIND:
                        service.rewind();
                        break;
                }
            }
        } else if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            MusicService service = getMusicService(context);
            if (service != null) {
                service.pause();
            }
        }
    }

    private MusicService getMusicService(Context context) {
        IBinder binder = peekService(context, new Intent(context, MusicService.class));
        if (binder == null) {
            return null;
        }
        return ((MusicBinder) binder).getService();
    }
}