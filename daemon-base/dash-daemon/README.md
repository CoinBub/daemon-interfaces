## Creating a testing chain ##

The Dash daemon requires the following parameters, at minimum, to work correctly when generating a test chain.

```
listen=1
server=1
regtest=1
```

While you do not _need_ two instances of the daemon open in order to mine, you still need two instances open in order to build a proper test chain with the required transactions. Something like the following should work:

```
dashd -rpcallowip=0.0.0.0/0 -rpcport=10001 -rpcuser=user -rpcpassword=pass -listen -server -regtest -printtoconsole
dashd -rpcallowip=0.0.0.0/0 -rpcport=10001 -rpcuser=user -rpcpassword=pass -listen -server -regtest -connect=<ip-of-first> -printtoconsole
```

1. Open two daemon instances and connect them. Generate new addreses (A + B)
2. Mine 5 blocks to address A
3. Mine 5 blocks to address B
4. Mine first 10 blocks to maturity. At time of writing, this means mining 110 blocks.
5. Verify that each address has an equal balance
6. A sends $29.95 _equivalent_ to B (Multiple vouts) f3f0c299b33446f03adef37a54952f51a484ed0db4f561739456cc7daf16b9ec
7. B sends $10.00 _equivalent_ to A (Multiple vouts) 3e8e4edca07aed4c7c285adbb680441daa72b29f8536e48ba38d7581235a45a1
8. A sends `blocksize * 1.5` to B (Multiple vouts + multiple vins) 8b2e01ff7c33b7a7d68fc7ebb196bb525ca103df5acb60e4a6b90f1d0cd5f416
9. Mine another block to package above transactions