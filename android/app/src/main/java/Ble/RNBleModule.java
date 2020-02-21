package Ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import static android.app.Activity.RESULT_OK;

public class RNBleModule  extends ReactContextBaseJavaModule  {


    public static final String LOG_TAG = "RNbleModule";
    private static final int REQUEST_ENABLE_BT = 539;
    private ReactApplicationContext reactContext;
    private BluetoothAdapter bluetoothAdapter;
    public Promise enableBluetoothPromise;


    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            Log.d(LOG_TAG, "onActivityResult");
            if (requestCode == REQUEST_ENABLE_BT && enableBluetoothPromise != null) {
                if (resultCode == RESULT_OK) {
                    Log.d("1", "entro aqui ");
                    enableBluetoothPromise.resolve(null);
                } else {
                    Log.d("2", "entro aqui ");
                    enableBluetoothPromise.reject("Error","User refused to enable");
                }
                enableBluetoothPromise = null;
            }
        }
    };


    public RNBleModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);

        this.reactContext = reactContext;
        reactContext.addActivityEventListener(mActivityEventListener);
    }


    private BluetoothAdapter getBluetoothAdapter() {
        if (bluetoothAdapter == null) {
            BluetoothManager manager = (BluetoothManager) reactContext.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = manager.getAdapter();
        }
        return bluetoothAdapter;
    }

    @Nonnull
    @Override
    public String getName() {
        return "RNbleModule";
    }


    @ReactMethod
    public void isActive (Promise promise) {
        Log.d("isEnabled?", getBluetoothAdapter().isEnabled() + "" );
        try {
          Boolean isEnabled = getBluetoothAdapter().isEnabled();
          promise.resolve(isEnabled);
        }catch (Error err){
            promise.reject(err);
        }

    }


    @ReactMethod
    public void activateBle (Promise promise) {
        enableBluetoothPromise = promise;
        if (getBluetoothAdapter() == null || !getBluetoothAdapter().isEnabled()) {
            try {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
              this.reactContext.getCurrentActivity().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }catch (Error err){
                promise.reject(err);
            }
        }else if(getBluetoothAdapter().isEnabled()) {
            promise.resolve(null);
        }else{
            promise.reject("Error","Unsupported device");
        }
    }


    @ReactMethod
    public void scanDevices(){
        List<ScanFilter> filters = new ArrayList<>();
    }
}
