# MaschinelleSprachverarbeitung03

This is a implemenation of the Hidden-Markov-Model and the Viterbi algorithm. As input the program is expecting a set of files with one sentence in each line. We have an example corpus in our directory.

First, the program builds for the Hidden-Markov-Model an emission and a transition matrix one the basis of the trainings corpus.

Second, the Viterbi algotithm does it's magic. It works with the matrices based on the training corpus and predicts the tags for each word in a sentence.

With 10-fold cross-validation on our corpus we got our precision. Our aim/task was to reach at least 66% precision.<br>
Here is what we got:

Result of 10-fold cross-validation
---
Fold | Precision
|:-------------| :-----:|
01|0.6277363717594194 |
02|0.6677003928669225
03|0.6713726465893464
04|0.6707880485111303
05|0.6662769012237721
06|0.6608963862405247
07|0.6673460617874272
08|0.6624397393691152
09|0.6603500809901061
10|0.6649933423218122

AVG|0.6619899971659576
|:-------------| :-----:|

