package tech.coinbub.daemon.support;

import java.math.BigDecimal;
import tech.coinbub.daemon.normalized.Normalized;
import tech.coinbub.daemon.normalized.model.Block;
import tech.coinbub.daemon.normalized.model.Info;
import tech.coinbub.daemon.normalized.model.Transaction;

public class NormalizedTestDaemon implements Normalized<TestDaemon> {
    private final TestDaemon client;
    
    public NormalizedTestDaemon(final TestDaemon client) {
        this.client = client;
    }

    @Override
    public String getbestblockhash() {
        return client.getbestblockhash();
    }

    @Override
    public String getSymbol() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Info getinfo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public String getblockhash(Long height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Block getblock(String hash) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Transaction gettransaction(String txid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getnewaddress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String sendtoaddress(String address, BigDecimal amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String sendtoaddress(String address, BigDecimal amount, String comment_from) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String sendtoaddress(String address, BigDecimal amount, String comment_from, String comment_to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
