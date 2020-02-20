import {
  createStackNavigator,
  createAppContainer,
  createDrawerNavigator
} from 'react-navigation';
import DualComponent from './index';
import Contact from './views/contacts';
import Config from './views/config';
import Chat from './views/home/Chat';
import Drawer from './components/Drawer';
import BleView from './views/bleView';

// import Gallery from "./components/Gallery";

export const AppStackNavigator = createStackNavigator({
  initial: DualComponent,
  contacts: Contact,
  config: Config,
  chat: Chat,
  ble: BleView
});

const MyDrawerNavigator = createDrawerNavigator(
  {
    Home: {
      screen: AppStackNavigator
    }
  },
  {
    useNativeAnimations: false,
    contentComponent: Drawer
  }
);

// eslint-disable-next-line no-undef
export default RouteContainer = createAppContainer(MyDrawerNavigator);
