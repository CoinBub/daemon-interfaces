package tech.coinbub.daemon.dash;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;

public class BlockchainInfo {
    public String chain;
    public Long blocks;
    public Long headers;
    public String bestblockhash;
    public BigDecimal difficulty;
    public Long mediantime;
    public Long verificationprogress;
    public String chainwork;
    public Boolean pruned;
    public List<Softfork> softforks;
    public List<Bip9Softfork> bip9_softforks;
    public String warnings;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Softfork {
        public String id;
        public Long version;
        public Payment enforce;
        public Payment reject;
        
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Payment {
            public Boolean status;
            public Long found;
            public Long required;
            public Long window;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Bip9Softfork {
        public String id;
        public String status;
    }
}
