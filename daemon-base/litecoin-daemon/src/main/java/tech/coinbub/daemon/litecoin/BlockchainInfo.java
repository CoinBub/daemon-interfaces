package tech.coinbub.daemon.litecoin;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
    public Map<String, Bip9Softfork> bip9_softforks;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Softfork {
        public String id;
        public Long version;
        public Reject reject;
        
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Reject {
            public Boolean status;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Bip9Softfork {
        public String status;
        public Long startTime;
        public Long timeout;
        public Long since;
    }
}
