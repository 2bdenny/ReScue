# -*- coding: utf-8 -*-
import sys

from scripts.utils.extractutils import *

def searchFile(dir):
    regs = getRegFromProject(dir, 'grep -P "(Pattern\.compile|\.replace)\(\\\".*?[^\\\\\\\\]\\\"" -rno --include \\*.java', r'(.*\.java):(\d+):(Pattern\.compile|\.replace)\s*\(\s*\"(.*?)\"$', raw_group = 4, lang = 'java')
    storeRegs(regs)

# searchJavaFile(sys.argv[1])
