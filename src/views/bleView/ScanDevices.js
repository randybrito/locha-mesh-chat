import React, { Component } from 'react';
import {
  View, Text, ActivityIndicator, StyleSheet
} from 'react-native';
import {
  ListItem, Left, Icon, Button, Body, Right,
} from 'native-base';
import { BleManager } from 'react-native-ble-plx';
import Header from '../../components/Header';
import { toast } from '../../utils/utils';

const bleManager = new BleManager();

export default class ScanDevices extends Component {
  constructor(props) {
    super(props);
    this.state = {
      devices: []
    };
  }

  // componentDidMount = () => {
  //   bleManager.startDeviceScan(null, null, (err, newDevice) => {
  //     if (err) {
  //       // eslint-disable-next-line no-console
  //       console.log(err);
  //       return;
  //     }
  //     if (this.state.devices.length === 0) {
  //       this.setState({ devices: this.state.devices.concat(newDevice) });
  //     } else {
  //       this.state.devices.forEach((device) => {
  //         if (device.id !== newDevice.id) {
  //           this.setState({
  //             devices: this.state.devices.concat(newDevice)
  //           });
  //         }
  //       });
  //     }
  //   });
  // }

  // connectWithTheDevice = (device) => {
  //   bleManager.isDeviceConnected(device.id).then((res) => {
  //     if (!res) {
  //       bleManager.connectToDevice(
  //         device.id
  //       ).then(async (connectedDevice) => {
  //         await connectedDevice.discoverAllServicesAndCharacteristics();
  //         const services = await connectedDevice.services();
  //         services.forEach(async (service) => {
  //           const characteristics = await service.characteristics();
  //           characteristics.forEach(async (data) => {
  //             const readCharacteristics = await data.read();
  //             console.log("aqui", readCharacteristics);
  //           });
  //         });
  //       });
  //     } else {
  //       toast('is already connected to this device');
  //     }
  //   });
  // }

  render() {
    const { devices } = this.state;
    const bleContainer = devices.length === 0
      ? styles.centerBleContainer : styles.normalBleContainer;
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
        <View style={bleContainer}>
          {this.state.devices.length === 0
            && (
              <ActivityIndicator
                color="#FAB300"
                size="large"
              />
            )}
          {devices.map((device) => (
            <ListItem
              key={device.id}
              icon
              button
              onPress={() => this.connectWithTheDevice(device)}
            >
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

const styles = StyleSheet.create({
  normalBleContainer: {
    flex: 1
  },

  centerBleContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center'
  }
});
