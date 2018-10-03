package tech.coinbub.daemon.litecoin;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletInfo {
    public String walletname;
    public Long walletversion;
    public BigDecimal balance;
    public BigDecimal unconfirmed_balance;
    public BigDecimal immature_balance;
    public Long txcount;
    public Long keypoololdest;
    public Long keypoolsize;
    public Long keypoolsize_hd_internal;
    public BigDecimal paytxfee;
    public String hdmasterkeyid;
}
