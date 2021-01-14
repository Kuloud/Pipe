// IPipeService.aidl
package com.kuloud.android.pipe;

import com.kuloud.android.pipe.Flow;

interface IPipeService {
    int subscribe(String pumpId);

    int unsubscribe(String pumpId);

    Flow process(String pumpId, in Flow input);
}