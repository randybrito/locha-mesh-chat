import React, { Component } from 'react';
import { View, Text, NativeModules } from 'react-native';
import ScanDevices from './ScanDevices';

const bleModule = NativeModules.RNbleModule;

export default class index extends Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }


  // eslint-disable-next-line react/sort-comp
  static navigationOptions = {
    header: null
  };

  render() {
    bleModule.activateBle().then((res) => {
      console.log("Aqui", res);
    }).catch(err =>{
      console.log(err)
    })
    return (
      <View>
        <ScanDevices {...this.props} />
      </View>
    );
  }
}
