package tech.coinbub.daemon.litecoin;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkInfo {
    public Long version;
    public String subversion;
    public Long protocolversion;
    public String localservices;
    public Boolean localrelay;
    public Long timeoffset;
    public Boolean networkactive;
    public Long connections;
    public List<Network> networks;
    public BigDecimal relayfee;
    public BigDecimal incrementalfee;
    public List<String> localaddresses;
    public String warnings;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Network {
        public String name;
        public Boolean limited;
        public Boolean reachable;
        public String proxy;
        public Boolean proxy_randomize_credentials;
    }
}
