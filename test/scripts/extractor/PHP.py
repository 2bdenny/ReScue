# -*- coding: utf-8 -*-
import sys

from scripts.utils.extractutils import *

def searchFile(dir):
    regs = getRegFromProject(dir, 'grep -P "preg_[a-z_]+\s*\\(\s*([\\\'\\\"])(.).*?\\2[imsxuXeSUAD]{0,3}\\1," -rno --include \\*.php', r'(.*\.php):(\d+):preg_[a-z_]+\s*\(\s*([\'\"])(.)(.*)\4[imsxuXeSUAD]{0,5}\3,$', raw_group = 5, lang = 'php')

    storeRegs(regs)

# searchPhpFile(sys.argv[1])
