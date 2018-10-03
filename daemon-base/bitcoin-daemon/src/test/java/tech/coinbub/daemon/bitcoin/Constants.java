package tech.coinbub.daemon.bitcoin;

import java.math.BigDecimal;

public interface Constants {
    public static final String BEST_BLOCK_HASH = "3cf12c2be37b180d6c955647cc8b468f814f1c5941dda2283bd9d903882fb6aa";
    public static final long ARBITRARY_BLOCK_HEIGHT = 22L;
    public static final String ARBITRARY_BLOCK_HASH = "29128d296a50754ebc1231a5ceba6b2526c995e4925b48a37159156f54196d73";
    public static final int ADDRESS_LENGTH = 35;
    public static final String VALID_ADDRESS = "mk9GeqVDJd4ZNf7U77bmi2bendv3hQxbxD";
    public static final String TRANSACTION_ID = "2553da6a3f4130e040c2dddfd9d14b1e394309c393d7f6a7cee8c5005fbb3f87";
    public static final BigDecimal FEE = new BigDecimal("-0.0000336");
    public static final BigDecimal FEE_DEVIATION = new BigDecimal("0.00002");
    public static final int LISTENER_PORT = 20010;
}
