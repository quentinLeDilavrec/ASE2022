# ASE2022
artifacts repository for ASE 2022

* refsolver/ contains the baseline tool with its intermediary results
* hyperAST/ contains our tool with its intermediary results
  
  To redo the computation of references do ``launch_all.sh the_results_dir the_log_dir``. Caution it can be long for large repositories and will need a lot of ram to run them all at the same time. If you want to try you should just extract one of them, prefereably for a reasonably sized repository.

* observable\_notebook\_results/ contains a notebook for the plotting of results
* repositories used for the evaluation are not included because they are too large even compressed to be put in a git repository, however you can download them, the links are available in hyperAST/launch_all.sh