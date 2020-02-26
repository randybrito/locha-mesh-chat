package Ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import static android.app.Activity.RESULT_OK;
import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class RNBleModule  extends ReactContextBaseJavaModule  {


    public static final String LOG_TAG = "RNbleModule";
    private static final int REQUEST_ENABLE_BT = 539;
    private ReactApplicationContext reactContext;
    private BluetoothAdapter bluetoothAdapter;
    public Promise enableBluetoothPromise;
    public Promise connectBluetoothPromise;
    public Promise characteristPromise;

    private Handler handler;
    private boolean mScanning = false;
    private static final long SCAN_PERIOD = 10000;
    public static BluetoothDevice device;
    private BluetoothGatt _gatt;

    public WritableNativeArray arrayMapCharacterist = new WritableNativeArray();
    public List<BluetoothGattCharacteristic> readQueue = new ArrayList<>();

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
//        handler=new Handler();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getBluetoothAdapter().getBluetoothLeScanner().startScan(mScanCallback);
            promise.resolve(null);
        }else {
            getBluetoothAdapter().startLeScan(leScanCallback);
            promise.resolve(null);
        }
//      if(getBluetoothAdapter().isEnabled()){
//
//          handler.postDelayed(new Runnable() {
//              @Override
//              public void run() {
//                  mScanning = false;
//                  getBluetoothAdapter().stopLeScan(leScanCallback);
//              }
//          }, SCAN_PERIOD);
//
//          mScanning = true;
//          getBluetoothAdapter().startLeScan(leScanCallback);
//      }else{
//         promise.reject("Error", "bluetooth is not activated");
//      }
    }


    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WritableMap map = asWritableMap(device,rssi );
                            sendEvent("scannedDevices", map);
                        }
                    });
                }
            };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback mScanCallback = new ScanCallback() {

                @Override
                public void onScanResult(final int callbackType, final ScanResult result) {
                    runOnUiThread(new Runnable() {


                        @Override
                        public void run() {
                            Log.d("aquiiiii!!!!!", "run: "+ result.getDevice().getAddress());
                            WritableMap map = null;
                                map = asWritableMap(result.getDevice(), result.getRssi());
                            sendEvent("scannedDevices", map);
                        }
                    });
                }
            };


    private void sendEvent (String eventName , Object params){
        Log.d("oh GOODDDD!!!", "sendEvent: Execute ");
        if (reactContext.hasActiveCatalystInstance()) {
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        }
    }



    private WritableMap asWritableMap(BluetoothDevice device , int  rssi ) {
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

    @ReactMethod
    public void readServices (Promise promise){
        if(getBluetoothAdapter().isEnabled() && _gatt != null ){
            characteristPromise = promise;
            _gatt.discoverServices();
        }else if (_gatt == null ) {
            promise.reject("Error " , "Device is not connected");
        }else {
            promise.reject("Error " , "Bluetooth is not activated");
        }
    }

    private final BluetoothGattCallback mGattCallback=new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState == STATE_CONNECTED){
              if(device != null){
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                      device.createBond();
                      reactContext.registerReceiver(boundReceiver , new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
                      _gatt = gatt;
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
            Log.d("onServicesDiscovered", "execute here:" + gatt.getServices());


            List<BluetoothGattService> services = gatt.getServices();
            for (BluetoothGattService gattService : services){

                List<BluetoothGattCharacteristic> listCharacteristic =  gattService.getCharacteristics();

                for (BluetoothGattCharacteristic characteristic : listCharacteristic){
                    readQueue.add(characteristic);
                }

            }

             int sizeArray = readQueue.size() - 1;
             BluetoothGattCharacteristic readCharacteristic = readQueue.get(sizeArray);
             gatt.readCharacteristic(readCharacteristic);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d("Hereeeeeeee!!!!!!!!!!!", "onCharacteristicRead: "   + characteristic.getUuid() );
          byte[] bytes = characteristic.getValue();
          WritableMap mapChateristic = Arguments.createMap();
                String value = new String(bytes);

                mapChateristic.putString("value", value);
                mapChateristic.putString("uid", characteristic.getUuid().toString());
                mapChateristic.putString("uidService", characteristic.getService().getUuid().toString() );
                mapChateristic.putBoolean("writte", characteristic.getProperties() == (BluetoothGattCharacteristic.PERMISSION_WRITE) );
                mapChateristic.putBoolean("readEncrypted", characteristic.getProperties() == (BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED));
                mapChateristic.putBoolean("read", (BluetoothGattCharacteristic.PERMISSION_READ) == characteristic.getProperties());


            arrayMapCharacterist.pushMap(mapChateristic);
            int sizeArray = readQueue.size() - 1;
            readQueue.remove(sizeArray);

            if(readQueue.size() == 1){
                try {

                    characteristPromise.resolve(arrayMapCharacterist);

                }catch (Error err){
                    Log.d("Error", "Error: " + err);
                }
            }else{
                gatt.readCharacteristic(readQueue.get(readQueue.size() -1));
            }
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
