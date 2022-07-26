# Hyper AST
A data structure and library to help with analysing code histories.


## Context
Software histories are great, they can be used to make sense of decisions and aborted attempts in development processes.
Such tasks combine at least 3 dimensions,
- code: as text file or as a tree
- references: for example between method calls and method declarations
- versions: for example using commits, where a commit can one or more parents

## Problem

But making sense of software histories can tedious and difficult due mainly to 
- referential relations (through a codebase) with complex semantics,
- evolutions (between versions) that might be interpreted in multiple ways as refactorings, bug fixes, added features, and
- the sheer size of a code base, even without distant references of complex structural changes, scaling problems will append.

## Approach

Main performance hypothesis: between consecutive versions, changes are small relative to the size of the whole code base.

The HyperAST structure can be seen as a Direct Acyclic Graph, that takes inspiration from Merkle DAGs (used in git), and has the granularity of a Concrete Syntax Tree (such as given by a parser)

The HyperAST take a git code base as input, and uses exiting parsers to extract the fine grained tree structure from code, which is then efficiently stored and indexed.
To improve efficiency and performances of code analysis, the HyperAST allows to persist intermediate computation on subtrees of code.

## Install
(see [INSTALL.md]())
