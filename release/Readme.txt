Usage:
java -jar rescue.jar -h
	Show this text.
	
java -jar rescue.jar <-sl string length> <-pz population size> <-g generations> 
					<-cp crossover probability> <-mp mutation probability> regex
					<-q> <-v>
	Attack the <regex> with the <attacker>
	by default(sl is 128, pz is 200, g is 200, cp is 10(0.1), mp is 10(0.1)):
		java -jar rescue.jar
	or:
		java -jar rescue.jar -sl 128 -pz 200 -g 200 -cp 10 -mp 10

-sl	Limit the string length

-pz Limit the population size

-g	Limit the generation

-cp Set the crossover possiblity, the real possibility is calculated by cp / 100.0

-mp Set the mutation possibility, the real possibility is calculated by mp / 100.0

-v	View the inner structure of a regex, usage:
	java -jar rescue.jar -v
	or combine with other options

-q	Quiet mode, do not show input message, usage:
	java -jar rescue.jar -q
	or combine with other options
	
For example:
	java -jar rescue.jar
	
	> Input regex: (a+)+0
	> 2: <9 : 9.5 : a><10 : 5.0 : a0>
	> ===Initiate End===
	> Node Coverage: 10/10
	> find attack string when cross
	> ===Genetic Algorithm End===
	> Node Coverage: 10/10
	> Vulnerable: ~~0aaaaaaaaaaaaaaa
	> 100027 : 4763.190476190476 : ~~0aaaaaaaaaaaaaaa
	> 100022 : 6668.133333333333 : ~aaaaaaaaaaaaa
	> 1000034 : ~aaaaaaaaaaaaaaaaa
	> 1000251 : 27 : ~aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
	> 100000251 : ~aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
	> TIME: 9.336777379 (s)
	> Attack success, attack string is:
	> ~aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
