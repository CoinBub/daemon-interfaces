#
# Daemon executable builder
#
FROM ubuntu:16.04 as builder

RUN apt-get -y update; \
    apt-get -y install \
        build-essential \
        git \
        libboost-filesystem-dev \
        libboost-program-options-dev \
        libboost-system-dev \
        libboost-thread-dev \
        libssl-dev \
        libdb++-dev \
        libminiupnpc-dev \
        libqrencode-dev

RUN git clone https://github.com/unifiedvalue/NIKO /tmp/niko \
 && cd /tmp/niko/src \
 && chmod +x leveldb/build_detect_platform \
 && make STATIC=1 USE_UPNP=0 -f makefile.unix

#
# Final Docker image
#
FROM coinbub/daemon-base

ENV DAEMON_CONF_DIR="/home/crypto/.NIKO"
ENV DAEMON_CONF_FILE="/home/crypto/.NIKO/NIKO.conf"

USER root

COPY --from=builder /tmp/niko/src/NIKOd /usr/local/bin/nikod

RUN mkdir -p $DAEMON_CONF_DIR \
 && chown crypto.crypto $DAEMON_CONF_DIR \
 && chmod 700 $DAEMON_CONF_DIR

USER crypto
CMD ["litecoind"]