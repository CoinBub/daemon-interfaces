FROM coinbub/daemon-base

ENV DAEMON_CONF_DIR="/home/crypto/.dashcore"
ENV DAEMON_CONF_FILE="/home/crypto/.dashcore/dash.conf"

USER root

ADD https://github.com/dashpay/dash/releases/download/v0.12.2.3/dashcore-0.12.2.3-linux64.tar.gz /tmp/

RUN tar -xvf /tmp/dashcore-*.tar.gz -C /tmp/ \
 && cp /tmp/dashcore*/bin/*  /usr/local/bin \
 && rm -rf /tmp/dashcore* \
 && mkdir -p $DAEMON_CONF_DIR \
 && chown crypto.crypto $DAEMON_CONF_DIR \
 && chmod 700 $DAEMON_CONF_DIR

USER crypto
CMD ["dashd"]