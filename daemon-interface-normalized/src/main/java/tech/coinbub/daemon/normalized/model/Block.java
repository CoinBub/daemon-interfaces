package tech.coinbub.daemon.normalized.model;

import java.util.List;

public class Block {
    public String hash;
    public Long confirmations;
    public Long size;
    public Long height;
    public Long time;
    public String previousblockhash;
    public String nextblockhash;
    public List<String> tx;
}
