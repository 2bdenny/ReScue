#!/bin/bash
#set -x #echo on

###############################################
# search regex in python file, store in python/py_regex.txt
# if already exist, rm and recreate
proj_dir=${1-"./"}
result_file=${2-"py_regex.txt"}
mkdir -p "$(dirname "$result_file")"

# only search regex from these directory( here is all directory )
mapfile -t py_targets < $proj_dir

for i in "${py_targets[@]}"
do
  grep -P $'((match)|(search)|(compile)|(find(all)?)|(split)|(sub))\(r?[\'\"].{2,}?[\'\"],' -rho --include \*.py $i | sort -u | sed 's/match(r*//g' | sed 's/search(r*//g' | sed 's/compile(r*//g' | sed 's/find(r*//g' | sed 's/findall(r*//g' | sed 's/split(r*//g' | sed 's/sub(r*//g' | sed 's/.$//g' >> $result_file
done

echo "search python projects $proj_dir done, regexes are stored in $result_file"
# WARNING all log file will be replaced here
# rxxr/code/scan.bin -i python/py_regex.txt > python/py_rxxr_log.txt
# echo "rxxr analyze python regex done, log in python/py_rxxr_log.txt"
###############################################
