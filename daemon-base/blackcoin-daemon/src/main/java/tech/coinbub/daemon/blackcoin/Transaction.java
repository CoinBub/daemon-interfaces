package tech.coinbub.daemon.blackcoin;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {
    public String txid;
    public Long version;
    public Long time;
    public Long locktime;
    public List<TxInput> vin;
    public List<TxOutput> vout;
    
    // Long-form transaction data
    public BigDecimal amount;
    public BigDecimal fee;
    public Long confirmations;
    public Boolean generated;
    public String blockhash;
    public Long blockindex;
    public Long blocktime;
    public Long timereceived;
    public List<TransactionDetail> details;
    public String comment;
    public String to;
    
    public Transaction() {}
    public Transaction(final String txid) {
        this.txid = txid;
    }
}
