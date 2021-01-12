// IPipeService.aidl
package com.kuloud.android.pipe;

import com.kuloud.android.pipe.Flow;

interface IPipeService {
    int subscribe(String id);

    int unsubscribe(String id);

    Flow process(String pumpId, in Flow input);
}