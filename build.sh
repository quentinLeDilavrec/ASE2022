#!/bin/sh

curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh

cd hyper_ast

cargo build --release

echo "all the HyperAST executables should be available."
