package tech.coinbub.daemon.dash;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TxOutSetInfo {
    public Long height;
    public String bestblock;
    public Long transactions;
    public Long txouts;
    public String hash_serialized_2;
    public Long disk_size;
    public BigDecimal total_amount;
}
