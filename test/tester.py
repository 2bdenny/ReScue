# -*- coding: utf-8 -*-
###
### This file is under developing,
### and it should be a controller of all scripts
###
import argparse

from scripts.crawler.git import analyzeGitUrl, getGitProject

parser = argparse.ArgumentParser()
parser.add_argument('-url', type = str, default = "", help = "Git repo url only")
parser.add_argument('-d', '--dir', type = str, default = './PUTs/', help = 'Store repo to this directory')
args = parser.parse_args()

print(args)

if args.url is not None and args.url != "":
    pass
    # (url, dir) = analyzeGitUrl(args.url, args.dir)
