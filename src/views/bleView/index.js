import React, { Component } from 'react';
import { View, Text } from 'react-native';
import ScanDevices from './ScanDevices';

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
    return (
      <View>
        <ScanDevices {...this.props} />
      </View>
    );
  }
}
