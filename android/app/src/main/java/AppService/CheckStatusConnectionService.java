package AppService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class CheckStatusConnectionService extends Service {
    private static AppServiceModule mModuleManager;
    private CountDownTimer mServiceTimer = null;
    private CountDownTimer mMissingConnectionTimer = null;
    private ConnectivityManager connectivity;

    @Override
    public void onCreate() {
        super.onCreate();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        connectivity = (ConnectivityManager) getBaseContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        // set check timer onCreate service that check status for ten seconds each one second
        // and restart if finish
        mServiceTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                checkConnectionStatus();
            };
            public void onFinish() {
                mServiceTimer.start();
            }
        };
        mServiceTimer.start();

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        mServiceTimer.cancel();
        if (mModuleManager != null) {
            mModuleManager.removeServiceReference();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    // Let me get the module manager reference to pass information
    public static void setUpdateListener(AppServiceModule moduleManager) {
        mModuleManager = moduleManager;
    }

    public void checkConnectionStatus(){
        // verify connection status and set
        boolean mConnected = false;
        String mName = null;
        if (connectivity != null && connectivity.getActiveNetworkInfo() != null) {
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                mName = activeNetwork.getTypeName();
                mConnected = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                mName = activeNetwork.getTypeName();
                mConnected = true;
            };
            stopMissingConnectionTimer();
        } else {
            startMissingConnectionTimer();
        }
        if (mModuleManager != null) {
            mModuleManager.setModuleParams(mConnected, mName);
        }
    }

    private void startMissingConnectionTimer() {
        if (mMissingConnectionTimer == null) {
            mMissingConnectionTimer = new CountDownTimer(10000, 10000) {
                public void onTick(long millisUntilFinished) {
                };
                public void onFinish() {
                    Toast.makeText(getBaseContext(), "Llevas 10 segundos sin internet, ¿no sientes comezón?", Toast.LENGTH_LONG).show();
//                    stopSelf();
                }
            };
            mMissingConnectionTimer.start();
        }

    }

    private void stopMissingConnectionTimer() {
        if (mMissingConnectionTimer != null) {
            mMissingConnectionTimer.cancel();
            mMissingConnectionTimer = null;
        }
    }
}