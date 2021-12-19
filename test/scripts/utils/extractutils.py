# -*- coding: utf-8 -*-
import hashlib
import json
import os
import re
import sys

from datetime import datetime
from os.path import join

# extract reg from project
def getRegFromProject(dir, grepreg, lang = 'js', extension = '.js'):
    cwd = os.getcwd()
    os.chdir(dir)

    regs = []
    for root, dirs, files in os.walk(dir):
        for f in files:
            if f.endswith(extension):
                fileFullPath = os.path.join(root, f)
                print(fileFullPath)
                with open(fileFullPath, "r", encoding='utf8') as fh:
                    content = fh.read()
                    print(re.findall(grepreg, content, re.MULTILINE | re.DOTALL))
                    matchiter = re.finditer(grepreg, content, re.MULTILINE | re.DOTALL)
                    for m in matchiter:
                        print(m.group('target'))
                        mStart = m.start()
                        mLineNo = content.count('\n', 0, mStart) + 1
                        mFile = fileFullPath
                        mRaw = m.group('target')
                        mHash = hashlib.md5(mRaw.encode('utf8')).hexdigest()
                        reg = {
                            'file': mFile,
                            'lineno': mLineNo,
                            'reg': mRaw,
                            'reg_hash': mHash,
                            'git_commit': '',#latest_git_log_hash,
                            'pl': lang,
                            'repo': ''#git_repo
                        }
                        regs.append(reg)
    os.chdir(cwd)
    return regs
