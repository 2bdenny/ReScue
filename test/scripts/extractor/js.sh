#!/bin/bash
#set -x #echo on

###############################################
# search regex in js file of dir $1, stored in file $2
# only search regex from these directory(server end js)
proj_dir=${1-"./"}
result_file=${2-"js_regex.txt"}
mkdir -p "$(dirname "$result_file")"

mapfile -t js_targets < $proj_dir

# search all useful regex from the target folders
for i in "${js_targets[@]}"
do
  grep -P '(replace)\(/.{1,}?/[igm]{0,3},' -rho --include \*.js $i | sort -u | sed 's/replace(//g' | sed 's/[igm]*,$//g' >> $result_file
  grep -P '(match)\(/.{1,}?/[igm]{0,3}\)' -rho --include \*.js $i | sort -u | sed 's/match(//g' | sed 's/[img]*)$//g' >> $result_file
  grep -P '[= \!;]/[^/]{2,}?/[igm]{0,3}(;|,|(\.((test)|(exec))))' -roh --include \*.js $i | sort -u | cut -c 2- | sed -e 's/\.test$//g' | sed -e 's/\.exec$//g' | sed -e 's/[img]*[,;]*$//g' >> $result_file
done

echo "search js projects in $proj_dir done, regexes are stored in $result_file"
