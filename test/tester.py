# -*- coding: utf-8 -*-
import argparse
import os

from os import listdir
from os.path import join
from subprocess import check_output

# Only used to test
def test():
    # test git lastest commit hash
    print(getLatestCommitHash())

    # test js regex extractor
    extractRegexFromJsProject()

# git latest commit hash
def getLatestCommitHash():
    out = check_output('git log -1 --format=\'%H\'', shell = True)
    hash = out.decode('utf8')[:-1]
    if len(hash) == 40:
        return hash
    else:
        print('Error: get latest commit hash')
        print(hash)
        return None

base_dir = {
    'js': './PUTs/js/',
    'python': './PUTs/python/',
    'php': './PUTs/php/',
    'java': './PUTs/java/'
}
def getProjects(lang):
    dirs = listdir(base_dir[lang])
    return [join(base_dir[lang], x) for x in dirs]

def extractRegexFromJsProject():
    jsProjects = getProjects('js')
    
    print(jsProjects)
    pass

parser = argparse.ArgumentParser()
parser.add_argument("-js", type = str, default = "", help = "extract regex from js project")
args = parser.parse_args()

if args.js != "":
    extractRegexFromJsProject()
else:
    print('js dir is empty')

test()
