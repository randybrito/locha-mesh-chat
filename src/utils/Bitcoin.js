/* eslint-disable no-unused-vars */
import crypto from 'crypto';
import Bitcore from 'bitcore-lib';
import Mnemonic from 'bitcore-mnemonic';

const networkConfiguration = {
  network_data: 'mainnet',
  bip44_id: 0
};
/**
 *
 *
 * @export
 * @class Bitcoin
 * @description here are all the functions of the wallet
 */

export default class Bitcoin {
  constructor() {
    this.WalletInfo = {};
    this.initialIndex = 0;
  }
  /**
   *
   * @function
   * @memberof Bitcoin
   * @description generate the private key and public key
   */

  generateAddress = async (seed) => {
    const code = new Mnemonic(seed);

    const hdPrivateKey = code.toHDPrivateKey(
      '',
      networkConfiguration.network_data
    );

    const derivationPath = hdPrivateKey
      .derive(44, true)
      .derive(networkConfiguration.bip44_id, true)
      .derive(0, true)
      .derive(0);

    this.WalletInfo = derivationPath.derive(this.initialIndex).privateKey;

    return this.WalletInfo;
  };
}
