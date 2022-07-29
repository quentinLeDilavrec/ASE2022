#!/bin/bash

set -ex

# Launch impact analysis on the INRIA/spoon repository.
SHORTNAME=spoon
REPO=INRIA/spoon
BEFORE=d44759888f41e67db3f32ea2cd4975cefa727691
AFTER=56e12a0c0e0e69ea70863011b4f4ca3305e0542b

# run the benchmark on a commit slice of spoon

mkdir -p results_simp/
mkdir -p results_simp/modules

cd hyperAST

mkdir -p results_simp/$SHORTNAME
mkdir -p logs_simp/

# For each repository, first construct the HyperAST with indexed references (reference oracles made with bloom filters),
# then for a sample of commits, for each declaration find all corresponding references.
target/release/hyper_ast_benchmark "$REPO" "$BEFORE" "$AFTER" "" results_simp/$SHORTNAME &> logs_simp/$SHORTNAME

tail logs_simp/$SHORTNAME

# extract commits and modules that had their declarations resolved.
target/release/ref-mining-evaluation modules --refs results_simp/$SHORTNAME > ../results_simp/modules/$SHORTNAME 2> /dev/null

# do the same reference analysis with Spoon
mkdir -p /tmp/spoongitinstances
(
cd /tmp/spoongitinstances

if [[ -d "$SHORTNAME" ]]
then
    cd "$SHORTNAME"
    git fetch
else
    git clone "https://github.com/$REPO" "$SHORTNAME"
fi
)

(
cd ../refsolver
mkdir -p comp_simp/$SHORTNAME
cat ../hyperAST/modules/$SHORTNAME | bash ana.sh /tmp/spoongitinstances/$SHORTNAME/ "$REPO" "" comp_simp/$SHORTNAME
)

# extract performances measurments on construction
target/release/ref-mining-evaluation multi-perfs-stats --json ../refsolver/comp_simp/$SHORTNAME/ results_simp/$SHORTNAME/ > ../results_simp/perfs_$SHORTNAME.json 2> /dev/null


# extract and compare reference relations found with the HyperAST to the one found with Spoon.
target/release/ref-mining-evaluation multi-compare-stats --json ../refsolver/comp_simp/$SHORTNAME/ results_simp/$SHORTNAME/ > ../results_simp/summary_$SHORTNAME.json 2> /dev/null

# display some stats on results
ls ../results_simp/

# a dedicated notebook is available to plot results
cd ../observable_notebook_results
if ! command -v npm &> /dev/null
then
    echo "npm could not be found, if you want to plot graphs with the notebook then you need to install or setup node.js and the node package manager"
else
    npx http-server
fi