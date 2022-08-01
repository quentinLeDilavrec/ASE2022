#!/bin/bash

set -e

HYPERAST_RESULTS="results"
SPOON_RESULTS="results"
HYPERAST_LOGS="logs"

# run the full benchmark
# WARNING it will take a very long time!
# You should first try run_benchmark_simp.sh and look at the result then if it looks right run this benchmark on a server.

mkdir -p results/
mkdir -p modules/

cd hyperAST
mkdir -p $HYPERAST_LOGS/

# For each repository, first construct the HyperAST with indexed references (reference oracles made with bloom filters),
# then for a sample of commits, for each declaration find all corresponding references.
while IFS=, read -r name repo before after path
do
    mkdir -p $HYPERAST_RESULTS/$name
    target/release/hyper_ast_benchmark "$repo" $before $after "" "$HYPERAST_RESULTS/$name" &> "$HYPERAST_LOGS/$name" &
    sleep 1
done < all.csv

# extract commits and modules that had their declarations resolved.
while IFS=, read -r name repo before after path
do
    target/release/ref-mining-evaluation modules --refs $HYPERAST_RESULTS/$name > ../modules/$name 2> /dev/null
done < all.csv

# clone repositories for beseline analysis with spoon
mkdir -p /tmp/spoongitinstances

while IFS=, read -r name repo before after path
do
    cd /tmp/spoongitinstances

    if [[ -d "$name" ]]
    then
        cd "$name"
        git fetch
    else
        git clone "https://github.com/$repo" "$name"
    fi
done < all.csv


# do the same reference analysis with Spoon
while IFS=, read -r name repo before after path
do
    cd ../refsolver
    mkdir -p $SPOON_RESULTS/$name
    cat ../modules/$name | bash ana.sh /tmp/spoongitinstances/$name/ "$REPO" "" $SPOON_RESULTS/$name
done < all.csv

while IFS=, read -r name repo before after path
do
    # extract performances measurments on construction
    target/release/ref-mining-evaluation multi-perfs-stats --json ../refsolver/$SPOON_RESULTS/$name/ $HYPERAST_RESULTS/$name/ > ../results/perfs_$name.json 2> /dev/null

    # extract and compare reference relations found with the HyperAST to the one found with Spoon.
    target/release/ref-mining-evaluation multi-compare-stats --json ../refsolver/$SPOON_RESULTS/$name/ $HYPERAST_RESULTS/$name/ > ../results/summary_$name.json 2> /dev/null
done < all.csv
cd ..

# display some stats on results
ls results/

# dedicated notebooks are available to plot results
if ! command -v npm &> /dev/null
then
    echo "npm could not be found, if you want to plot graphs with the notebook then you need to install or setup node and the node package manager"
else
(
    cd ../notebook_construction_perfs
    npx http-server &
)
(
    cd ../notebook_search
    npx http-server &
)
fi