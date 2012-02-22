WMActivationTracker
Author: Nate Derbinsky

== Intro ==
This is a simple pair of scripts to help debug/visualize working-memory activation values. The first script (gen_wma.cpp) runs a Soar agent for a fixed number of decisions, outputting working memory at each decision to a specially named file. The second script (aggregate_wma.php) takes these files and produces a CSV file where every working-memory element (columns) has an activation value for every decision (rows). This allows for easy graphing of activation in Excel, for instance.

== Usage: Prerequisites ==
The first script is written in C++ (for speed reasons), whereas the second is in PHP. Thus, you will a Soar distribution to link the C++ program and the PHP CLI on the system to run.

== Usage: Building ==
I've provided a build script (build.sh) that works on Mac, assuming the SOAR_HOME environmental variable has been defined. This should also work on Linux. I have never tried this on Windows.

== Usage: Running ==
The data producer (gen_wma.cpp) has hard-coded the agent file it's going to use (agent.soar). I've provided one such agent as an example, which simply counts and, using mods, has rules match WMEs on fixed patterns. It will output wma_#.txt for each decision, where # refers to that decision number. The number of decisions to run is provided as a command-line parameter.

The data consumer (aggregate_wma.php) also takes as a command-line parameter the number of decisions, looks for the corresponding set of wma_#.txt files (for i=1..#). It outputs wma.csv after the analysis (beware of clobbering data!).

I've provided a run script (run.sh) that takes as a command-line parameter the number of decisions to run. It calls the producer, then consumer, then cleans up the text files. It's a bash script, and so should run fine on Mac/Linux.
