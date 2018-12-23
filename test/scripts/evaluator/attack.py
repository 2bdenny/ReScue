# -*- coding: utf-8 -*-
import multiprocessing
import os
import re
import sys
import time

from datetime import datetime
from os import makedirs
from os.path import basename, exists, join

def thename(target):
    atk = target[0]
    # regfilename = target[1]
    index = target[1]
    logDir = target[2]

    # atkname = os.path.basename(atk)
    regfile = os.path.join(logDir, 'r' + str(index) + '.txt')
    logfile = os.path.join(logDir, str(index) + '.txt')

    os.system('timeout 600 java -d64 -Xms512m -Xmx2g -jar ' + atk + ' -q -ml 128 -pz 200 -g 100 -mp 10 -cp 10 < ' + regfile + ' > ' + logfile + ' 2>&1')
    os.system('rm ' + regfile)

# Attack method
# attacker: Attacker file (.jar)
# regexes: Regex file (.txt and so on; A regex a line)
# outdir: Attack result output directory

def prepare(regfile, logDir):
    regs = None
    with open(regfile) as rf:
        regs = rf.readlines()

    for i in range(len(regs)):
        rfile = os.path.join(logDir, 'r' + str((i+1)) + '.txt');
        with open(rfile, 'w') as f:
            f.write(regs[i])

base_atk_path = '../release/'
base_reg_path = './data/'
base_log_path = './logs/'
def attack(attacker, regexes):
    whole_atk_path = join(base_atk_path, attacker)
    whole_reg_path = join(base_reg_path, regexes)
    whole_log_path = join(base_log_path, attacker, regexes, str(int(datetime.today().timestamp() * 1000)))
    if not exists(whole_log_path):
        makedirs(whole_log_path)

    prepare(whole_reg_path, whole_log_path)

    print(whole_atk_path)
    print(whole_reg_path)
    print('------')
    print('Current regex file:', regexes)
    print('Current log dir:', whole_log_path)
    print('The collect script take these two arguments as inputs.')
    print('------')

    regs = None
    with open(whole_reg_path) as f:
        regs = f.read().splitlines()

    # Clock start
    start_time = time.time()
    if regs != None:
        targets = [(whole_atk_path, i, whole_log_path) for i in range(1, len(regs) + 1)]
        p = multiprocessing.Pool(multiprocessing.cpu_count() - 1)
        p.map(thename, targets, 1)

    # Normally exit tip
    print(attacker + " attack " + regexes + " end.")
    # Clock end
    elapsed_time = time.time() - start_time
    print('Elapsed ' + str(elapsed_time) + " seconds.")

# # If input arguments are correct
# if (len(sys.argv) == 3):
#     attack(sys.argv[1], sys.argv[2])
# # If error
# else:
#     print('Please check your attacker name or regex file name.')
#     print('Expect: python attack.py ReScue.jar test_format.txt')
