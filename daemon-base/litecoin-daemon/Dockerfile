FROM coinbub/daemon-base

ENV DAEMON_CONF_DIR="/home/crypto/.litecoin"
ENV DAEMON_CONF_FILE="/home/crypto/.litecoin/litecoin.conf"

USER root

ADD https://download.litecoin.org/litecoin-0.15.1/linux/litecoin-0.15.1-x86_64-linux-gnu.tar.gz /tmp/

RUN tar -xvf /tmp/litecoin-*.tar.gz -C /tmp/ \
 && cp /tmp/litecoin-*/bin/*  /usr/local/bin \
 && rm -rf /tmp/litecoin-* \
 && mkdir -p $DAEMON_CONF_DIR \
 && chown crypto.crypto $DAEMON_CONF_DIR \
 && chmod 700 $DAEMON_CONF_DIR

USER crypto
CMD ["litecoind"]