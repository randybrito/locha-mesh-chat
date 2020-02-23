package Ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import javax.annotation.Nonnull;

import static android.app.Activity.RESULT_OK;
import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;


public class RNBleModule  extends ReactContextBaseJavaModule  {


    public static final String LOG_TAG = "RNbleModule";
    private static final int REQUEST_ENABLE_BT = 539;
    private ReactApplicationContext reactContext;
    private BluetoothAdapter bluetoothAdapter;
    public Promise enableBluetoothPromise;
    public  Promise connectBluetoothPromise;
    private Handler handler;
    private boolean mScanning = false;
    private static final long SCAN_PERIOD = 10000;
    public static BluetoothDevice device;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;


    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            Log.d(LOG_TAG, "onActivityResult");
            if (requestCode == REQUEST_ENABLE_BT && enableBluetoothPromise != null) {
                if (resultCode == RESULT_OK) {
                    enableBluetoothPromise.resolve(null);
                } else {
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
        handler=new Handler();
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
    public void scanDevices(Promise promise){
      if(getBluetoothAdapter().isEnabled()){

          handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                  mScanning = false;
                  getBluetoothAdapter().stopLeScan(leScanCallback);
              }
          }, SCAN_PERIOD);

          mScanning = true;
          getBluetoothAdapter().startLeScan(leScanCallback);
      }else{
         promise.reject("Error", "bluetooth is not activated");
      }
    }


    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                             WritableMap map = asWritableMap(device,rssi ,scanRecord);
                             sendEvent("scanned devices", map);
                        }
                    });
                }
            };


    private void sendEvent (String eventName , Object params){
        if (reactContext.hasActiveCatalystInstance()) {
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        }
    }



    private WritableMap asWritableMap(BluetoothDevice device , int  rssi , byte[] scanRecord ) {
        WritableMap map = Arguments.createMap();
        try{
            map.putString("name", device.getName());
            map.putString("id", device.getAddress()); // mac address
            map.putInt("rssi", rssi);
            map.putBoolean("isConnectable", true);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    };

    @ReactMethod
    public void connectDevices (String uidDevice, Promise promise){
        if(uidDevice == null){
            promise.reject("Error", "Device uid is not defined");
        }
        Log.d("new", uidDevice);
        device =  getBluetoothAdapter().getRemoteDevice(uidDevice);
        connectBluetoothPromise = promise;
        device.connectGatt(reactContext , false , mGattCallback);

    }


    private final BluetoothGattCallback mGattCallback=new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState == STATE_CONNECTED){
              if(device != null){
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                      device.createBond();
                      reactContext.registerReceiver(boundReceiver , new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
                  }else{
                      connectBluetoothPromise.reject("Error", "Device is not compatible");
                  }
              }
            } else if (newState == STATE_DISCONNECTED) {
                Log.d(LOG_TAG, "desconectado:");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d("onServicesDiscovered", "execute here: ");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d("onCharacteristicRead", "execute here: ");

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d("onCharacteristicChanged", "execute here: ");
        }
    };



    private final BroadcastReceiver boundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                final int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                switch (bondState){
                    case BluetoothDevice.BOND_BONDED:
                        Log.d("onReceive", "BOND_BONDED");
                        connectBluetoothPromise.resolve(null);
                        connectBluetoothPromise= null;
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.d("onReceive", "BOND_BONDING");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        connectBluetoothPromise.reject("Error", "Bond request has been denied");
                        connectBluetoothPromise = null ;
                        break;

                }

            }

        }
    };

}
