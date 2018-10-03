package tech.coinbub.daemon.blackcoin;

import java.math.BigDecimal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Blackcoin;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetBlockIT;
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;

@ExtendWith(Dockerized.class)
@DockerizedBlackcoin
public class GetBlockIT implements StandardGetBlockIT<Blackcoin> {
    private static final String BLOCK = "8d5598d979159a4c75b2828b63b4e6dbb2e7d87ccf405922d86ab38f05871b32";
    
    @Test
    @Override
    public void canGetSimpleBlock(final Blackcoin blackcoin) {
        final Block block = blackcoin.getblock(BLOCK);
        assertThat(block, hasAllIgnoringNull(
                property("hash", is(equalTo("8d5598d979159a4c75b2828b63b4e6dbb2e7d87ccf405922d86ab38f05871b32"))),
                property("confirmations", is(equalTo(1L))),
                property("size", is(equalTo(1013L))),
                property("height", is(equalTo(21L))),
                property("version", is(equalTo(7L))),
                property("merkleroot", is(equalTo("da40d9478fb580ca7861268d769feadcf0842ee2ecf08a9e4c66d54564c13af8"))),
                property("mint", is(equalTo(new BigDecimal("10000.0003")))),
                property("time", is(equalTo(1537822062L))),
                property("nonce", is(equalTo(508297440L))),
                property("bits", is(equalTo("1e1ac10b"))),
                property("difficulty", is(equalTo(new BigDecimal("0.000146")))),
                property("blocktrust", is(equalTo("99192"))),
                property("chaintrust", is(equalTo("4f51e5"))),
                property("previousblockhash", is(equalTo("66bf49c5cf32f32b5212c827f2016c23a1e65d50c789725fbe73bb555ffc6193"))),
                property("flags", is(equalTo("proof-of-work"))),
                property("proofhash", is(equalTo("000001fbb4b0066d29e5a49bcdb9c0f3419e76f5a1dd615a37f1c6991499d1d6"))),
                property("entropybit", is(equalTo(0L))),
                property("modifier", is(equalTo("0000000000000000"))),
                property("modifierv2", is(equalTo("063425992b1a47cb66be4dc415a172ad58525ae37542070d28732a80b02bc116"))),
                property("tx", hasSize(4))
        ));
        
        assertThat(block.tx.get(0).txid, is(equalTo("f08a9509d723a65b364ce701cbda2b70345eda9605636fedc90b55d12712deb7")));
        assertThat(block.tx.get(1).txid, is(equalTo("e3348178922d0e9f27422bd2a62bb22be97f20b90bc0121d833adcc5d07997a0")));
        assertThat(block.tx.get(2).txid, is(equalTo("696cd55f9d1b9975a0e227751fb9ae34b543ca7b9af2930ce090631da7080010")));
        assertThat(block.tx.get(3).txid, is(equalTo("a8492e72645a0df723915daf03bd322602d21ccfb6e510f877eb185754d668fd")));
    }

    @Test
    @Override
    public void canGetVerboseBlock(final Blackcoin blackcoin) {
        final Block block = blackcoin.getblock(BLOCK, true);
        // Block details verified in `supportsShortTransactionList()`
        
        final Transaction tx = block.tx.get(0);
        assertThat(tx, hasAllIgnoringNull(
                property("txid", is(equalTo("f08a9509d723a65b364ce701cbda2b70345eda9605636fedc90b55d12712deb7"))),
                property("version", is(equalTo(1L))),
                property("time", is(equalTo(1537822057L))),
                property("locktime", is(equalTo(0L))),
                property("vin", hasSize(1)),
                property("vout", hasSize(1))
        ));

        final TxInput in = tx.vin.get(0);
        assertThat(in, hasAllIgnoringNull(
                property("coinbase", is(equalTo("01150102"))),
                property("sequence", is(equalTo(4294967295L)))
        ));

        final TxOutput out = tx.vout.get(0);
        assertThat(out, hasAllIgnoringNull(
                property("value", is(equalTo(new BigDecimal("10000.0003")))),
                property("n", is(equalTo(0L))),
                property("scriptPubKey", is(not(nullValue())))
        ));

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
    
}
