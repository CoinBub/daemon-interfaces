package tech.coinbub.daemon.blackcoin;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Unspent {
    public String txid;
    public Long vout;
    public String address;
    public String account;
    public String scriptPubKey;
    public BigDecimal amount;
    public Long confirmations;
    public Boolean spendable;
}
