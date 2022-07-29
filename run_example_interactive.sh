#!/bin/bash

set -ex

#!/bin/sh
# this script construct an hyperAST and to compute the reference relations.

read -e -p "Enter the user for the github repository: " -i  "INRIA" GITHUB_USER
read -e -p "Enter the name of the github repository: " -i  "spoon" REPO_NAME
read -e -p "Enter the first wanted commit/reference: " -i  "3acf94ce97e6f4af9eb7b69a9b9f94d4a6d5a164" BEFORE
read -e -p "Enter the last wanted commit/reference: " -i  "56e12a0c0e0e69ea70863011b4f4ca3305e0542b" AFTER

read -e -p "Enter the directory for intermediate hyperAST results: " -i  "hyperast_comp" HAST_R
read -e -p "Enter the directory for intermediate spoon results: " -i  "spoon_comp" SPOON_R

read -e -p "Do you want to compute relations? [Y,n]" input
if [[ $input == "Y" || $input == "y"  || $input == "" ]]; then
    read -e -p "Enter the directory for modules: " -i  "modules_comp" MODULES
    mkdir -p "$MODULES"
    mkdir -p "$HAST_R/$REPO_NAME"
    (
    cd hyperAST
    target/release/hyper_ast_benchmark "$GITHUB_USER/$REPO_NAME" "$BEFORE" "$AFTER" "" "../$HAST_R/$REPO_NAME"
    # extract commits and modules that had their declarations resolved.
    target/release/ref-mining-evaluation modules --refs ../$HAST_R/$REPO_NAME > ../$MODULES/$REPO_NAME
    )
    mkdir -p "SPOON_R"
    # do the same reference analysis with Spoon
    mkdir -p /tmp/spoongitinstances
    (
    cd /tmp/spoongitinstances

    if [[ -d "$REPO_NAME" ]]
    then
        cd "$REPO_NAME"
        git fetch
    else
        git clone "https://github.com/$GITHUB_USER/$REPO_NAME" "$REPO_NAME"
    fi
    )

    (
    # compute relations with spoon
    cd refsolver
    mkdir -p "../$SPOON_R/$REPO_NAME"
    cat ../$MODULES/$REPO_NAME | bash ana.sh /tmp/spoongitinstances/$REPO_NAME/ "$GITHUB_USER/$REPO_NAME" "" "../$SPOON_R/$REPO_NAME"
    )
fi

hyperAST/target/release/ref-mining-evaluation interactive --repository "https://github.com/$GITHUB_USER/$REPO_NAME" --commit $AFTER $SPOON_R/$REPO_NAME/$AFTER $HAST_R/$REPO_NAME/$AFTER