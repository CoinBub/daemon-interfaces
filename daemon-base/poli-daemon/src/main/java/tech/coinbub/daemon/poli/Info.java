package tech.coinbub.daemon.poli;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Info {
    public String version;
    public Long protocolversion;
    public Long walletversion;
    public BigDecimal balance;
    public BigDecimal newmint;
    public BigDecimal stake;
    public Long blocks;
    public Long timeoffset;
    public BigDecimal moneysupply;
    public Long connections;
    public String proxy;
    public String ip;
    public Difficulty difficulty;
    public Boolean testnet;
    public Long keypoololdest;
    public Long keypoolsize;
    public BigDecimal paytxfee;
    public BigDecimal mininput;
    public String errors;
    
    public static class Difficulty {
        @JsonAlias("proof-of-work")
        public BigDecimal proofOfWork;
        @JsonAlias("proof-of-stake")
        public BigDecimal proofOfStake;
    }
}
