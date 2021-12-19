# -*- coding: utf-8 -*-
import importlib
import json
import os
import sys
import re

from importlib import util
from os import makedirs
from os.path import exists, join

from scripts.crawler.git import *
from scripts.utils.extractutils import *
from scripts.utils.webutils import *

def txtRegs(regs, developer, project, dataDir = './data'):
    txtName = developer + '_' + project

    with open(join(dataDir, txtName + '.json'), 'w', encoding='utf8') as jf:
        json.dump(regs, jf)
    with open(join(dataDir, txtName + '.txt'), 'w', encoding='utf8') as tf:
        for reg in regs:
            tf.write(reg['reg'] + '\n')

    return txtName

def extractRegexFromLocalRepo(developer, project, projDir):
    supportedLang = ['JavaScript', 'Python', 'Java', 'PHP']

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
