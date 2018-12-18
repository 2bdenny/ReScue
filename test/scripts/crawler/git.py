# -*- coding: utf-8 -*-
import os
import sys
import re

from os import makedirs
from os.path import exists, join

def analyzeGitUrl(url, storeDir):
    res = re.search('.*github.com/(.*)/(.*)(\.git)?', url)
    developer = res.group(1)
    project = res.group(2)
    print(developer, project)

    repo_page = 'https://github.com/' + developer + '/' + project
    resp = requestPage(repo_page)

    res = re.findall('<span class="lang">(.*?)</span>\s*<span class="percent">(.*%)</span>', resp)

    lang = None
    if len(res) > 0:
        lang = res[0][0]

    if lang is not None:
        placeDir = join(storeDir, lang)
        if not exists(placeDir):
            makedirs(placeDir)
        return (lang, placeDir)
    else:
        print('Error: cannot extract language from its project page')
        return (None, None)

def getGitProject(url, dir):
    cwd = os.getcwd()
    os.chdir(dir)
    os.system('git clone ' + url)
    os.chdir(cwd)

# analyzeGitUrl(sys.argv[1], sys.argv[2])
