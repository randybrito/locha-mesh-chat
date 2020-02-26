import React, { Component } from 'react';
import { View, NativeModules, NativeEventEmitter } from 'react-native';
import ScanDevices from './ScanDevices';

const bleModule = NativeModules.RNbleModule;

export default class index extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isActive: false
    };
  }


  componentDidMount = () => {
    bleModule.isActive().then((activate) => {
      if (!activate) {
        bleModule.activateBle().then(() => {
          this.setState({ isActive: true });
        });
      } else {
        this.setState({ isActive: true });
      }
    });
  }

  // eslint-disable-next-line react/sort-comp
  static navigationOptions = {
    header: null
  };

  render() {
    return (
      <View>
        {this.state.isActive
          && <ScanDevices {...this.props} />}
      </View>
    );
  }
}
