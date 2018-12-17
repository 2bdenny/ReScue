# -*- coding: utf-8 -*-
import sys
from utils.base import getRegFromProject, storeRegs

def searchPythonFile(dir):
    regs = getRegFromProject(dir, 'egrep "(match|search|compile|find(all)?|split|sub)\s*\(\s*r?([\\\'\\\"]).+?\\3," -rno --include \\*.py', r'(.*\.py):(\d+):[a-z]+\s*\(\s*r?([\'\"])(.+?)\3,$', raw_group = 4, lang = 'python')

    storeRegs(regs)

# searchPythonFile(sys.argv[1])
