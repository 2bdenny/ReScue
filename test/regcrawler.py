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

# Maximum extract project numbers once
TODAY_MAX_COUNT = 5
CURRENT_SUPPORT_LANG = ['Java', 'JavaScript', 'Python', 'PHP']

def test(args):
    if args.url is not None and args.url != '':
        (lang, developer, project, dir) = analyzeGitUrl(args.url, args.dir)
        print(lang, developer, project, dir)
        extractRegexFromGitRepo(lang, developer, project, dir)

    if args.key is not None and args.key != '':
        repos = queryGitHubRepos(args.key)

        stopCount = 0
        for repo in repos:
            print('extracting', repo)
            if repo['lang'] in CURRENT_SUPPORT_LANG:
                extractRegexFromGitRepo(repo['lang'], repo['developer'], repo['project'], args.dir)
            else:
                print('language', repo['lang'], 'is not supported now')
                continue

            # For not be banned by GitHub
            stopCount += 1
            time.sleep(randint(1, 5))
            if stopCount == TODAY_MAX_COUNT:
                print('Today mission', TODAY_MAX_COUNT, 'finished')
                return


parser = argparse.ArgumentParser()
parser.add_argument('-url', type = str, default = '', help = 'Git repo url only')
parser.add_argument('-key', type = str, default = '', help = 'Search key of git repo')
parser.add_argument('-dir', type = str, default = './PUTs/', help = 'Store git repo in this dir')
# parser.add_argument('-clear', type = )

if len(sys.argv) == 1:
    parser.print_help()
else:
    args = parser.parse_args()
    test(args)
