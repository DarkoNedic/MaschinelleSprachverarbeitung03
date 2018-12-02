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
01|0.6280835596486484
02|0.6680500887945453
03|0.6715968698945206
04|0.6712078806360708
05|0.66660032939972
06|0.6611815842133832
07|0.66755750948302
08|0.6627386314654603
09|0.6604775503681545
10|0.6652869062463428

AVG|0.6622780910149866
|:-------------| :-----:|

Missing:
- Import matrices
- Run program in annotate mode
