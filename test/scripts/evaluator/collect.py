# -*- coding: utf-8 -*-
import os
import re
import sys

from os.path import join
# Collect information from log files in the outdir

base_atk_path = '../release/'
base_reg_path = './data/'
base_log_path = './logs/'
def collect(logDir, regexes):
    # Summary file
    # All collected information are stored in this dir
    collect_file = os.path.join(logDir, 'collect_summary.txt')
    uncover_file = os.path.join(logDir, 'uncover.txt')

    # Some record vars
    atk_success = 0 # First Judge success
    atk_success_init = 0
    atk_success_cross = 0
    atk_success_mutate = 0
    atk_success_pumping = 0 # Second Judge success
    atk_failed = 0 # Attack failed
    atk_compile_error = 0
    atk_failed_init = 0
    atk_failed_cross = 0
    atk_failed_select = 0
    atk_failed_normal = 0 # Generation is up to maximum, but cannot find attack string
    atk_failed_pumping = 0 # First Judge passed, Second Judge failed
    atk_timeout = 0
    atk_total = 0

    node_init_covered = 0 # Node cover of seeds
    node_covered = 0 # Node cover after GA
    node_total = 0 # Total node in the regex

    START = 0
    REGEX = 1
    TIME = 2

    regfile = join(base_reg_path, regexes)
    regs = None
    with open(regfile, 'r') as rf:
        regs = rf.readlines()

    if regs == None:
        print_help()
        return

    with open(collect_file, 'w') as cf, open(uncover_file, 'w') as uf:
        # Number of regex being attacked
        atk_total = len(regs)
        for fno in range(1, atk_total + 1):
            filedir = join(logDir, str(fno) + '.txt')
            # Read info from log file
            lines = None
            with open(filedir, 'r') as log:
                lines = log.readlines()
            if lines == None:
                print('No such file: ' + filedir)
                return
            else:
                if len(lines) > 1:
                    # Regex Compile error at line 2
                    if re.search('Regex compile error', lines[-1]):
                        time = re.search('TIME: (.*) \(s\)', lines[-2]).group(1)
                        atk_compile_error = atk_compile_error + 1
                        cf.write(str(fno) + ' : unparsable : unparsable : ' + time + ' (s) : ' + lines[0])
                        uf.write(str(fno) + ' : uncomplete\n')
                    # Attack failed
                    elif re.search('Attack failed', lines[-1]):
                        time = re.search('TIME: (.*) \(s\)', lines[-2]).group(1)
                        atk_failed = atk_failed + 1
                        find_failed_result = False
                        for line in lines:
                            # Failed when seeds generation
                            if re.search('failed when init', line):
                                atk_failed_init = atk_failed_init + 1
                                cf.write(str(fno) + ' : failed : error init : ' + time + ' (s) : ' + lines[0])
                                find_failed_result = True
                                break
                            # Failed when crossover
                            elif re.search('failed when cross', line):
                                atk_failed_cross = atk_failed_cross + 1
                                cf.write(str(fno) + ' : failed : error cross : ' + time + ' (s) : ' + lines[0])
                                find_failed_result = True
                                break
                            # Failed when selection
                            elif re.search('failed when select', line):
                                atk_failed_select = atk_failed_select + 1
                                cf.write(str(fno) + ' : failed : error select : ' + time + ' (s) : ' + lines[0])
                                find_failed_result = True
                                break
                            # Failed when generation number up to maximum
                            elif re.search('Normal fail', line):
                                atk_failed_normal = atk_failed_normal + 1
                                cf.write(str(fno) + ' : failed : failed : ' + time + ' (s) : ' + lines[0])
                                find_failed_result = True
                                break
                        # Failed when Second Judge
                        if not find_failed_result:
                            atk_failed_pumping = atk_failed_pumping + 1
                            cf.write(str(fno) + ' : failed : pumping failed : ' + time + ' (s) : ' + lines[0])

                    # Attack success or timeout
                    else:
                        atk_str_filename = join(logDir, str(fno) + '_atk.txt')
                        is_timeout = True
                        time = None
                        tmp_result = None

                        # Attack success
                        for i in range(len(lines)):
                            line = lines[i]
                            if re.search('find attack string when', line):
                                # Pass First Judge at init
                                if re.search('find attack string when init', line):
                                    atk_success_init = atk_success_init + 1
                                    tmp_result = 'init'
                                # Pass First Judge at crossover
                                elif re.search('find attack string when cross', line):
                                    atk_success_cross = atk_success_cross + 1
                                    tmp_result = 'cross'
                                # Pass First Judge at mutation
                                elif re.search('find attack string when mutate', line):
                                    atk_success_mutate = atk_success_mutate + 1
                                    tmp_result = 'mutate'
                            # Extract the attack string
                            elif re.search('Attack success, attack string is:', line):
                                if tmp_result == None:
                                    tmp_result = 'success'
                                is_timeout = False
                                time = re.search('TIME: (.*) \(s\)', lines[i-1]).group(1)
                                cf.write(str(fno) + ' : success : ' + tmp_result + ' : ' + time + ' (s) : ' + lines[0])
                                atk_success_pumping = atk_success_pumping + 1
                                with open(atk_str_filename, 'w') as atk_str_file:
                                    for j in range(i+1, len(lines)):
                                        atk_str_file.write(lines[j])
                                break

                        # timeout
                        if is_timeout:
                            atk_failed = atk_failed + 1
                            atk_timeout = atk_timeout + 1
                            cf.write(str(fno) + ' : failed : timeout : 600 (s) : ' + lines[0])
                            # Timeout node coverage is not complete
                            uf.write(str(fno) + ' : uncomplete\n')
                            continue

                    # Extract node coverage info
                    next_init = False
                    for line in lines:
                        if re.search('===Initiate End===', line):
                            next_init = True
                        if re.search('Node Coverage: \d+/\d+', line):
                            nodes = re.findall(r'\d+', line)
                            if (next_init):
                                node_init_covered = node_init_covered + int(nodes[0])
                                next_init = False
                            else:
                                # if int(nodes[0]) < int(nodes[1]):
                                uf.write(str(fno) + ' : ' + nodes[0] + '/' + nodes[1] + '\n')
                                node_covered = node_covered + int(nodes[0])
                                node_total = node_total + int(nodes[1])

                # Input is not a regex, and something wrong
                else:
                    atk_compile_error = atk_compile_error + 1
                    cf.write(str(fno) + ' failed : unparsable : 0 (s) : ' + lines[0])
                    uf.write(str(fno) + ' : uncomplete\n')

        # Output for user
        print('Attack Summary')
        print('First Success: ' + str(atk_failed_pumping + atk_success_pumping))
        print('Real Success: ' + str(atk_success_pumping))
        # print('\tInit: ' + str(atk_success_init) + '; Cross: ' + str(atk_success_cross) + '; Mutate: ' + str(atk_success_mutate))
        print('Failed: ' + str(atk_failed))
        print('\tPumping: ' + str(atk_failed_pumping) + '; Timeout: ' + str(atk_timeout) + '; Normal: ' + str(atk_failed_normal) + '; Init: ' + str(atk_failed_init) + '; Cross: ' + str(atk_failed_cross) + '; Select: ' + str(atk_failed_select))
        print('Unparsable: ' + str(atk_compile_error))
        print('Total: ' + str(atk_total))
        print('Node Coverage: init/ga/total ' + str(node_init_covered) + '/' + str(node_covered) + '/' + str(node_total))

def print_help():
    print('Input error, expect: python collect.py logDir test.txt')

# if (len(sys.argv) == 2):
#     collect(sys.argv[1])
# elif (len(sys.argv) == 3):
#     collect(sys.argv[1], sys.argv[2])
# else:
#     print_help()
