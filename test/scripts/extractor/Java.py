# -*- coding: utf-8 -*-
import sys

from scripts.utils.extractutils import *

def getExtension():
    return ".java"

def searchFile(dir):
    regs = getRegFromProject(dir, r'(?:Pattern\.compile|\.replace)\("(?P<target>.*?[^\\])"', lang = 'java', extension = getExtension())
    return regs
    # storeRegs(regs)

# searchJavaFile(sys.argv[1])
