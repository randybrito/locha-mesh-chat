import React, { Component } from 'react';
import { View, NativeModules, NativeEventEmitter } from 'react-native';
import ScanDevices from './ScanDevices';

const bleModule = NativeModules.RNbleModule;

export default class index extends Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }


  componentDidMount = () => {
    bleModule.scanDevices();
    const emitter = new NativeEventEmitter(bleModule);
    emitter.addListener('scanned devices', (event) => {
      console.log(event);
    });
  }

  // eslint-disable-next-line react/sort-comp
  static navigationOptions = {
    header: null
  };

  render() {
    bleModule.connectDevices('BC:DD:C2:D1:B6:1A');
    return (
      <View>
        <ScanDevices {...this.props} />
      </View>
    );
  }
}
