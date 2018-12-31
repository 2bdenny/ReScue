
> ReScue is under testing & debugging now.

## Using ReScue
### Simple use
Download the zip, decompress it, then:
```
cd release/
java -jar ReScue.jar
```
Sample output should be:
```
Input regex: (?=(a+)+b)aaabx
(?=(a+)+b)aaabx
1: <14 : 4.0 : aaabx>
===Initiate End===
Node Coverage: 14/14
find attack string when cross
===Genetic Algorithm End===
Node Coverage: 14/14
Vulnerable: babaaabbaaaaaaaaaaaaaaaa
100027 : 4001.08 : babaaabbaaaaaaaaaaaaaaaa
100017 : 7144.071428571428 : aaaaaaaaaaaaa
1000029 : aaaaaaaaaaaaaaaaa
1000244 : 27 : aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
100000246 : aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
TIME: 20.548258612 (s)
Attack success, attack string is:
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
```

### Batch test
Download the zip, decompress it, then:
1. Put your regexes in one file, like `my_regexes.txt`, a regex per line
2. Put the file in `test/data/`
3. Find manul of how to run the test script:
```
cd test/
python batchtester.py
```
4. Or just run the test:
```
python batchtester.py -a -reg my_regexes.txt
```
5. Wait a lot of time, and be careful about your CPU, monitor it by `htop` or something else
6. Auto collect the evaluation result:
```
python batchtester.py -c -logDir ./test/logs/ReScue.jar/my_regexes.txt/<digits>/
```
7. You will get some report like this:
```
Attack Summary
First Success: 0
Real Success: 0
Failed: 22
	Pumping: 0; Timeout: 0; Normal: 22; Init: 0; Cross: 0; Select: 0
Unparsable: 3
Total: 25
Node Coverage: init/ga/total 179/179/194
```
8. And you can find the attack strings in the `logDir`

## Directory structure
```
  ReScue
├─release	# ReScue.jar
├─src/cn/edu/nju/moon/redos/
│     ├─attackers
│     │  ├─ga
│     │  │  ├─crossovers
│     │  │  ├─initiators
│     │  │  ├─mutators
│     │  │  └─selectors
│     │  └─pp
│     ├─regex
│     ├─tester	# Look at the MyTester.java, you can add your own tester here
│     └─utils
│     └─gui     # TODO What is this?
└─test
	└─scripts			# Some scripts used for evaluation
```

## Building

This project uses Apache Maven.
- Run `mvn clean compile` to build it.
- There is a Maven guide available [here](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html).

## Confirmed ReDoS vulnerabilities
1. https://github.com/nhnent/tui.editor/issues/141
2. https://github.com/ajaxorg/ace/issues/3638
3. https://github.com/meteor/meteor/issues/9731
4. https://github.com/openstates/openstates/issues/2020

## ReDoS vulnerabilities under testing
1. https://github.com/metabase/metabase/issues/7354
2. https://github.com/prose/prose/issues/1071
3. https://github.com/adobe/brackets/issues/14154
