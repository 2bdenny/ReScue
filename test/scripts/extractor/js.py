# -*- coding: utf-8 -*-
import sys
from utils.base import getRegFromProject

def searchJsFile(dir):
    regs = []

    regs += getRegFromProject(dir, 'grep -P \'(replace)\(/.{1,}?/[igm]{0,3},\' -rno --include \\*.js', '(.*\.js):(\d+):replace\(/(.*)/[igm]{0,3},$')

    regs += getRegFromProject(dir, 'grep -P \'(match)\(/.{1,}?/[igm]{0,3}\)\' -rno --include \\*.js', '(.*\.js):(\d+):match\(/(.*)/[igm]{0,3}\)')

    regs += getRegFromProject(dir, 'grep -P \'[= \!;]/[^/]{2,}?/[igm]{0,3}(;|,|(\.((test)|(exec))))\' -rno --include \*.js', '(.*\.js):(\d+):[=\!;]/(.*)/[igm]{0,3}(;|,|(\.((test)|(exec))))$')

    return regs

def storeJsRegs(regs):
    pass

regs = searchJsFile(sys.argv[1])
storeJsRegs(regs)
