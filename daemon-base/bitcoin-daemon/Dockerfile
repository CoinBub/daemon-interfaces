FROM coinbub/daemon-base

ENV DAEMON_CONF_DIR="/home/crypto/.bitcoin"
ENV DAEMON_CONF_FILE="/home/crypto/.bitcoin/bitcoin.conf"

USER root

ADD https://bitcoincore.org/bin/bitcoin-core-0.17.0/bitcoin-0.17.0-x86_64-linux-gnu.tar.gz /tmp/

RUN tar -xvf /tmp/bitcoin-*.tar.gz -C /tmp/ \
 && cp /tmp/bitcoin-*/bin/*  /usr/local/bin \
 && rm -rf /tmp/bitcoin-* \
 && mkdir -p $DAEMON_CONF_DIR \
 && chown crypto.crypto $DAEMON_CONF_DIR \
 && chmod 700 $DAEMON_CONF_DIR

USER crypto
CMD ["bitcoind"]