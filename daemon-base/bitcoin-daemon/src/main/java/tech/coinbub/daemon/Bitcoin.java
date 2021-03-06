package tech.coinbub.daemon;

import tech.coinbub.daemon.bitcoin.Block;
import tech.coinbub.daemon.bitcoin.Transaction;
import java.math.BigDecimal;
import tech.coinbub.daemon.bitcoin.BlockchainInfo;
import tech.coinbub.daemon.bitcoin.NetworkInfo;
import tech.coinbub.daemon.bitcoin.TxOutSetInfo;
import tech.coinbub.daemon.bitcoin.WalletInfo;

public interface Bitcoin {
    public static final String SYMBOL = "BTC";

    BlockchainInfo getblockchaininfo();
    NetworkInfo getnetworkinfo();
    WalletInfo getwalletinfo();
    TxOutSetInfo gettxoutsetinfo();

    /**
     * `getnewaddress ( "label" "address_type" )`
     *
     * Returns a new Bitcoin address for receiving payments. If 'label' is specified, it is added to the address book 
     * so payments received with the address will be associated with 'label'.
     * 
     * Arguments:
     * 1. "label"          (string, optional) The label name for the address to be linked to. If not provided, the default label "" is used. It can also be set to the empty string "" to represent the default label. The label does not need to exist, it will be created if there is no label by the given name.
     * 2. "address_type"   (string, optional) The address type to use. Options are "legacy", "p2sh-segwit", and "bech32". Default is set by -addresstype.
     * 
     * Result:
     * "address"    (string) The new bitcoin address
     * 
     * Examples:
     * > bitcoin-cli getnewaddress 
     * > curl --user myusername --data-binary '{"jsonrpc": "1.0", "id":"curltest", "method": "getnewaddress", "params": [] }' -H 'content-type: text/plain;' http://127.0.0.1:8332/
     * @return The new bitcoin address
     */
    String getnewaddress();
    String getnewaddress(String label);
    String getnewaddress(String label, String address_type);

    String getbestblockhash();
    
    String getblockhash(Long height);

    Block getblock(String blockhash);
    Block getblock(String blockhash, int verbosity);
    
    /**
     * gettransaction "txid" ( include_watchonly )
     * 
     * Get detailed information about in-wallet transaction <txid>
     * 
     * Arguments:
     * 1. "txid"                  (string, required) The transaction id
     * 2. "include_watchonly"     (bool, optional, default=false) Whether to include watch-only addresses in balance calculation and details[]
     * 
     * Result:
     * {
     *   "amount" : x.xxx,        (numeric) The transaction amount in BTC
     *   "fee": x.xxx,            (numeric) The amount of the fee in BTC. This is negative and only available for the 
     *                               'send' category of transactions.
     *   "confirmations" : n,     (numeric) The number of confirmations
     *   "blockhash" : "hash",  (string) The block hash
     *   "blockindex" : xx,       (numeric) The index of the transaction in the block that includes it
     *   "blocktime" : ttt,       (numeric) The time in seconds since epoch (1 Jan 1970 GMT)
     *   "txid" : "transactionid",   (string) The transaction id.
     *   "time" : ttt,            (numeric) The transaction time in seconds since epoch (1 Jan 1970 GMT)
     *   "timereceived" : ttt,    (numeric) The time received in seconds since epoch (1 Jan 1970 GMT)
     *   "bip125-replaceable": "yes|no|unknown",  (string) Whether this transaction could be replaced due to BIP125 (replace-by-fee);
     *                                                    may be unknown for unconfirmed transactions not in the mempool
     *   "details" : [
     *     {
     *       "account" : "accountname",      (string) DEPRECATED. This field will be removed in a V0.18. To see this deprecated field, start bitcoind with -deprecatedrpc=accounts. The account name involved in the transaction, can be "" for the default account.
     *       "address" : "address",          (string) The bitcoin address involved in the transaction
     *       "category" : "send|receive",    (string) The category, either 'send' or 'receive'
     *       "amount" : x.xxx,                 (numeric) The amount in BTC
     *       "label" : "label",              (string) A comment for the address/transaction, if any
     *       "vout" : n,                       (numeric) the vout value
     *       "fee": x.xxx,                     (numeric) The amount of the fee in BTC. This is negative and only available for the 
     *                                            'send' category of transactions.
     *       "abandoned": xxx                  (bool) 'true' if the transaction has been abandoned (inputs are respendable). Only available for the 
     *                                            'send' category of transactions.
     *     }
     *     ,...
     *   ],
     *   "hex" : "data"         (string) Raw data for transaction
     * }
     * 
     * Examples:
     * > bitcoin-cli gettransaction "1075db55d416d3ca199f55b6084e2115b9345e16c5cf302fc80e9d5fbf5d48d"
     * > bitcoin-cli gettransaction "1075db55d416d3ca199f55b6084e2115b9345e16c5cf302fc80e9d5fbf5d48d" true
     * > curl --user myusername --data-binary '{"jsonrpc": "1.0", "id":"curltest", "method": "gettransaction", "params": ["1075db55d416d3ca199f55b6084e2115b9345e16c5cf302fc80e9d5fbf5d48d"] }' -H 'content-type: text/plain;' http://127.0.0.1:8332/*
     */
    Transaction gettransaction(String txid);
    Transaction gettransaction(String txid, boolean include_watchonly);

