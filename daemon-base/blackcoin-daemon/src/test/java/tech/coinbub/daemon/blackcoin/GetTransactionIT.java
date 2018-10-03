package tech.coinbub.daemon.blackcoin;

import java.math.BigDecimal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Blackcoin;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetTransactionIT;
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;

@ExtendWith(Dockerized.class)
@DockerizedBlackcoin
public class GetTransactionIT implements StandardGetTransactionIT<Blackcoin> {
    private static final String MULTI_VOUT = "e3348178922d0e9f27422bd2a62bb22be97f20b90bc0121d833adcc5d07997a0";

    @Test
    public void canGetTransaction(final Blackcoin blackcoin) {
        final Transaction tx = blackcoin.gettransaction("f08a9509d723a65b364ce701cbda2b70345eda9605636fedc90b55d12712deb7");
        assertThat(tx, hasAllIgnoringNull(
                property("txid", is(equalTo("f08a9509d723a65b364ce701cbda2b70345eda9605636fedc90b55d12712deb7"))),
                property("version", is(equalTo(1L))),
                property("time", is(equalTo(1537822057L))),
                property("locktime", is(equalTo(0L))),
                property("vin", hasSize(1)),
                property("vout", hasSize(1)),
                property("blockhash", is(equalTo("8d5598d979159a4c75b2828b63b4e6dbb2e7d87ccf405922d86ab38f05871b32"))),
                property("confirmations", is(equalTo(1L)))
        ));
        
        // Identical to that found in `TestGetBlock.supportsShortTransactionList`
        final TxInput in = tx.vin.get(0);
        assertThat(in, hasAllIgnoringNull(
                property("coinbase", is(equalTo("01150102"))),
                property("sequence", is(equalTo(4294967295L)))
        ));

        // Identical to that found in `TestGetBlock.supportsShortTransactionList`
        final TxOutput out = tx.vout.get(0);
        assertThat(out, hasAllIgnoringNull(
                property("value", is(equalTo(new BigDecimal("10000.0003")))),
                property("n", is(equalTo(0L))),
                property("scriptPubKey", is(not(nullValue())))
        ));

        // Identical to that found in `TestGetBlock.supportsShortTransactionList`
        final ScriptPublicKey key = out.scriptPubKey;
        assertThat(key, hasAllIgnoringNull(
                property("asm", is(equalTo("OP_DUP OP_HASH160 f750c2072f5100dac4027d323fb564ad5abc8f8c OP_EQUALVERIFY OP_CHECKSIG"))),
                property("hex", is(equalTo("76a914f750c2072f5100dac4027d323fb564ad5abc8f8c88ac"))),
                property("type", is(equalTo(ScriptPublicKey.Type.pubkeyhash))),
                property("reqSigs", is(equalTo(1L))),
                property("addresses", hasSize(1))
        ));
        assertThat(key.addresses.get(0), is(equalTo("n44dwbTcYVfU7r9MAXhCaaWAZPNT9YGVz7")));
    }

