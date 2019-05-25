package io.zcx.apk;

import android.os.Bundle;

public interface InstallerCallback {

    void onSuccess(Bundle bundle);

    void onFailure(Bundle bundle);

    void onAborted(Bundle bundle);

    void onPending(Bundle bundle);

}
