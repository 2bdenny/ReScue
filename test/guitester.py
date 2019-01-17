# -*- coding: utf-8 -*-
###
### This is under developing, and we only support extract regex from files
### written in some languages
###
import argparse
import importlib
import time

from os.path import join
from random import randint
from scripts.crawler.git import *
from scripts.crawler.local import *

from scripts.evaluator.attack import attack
from scripts.evaluator.collect import collect

# Maximum extract project numbers once
TODAY_MAX_COUNT = 5
CURRENT_SUPPORT_LANG = ['Java', 'JavaScript', 'Python', 'PHP']

def test(args):
    if args.down and args.url is not None and args.url != '':
        (lang, developer, project, dir) = analyzeGitUrl(args.url, args.dir)
        if lang is not None:
            projDir = downloadProject(lang, developer, project, dir)
            txtName = extractRegexFromLocalRepo(developer, project, projDir)
            print('txtName:', txtName)

parser = argparse.ArgumentParser()
parser.add_argument('-down', default = False, action = 'store_true', help = 'Download project from url')
parser.add_argument('-url', type = str, default = '', help = 'Git repo url only')
parser.add_argument('-dir', type = str, default = './PUTs/', help = 'Store git repo in this dir')

if len(sys.argv) == 1:
    parser.print_help()
else:
    args = parser.parse_args()
    test(args)
