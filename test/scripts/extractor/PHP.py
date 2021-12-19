# -*- coding: utf-8 -*-
import sys

from scripts.utils.extractutils import *

def getExtension():
    return ".php"

def searchFile(dir):
    regs = getRegFromProject(dir, r'preg_[a-z_]+\s*\(\s*"(.)(?P<target>.+?)\1[imsxuXeSUAD]{0,3}",', lang = 'php', extension = getExtension())

    return regs
    # storeRegs(regs)

# searchPhpFile(sys.argv[1])
