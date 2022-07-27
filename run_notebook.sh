#!/bin/sh

set -e

# a dedicated notebook is available to plot results
cd observable_notebook_results
if ! command -v npm &> /dev/null
then
    echo "npm could not be found, if you want to plot graphs with the notebook then you need to install or setup node.js and the node package manager"
else
    npx http-server
fi