# sphericalkmeans
An implementation for Spherical K Means Clustering on Documents

The corresponding data set is the Reuter 21578 articles data set.

Current implementation involves cleaning up the data in 5 steps (the Python script) and then to cluster the data.

Future developments will involve other clustering algorithms like DBScan

To run the algorithm, download the repository and the data set. The Dataset should be in the same directory as the code. 

cd to the code and give the commands "make preprocess" from terminal to preprocess the data set. 

"make kmeans" will make the java file for k means.

The command line for the actual clustering will be of the form "java sphkmeans #REP*.csv reuters.class #nooftrials #noofclusters #outputfile


#REP is used to signify representation. Pass one of the following representations as commandline args - bag.csv, char3.csv, char5.csv or char7.csv
