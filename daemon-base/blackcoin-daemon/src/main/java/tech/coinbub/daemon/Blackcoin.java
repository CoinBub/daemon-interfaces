package tech.coinbub.daemon;

import java.math.BigDecimal;
import tech.coinbub.daemon.blackcoin.Block;
import tech.coinbub.daemon.blackcoin.Info;
import tech.coinbub.daemon.blackcoin.Transaction;

public interface Blackcoin {
    public static final String SYMBOL = "BLK";

    Info getinfo();
    String getnewaddress();
    String getnewaddress(String label);
    String getnewaddress(String label, String address_type);
    String getbestblockhash();
    String getblockhash(Long index);
    Block getblock(String hash);
    Block getblock(String hash, boolean txinfo);
    Transaction gettransaction(String txid);
    String sendtoaddress(String address, BigDecimal amount);
    String sendtoaddress(String address, BigDecimal amount, String comment);
    String sendtoaddress(String address, BigDecimal amount, String comment, String commentTo);

    public enum NodeAction {
        add,
        remove,
        onetry
    }
}
