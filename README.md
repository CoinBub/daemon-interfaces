# CoinBub Daemon Interfaces #

This project provides a set of RPC interfaces for various cryptocurrency daemon implementations. In addition to the raw, low-level interfaces, this library also provides a "normalized" interface pattern which will allow you to integrate any and all supporting daemon interface implementations without worrying about differing behaviours between the implementations.


## Normalized Features ##

The [`daemon-interface-normalized`](https://github.com/CoinBub/daemon-interfaces/blob/master/daemon-interface-normalized/README.md) module provides a set of interfaces and value objects that represent a standard set of features and responses supported by all [supported daemons](#supported-daemons). At the time of writing, the list of normalized features supported is quite limited. Generally, these methods are named after the same fashion as the majority of the daemons' usages.

* `getinfo`
* `getbestblockhash`
* `getblockhash`
* `getblock`
* `gettransaction`
* `getnewaddress`
* `sendtoaddress`
* `normalizeException`

This is obviously a very limited feature set, but if you have requirements beyond this, feel free to [contribute](https://github.com/CoinBub/daemon-interfaces/blob/master/CONTRIBUTE.md) or to [request an enhancement](https://github.com/CoinBub/daemon-interfaces/issues)!

## Supported Daemons ##

* [Bitcoin](https://github.com/CoinBub/daemon-interfaces/blob/master/interfaces/daemon-interface-bitcoin/README.md)
* [Blackcoin](https://github.com/CoinBub/daemon-interfaces/blob/master/interfaces/daemon-interface-blackcoin/README.md)
* [Dash](https://github.com/CoinBub/daemon-interfaces/blob/master/interfaces/daemon-interface-dash/README.md)
* [Litecoin](https://github.com/CoinBub/daemon-interfaces/blob/master/interfaces/daemon-interface-litecoin/README.md)
* [Poli](https://github.com/CoinBub/daemon-interfaces/blob/master/interfaces/daemon-interface-poli/README.md)
* [Niko](https://github.com/CoinBub/daemon-interfaces/blob/master/interfaces/daemon-interface-niko/README.md)

- If you have crafted an interface implementation that you wish to be included in this list, [please file a ticket](https://github.com/CoinBub/daemon-interfaces/issues)!
- If you have crafted an interface implementation that you wish to be folded into this library, [please file a ticket](https://github.com/CoinBub/daemon-interfaces/issues)!

