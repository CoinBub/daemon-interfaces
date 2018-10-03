package tech.coinbub.daemon.blackcoin;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a single block in the blockchain.
 * 
 * Received when calling `getblock`.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Block {
    public String hash;
    public Long confirmations;
    public Long size;
    public Long height;
    public Long version;
    public String merkleroot;
    public BigDecimal mint;
    public Long time;
    public Long nonce;
    public String bits;
    public BigDecimal difficulty;
    public String blocktrust;
    public String chaintrust;
    public String previousblockhash;
    public String nextblockhash;
    public String flags;
    public String proofhash;
    public Long entropybit;
    public String modifier;
    public String modifierv2;
    public List<Transaction> tx;
    // XXX Thus far, `signature` has only shown up in block e2fcfcd8e73c13b7c5981c7040ca679f488360161d0c7de724850aad954bd3fb
    // I have no idea how to replicate it at this time.
    public String signature;
}
