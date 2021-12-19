# -*- coding: utf-8 -*-
import sys

from scripts.utils.extractutils import *

def getExtension():
    return ".js"

def searchFile(dir):
    # the replace syntax
    regs = getRegFromProject(dir, r'(?:replace)\(/(?P<target>.{1,}?)/[igm]{0,3},', lang = 'js', extension = getExtension())
    # storeRegs(regs)

    # the match syntax
    regs += getRegFromProject(dir, r'(?:match|split)\(/(?P<target>.{1,}?)/[igm]{0,3}\)', lang = 'js', extension = getExtension())
    # storeRegs(regs)

    # other post-call syntax
    regs += getRegFromProject(dir, r'[= \!;]/(?P<target>[^/]{2,}?)/[igm]{0,3}(?:;|,|(?:\.(?:test|exec)))', lang = 'js', extension = getExtension())
    # storeRegs(regs)

    return regs