    @Test
    @Override
    public void canGetTransactionWithMultipleOuts(final Blackcoin blackcoin) {
        final Transaction tx = blackcoin.gettransaction(MULTI_VOUT);
        assertThat(tx, hasAllIgnoringNull(
                property("txid", is(equalTo(MULTI_VOUT))),
                property("version", is(equalTo(1L))),
                property("time", is(equalTo(1537821085L))),
                property("locktime", is(equalTo(10L))),
                property("vin", hasSize(1)),
                property("vout", hasSize(2)),
                property("amount", is(equalTo(new BigDecimal("-345.76709498")))),
                property("fee", is(equalTo(new BigDecimal("-0.00010")))),
                property("confirmations", is(equalTo(1L))),
                property("blockhash", is(equalTo("8d5598d979159a4c75b2828b63b4e6dbb2e7d87ccf405922d86ab38f05871b32"))),
                property("blockindex", is(equalTo(1L))),
                property("blocktime", is(equalTo(1537822062L))),
                property("timereceived", is(equalTo(1537821085L))),
                property("details", hasSize(1))
        ));
        
        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("account", isEmptyString()),
                property("address", is(equalTo("n17aowaVLQipu7Bfa5P51G3K76jMkFx3Hs"))),
                property("category", is(equalTo(TransactionDetail.Category.send))),
                property("amount", is(equalTo(new BigDecimal("-345.76709498")))),
                property("fee", is(equalTo(new BigDecimal("-0.00010"))))
        ));
        
        final TxInput in = tx.vin.get(0);
        assertThat(in, hasAllIgnoringNull(
                property("txid", is(equalTo("c50139662dcb47790ca45470c098832aadef15c142aa0a400ea0b128f48e36af"))),
                property("sequence", is(equalTo(4294967294L))),
                property("vout", is(equalTo(0L))),
                property("scriptSig", is(not(nullValue())))
        ));

        final ScriptSignature sig = in.scriptSig;
        assertThat(sig, hasAllIgnoringNull(
                property("asm", is(equalTo("304502210083f512c25dd9410253f067b689dea763f03ca83364b69df1203c73e9fe89f77002207b0c6842e341437502312744dc7fafcb6795a859e8336122bdade88299dc3e0601 0366a68eb47506d5dcdeffcaa13059f9a572960dd905660139d73da6af76dea451"))),
                property("hex", is(equalTo("48304502210083f512c25dd9410253f067b689dea763f03ca83364b69df1203c73e9fe89f77002207b0c6842e341437502312744dc7fafcb6795a859e8336122bdade88299dc3e0601210366a68eb47506d5dcdeffcaa13059f9a572960dd905660139d73da6af76dea451")))
        ));

        // Check first vout
        final TxOutput firstOut = tx.vout.get(0);
        assertThat(firstOut, hasAllIgnoringNull(
                property("value", is(equalTo(new BigDecimal("9654.23280502")))),
                property("n", is(equalTo(0L))),
                property("scriptPubKey", is(not(nullValue())))
        ));

        final ScriptPublicKey firstKey = firstOut.scriptPubKey;
        assertThat(firstKey, hasAllIgnoringNull(
                property("asm", is(equalTo("OP_DUP OP_HASH160 ec8fbb4e264023eaf61a1f368fb5a2f0a3eb3660 OP_EQUALVERIFY OP_CHECKSIG"))),
                property("hex", is(equalTo("76a914ec8fbb4e264023eaf61a1f368fb5a2f0a3eb366088ac"))),
                property("type", is(equalTo(ScriptPublicKey.Type.pubkeyhash))),
                property("reqSigs", is(equalTo(1L))),
                property("addresses", hasSize(1))
        ));
        assertThat(firstKey.addresses.get(0), is(equalTo("n35mwycYPcgYavbfx3LQznKEpc1sEPH9er")));
        
        // Check second vout
        final TxOutput secondOut = tx.vout.get(1);
        assertThat(secondOut, hasAllIgnoringNull(
                property("value", is(equalTo(new BigDecimal("345.76709498")))),
                property("n", is(equalTo(1L))),
                property("scriptPubKey", is(not(nullValue())))
        ));

        final ScriptPublicKey secondKey = secondOut.scriptPubKey;
        assertThat(secondKey, hasAllIgnoringNull(
                property("asm", is(equalTo("OP_DUP OP_HASH160 d6f70397d595c95e41fd59bdb34f944077fb71df OP_EQUALVERIFY OP_CHECKSIG"))),
                property("hex", is(equalTo("76a914d6f70397d595c95e41fd59bdb34f944077fb71df88ac"))),
                property("type", is(equalTo(ScriptPublicKey.Type.pubkeyhash))),
                property("reqSigs", is(equalTo(1L))),
                property("addresses", hasSize(1))
        ));
        assertThat(secondKey.addresses.get(0), is(equalTo("n17aowaVLQipu7Bfa5P51G3K76jMkFx3Hs")));
    }

}
