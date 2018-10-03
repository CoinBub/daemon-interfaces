package tech.coinbub.daemon;

import tech.coinbub.daemon.normalized.Normalized;
import java.math.BigDecimal;
import java.util.ArrayList;
import tech.coinbub.daemon.poli.Block;
import tech.coinbub.daemon.poli.Info;
import tech.coinbub.daemon.poli.Transaction;
import tech.coinbub.daemon.poli.TransactionDetail;

public class NormalizedPoli implements Normalized<Poli> {
    private final Poli client;

    public NormalizedPoli(final Poli client) {
        this.client = client;
    }

    @Override
    public String getSymbol() {
        return Poli.SYMBOL;
    }

    @Override
    public tech.coinbub.daemon.normalized.model.Info getinfo() {
        final Info raw = client.getinfo();
        final tech.coinbub.daemon.normalized.model.Info result = new tech.coinbub.daemon.normalized.model.Info();
        result.version = raw.version;
        result.protocolversion = raw.protocolversion;
        result.walletversion = raw.walletversion;
        result.balance = raw.balance;
        result.newmint = raw.newmint;
        result.blocks = raw.blocks;
        result.timeoffset = raw.timeoffset;
        result.moneysupply = raw.moneysupply;
        result.connections = raw.connections;
        result.proxy = raw.proxy;
        result.ip = raw.ip;
        result.difficulty = new tech.coinbub.daemon.normalized.model.Info.Difficulty();
        result.difficulty.proofOfWork = raw.difficulty.proofOfWork;
        result.difficulty.proofOfStake = raw.difficulty.proofOfStake;
        result.testnet = raw.testnet;
        result.keypoololdest = raw.keypoololdest;
        result.keypoolsize = raw.keypoolsize;
        result.paytxfee = raw.paytxfee;
        result.errors = raw.errors;
        return result;
    }

    @Override
    public String getbestblockhash() {
        return client.getbestblockhash();
    }

    @Override
    public String getblockhash(final Long height) {
        return client.getblockhash(height);
    }

    @Override
    public tech.coinbub.daemon.normalized.model.Block getblock(final String hash) {
        final Block block = client.getblock(hash);
        final tech.coinbub.daemon.normalized.model.Block result
                = new tech.coinbub.daemon.normalized.model.Block();
        result.hash = block.hash;
        result.confirmations = block.confirmations;
        result.size = block.size;
        result.height = block.height;
        result.time = block.time;
        result.previousblockhash = block.previousblockhash;
        result.nextblockhash = block.nextblockhash;
        
        if (!block.tx.isEmpty()) {
            result.tx = new ArrayList<>();
        }
        for (Transaction tx : block.tx) {
            result.tx.add(tx.txid);
        }
        return result;
    }

    @Override
    public tech.coinbub.daemon.normalized.model.Transaction gettransaction(final String txid) {
        return normalizeTransaction(client.gettransaction(txid));
    }

    @Override
    public String getnewaddress() {
        return client.getnewaddress();
    }

    @Override
    public String sendtoaddress(final String address, final BigDecimal amount) {
        return client.sendtoaddress(address, amount);
    }

    @Override
    public String sendtoaddress(final String address, final BigDecimal amount, final String comment_from) {
        return client.sendtoaddress(address, amount, comment_from);
    }

    @Override
    public String sendtoaddress(final String address, final BigDecimal amount, final String comment_from, final String comment_to) {
        return client.sendtoaddress(address, amount, comment_from, comment_to);
    }

    //
    // Helpers and Utilities
    //
    private tech.coinbub.daemon.normalized.model.Transaction normalizeTransaction(final Transaction tx) {
        final tech.coinbub.daemon.normalized.model.Transaction result
                = new tech.coinbub.daemon.normalized.model.Transaction();
        result.id = tx.txid;
        result.amount = tx.amount;
        result.fee = tx.fee;
        result.time = tx.time;
        result.confirmations = tx.confirmations;
        result.blockhash = tx.blockhash;
        result.comment_from = tx.comment;
        result.comment_to = tx.to;

        if (tx.details != null && !tx.details.isEmpty()) {
            result.details = new ArrayList<>();
            for (TransactionDetail rawDetail : tx.details) {
                final tech.coinbub.daemon.normalized.model.TransactionDetail detail
                        = new tech.coinbub.daemon.normalized.model.TransactionDetail();
                detail.address = rawDetail.address;
                detail.amount = rawDetail.amount;
                detail.fee = rawDetail.fee;
                result.details.add(detail);
            }
        }

        return result;
    }
}

