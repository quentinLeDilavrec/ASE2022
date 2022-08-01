FROM silkeh/clang:latest


RUN set -ex \
    && sed -i -- 's/# deb-src/deb-src/g' /etc/apt/sources.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        build-essential \
        pkg-config \
        libssl-dev \
        cdbs \
        devscripts \
        equivs \
        fakeroot \
		ca-certificates \
		curl \
		netbase \
		wget \
    && apt-get clean \
    && rm -rf /tmp/* /var/tmp/* \
	&& rm -rf /var/lib/apt/lists/*

# RUST
ENV RUSTUP_HOME=/usr/local/rustup \
    CARGO_HOME=/usr/local/cargo \
    PATH=/usr/local/cargo/bin:$PATH \
    RUST_VERSION=1.62.1

RUN set -eux; \
    dpkgArch="$(dpkg --print-architecture)"; \
    case "${dpkgArch##*-}" in \
        amd64) rustArch='x86_64-unknown-linux-gnu'; rustupSha256='3dc5ef50861ee18657f9db2eeb7392f9c2a6c95c90ab41e45ab4ca71476b4338' ;; \
        armhf) rustArch='armv7-unknown-linux-gnueabihf'; rustupSha256='67777ac3bc17277102f2ed73fd5f14c51f4ca5963adadf7f174adf4ebc38747b' ;; \
        arm64) rustArch='aarch64-unknown-linux-gnu'; rustupSha256='32a1532f7cef072a667bac53f1a5542c99666c4071af0c9549795bbdb2069ec1' ;; \
        i386) rustArch='i686-unknown-linux-gnu'; rustupSha256='e50d1deb99048bc5782a0200aa33e4eea70747d49dffdc9d06812fd22a372515' ;; \
        *) echo >&2 "unsupported architecture: ${dpkgArch}"; exit 1 ;; \
    esac; \
    url="https://static.rust-lang.org/rustup/archive/1.24.3/${rustArch}/rustup-init"; \
    wget "$url"; \
    echo "${rustupSha256} *rustup-init" | sha256sum -c -; \
    chmod +x rustup-init; \
    ./rustup-init -y --no-modify-path --profile minimal --default-toolchain $RUST_VERSION --default-host ${rustArch}; \
    rm rustup-init; \
    chmod -R a+w $RUSTUP_HOME $CARGO_HOME; \
    rustup --version; \
    cargo --version; \
    rustc --version;

RUN set -ex; \
	if ! command -v gpg > /dev/null; then \
		apt-get update; \
		apt-get install -y --no-install-recommends \
			gnupg \
			dirmngr \
		; \
		rm -rf /var/lib/apt/lists/*; \
	fi


# JAVA
# RUN set -eux; \
# 	apt-get update; \
# 	apt-get install -y --no-install-recommends \
# 		bzip2 \
# 		unzip \
# 		xz-utils \
# 		\
# # java.lang.UnsatisfiedLinkError: /usr/local/openjdk-11/lib/libfontmanager.so: libfreetype.so.6: cannot open shared object file: No such file or directory
# # java.lang.NoClassDefFoundError: Could not initialize class sun.awt.X11FontManager
# # https://github.com/docker-library/openjdk/pull/235#issuecomment-424466077
# 		fontconfig libfreetype6 \
# 		\
# # utilities for keeping Debian and OpenJDK CA certificates in sync
# 		ca-certificates p11-kit \
# 	; \
# 	rm -rf /var/lib/apt/lists/*

# ENV JAVA_HOME /usr/local/openjdk-11
# RUN { echo '#/bin/sh'; echo 'echo "$JAVA_HOME"'; } > /usr/local/bin/docker-java-home && chmod +x /usr/local/bin/docker-java-home && [ "$JAVA_HOME" = "$(docker-java-home)" ] # backwards compatibility
# ENV PATH $JAVA_HOME/bin:$PATH

# # Default to UTF-8 file.encoding
# ENV LANG C.UTF-8

# # https://adoptopenjdk.net/upstream.html
# # >
# # > What are these binaries?
# # >
# # > These binaries are built by Red Hat on their infrastructure on behalf of the OpenJDK jdk8u and jdk11u projects. The binaries are created from the unmodified source code at OpenJDK. Although no formal support agreement is provided, please report any bugs you may find to https://bugs.java.com/.
# # >
# ENV JAVA_VERSION 11.0.16
# # https://github.com/docker-library/openjdk/issues/320#issuecomment-494050246
# # >
# # > I am the OpenJDK 8 and 11 Updates OpenJDK project lead.
# # > ...
# # > While it is true that the OpenJDK Governing Board has not sanctioned those releases, they (or rather we, since I am a member) didn't sanction Oracle's OpenJDK releases either. As far as I am aware, the lead of an OpenJDK project is entitled to release binary builds, and there is clearly a need for them.
# # >

# RUN set -eux; \
# 	\
# 	arch="$(dpkg --print-architecture)"; \
# 	case "$arch" in \
# 		'amd64') \
# 			downloadUrl='https://github.com/AdoptOpenJDK/openjdk11-upstream-binaries/releases/download/jdk-11.0.16%2B8/OpenJDK11U-jre_x64_linux_11.0.16_8.tar.gz'; \
# 			;; \
# 		'arm64') \
# 			downloadUrl='https://github.com/AdoptOpenJDK/openjdk11-upstream-binaries/releases/download/jdk-11.0.16%2B8/OpenJDK11U-jre_aarch64_linux_11.0.16_8.tar.gz'; \
# 			;; \
# 		*) echo >&2 "error: unsupported architecture: '$arch'"; exit 1 ;; \
# 	esac; \
# 	\
# 	wget --progress=dot:giga -O openjdk.tgz "$downloadUrl"; \
# 	wget --progress=dot:giga -O openjdk.tgz.asc "$downloadUrl.sign"; \
# 	\
# 	export GNUPGHOME="$(mktemp -d)"; \
# # pre-fetch Andrew Haley's (the OpenJDK 8 and 11 Updates OpenJDK project lead) key so we can verify that the OpenJDK key was signed by it
# # (https://github.com/docker-library/openjdk/pull/322#discussion_r286839190)
# # we pre-fetch this so that the signature it makes on the OpenJDK key can survive "import-clean" in gpg
# 	gpg --batch --keyserver keyserver.ubuntu.com --recv-keys EAC843EBD3EFDB98CC772FADA5CD6035332FA671; \
# # TODO find a good link for users to verify this key is right (https://mail.openjdk.java.net/pipermail/jdk-updates-dev/2019-April/000951.html is one of the only mentions of it I can find); perhaps a note added to https://adoptopenjdk.net/upstream.html would make sense?
# # no-self-sigs-only: https://salsa.debian.org/debian/gnupg2/commit/c93ca04a53569916308b369c8b218dad5ae8fe07
# 	gpg --batch --keyserver keyserver.ubuntu.com --keyserver-options no-self-sigs-only --recv-keys CA5F11C6CE22644D42C6AC4492EF8D39DC13168F; \
# 	gpg --batch --list-sigs --keyid-format 0xLONG CA5F11C6CE22644D42C6AC4492EF8D39DC13168F \
# 		| tee /dev/stderr \
# 		| grep '0xA5CD6035332FA671' \
# 		| grep 'Andrew Haley'; \
# 	gpg --batch --verify openjdk.tgz.asc openjdk.tgz; \
# 	gpgconf --kill all; \
# 	rm -rf "$GNUPGHOME"; \
# 	\
# 	mkdir -p "$JAVA_HOME"; \
# 	tar --extract \
# 		--file openjdk.tgz \
# 		--directory "$JAVA_HOME" \
# 		--strip-components 1 \
# 		--no-same-owner \
# 	; \
# 	rm openjdk.tgz*; \
# 	\
# # update "cacerts" bundle to use Debian's CA certificates (and make sure it stays up-to-date with changes to Debian's store)
# # see https://github.com/docker-library/openjdk/issues/327
# #     http://rabexc.org/posts/certificates-not-working-java#comment-4099504075
# #     https://salsa.debian.org/java-team/ca-certificates-java/blob/3e51a84e9104823319abeb31f880580e46f45a98/debian/jks-keystore.hook.in
# #     https://git.alpinelinux.org/aports/tree/community/java-cacerts/APKBUILD?id=761af65f38b4570093461e6546dcf6b179d2b624#n29
# 	{ \
# 		echo '#!/usr/bin/env bash'; \
# 		echo 'set -Eeuo pipefail'; \
# 		echo 'trust extract --overwrite --format=java-cacerts --filter=ca-anchors --purpose=server-auth "$JAVA_HOME/lib/security/cacerts"'; \
# 	} > /etc/ca-certificates/update.d/docker-openjdk; \
# 	chmod +x /etc/ca-certificates/update.d/docker-openjdk; \
# 	/etc/ca-certificates/update.d/docker-openjdk; \
# 	\
# # https://github.com/docker-library/openjdk/issues/331#issuecomment-498834472
# 	find "$JAVA_HOME/lib" -name '*.so' -exec dirname '{}' ';' | sort -u > /etc/ld.so.conf.d/docker-openjdk.conf; \
# 	ldconfig; \
# 	\
# # https://github.com/docker-library/openjdk/issues/212#issuecomment-420979840
# # https://openjdk.java.net/jeps/341
# 	java -Xshare:dump; \
# 	\
# # basic smoke test
# 	java --version

RUN set -ex \
    && sed -i -- 's/# deb-src/deb-src/g' /etc/apt/sources.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
            openjdk-11-jdk \
            maven \
    && apt-get clean \
    && rm -rf /tmp/* /var/tmp/*

# NODE
RUN groupadd --gid 1000 node \
  && useradd --uid 1000 --gid node --shell /bin/bash --create-home node

ENV NODE_VERSION 18.7.0

RUN ARCH= && dpkgArch="$(dpkg --print-architecture)" \
  && case "${dpkgArch##*-}" in \
    amd64) ARCH='x64';; \
    ppc64el) ARCH='ppc64le';; \
    s390x) ARCH='s390x';; \
    arm64) ARCH='arm64';; \
    armhf) ARCH='armv7l';; \
    i386) ARCH='x86';; \
    *) echo "unsupported architecture"; exit 1 ;; \
  esac \
  # gpg keys listed at https://github.com/nodejs/node#release-keys
  && set -ex \
  && for key in \
    4ED778F539E3634C779C87C6D7062848A1AB005C \
    141F07595B7B3FFE74309A937405533BE57C7D57 \
    94AE36675C464D64BAFA68DD7434390BDBE9B9C5 \
    74F12602B6F1C4E913FAA37AD3A89613643B6201 \
    71DCFD284A79C3B38668286BC97EC7A07EDE3FC1 \
    61FC681DFB92A079F1685E77973F295594EC4689 \
    8FCCA13FEF1D0C2E91008E09770F7A9A5AE15600 \
    C4F0DFFF4E8C1A8236409D08E73BC641CC11F4C8 \
    890C08DB8579162FEE0DF9DB8BEAB4DFCF555EF4 \
    C82FA3AE1CBEDC6BE46B9360C43CEC45C17AB93C \
    DD8F2338BAE7501E3DD5AC78C273792F7D83545D \
    A48C2BEE680E841632CD4E44F07496B3EB3C1762 \
    108F52B48DB57BB0CC439B2997B01419BD92F80A \
    B9E2F5981AA6E0CD28160D9FF13993A75599653C \
  ; do \
      gpg --batch --keyserver hkps://keys.openpgp.org --recv-keys "$key" || \
      gpg --batch --keyserver keyserver.ubuntu.com --recv-keys "$key" ; \
  done \
  && curl -fsSLO --compressed "https://nodejs.org/dist/v$NODE_VERSION/node-v$NODE_VERSION-linux-$ARCH.tar.xz" \
  && curl -fsSLO --compressed "https://nodejs.org/dist/v$NODE_VERSION/SHASUMS256.txt.asc" \
  && gpg --batch --decrypt --output SHASUMS256.txt SHASUMS256.txt.asc \
  && grep " node-v$NODE_VERSION-linux-$ARCH.tar.xz\$" SHASUMS256.txt | sha256sum -c - \
  && tar -xJf "node-v$NODE_VERSION-linux-$ARCH.tar.xz" -C /usr/local --strip-components=1 --no-same-owner \
  && rm "node-v$NODE_VERSION-linux-$ARCH.tar.xz" SHASUMS256.txt.asc SHASUMS256.txt \
  && ln -s /usr/local/bin/node /usr/local/bin/nodejs \
  # smoke tests
  && node --version \
  && npm --version

ENV YARN_VERSION 1.22.19

RUN set -ex \
  && for key in \
    6A010C5166006599AA17F08146C2130DFD2497F5 \
  ; do \
    gpg --batch --keyserver hkps://keys.openpgp.org --recv-keys "$key" || \
    gpg --batch --keyserver keyserver.ubuntu.com --recv-keys "$key" ; \
  done \
  && curl -fsSLO --compressed "https://yarnpkg.com/downloads/$YARN_VERSION/yarn-v$YARN_VERSION.tar.gz" \
  && curl -fsSLO --compressed "https://yarnpkg.com/downloads/$YARN_VERSION/yarn-v$YARN_VERSION.tar.gz.asc" \
  && gpg --batch --verify yarn-v$YARN_VERSION.tar.gz.asc yarn-v$YARN_VERSION.tar.gz \
  && mkdir -p /opt \
  && tar -xzf yarn-v$YARN_VERSION.tar.gz -C /opt/ \
  && ln -s /opt/yarn-v$YARN_VERSION/bin/yarn /usr/local/bin/yarn \
  && ln -s /opt/yarn-v$YARN_VERSION/bin/yarnpkg /usr/local/bin/yarnpkg \
  && rm yarn-v$YARN_VERSION.tar.gz.asc yarn-v$YARN_VERSION.tar.gz \
  # smoke test
  && yarn --version

COPY hyperAST ./hyperAST
COPY refsolver ./refsolver
COPY notebook_construction_perfs ./notebook_construction_perfs
COPY notebook_search ./notebook_search
COPY build.sh ./
COPY run_benchmark_all.sh run_benchmark_simp.sh run_example_interactive.sh run_example_simp.sh run_notebook.sh ./

EXPOSE 8080
EXPOSE 8081

RUN cd refsolver && mvn package -Dmaven.test.skip

RUN rustup toolchain install nightly && rustup default nightly

RUN cd hyperAST && cargo clean && cargo build --release

ENTRYPOINT ["/bin/bash"]