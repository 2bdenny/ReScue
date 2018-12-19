# -*- coding: utf-8 -*-
###
### This file is under developing,
### and it should be a controller of all scripts
###
import argparse
import importlib

# import scripts.extractor.Java
# import scripts.extractor.JavaScript
# import scripts.extractor.PHP
# import scripts.extractor.Python

from scripts.crawler.git import *

from os.path import join

parser = argparse.ArgumentParser()
parser.add_argument('-url', type = str, default = "", help = "Git repo url only")
parser.add_argument('-d', '--dir', type = str, default = './PUTs/', help = 'Store repo to this directory')
args = parser.parse_args()

print(args)

if args.url is not None and args.url != "":
    # pass
    (lang, project, dir) = analyzeGitUrl(args.url, args.dir)
    print(lang, project, dir)
    if isProjectExist(args.url):
        print(args.url, 'already exists')
    else:
        getGitProject(args.url, dir)

        projDir = join(dir, project)
        mod = importlib.import_module('scripts.extractor.' + lang)
        mod.searchFile(projDir)
