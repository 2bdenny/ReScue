# -*- coding: utf-8 -*-
import sys

from scripts.utils.extractutils import *

def getExtension():
    return ".py"

def searchFile(dir):
    regs = getRegFromProject(dir, r'[^\'](match|search|compile|find(all)?|split|sub)\s*\(\s*r?\'(?P<target>.+?)\',', lang = 'python', extension = getExtension())
    regs += getRegFromProject(dir, r'[^\'](match|search|compile|find(all)?|split|sub)\s*\(\s*r?"(?P<target>.+?)"[,\)]', lang = 'python', extension = getExtension())
    return regs
    # storeRegs(regs)

# searchPythonFile(sys.argv[1])
