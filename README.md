
> ReScue is under testing & debugging now.

## Using ReScue
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
│     ├─tester	# Main classes are here
│     └─utils
└─test
	├─data				# Put regexes txt files here
	└─scripts
		└─extractor	# Scripts to extract regexes from projects' source
```

## Dependencies
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
