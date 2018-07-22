#!/bin/bash
#set -x #echo on

###############################################
# search regex in $1, stored in $2
# only search regex from these directory
proj_dir=${1-"./"}
result_file=${2-"php_regex.txt"}
mkdir -p "$(dirname "$result_file")"
mapfile -t php_targets < $proj_dir

# search all useful regex from the target folders
for i in "${php_targets[@]}"
do
  grep -P "preg_match *\( *([\'\"])(.).*?\2[imsxuXeSUAD]{0,3}\1" -rho --include \*.php $i | sed "s/preg_match *( *//" | sed -E "s/([\'\"])(.)(.*)\2[imsxuXeSUAD]{0,3}\1/\3/" | sort -u >> $result_file
  grep -P "preg_match_all *\( *([\'\"])(.).*?\2[imsxuXeSUAD]{0,5}\1" -rho --include \*.php $i | sed "s/preg_match_all *( *//" | sed -E "s/([\'\"])(.)(.*)\2[imsxuXeSUAD]{0,3}\1/\3/" | sort -u >> $result_file
  grep -P "preg_replace *\( *([\'\"])(.).*?\2[imsxuXeSUAD]{0,3}\1" -rho --include \*.php $i | sed "s/preg_replace *( *//" | sed -E "s/([\'\"])(.)(.*)\2[imsxuXeSUAD]{0,3}\1/\3/" | sort -u >> $result_file
  grep -P "preg_replace_callback *\( *([\'\"])(.).*?\2[imsxuXeSUAD]{0,3}\1" -rho --include \*.php $i | sed "s/preg_replace_callback *( *//" | sed -E "s/([\'\"])(.)(.*)\2[imsxuXeSUAD]{0,3}\1/\3/" | sort -u >> $result_file
  grep -P "preg_split *\( *([\'\"])(.).*?\2[imsxuXeSUAD]{0,3}\1" -rho --include \*.php $i | sed "s/preg_split *( *//" | sed -E "s/([\'\"])(.)(.*)\2[imsxuXeSUAD]{0,3}\1/\3/" | sort -u >> $result_file
  grep -P "preg_grep *\( *([\'\"])(.).*?\2[imsxuXeSUAD]{0,3}\1" -rho --include \*.php $i | sed "s/preg_grep *( *//" | sed -E "s/([\'\"])(.)(.*)\2[imsxuXeSUAD]{0,3}\1/\3/" | sort -u >> $result_file
  grep -P "([\'\"]).*?[^\\\]\1 *\. *preg_quote" -rho --include \*.php $i | sed -E "s/([\'\"])(.*?)[^\\\]\1 *\. *preg_quote/\2/" | sort -u >> $result_file
done

echo "search php projects in $proj_dir done, regexes are stored in $result_file"
