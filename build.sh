#!/bin/sh

set -e

curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh

source ~/.profile

rustup toolchain install nightly

cd hyperAST

cargo build --release

echo "all the HyperAST executables should be available."
