# -*- coding: utf-8 -*-
import sys

from scripts.utils.extractutils import *

def searchFile(dir):
    # the replace syntax
    regs = getRegFromProject(dir, 'grep -P \'(replace)\(/.{1,}?/[igm]{0,3},\' -rno --include \\*.js', '(.*\.js):(\d+):replace\(/(.*)/[igm]{0,3},$', lang = 'js')
    storeRegs(regs)

    # the match syntax
    regs = getRegFromProject(dir, 'grep -P \'(match)\(/.{1,}?/[igm]{0,3}\)\' -rno --include \\*.js', '(.*\.js):(\d+):match\(/(.*)/[igm]{0,3}\)', lang = 'js')
    storeRegs(regs)

    # other post-call syntax
    regs = getRegFromProject(dir, 'grep -P \'[= \!;]/[^/]{2,}?/[igm]{0,3}(;|,|(\.((test)|(exec))))\' -rno --include \*.js', '(.*\.js):(\d+):[=\!;]/(.*)/[igm]{0,3}(;|,|(\.((test)|(exec))))$', lang = 'js')
    storeRegs(regs)
