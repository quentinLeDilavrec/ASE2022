#!/bin/bash
# this script construct an hyperAST to computes the reference relations.

read -e -p "Enter the output directory for results: " -i  "example_simp" OUTDIR
read -e -p "Enter the user and name of the github repository: " -i  "INRIA/spoon" REPO
read -e -p "Enter the first wanted commit/reference: " -i  "d44759888f41e67db3f32ea2cd4975cefa727691" BEFORE
read -e -p "Enter the last wanted commit/reference: " -i  "56e12a0c0e0e69ea70863011b4f4ca3305e0542b" AFTER

mkdir -p "$OUTDIR"
cd hyperAST

target/release/hyper_ast_benchmark "$REPO" "$BEFORE" "$AFTER" "" "../$OUTDIR"

ls "../$OUTDIR"