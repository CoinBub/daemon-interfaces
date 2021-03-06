package tech.coinbub.daemon;

import java.math.BigDecimal;
import java.util.ArrayList;
import tech.coinbub.daemon.bitcoin.Block;
import tech.coinbub.daemon.bitcoin.BlockchainInfo;
import tech.coinbub.daemon.bitcoin.NetworkInfo;
//import tech.coinbub.daemon.bitcoin.Info;
import tech.coinbub.daemon.bitcoin.Transaction;
import tech.coinbub.daemon.bitcoin.TransactionDetail;
import tech.coinbub.daemon.bitcoin.TxOutSetInfo;
import tech.coinbub.daemon.bitcoin.WalletInfo;
import tech.coinbub.daemon.normalized.Normalized;

public class NormalizedBitcoin implements Normalized<Bitcoin> {
    private final Bitcoin client;

    public NormalizedBitcoin(final Bitcoin client) {
        if (client == null) {
            throw new IllegalArgumentException("Client must not be null");
        }
        this.client = client;
    }

    @Override
    public String getSymbol() {
        return Bitcoin.SYMBOL;
    }

    @Override
    public tech.coinbub.daemon.normalized.model.Info getinfo() {
        final BlockchainInfo blockchain = client.getblockchaininfo();
        final NetworkInfo network = client.getnetworkinfo();
        final WalletInfo wallet = client.getwalletinfo();
        final TxOutSetInfo txouts = client.gettxoutsetinfo();

        final tech.coinbub.daemon.normalized.model.Info result = new tech.coinbub.daemon.normalized.model.Info();
        result.version = network.version.toString();
        result.protocolversion = network.protocolversion;
        result.walletversion = wallet.walletversion;
        result.balance = wallet.balance;
        result.newmint = wallet.immature_balance;
        result.blocks = blockchain.blocks;
        result.timeoffset = network.timeoffset;
        result.moneysupply = txouts.total_amount;
        result.connections = network.connections;
//        result.proxy = raw.proxy;
//        result.ip = raw.ip;
        result.difficulty = new tech.coinbub.daemon.normalized.model.Info.Difficulty();
        result.difficulty.proofOfWork = blockchain.difficulty;
//        result.testnet = raw.testnet;
        result.keypoololdest = wallet.keypoololdest;
        result.keypoolsize = wallet.keypoolsize;
        result.paytxfee = wallet.paytxfee;
        result.warnings = network.warnings;
//        result.errors = raw.errors;
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
        result.size = tx.size;
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
