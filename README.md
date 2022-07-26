# ASE2022
artifacts repository for ASE 2022

## Try the tool

You first need to build with [build.sh](build.sh)

Then you can try one of the [run_*.sh](run_*.sh) script.

* [run_benchmark_all.sh](run_benchmark_all.sh) allows you to run the whole benchmark presented in the article. Caution it will take a long time, hours depending on your hardware.
* [run_benchmark_simp.sh](run_benchmark_simp.sh) allows you to run a part of the benchmark presented in the article. It should take a few minutes to run.
* [run_example_interactive.sh](run_example_interactive.sh) allows you to interactively look at references missed by our tool compared to spoon.
* [run_notebook.sh](run_notebook.sh) allows you to plot figures presented in the article, through a local observablehq notebook.
* [run_example_simp.sh](run_example_simp.sh) allows you to construct an hyperAST and to compute reference relations.


## Directories

* [hyperAST/](hyperAST/) contains our tool with its intermediary results

  Our tool uses nightly rust https://rust-lang.github.io/rustup/concepts/channels.html, then you can compile with ``cargo build --release``
  
  To redo the computation of references do ``launch_all.sh the_results_dir the_log_dir``. Caution it can be long for large repositories and will need a lot of ram to run them all at the same time. If you want to try you should just extract one of them, prefereably for a reasonably sized repository.

  Once you have the results you can extract the commits and modules ``target/release/ref-mining-evaluation modules --refs the_results_dir/the_repo/ > the_modules_dir/the_repo`` that will be used by the baseline tool.

  Finally, with the results from our tool and from the baseline you can use a command line tool that we made ``target/release/ref-mining-evaluation``, it has an help menu. For example you can compute the statistics to compare the previously computed references ``target/release/ref-mining-evaluation multi-^Cmpare-stats --json ../refsolver/comp_jacoco/ ../rusted_gumtree_mask2or/results_1000_commits2/jacoco/ > summary5_jacoco.json``.
  It is also possible to interactively look at the differences with the subcommand: ``target/release/ref-mining-evaluation interactive --repository github.com/user/repo --commit the_commmitid ``.

* [refsolver/](refsolver/) contains the baseline tool with its intermediary results

To run the baseline tool you need to have maven installed then ``mvn compile``.

For each repository after having run the HyperAST and extracted the modules, you can use the following command to compute the referential relations
``cat the_modules_dir/the_repo/the_repo | bash ana.sh the_git_repository_dir user/repo "" the_results_dir/the_repo/``.

* [observable\_notebook\_results/](observable\_notebook\_results/) contains a notebook for the plotting of results
* repositories used for the evaluation are not included because they are too large (even compressed) to be put in a git repository, however you can clone them with git, the links are available in hyperAST/launch_all.sh