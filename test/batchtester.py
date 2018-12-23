# -*- coding: utf-8 -*-
import argparse
import sys

from scripts.evaluator.attack import attack
from scripts.evaluator.collect import collect
from scripts.utils.extractutils import *

parser = argparse.ArgumentParser()
parser.add_argument('-atk', '--attacker', type = str, default = 'ReScue.jar', help = 'The binary attacker (It should be put in the release/ directory).', required = False)
parser.add_argument('-reg', '--regexes', type = str, default = 'test_format.txt', help = 'The regex file (It should be put in the test/data/ directory).', required = False)
parser.add_argument('-logDir', type = str, default = '', help = 'The log directory of the last attack result.')

parser.add_argument('-a', '--attack', default = False, action = 'store_true', help = 'Current operation is attack.')
parser.add_argument('-c', '--collect', default = False, action = 'store_true', help = 'Current operation is collect.')

parser.add_argument('-dump', default = False, action = 'store_true', help = 'Dump regs in DB.')

def test(args):
    if args.attack:
        attack(args.attacker, args.regexes)
    if args.collect:
        if args.logDir is None or args.logDir == '':
            print('Error: logDir not set.')
            return
        collect(args.logDir, args.regexes)
    if args.dump:
        dumpRegs()

if len(sys.argv) == 1:
    parser.print_help()
else:
    args = parser.parse_args()
    # print(args)
    test(args)
