#!/bin/sh

# run the full benchmark
# WARNING it will take a very long time!
# You should first try run_benchmark_simp.sh and look at the result then if it looks right run this benchmark on a server.


cd HyperAST

# launch impact analysis on all considered repositories
# For each repository, first construct the HyperAST with indexed references (reference oracles made with bloom filters),
# then for a sample of commits, for each declaration find all corresponding references.
./launch_all.sh 

# extract commits that had their declarations resolved.
# TODO
target/release/ref-mining-evaluation

# extract performances measurments on construction
# TODO
target/release/ref-mining-evaluation

# do the same kind of analysis with Spoon
# TODO

# extract and compare reference relations found with the HyperAST to the one found with Spoon.
# TODO
target/release/ref-mining-evaluation 

# display some stats on results
# TODO
ls results

# a notebook is available to plot results
# TODO
cd ../observable_notebook_results
npm install --dev http-server
npx http-server
