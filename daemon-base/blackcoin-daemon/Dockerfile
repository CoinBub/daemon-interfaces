FROM coinbub/daemon-base

ENV DAEMON_CONF_DIR="/home/crypto/.blackcoin"
ENV DAEMON_CONF_FILE="/home/crypto/.blackcoin/blackcoin.conf"

USER root

ADD https://github.com/CoinBlack/blackcoin/releases/download/v1.2.5.2/blackcoin-1.2.5.2-linux.zip /tmp/

RUN apt-get -y update \
 && apt-get -y install unzip \
 && unzip /tmp/blackcoin-*.zip  -d /tmp/blackcoin \
 && cp /tmp/blackcoin/blackcoind /usr/local/bin \
 && rm -rf /tmp/blackcoin \
 && mkdir -p $DAEMON_CONF_DIR \
 && chown crypto.crypto $DAEMON_CONF_DIR \
 && chmod 700 $DAEMON_CONF_DIR

USER crypto
CMD ["blackcoind"]