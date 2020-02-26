package com.lochameshchat;


import android.app.Application;

import com.bitgo.randombytes.RandomBytesPackage;
import com.facebook.react.ReactApplication;
import com.polidea.reactnativeble.BlePackage;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.horcrux.svg.SvgPackage;
import com.oblador.vectoricons.VectorIconsPackage;
import com.ocetnik.timer.BackgroundTimerPackage;
import com.peel.react.TcpSocketsModule;
import com.reactlibrary.LanguageDetectorPackage;
import com.reactnative.ivpusic.imagepicker.PickerPackage;
import com.reactnativecommunity.asyncstorage.AsyncStoragePackage;
import com.reactnativecommunity.cameraroll.CameraRollPackage;
import com.reactnativecommunity.slider.ReactSliderPackage;
import com.rnfs.RNFSPackage;
import com.rnim.rn.audio.ReactNativeAudioPackage;
import com.swmansion.gesturehandler.react.RNGestureHandlerPackage;
import com.tradle.react.UdpSocketsModule;
import com.wenkesj.voice.VoicePackage;
import com.zmxv.RNSound.RNSoundPackage;

import org.reactnative.camera.RNCameraPackage;

import java.util.Arrays;
import java.util.List;

import Ble.RNBlePackage;
import DeviceInfo.DeviceInfoPackage;
import LocalNotification.LocalNotificationPackage;
import cl.json.RNSharePackage;
import io.github.elyx0.reactnativedocumentpicker.DocumentPickerPackage;
import io.realm.react.RealmReactPackage;

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
            new MainReactPackage(),
            new BlePackage(),
            new AsyncStoragePackage(),
            new LocalNotificationPackage(),
            new LanguageDetectorPackage(),
            new RNSharePackage(),
            new VoicePackage(),
            new UdpSocketsModule(),
            new TcpSocketsModule(),
            new ReactSliderPackage(),
            new RNSoundPackage(),
            new ReactNativeAudioPackage(),
            new BackgroundTimerPackage(),
            new RandomBytesPackage(),
            new SvgPackage(),
            new RealmReactPackage(),
            new RNCameraPackage(),
            new RNFSPackage(),
            new PickerPackage(),
            new CameraRollPackage(),
            new RNGestureHandlerPackage(),
            new VectorIconsPackage(),
            new DocumentPickerPackage(),
            new DeviceInfoPackage(),
            new RNBlePackage()
      );
    }

    @Override
    protected String getJSMainModuleName() {
      return "index";
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);
  }
}
