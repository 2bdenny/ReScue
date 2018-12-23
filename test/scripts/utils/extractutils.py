# -*- coding: utf-8 -*-
import hashlib
import json
import mysql.connector
import os
import re
import sys

from datetime import datetime
from os.path import join
from subprocess import check_output

# git latest commit hash
def getLatestCommitHash():
    out = check_output('git log -1 --format=\'%H\'', shell = True)
    hash = out.decode('utf8')[:-1]
    if len(hash) == 40:
        return hash
    else:
        print('Error: get latest commit hash')
        print(hash)
        return None

# get repo url
def getGitRepo():
    out = check_output('git remote -v', shell = True).decode('utf8')
    return re.search('origin\s+(.*)\s+\(fetch\)', out).group(1)

# extract reg from project
def getRegFromProject(dir, grepreg, inforeg, file_group = 1, lineno_group = 2, raw_group = 3, lang = 'js'):
    cwd = os.getcwd()
    os.chdir(dir)

    latest_git_log_hash = getLatestCommitHash()
    git_repo = getGitRepo()

    regs = []
    output = check_output(grepreg, shell = True)
    lines = output.decode('utf8').split('\n')
    for line in lines:
        if len(line) == 0:
            continue
        else:
            res = re.search(inforeg, line)
            if res is None:
                print(inforeg)
                print(line)
                continue
            print(res.groups())

            reg_file = res.group(file_group)
            reg_lineno = res.group(lineno_group)
            reg_raw = res.group(raw_group)

            reg_hash = hashlib.md5(reg_raw.encode('utf8')).hexdigest()

            reg = {
                'file': reg_file,
                'lineno': reg_lineno,
                'reg': reg_raw,
                'reg_hash': reg_hash,
                'git_commit': latest_git_log_hash,
                'pl': lang,
                'repo': git_repo
            }
            regs.append(reg)

    os.chdir(cwd)
    return regs

def connectDB():
    # read db info from file
    dbinfo = None
    with open('db.json', 'r') as dbf:
        dbinfo = json.load(dbf)

    if dbinfo is None:
        print('Errir: Need db.json')
        return

    # connect db
    db = mysql.connector.connect(
        host = dbinfo['host'],
        port = dbinfo['port'],
        user = dbinfo['username'],
        password = dbinfo['password'],
        database = dbinfo['database']
    )

    return db

# store regs into db
def storeRegs(regs):
    db = connectDB()
    cs = db.cursor()

    # insert into db
    for reg in regs:
        sql = 'INSERT IGNORE INTO regs (reg, reg_hash, repo, file, lineno, git_commit, pl) VALUES (%s, %s, %s, %s, %s, %s, %s)'
        val = (reg['reg'], reg['reg_hash'], reg['repo'], reg['file'], reg['lineno'], reg['git_commit'], reg['pl'])
        cs.execute(sql, val)
    db.commit()

    print(len(regs), "regex stored")

# dump regs from db to file
def dumpRegs():
    db = connectDB()
    cs = db.cursor()
    cs.execute('select * from regs')
    row_headers = [x[0] for x in cs.description]
    rs = cs.fetchall()

    jrs = []
    for row in rs:
        jrs.append(dict(zip(row_headers, row)))

    jsonFile = join('./data', str(int(datetime.today().timestamp() * 1000)) + '.json')
    regFile = join('./data', str(int(datetime.today().timestamp() * 1000)) + '.txt')
    with open(jsonFile, 'w') as jf:
        json.dump(jrs, jf)
    with open(regFile, 'w') as rf:
        for r in jrs:
            rf.write(r['reg'] + '\n')
    print(len(jrs), 'regs are dumped to', jsonFile, 'and', regFile)
