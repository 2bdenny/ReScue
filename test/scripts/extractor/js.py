import os
import re
import sys

from subprocess import check_output

def getRegFromProject(grepreg, inforeg):
    regs = []

    output = check_output(grepreg, shell = True)
    lines = output.decode('utf8').split('\n')
    for line in lines:
        if len(line) == 0:
            continue
        else:
            res = re.search(inforeg, line)
            reg_file = res.group(1)
            reg_lineno = res.group(2)
            reg_raw = res.group(3)

            reg = {
                "file": reg_file,
                "lineno": reg_lineno,
                "reg": reg_raw
            }
            regs.append(reg)
    return regs

def searchJsFile(dir):
    regs = []

    cwd = os.getcwd()
    os.chdir(dir)
    lines = []

    regs += getRegFromProject('grep -P \'(replace)\(/.{1,}?/[igm]{0,3},\' -rno --include \\*.js', '(.*\.js):(\d+):replace\(/(.*)/[igm]{0,3},$')

    regs += getRegFromProject('grep -P \'(match)\(/.{1,}?/[igm]{0,3}\)\' -rno --include \\*.js', '(.*\.js):(\d+):match\(/(.*)/[igm]{0,3}\)')

    regs += getRegFromProject('grep -P \'[= \!;]/[^/]{2,}?/[igm]{0,3}(;|,|(\.((test)|(exec))))\' -rno --include \*.js', '(.*\.js):(\d+):[=\!;]/(.*)/[igm]{0,3}(;|,|(\.((test)|(exec))))$')

    os.chdir(cwd)
    print(regs)
    return regs

searchJsFile(sys.argv[1])
