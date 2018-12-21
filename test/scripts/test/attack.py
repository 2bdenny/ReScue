import os, sys
import multiprocessing
import time
import re

def thename(target):
    atk = target[0]
    regfilename = target[1]
    index = target[2]
    defaultResultDir = target[3]

    atkname = os.path.basename(atk)
    regfile = os.path.join(defaultResultDir, atkname, regfilename, 'r' + str(index) + '.txt')
    logfile = os.path.join(defaultResultDir, atkname, regfilename, str(index) + '.txt')

    os.system('timeout 600 java -d64 -Xms512m -Xmx2g -jar ' + atk + ' -q -ml 128 -pz 200 -g 100 -mp 10 -cp 10 < ' + regfile + ' > ' + logfile + ' 2>&1')
    os.system('rm ' + regfile)

# Attack method
# attacker: Attacker file (.jar)
# regexes: Regex file (.txt and so on; A regex a line)
# outdir: Attack result output directory
def attack(attacker, regexes, outdir):
    atkname = os.path.basename(attacker)
    regfilename = os.path.basename(regexes)

    regs = None
    with open(regexes) as f:
        regs = f.read().splitlines()

    # Clock start
    start_time = time.time()
    if regs != None:
        targets = [(attacker, regfilename, i, outdir) for i in range(1, len(regs) + 1)]
        p = multiprocessing.Pool(multiprocessing.cpu_count() - 1)
        p.map(thename, targets, 1)

    # Normally exit tip
    print(attacker + " attack " + regexes + " end.")
    # Clock end
    elapsed_time = time.time() - start_time
    print('Elapsed ' + str(elapsed_time) + " seconds.")

# If input arguments are correct
if (len(sys.argv) == 4):
    attack(sys.argv[1], sys.argv[2], sys.argv[3])
# If error
else:
    print('Please check your attacker name or regex file name.')
