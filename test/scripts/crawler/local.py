# -*- coding: utf-8 -*-
import importlib
import json
import os
import sys
import re

from os import makedirs
from os.path import exists, join

from scripts.crawler.git import *
from scripts.utils.extractutils import *
from scripts.utils.webutils import *

def txtRegs(regs, developer, project, dataDir = './data'):
    txtName = developer + '_' + project

    with open(join(dataDir, txtName + '.json'), 'w') as jf:
        json.dump(regs, jf)
    with open(join(dataDir, txtName + '.txt'), 'w') as tf:
        for reg in regs:
            tf.write(reg['reg'] + '\n')

    return txtName

def extractRegexFromLocalRepo(lang, developer, project, dir):
    supportedLang = ['JavaScript', 'Python', 'Java', 'PHP']

    if isProjectExist(developer, project):
        print(developer + '/' + project, 'already exists')
    else:
        langDir = join(dir, lang)
        getGitProject(developer, project, langDir)
        projDir = join(langDir, project)

        regs = []
        for tmpLang in supportedLang:
            modname = 'scripts.extractor.' + tmpLang
            modexist = importlib.util.find_spec(modname)
            if modexist is None:
                print(modname, 'is not exist')
            else:
                mod = importlib.import_module(modname)
                regs += mod.searchFile(projDir)

        return txtRegs(regs, developer, project)
