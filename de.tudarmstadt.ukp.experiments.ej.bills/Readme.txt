Readme.txt

This project runs three experiments.

1. Does the readability of the bill description impact the timespan 
between bill introduction and bill enactment/failure? Compared against an ngram baseline.
Experiment details: SMOreg regression experiment, where ML instance value is the 
number of days.  Features sets {readability features} versus {ngram features}.

2. Given the text of a bill, predict the sponsors.
Experiment details: Meka multi-label classification; ngram features.  

3. Is text similarity between a bill's title and description predictive of it ever passing?
Experiment details: Pair classification; features are text similarity metrics
computed over both texts.  Single label classification, SMO.