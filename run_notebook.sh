#!/bin/bash

set -ex

# dedicated notebooks are available to plot results
if ! command -v npm &> /dev/null
then
    echo "npm could not be found, if you want to plot graphs with the notebook then you need to install or setup node and the node package manager"
else
(
    cd notebook_construction_perfs
    npx http-server &
)
(
    cd notebook_search
    npx http-server &
)
fi