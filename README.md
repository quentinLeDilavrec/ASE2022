# ASE2022
artifacts repository for ASE 2022

## Requirements

### Rust
The HyperAST library is written in Rust, and we currently use the nightly channel.
If you have a stable version of Rust, you first have to uninstall it.
Then run:

curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh

to install Rustup. Select 2) for Customize installation. 
For the Default toolchain? (stable/beta/nightly/none), select nightly.

You can find up to date instructions on how to install Rustup, the Rust version manager here: https://rust-lang.github.io/rustup/installation/index.html and instructions for switching to the nightly channel here: https://rust-lang.github.io/rustup/concepts/channels.html.

### Java

You also need Java 11 and Maven to run the experiments (to use Spoon).

### Notebook to plot figures

`http-server` is required to launch the notebook that plots the figures (it itself needs `npm` (`Node`)) (we use http-server version 14.1, automatically asked at the end of benchmark if is not installed).

## Try the tool

Once build requirements are fulfilled, while being in `hyperAST/`, you should be able to compile with `cargo build --release`.

Then you can try one of the [run_*.sh](run_*.sh) script.

* __[run_benchmark_simp.sh](run_benchmark_simp.sh)__ allows you to run a part of the benchmark presented in the article. It should just take a few minutes to run.
* [run_benchmark_all.sh](run_benchmark_all.sh) allows you to run the whole benchmark presented in the article. __Caution__ it will take a long time, hours depending on your hardware. Note: you can change what is run by modifying `hyperAST/all.csv`
* [run_example_interactive.sh](run_example_interactive.sh) allows you to interactively look at references missed by our tool compared to spoon.
* [run_notebook.sh](run_notebook.sh) allows you to plot figures presented in the article, through a local observablehq notebook.
* [run_example_simp.sh](run_example_simp.sh) allows you to construct an HyperAST and to compute the reference relations.

### Docker
You can also use the provided Dockerfile to run the scripts from inside a container.
It can significantly change performances of things ran inside and it also takes more space.
You first need to build the image `docker build -t hyperast_evalpack .` then run the container in interactive mode `docker run -ti hyperast_evalpack -p 8080:8080 -p 8081:8081`, once inside you can run the same provided shell scripts.

## Directories

* [hyperAST/](hyperAST/) contains our tool with its intermediary results
* [refsolver/](refsolver/) contains the baseline tool with its intermediary results
* [observable\_notebook\_results/](observable\_notebook\_results/) contains a notebook for the plotting of results
* repositories used for the evaluation are not included because they are too large (even compressed) to be put in a git repository, however you can clone them with git, the links are available in hyperAST/launch_all.sh