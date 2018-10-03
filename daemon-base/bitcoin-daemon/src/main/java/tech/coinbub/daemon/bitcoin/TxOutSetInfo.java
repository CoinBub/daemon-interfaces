package tech.coinbub.daemon.bitcoin;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TxOutSetInfo {
    public Long height;
    public String bestblock;
    public Long transactions;
    public Long txouts;
    public Long bogosize;
    public String hash_serialized_2;
    public Long disk_size;
    public BigDecimal total_amount;
}
