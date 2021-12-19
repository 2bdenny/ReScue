# -*- coding: utf-8 -*-
import importlib
import os
import sys
import re
import platform

from os import makedirs
from os.path import exists, join

from scripts.utils.webutils import *
from scripts.utils.extractutils import *

# Get information from url
# the language/developer/project name/which dir to store/the zip file url/last git commit
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

    res = re.findall('<a.*?Repository, language.*?</svg>.*?(?:<span.*?>(.*?)</span>)+.*?</a>', resp, re.M | re.S)
    #zip_res = re.findall('<a.*?DOWNLOAD_ZIP.*?href="(.*\.zip)">.*?</a>', resp, re.M | re.S)
    zip_res = re.findall('href="(.*\.zip)">', resp, re.M | re.S)
    print(res)
    print(zip_res)
    print(resp)
    exit(1)
    zipUrl = 'https://github.com' + zip_res[0]
    cmt_res = re.findall('<a class="commit-tease-sha" href=".*?commit/(.*?)" data-pjax>', resp)
    lastCommit = cmt_res[0]

    lang = None
    if len(res) > 0:
        lang = res[0][0]

    if lang is not None:
        if not exists(storeDir):
            makedirs(storeDir)
        return (lang, developer, project, storeDir, zipUrl, lastCommit)
    else:
        print('Error: cannot extract language from its project page')
        return (None, developer, project, None, None, None)

# Gen ssh url from developer and project
def combineGitSSHUrl(developer, project):
    return 'git@github.com:' + developer + '/' + project + '.git'

# Gen https url from developer and project
def combineGitHttpsUrl(developer, project):
    return 'https://github.com/' + developer + '/' + project + '.git'

# Download git project
def getGitProject(developer, project, dir, zipUrl):
    # ssh_url = combineGitSSHUrl(developer, project)

    cwd = os.getcwd()
    if not exists(dir):
        makedirs(dir)

    os.chdir(dir)
    if not exists(project):
        # Too expensive
        # os.system('git clone ' + ssh_url)

        os.system('wget "' + zipUrl + '"')
        zip_filename = zipUrl.split('/')[-1]
        os.system('unzip -d ' + project + ' ' + zip_filename)
        os.system('rm ' + zip_filename)
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

def extractRegexFromGitRepo(lang, developer, project, dir, zipUrl, lastCommit):
    if isProjectExist(developer, project):
        print(developer + '/' + project, 'already exists')
    else:
        langDir = join(dir, lang)
        getGitProject(developer, project, langDir, zipUrl)
        projDir = join(langDir, project)
        modname = 'scripts.extractor.' + lang
        modexist = importlib.util.find_spec(modname)
        if modexist is None:
            print(modname, 'is not exist')
        else:
            mod = importlib.import_module(modname)
            regs = mod.searchFile(projDir)
            storeRegs(regs, lastCommit, combineGitSSHUrl(developer, project))

# analyzeGitUrl('https://github.com/substratum/template', 'cur')
