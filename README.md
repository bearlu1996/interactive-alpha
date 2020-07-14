# An interactive approach to discover process models from incomplete event logs with domain knowledge

For the Bron-Kerbosch algorithm, we modified the code directly from a public implementation on https://github.com/lizi-git/Bron-Kerbosch/blob/master/Bron-Kerbosch.java

We implement our algorithm directly on the code of the classical alpha algorithm in the ProM framework.

# How to use the code?
1. Import the AlphaMiner of the proM framework into eclipse
2. Copy all the .java file into the org.processmining.alphaminer.abstractions (Please replace the AlphaAbstractionFactory.java)
3. Run the proM from eclipse and use the classical alpha miner to discover models
4. Follow the instructions in the eclipse console, type "yes" if you want to use our algorithm
5. Type in the parallel activities in the following format and type "finish" after you have entered all of them:
activityname1,activityname2
...
finish
6. After you type "finish", you can see the model on the proM UI
