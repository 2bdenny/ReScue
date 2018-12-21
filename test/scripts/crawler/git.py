# -*- coding: utf-8 -*-
import importlib
import os
import sys
import re

from os import makedirs
from os.path import exists, join

from scripts.utils.webutils import *
from scripts.utils.extractutils import *

def analyzeGitUrl(url, storeDir):
    developer = None
    project = None
    if url.startswith('https'):
        res = re.search(r'.*github.com/([^/]*)/([^/]*)', url)
        developer = res.group(1)
        project = res.group(2)
        if project.endswith('.git'):
            project = project[:-4]
    elif url.startswith('git@'):
        res = re.search(r'git@github\.com:([^/]*)/([^/]*)', url)
        developer = res.group(1)
        project = res.group(2)
        if project.endswith('.git'):
            project = project[:-4]
    print(developer, project)

    repo_page = 'https://github.com/' + developer + '/' + project
    resp = requestPage(repo_page)

    res = re.findall('<span class="lang">(.*?)</span>\s*<span class="percent">(.*%)</span>', resp)

    lang = None
    if len(res) > 0:
        lang = res[0][0]

    if lang is not None:
        if not exists(storeDir):
            makedirs(storeDir)
        return (lang, developer, project, storeDir)
    else:
        print('Error: cannot extract language from its project page')
        return (None, developer, project, None)

def combineGitSSHUrl(developer, project):
    return 'git@github.com:' + developer + '/' + project + '.git'

def combineGitHttpsUrl(developer, project):
    return 'https://github.com/' + developer + '/' + project + '.git'

def getGitProject(developer, project, dir):
    ssh_url = combineGitSSHUrl(developer, project)

    cwd = os.getcwd()

    if not exists(dir):
        makedirs(dir)
    
    os.chdir(dir)
    if not exists(project):
        os.system('git clone ' + ssh_url)
    else:
        print(project, 'already cloned')
    os.chdir(cwd)

def isProjectExist(developer, project):
    ssh_url = combineGitSSHUrl(developer, project)
    https_url = combineGitHttpsUrl(developer, project)

    db = connectDB()
    cs = db.cursor()
    sql = 'SELECT DISTINCT repo FROM regs'
    cs.execute(sql)

    res = cs.fetchall()
    for r in res:
        if ssh_url == r[0]:
            return True
        if https_url == r[0]:
            return True

    return False

def extractRegexFromGitRepo(lang, developer, project, dir):
    if isProjectExist(developer, project):
        print(developer + '/' + project, 'already exists')
    else:
        langDir = join(dir, lang)
        getGitProject(developer, project, langDir)
        projDir = join(langDir, project)
        modname = 'scripts.extractor.' + lang
        modexist = importlib.util.find_spec(modname)
        if modexist is None:
            print(modname, 'is not exist')
        else:
            mod = importlib.import_module(modname)
            mod.searchFile(projDir)
