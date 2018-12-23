
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

## Directory
```
ReScue
├─jars 		# Put dependencies here (prefuse.jar, etc.)
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
└─test
	├─data				# Put regexes txt files here
	└─scripts			# Some scripts used for evaluation
```

## Dependencies
### Dependencies can be installed by maven automatically now, I love maven!
### Following is the deprecated dependencies intro
1. JDK 1.8 and Python3
2. The prefuse visualization toolkit: [prefuse.jar](http://prefuse.org/)
3. [junit-4.12.jar](http://search.maven.org/remotecontent?filepath=junit/junit/4.12/junit-4.12.jar)
4. [hamcrest-core-1.3.jar](http://search.maven.org/remotecontent?filepath=org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar)
5. [commons-lang-2.6.jar](http://mirrors.hust.edu.cn/apache//commons/lang/binaries/commons-lang-2.6-bin.tar.gz)

## Confirmed ReDoS vulnerabilities
1. https://github.com/nhnent/tui.editor/issues/141
2. https://github.com/ajaxorg/ace/issues/3638
3. https://github.com/meteor/meteor/issues/9731
4. https://github.com/openstates/openstates/issues/2020

## ReDoS vulnerabilities under testing
1. https://github.com/metabase/metabase/issues/7354
2. https://github.com/prose/prose/issues/1071
3. https://github.com/adobe/brackets/issues/14154

## Document Build Instructions
0. This is a *temporary solution*
1. The main class for ReScue is located in `cn.edu.nju.moon.redos.tester.RedosTester.java`
2. To build the `jar` file, create a `java project` in `Eclipse` on directory `ReScue` and use `File - Export-Runnable JAR File - Launch configuration: RedosTester` to generate it
3. **Do not forget to import dependencies in `Java Build Path`**
