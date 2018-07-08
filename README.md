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

## Dependencies
1. JDK 1.8
2. The prefuse visualization toolkit: [prefuse.jar](http://prefuse.org/)
