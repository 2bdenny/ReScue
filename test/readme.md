# Regex Sets
> These two regex sets are shared by existing work: [RXXR2](http://www.cs.bham.ac.uk/~hxt/research/rxxr2/), followings are the direct download links.
- [Snort](http://www.cs.bham.ac.uk/~hxt/research/rxxr2/snort-input.txt)
- [RegLib](http://www.cs.bham.ac.uk/~hxt/research/rxxr2/regexlib-input.txt)

> Another regex set is collected by Chapman, Carl, and Kathryn T. Stolee. And available on their GitHub: [tour_de_source](https://github.com/softwarekitty/tour_de_source), following is the direct download link.
- [Corpus](https://github.com/softwarekitty/tour_de_source/blob/master/analysis/pattern_tracking/corpusPatterns.txt)

---

# Tools
>### SlowFuzz
- SlowFuzz is available on GitHub: [slowfuzz](https://github.com/nettrino/slowfuzz)
- The regular expression library we used in the experiment is the version 10.31 (the latest version by 2018.7.7) of [PCRE2](ftp://ftp.csx.cam.ac.uk/pub/software/programming/pcre/).

>### RXXR2
- RXXR2 is available at authors' page: [rxxr2](http://www.cs.bham.ac.uk/~hxt/research/rxxr2/)

>### Rexploiter
- Rexploiter is not available directly.
- We requested for a runnable version by sending an email to one of the authors, Mr. Wüstholz, whose website is [www.wuestholz.com](http://www.wuestholz.com/).

>### NFAA
- NFAA is a short for the tool presented in the paper: [weideman2016analyzing](https://rd.springer.com/chapter/10.1007/978-3-319-40946-7_27).
- The tool is available on authors' GitHub: [RegexStaticAnalysis](https://github.com/NicolaasWeideman/RegexStaticAnalysis)
- We use the short NFAA (extracted from the paper title) to differ this tool from other static analysis tools.

---

# Log
The result of testing a regex set with a tool should be generated following the format:
```
1 : failed : success : 2 (s) : "h(i|ello)"
```
which is organized as:
```
Line-Number : Validate-Result : Tool-Report-Result : Tool-Analyze-Time : The-Regex
```
Our experiment result is collected from such result files by a python script.

# Evaluation Tools
- `cd test`
- The regex extractor: `python regcrawler.py`
- The batch evaluator: `python batchtester.py`
