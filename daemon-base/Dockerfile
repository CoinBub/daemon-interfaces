FROM ubuntu:16.04

COPY .docker /

RUN chmod a+x /usr/local/bin/* \
 && apt-get -y update \
 && apt-get -y install gettext \
                       iproute2 \
                       netcat \
 && groupadd --gid 1000 crypto \
 && useradd --uid 1000 --gid crypto --shell /bin/false --create-home crypto

USER crypto
WORKDIR /home/crypto

ENTRYPOINT [ "/usr/local/bin/entrypoint" ]
