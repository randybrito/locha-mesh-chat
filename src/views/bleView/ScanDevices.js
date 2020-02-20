import React, { Component } from 'react';
import { View, Text, ActivityIndicator } from 'react-native';
import {
  ListItem, Left, Icon, Button, Body, Right,
} from 'native-base';
import { BleManager } from 'react-native-ble-plx';
import Header from '../../components/Header';

const bleManager = new BleManager();

export default class ScanDevices extends Component {
  constructor(props) {
    super(props);
    this.state = {
      devices: []
    };
  }

  componentDidMount = () => {
    bleManager.startDeviceScan(null, null, (err, newDevice) => {
      if (err) { return; }
      if (this.state.devices.length === 0) {
        this.setState({ devices: this.state.devices.concat(newDevice) });
      } else {
        this.state.devices.forEach((device) => {
          if (device.id !== newDevice.id) {
            this.setState({
              devices: this.state.devices.concat(newDevice)
            });
          }
        });
      }
    });
  }

  render() {
    const { devices } = this.state;

    return (
      <View style={{ height: '100%' }}>
        <Header {...this.props} name="Scan devices" />
        <View style={{
          paddingHorizontal: 10, paddingTop: 20, paddingBottom: 20, alignItems: 'center'
        }}
        >
          <Text>This application only allows pairing with devices created by the lochaMesh team </Text>
        </View>

        <View style={{ backgroundColor: '#e0e0e0', height: 10, }} />
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
          {this.state.devices.length === 0
            && (
              <ActivityIndicator
                color="#FAB300"
                size="large"
              />
            )}
          {devices.map((device) => (
            <ListItem key={device.id} icon>
              <Left>
                <Button style={{ backgroundColor: '#007AFF' }}>
                  <Icon active name="bluetooth" />
                </Button>
              </Left>
              <Body>
                <Text>{device.name}</Text>
              </Body>
              <Right>
                <Text>On</Text>
              </Right>
            </ListItem>
          ))}
        </View>
        <View style={{ backgroundColor: '#e0e0e0', height: 10 }} />
      </View>
    );
  }
}
