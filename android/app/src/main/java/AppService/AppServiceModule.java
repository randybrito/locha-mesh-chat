package AppService;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import javax.annotation.Nonnull;


public class AppServiceModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private Intent mService;
    private boolean mConnected = false;
    private String mName = null;

    public AppServiceModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        CheckStatusConnectionService.setUpdateListener(this);



    }

    @Nonnull
    @Override
    public String getName() {
        return "AppService";
    }


    public void setModuleParams(boolean connected, String name) {
        mConnected = connected;
        mName = name;
    }

    public void removeServiceReference() {
        mService = null;
    }

    @ReactMethod
    public void checkConnectionStatus(Callback callback) {
        if (mService == null) {
            mService = new Intent(reactContext, CheckStatusConnectionService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                reactContext.startForegroundService(mService);
            }else{
                reactContext.startService(mService);
            }
        }
        callback.invoke(mName, mConnected);
    }

}
