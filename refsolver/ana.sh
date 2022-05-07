
echo "work on directory $1"
echo "for repo $2"
echo "for module $3"
echo "into directory $4"

start=`date +%s`
totalnb=0
one=1

while IFS= read -r line;
do
    totalnb=$((totalnb+1))
    # startl=`date +%s`
    echo "(cd $1; git checkout $line)";
    (cd $1; git checkout $line);
    commit=${line%% *}
    echo "java @.argfile fr.quentin.refSolver.EvalValidPerf \"$1\" \"$2\" \"$3\" \"$line\" > $4/$commit";
    java @.argfile fr.quentin.refSolver.EvalValidPerf "$1" "$2" "$line" > $4/$commit
    endl=`date +%s`
    runtimel=$((endl-startl))
    echo "$line commit time taken $runtimel"
done < /dev/stdin

end=`date +%s`

runtime=$((end-start))

echo "total time taken ($totalnb) $runtime"
