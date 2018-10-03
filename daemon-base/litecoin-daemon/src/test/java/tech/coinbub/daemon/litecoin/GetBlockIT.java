package tech.coinbub.daemon.litecoin;

import java.math.BigDecimal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Litecoin;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;
import tech.coinbub.daemon.testutils.suites.StandardGetBlockIT;
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;

@ExtendWith(Dockerized.class)
@DockerizedLitecoin
public class GetBlockIT implements StandardGetBlockIT<Litecoin> {
    @Test
    @Override
    public void canGetSimpleBlock(final Litecoin litecoin) {
        final Block block = litecoin.getblock("fd5aa78425d7dc2200de2d500d034d544abbccbec50bc3220561a6ede242a97b");
        assertThat(block, hasAllIgnoringNull(
                property("hash", is(equalTo("fd5aa78425d7dc2200de2d500d034d544abbccbec50bc3220561a6ede242a97b"))),
                property("confirmations", is(equalTo(1L))),
                property("strippedsize", is(equalTo(227L))),
                property("size", is(equalTo(227L))),
                property("weight", is(equalTo(908L))),
                property("height", is(equalTo(120L))),
                property("version", is(equalTo(536870912L))),
                property("versionHex", is(equalTo("20000000"))),
                property("merkleroot", is(equalTo("76d3cb8276d4f8376e960278d766ff5066f40a78192a5b161c05739f26182da9"))),
                property("tx", hasSize(1)),
                property("time", is(equalTo(1526482399L))),
                property("mediantime", is(equalTo(1526482398L))),
                property("nonce", is(equalTo(0L))),
                property("bits", is(equalTo("207fffff"))),
                property("difficulty", is(equalTo(new BigDecimal("4.656542373906925e-10")))),
                property("chainwork", is(equalTo("00000000000000000000000000000000000000000000000000000000000000f2"))),
                property("previousblockhash", is(equalTo("93b514a0bae42cdb3e92dca82ca7bd93644478fc980914a486217271a7c241d3")))
        ));
        
        assertThat(block.tx.get(0), hasAllIgnoringNull(
                property("txid", is(equalTo("76d3cb8276d4f8376e960278d766ff5066f40a78192a5b161c05739f26182da9")))
        ));
    }

    @Test
    @Override
    public void canGetVerboseBlock(final Litecoin litecoin) {
        final Block block = litecoin.getblock("fd5aa78425d7dc2200de2d500d034d544abbccbec50bc3220561a6ede242a97b", 2);
        assertThat(block, hasAllIgnoringNull(
                property("hash", is(equalTo("fd5aa78425d7dc2200de2d500d034d544abbccbec50bc3220561a6ede242a97b"))),
                property("confirmations", is(equalTo(1L))),
                property("strippedsize", is(equalTo(227L))),
                property("size", is(equalTo(227L))),
                property("weight", is(equalTo(908L))),
                property("height", is(equalTo(120L))),
                property("version", is(equalTo(536870912L))),
                property("versionHex", is(equalTo("20000000"))),
                property("merkleroot", is(equalTo("76d3cb8276d4f8376e960278d766ff5066f40a78192a5b161c05739f26182da9"))),
                property("tx", hasSize(1)),
                property("time", is(equalTo(1526482399L))),
                property("mediantime", is(equalTo(1526482398L))),
                property("nonce", is(equalTo(0L))),
                property("bits", is(equalTo("207fffff"))),
                property("difficulty", is(equalTo(new BigDecimal("4.656542373906925e-10")))),
                property("chainwork", is(equalTo("00000000000000000000000000000000000000000000000000000000000000f2"))),
                property("previousblockhash", is(equalTo("93b514a0bae42cdb3e92dca82ca7bd93644478fc980914a486217271a7c241d3")))
        ));
        
        final Transaction tx = block.tx.get(0);
        assertThat(tx, hasAllIgnoringNull(
                property("txid", is(equalTo("76d3cb8276d4f8376e960278d766ff5066f40a78192a5b161c05739f26182da9"))),
                property("hash", is(equalTo("76d3cb8276d4f8376e960278d766ff5066f40a78192a5b161c05739f26182da9"))),
                property("version", is(equalTo(2L))),
                property("size", is(equalTo(146L))),
                property("vsize", is(equalTo(146L))),
                property("locktime", is(equalTo(0L))),
                property("vin", hasSize(1)),
                property("vout", hasSize(2)),
                property("hex", is(equalTo("02000000010000000000000000000000000000000000000000000000000000000000000000ffffffff0401780101ffffffff0200f2052a0100000023210206da9d299c43f2e4e51c31ad10cb05946ae60579c342423e8f5ca92501c36068ac0000000000000000266a24aa21a9ede2f61c3f71d1defd3fa999dfa36953755c690689799962b48bebd836974e8cf900000000")))
        ));

        final TxInput in = tx.vin.get(0);
        assertThat(in, hasAllIgnoringNull(
                property("coinbase", is(equalTo("01780101"))),
                property("sequence", is(equalTo(4294967295L)))
        ));

        final TxOutput firstOut = tx.vout.get(0);
        assertThat(firstOut, hasAllIgnoringNull(
                property("value", is(equalTo(new BigDecimal("50.0")))),
                property("n", is(equalTo(0L))),
                property("scriptPubKey", is(not(nullValue())))
        ));

        final ScriptPublicKey firstKey = firstOut.scriptPubKey;
        assertThat(firstKey, hasAllIgnoringNull(
                property("asm", is(equalTo("0206da9d299c43f2e4e51c31ad10cb05946ae60579c342423e8f5ca92501c36068 OP_CHECKSIG"))),
                property("hex", is(equalTo("210206da9d299c43f2e4e51c31ad10cb05946ae60579c342423e8f5ca92501c36068ac"))),
                property("reqSigs", is(equalTo(1L))),
                property("type", is(equalTo(ScriptPublicKey.Type.pubkey))),
                property("addresses", hasSize(1))
        ));
        assertThat(firstKey.addresses.get(0), is(equalTo("n35eVUVBZZVbhBZeF5x8Q8LRpUonKXdAY7")));

        final TxOutput secondOut = tx.vout.get(1);
        assertThat(secondOut, hasAllIgnoringNull(
                property("value", is(equalTo(new BigDecimal("0.0")))),
                property("n", is(equalTo(1L))),
                property("scriptPubKey", is(not(nullValue())))
        ));
        
        final ScriptPublicKey secondKey = secondOut.scriptPubKey;
        assertThat(secondKey, hasAllIgnoringNull(
                property("asm", is(equalTo("OP_RETURN aa21a9ede2f61c3f71d1defd3fa999dfa36953755c690689799962b48bebd836974e8cf9"))),
                property("hex", is(equalTo("6a24aa21a9ede2f61c3f71d1defd3fa999dfa36953755c690689799962b48bebd836974e8cf9"))),
                property("type", is(equalTo(ScriptPublicKey.Type.nulldata)))
        ));
    }
}
