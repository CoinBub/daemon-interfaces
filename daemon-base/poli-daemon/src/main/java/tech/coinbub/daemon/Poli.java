package tech.coinbub.daemon;

import java.math.BigDecimal;
import tech.coinbub.daemon.poli.Block;
import tech.coinbub.daemon.poli.Info;
import tech.coinbub.daemon.poli.Transaction;

public interface Poli {
    public static final String SYMBOL = "POLI";
    /**
     * `getinfo`
     *
     * Returns an object containing various state info.
     */
    Info getinfo();
    /**
     * `getnewaddress [account]`
     * 
     * Returns a new NIKO address for receiving payments.  If [account] is specified, it is added to the address book so
     * payments received with the address will be credited to [account].
     */
    String getnewaddress();
    String getnewaddress(String label);
    String getnewaddress(String label, String address_type);

    /**
     * `getbestblockhash`
     * 
     * Returns the hash of the best block in the longest block chain.
     */
    String getbestblockhash();

    /**
     * getblockhash <index>
     * Returns hash of block in best-block-chain at <index>.
     */
    String getblockhash(Long index);

    /**
     * `getblock <hash> [txinfo]`
     * 
     * Returns details of a block with given block-hash.
     * 
     * txinfo optional to print more detailed tx info
     */
    Block getblock(String hash);
    Block getblock(String hash, boolean txinfo);
    
    /**
     * `gettransaction <txid>`
     * 
     * Get detailed information about <txid>
     */
    Transaction gettransaction(String txid);
    /**
     * `sendtoaddress <NIKOaddress> <amount> [comment] [comment-to]`
     * 
     * <amount> is a real and is rounded to the nearest 0.000001
     */
    String sendtoaddress(String address, BigDecimal amount);
    String sendtoaddress(String address, BigDecimal amount, String comment);
    String sendtoaddress(String address, BigDecimal amount, String comment, String commentTo);

    public enum NodeAction {
        add,
        remove,
        onetry
    }
}