    /**
     * sendtoaddress "address" amount ( "comment" "comment_to" subtractfeefromamount replaceable conf_target "estimate_mode")
     * 
     * Send an amount to a given address.
     * 
     * Arguments:
     * 1. "address"            (string, required) The bitcoin address to send to.
     * 2. "amount"             (numeric or string, required) The amount in BTC to send. eg 0.1
     * 3. "comment"            (string, optional) A comment used to store what the transaction is for. 
     *                              This is not part of the transaction, just kept in your wallet.
     * 4. "comment_to"         (string, optional) A comment to store the name of the person or organization 
     *                              to which you're sending the transaction. This is not part of the 
     *                              transaction, just kept in your wallet.
     * 5. subtractfeefromamount  (boolean, optional, default=false) The fee will be deducted from the amount being sent.
     *                              The recipient will receive less bitcoins than you enter in the amount field.
     * 6. replaceable            (boolean, optional) Allow this transaction to be replaced by a transaction with higher fees via BIP 125
     * 7. conf_target            (numeric, optional) Confirmation target (in blocks)
     * 8. "estimate_mode"      (string, optional, default=UNSET) The fee estimate mode, must be one of:
     *        "UNSET"
     *        "ECONOMICAL"
     *        "CONSERVATIVE"
     * 
     * Result:
     * "txid"                  (string) The transaction id.
     * 
     * Examples:
     * > bitcoin-cli sendtoaddress "1M72Sfpbz1BPpXFHz9m3CdqATR44Jvaydd" 0.1
     * > bitcoin-cli sendtoaddress "1M72Sfpbz1BPpXFHz9m3CdqATR44Jvaydd" 0.1 "donation" "seans outpost"
     * > bitcoin-cli sendtoaddress "1M72Sfpbz1BPpXFHz9m3CdqATR44Jvaydd" 0.1 "" "" true
     * > curl --user myusername --data-binary '{"jsonrpc": "1.0", "id":"curltest", "method": "sendtoaddress", "params": ["1M72Sfpbz1BPpXFHz9m3CdqATR44Jvaydd", 0.1, "donation", "seans outpost"] }' -H 'content-type: text/plain;' http://127.0.0.1:8332/
     * 
     */
    String sendtoaddress(String address, BigDecimal amount);
    String sendtoaddress(String address, BigDecimal amount, String comment);
    String sendtoaddress(String address, BigDecimal amount, String comment, String comment_to);
    String sendtoaddress(String address, BigDecimal amount, String comment, String comment_to, boolean subtractfeefromamount);
    String sendtoaddress(String address, BigDecimal amount, String comment, String comment_to, boolean subtractfeefromamount, boolean replaceable);
    String sendtoaddress(String address, BigDecimal amount, String comment, String comment_to, boolean subtractfeefromamount, boolean replaceable, long conf_target);
    String sendtoaddress(String address, BigDecimal amount, String comment, String comment_to, boolean subtractfeefromamount, boolean replaceable, long conf_target, EstimateMode estimate_mode);

    public enum EstimateMode {
        UNSET,
        ECONOMICAL,
        CONSERVATIVE
    }
}
