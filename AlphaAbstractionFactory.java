package org.processmining.alphaminer.abstractions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.alphaminer.abstractions.impl.AlphaClassicAbstractionImpl;
import org.processmining.alphaminer.abstractions.impl.AlphaPlusAbstractionImpl;
import org.processmining.alphaminer.abstractions.impl.AlphaPlusPlusAbstractionImpl;
import org.processmining.alphaminer.abstractions.impl.AlphaRobustAbstractionImpl;
import org.processmining.alphaminer.abstractions.impl.AlphaSharpAbstractionImpl;
import org.processmining.alphaminer.parameters.AlphaRobustMinerParameters;
import org.processmining.framework.util.Pair;
import org.processmining.logabstractions.factories.ActivityCountAbstractionFactory;
import org.processmining.logabstractions.factories.CausalAbstractionFactory;
import org.processmining.logabstractions.factories.DirectlyFollowsAbstractionFactory;
import org.processmining.logabstractions.factories.LongTermFollowsAbstractionFactory;
import org.processmining.logabstractions.factories.LoopAbstractionFactory;
import org.processmining.logabstractions.factories.StartEndActivityFactory;
import org.processmining.logabstractions.models.CausalPrecedenceAbstraction;
import org.processmining.logabstractions.models.CausalSuccessionAbstraction;
import org.processmining.logabstractions.util.XEventClassUtils;



public class AlphaAbstractionFactory {
	
	//random sampling
	//https://www.geeksforgeeks.org/randomly-select-items-from-a-list-in-java/
	public static void getRandomElement(XLog log, int numToDelete) {
		Random random = new Random(); 
		for(int i = 0;i < numToDelete;i ++) {
			int randomIndex = random.nextInt(log.size());
			log.remove(randomIndex);
			
			//System.out.println(log.size());
			//System.out.println(randomIndex);
		}
	}
	
	public static AlphaClassicAbstraction<XEventClass> createAlphaClassicAbstraction(XLog log,
			XEventClassifier classifier) {
		Scanner keyboard = new Scanner(System.in);
		//System.out.println("Number of traces to be deleted: ");
		//int numDelete = Integer.parseInt(keyboard.nextLine());
		//random sample
		//getRandomElement(log, numDelete);
		
		XEventClasses classes = XEventClasses.deriveEventClasses(classifier, log);
		double[][] dfa = new double[classes.size()][classes.size()]; // directly follows
		//double[][] completeMatrix = new double[classes.size()][classes.size()];
		/*Scanner matrixReader = null;
		try {
			matrixReader = new Scanner(new File("matrix.txt"));
		} catch (FileNotFoundException e1) {
			
			e1.printStackTrace();
		}*/
		
		
		double[] starts = new double[classes.size()]; // start activity
		double[] ends = new double[classes.size()]; // end activity
		double[] lol = new double[classes.size()]; // length one loop
		for (XTrace trace : log) {
			if (!trace.isEmpty()) {
				starts[classes.getClassOf(trace.get(0))
						.getIndex()] += StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN;
				ends[classes.getClassOf(trace.get(trace.size() - 1))
						.getIndex()] += StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN;
				for (int i = 0; i < trace.size() - 1; i++) {
					XEventClass from = classes.getClassOf(trace.get(i));
					XEventClass to = classes.getClassOf(trace.get(i + 1));
					dfa[from.getIndex()][to.getIndex()] = DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
					if (from.equals(to)) {
						lol[from.getIndex()] = LoopAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
					}
				}
			}
		}
		for(double[] rc: dfa){  //using for each loop
			System.out.println(Arrays.toString(rc));
	    }
		
		/*for(int i = 0;i < classes.size();i ++) {
			String currentLine = matrixReader.nextLine();
			for(int a = 0;a < classes.size();a ++) {
				completeMatrix[i][a] = Double.parseDouble(currentLine.split(",")[a]);
			}
		}*/
		
		/*int numOfOriginDirect = 0;
		for(int i = 0;i < classes.size();i ++) {
			for(int a = 0;a < classes.size();a ++) {
				if(dfa[i][a] == 1.0) {
					numOfOriginDirect ++;
				}
				
			}
		}
		System.out.println("Origin: " + numOfOriginDirect);
		double TP = 0;
		double FP = 0;
		double FN = 0;
		double TN = 0;
		for(int i = 0;i < classes.size();i ++) {
			for(int a = 0;a < classes.size();a ++) {
				if(dfa[i][a] == 1.0 && completeMatrix[i][a] == 1) {
					TP ++;
				}
				else if(dfa[i][a] == 0 && completeMatrix[i][a] == 0) {
					TN ++;
				}
				else if(dfa[i][a] == 1 && completeMatrix[i][a] == 0) {
					FP ++;
				}
				else if(dfa[i][a] == 0 && completeMatrix[i][a] == 1) {
					FN ++;
				}
				
				
			}
		}
		double recall = TP / (TP + FN);
		double precision = TP / (TP + FP);
		double accuracy = (TP + TN) / (TP + FP + FN + TN);
		double fScore = 2 * recall * precision / (recall + precision);*/
		
		/*System.out.println("TP: " + TP);
		System.out.println("FP: " + FP);
		System.out.println("FN: " + FN);
		System.out.println("TN: " + TN);
		System.out.println("precision: " + precision);
		System.out.println("recall: " + recall);
		System.out.println("accuracy: " + accuracy);
		System.out.println("F: " + fScore);*/
		
		identifyParallelActivities(dfa, log, classifier);
		System.out.println("Number of traces in the sample: " + log.size());
		System.out.println("Use new method? (type yes if you want to use the new method)");
		int numOfDuplicate = 0;
		if(keyboard.nextLine().equals("yes")) {
			System.out.println("Please add parallel pairs: ");
			System.out.println("format: activityname1,activityname2");
			System.out.println("Press enter after you have entered each pair, and type finish after you have entered all pairs.");
			Scanner s = null;
			try {
				//s = new Scanner(new File("model1input.txt"));
				s = new Scanner(System.in);
			}
			catch(Exception e) {
				
			}
			
			while(s.hasNextLine()) {
				String line = s.nextLine();
				if(line.equals("finish")) {
					break;
				}
				String activity1 = line.split(",")[0];
				String activity2 = line.split(",")[1];
				int a1 = classes.getByIdentity(activity1).getIndex();
				int a2 = classes.getByIdentity(activity2).getIndex();
				if(dfa[a1][a2] == DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN 
						&& dfa[a2][a1] == DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN) {
					System.out.println("Relation exists");
					numOfDuplicate ++;
					continue;
				}
				dfa[a1][a2] = DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
				dfa[a2][a1] = DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
			}
			Graph graph = BuildParallelGraph(dfa, log, classifier);
			System.out.println("Size of graph: " + graph.nodes.size());
			ArrayList<Graph> subgraphs = new ArrayList<>();
			for(Node i:graph.nodes) {
				if(i.visited == false) {
					Graph subgraph = new Graph();
					subgraphs.add(subgraph);
					dfs(i, subgraph);
				}			
			}
			System.out.println("Number of subgraphs: " + subgraphs.size());
			GroupSet groupSet = buildGroupSet(subgraphs);
			for(int i = 0;i < groupSet.groups.size();i ++) {
				for(Node a:groupSet.groups.get(i).nodes) {
					System.out.print(a.activity + " ");
				}
			     System.out.println();
			}
			for (XTrace trace : log) {
				for(ActivityGroup current:groupSet.groups) {
					Graph subgraph = subgraphs.get(current.subgraphIndex);
					HashSet<Node> SubgraphNodes = new HashSet<Node>(subgraph.nodes);
					SubgraphNodes.removeAll(current.nodes);
					HashSet<String> activities = new HashSet<>();
					for(Node node:SubgraphNodes) {
						activities.add(node.activity);
					}
					int prevIndex = -1;
					for (int i = 0; i < trace.size(); i++) {
						XEventClass currentActivity = classes.getClassOf(trace.get(i));
						if(!activities.contains(currentActivity.getId())) {
							if(prevIndex != -1) {
								dfa[prevIndex][currentActivity.getIndex()] = DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
							}
							
							prevIndex = currentActivity.getIndex();
						}
					}
					
				}
			}
		}
		
		/*for(double[] rc: dfa){  //using for each loop
			System.out.println(Arrays.toString(rc));
	    }*/
		int numOfAfterDirect = 0;
		for(int i = 0;i < classes.size();i ++) {
			for(int a = 0;a < classes.size();a ++) {
				if(dfa[i][a] == 1.0) {
					numOfAfterDirect ++;
				}
				
			}
		}
		/*System.out.println("Duplicate parallel: " + numOfDuplicate);
		System.out.println("After: " + numOfAfterDirect);
		System.out.println("New: " + (numOfAfterDirect - numOfOriginDirect));
		int numOfIncorrect = 0;
		for(int i = 0;i < classes.size();i ++) {
			for(int a = 0;a < classes.size();a ++) {
				if(dfa[i][a] == 1.0 && completeMatrix[i][a] == 0) {
					numOfIncorrect ++;
				}
				
			}
		}
		
		TP = 0;
		FP = 0;
		FN = 0;
		TN = 0;
		for(int i = 0;i < classes.size();i ++) {
			for(int a = 0;a < classes.size();a ++) {
				if(dfa[i][a] == 1.0 && completeMatrix[i][a] == 1) {
					TP ++;
				}
				else if(dfa[i][a] == 0 && completeMatrix[i][a] == 0) {
					TN ++;
				}
				else if(dfa[i][a] == 1 && completeMatrix[i][a] == 0) {
					FP ++;
				}
				else if(dfa[i][a] == 0 && completeMatrix[i][a] == 1) {
					FN ++;
				}
				
				
			}
		}
		recall = TP / (TP + FN);
		precision = TP / (TP + FP);
		accuracy = (TP + TN) / (TP + FP + FN + TN);
		fScore = 2 * recall * precision / (recall + precision);
		
		System.out.println("TP: " + TP);
		System.out.println("FP: " + FP);
		System.out.println("FN: " + FN);
		System.out.println("TN: " + TN);
		System.out.println("precision: " + precision);
		System.out.println("recall: " + recall);
		System.out.println("accuracy: " + accuracy);
		System.out.println("F: " + fScore);*/
		//System.out.println("FP: " + numOfIncorrect);
		//System.out.println("TP: " + (numOfAfterDirect - numOfOriginDirect - numOfIncorrect));
		//double precision = (numOfAfterDirect - numOfOriginDirect - numOfIncorrect) / (numOfAfterDirect - numOfOriginDirect - numOfIncorrect + numOfIncorrect);
		//System.out.println("Precision: " + precision);
		XEventClass[] arr = XEventClassUtils.toArray(classes);
		return new AlphaClassicAbstractionImpl<>(arr,
				DirectlyFollowsAbstractionFactory.constructDirectlyFollowsAbstraction(arr, dfa,
						DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN),
				StartEndActivityFactory.constructStartActivityAbstraction(arr, starts,
						StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN),
				StartEndActivityFactory.constructEndActivityAbstraction(arr, ends,
						StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN),
				LoopAbstractionFactory.constructLengthOneLoopAbstraction(arr, lol,
						LoopAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN));
	}
	
	public static void identifyParallelActivities(double[][] matrix, XLog log,
			XEventClassifier classifier) {
		XEventClasses classes = XEventClasses.deriveEventClasses(classifier, log);
		
		int counter = 1;
		for(int a = 0;a < matrix.length;a ++) {
			for(int b = a;b < matrix[a].length;b ++) {
				if(matrix[a][b] == DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN 
						&& matrix[b][a] == DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN) {
					String activity1 = classes.getByIndex(a).getId();
					String activity2 = classes.getByIndex(b).getId();
					System.out.println("Parallel pair " + counter + ": " + activity1 + "     " + activity2);
					counter ++;
				}
			
			}
		}
		
		//Scanner keyboard = new Scanner(System.in);
		//while(keyboard.hasNext()) {
			
		//}
		
	} 
	
	public static Graph BuildParallelGraph(double[][] matrix, XLog log,
			XEventClassifier classifier) {
		
		XEventClasses classes = XEventClasses.deriveEventClasses(classifier, log);
		Graph graph = new Graph();
		
		for(int i = 0;i < classes.size();i++) {
			String activity = classes.getByIndex(i).getId();
			Node node = new Node(activity);
			graph.nodes.add(node);
		}
		
		for(int a = 0;a < matrix.length;a ++) {
			for(int b = 0;b < matrix[a].length;b ++) {
				if(matrix[a][b] == DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN 
						&& matrix[b][a] == DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN) {
					Node node1 = graph.nodes.get(a);
					Node node2 = graph.nodes.get(b);
					node1.connections.add(node2);
					node2.connections.add(node1);
				}
			
			}
		}
		
		//remove nodes without any edges
		for(int i = 0;i < graph.nodes.size();i ++) {
			if(graph.nodes.get(i).connections.size() == 0) {
				graph.nodes.remove(graph.nodes.get(i));
				i --;
			}
		}
		
		return graph;
	}
	
	public static void dfs(Node node, Graph graph)
	{
		graph.nodes.add(node);
		HashSet<Node> neighbours=node.connections;
        node.visited=true;
        
		for (Node n:neighbours) {
			if(n!=null && !n.visited)
			{
				dfs(n, graph);
			}
		}
	}
	
	/*public static GroupSet buildGroupSet(ArrayList<Graph> subgraphs) {
		
		GroupSet groupSet = new GroupSet();
		int i = 0;
		for(Graph graph:subgraphs) {
			for(Node node:graph.nodes) {
				ActivityGroup group = new ActivityGroup();
				group.subgraphIndex = i;
				group.addNode(node);
				HashSet<Node> allNodes = new HashSet<>(graph.nodes);
				allNodes.removeAll(node.connections);
				group.nodes.addAll(allNodes);
				if(group.verify()) {
					groupSet.groups.add(group);
				}
			}
			i ++;
		}
		
		groupSet.removeIdentical();
		return groupSet;
	}*/
	
	public static GroupSet buildGroupSet(ArrayList<Graph> subgraphs) {
		GroupSet groupSet = new GroupSet();
		int i = 0;
		for(Graph graph:subgraphs) {
			for(Node current:graph.nodes) {
				HashSet<Node> mask = new HashSet<>(graph.nodes);
				mask.removeAll(current.connections);
				current.connections = mask;
			}
			
			Bron_Kerbosch calculator = new Bron_Kerbosch(graph);
			Collection<Set<Node>> groups = calculator.getAllMaximalCliques();
			for(Set<Node> current:groups) {
				ActivityGroup group = new ActivityGroup();
				group.subgraphIndex = i;
				groupSet.groups.add(group);
				for(Node a:current) {
					group.addNode(a);
				}
			}
			
			i ++;
		}
		
		groupSet.removeIdentical();
		return groupSet;
	}
	
	public static AlphaPlusAbstraction<XEventClass> createAlphaPlusAbstraction(XLog log, XEventClassifier classifier) {
		XEventClasses classes = XEventClasses.deriveEventClasses(classifier, log);
		AlphaClassicAbstraction<XEventClass> aca = createAlphaClassicAbstraction(log, classifier);
		Pair<XEventClass[], int[]> reducedClasses = XEventClassUtils
				.stripLengthOneLoops(XEventClassUtils.toArray(classes), aca.getLengthOneLoopAbstraction());
		double[][] ltl = new double[classes.getClasses().size()][classes.getClasses().size()];
		double[][] dfaLf = new double[reducedClasses.getFirst().length][reducedClasses.getFirst().length];
		double[][] ltlLf = new double[reducedClasses.getFirst().length][reducedClasses.getFirst().length];
		double[] startsLf = new double[reducedClasses.getFirst().length];
		double[] endsLf = new double[reducedClasses.getFirst().length];
		for (XTrace trace : log) {
			if (!trace.isEmpty()) {
				XEventClass first, second, third, firstLf, secondLf, thirdLf, current;
				first = second = third = firstLf = secondLf = thirdLf = current = null;
				for (int i = 0; i < trace.size(); i++) {
					current = classes.getClassOf(trace.get(i));
					if (i == 0) {
						first = current;
					} else if (i == 1) {
						second = current;
					} else if (i == 2) {
						third = current;
					} else {
						// shift
						first = second;
						second = third;
						third = current;
					}
					if (first != null && second != null && third != null && first.equals(third)) {
						ltl[first.getIndex()][second.getIndex()] = LoopAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
					}
					if (aca.getLengthOneLoopAbstraction().holds(current.getIndex()))
						continue;
					if (firstLf == null) {
						firstLf = current;
						startsLf[reducedClasses.getSecond()[firstLf
								.getIndex()]] = StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN;
						continue;
					}
					if (secondLf == null) {
						secondLf = current;
						dfaLf[reducedClasses.getSecond()[firstLf.getIndex()]][reducedClasses.getSecond()[secondLf
								.getIndex()]] = DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
						continue;
					}
					if (thirdLf != null) {
						firstLf = secondLf;
						secondLf = thirdLf;
					}
					thirdLf = current;
					dfaLf[reducedClasses.getSecond()[secondLf.getIndex()]][reducedClasses.getSecond()[thirdLf
							.getIndex()]] = DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
					if (firstLf.equals(thirdLf)) {
						ltlLf[reducedClasses.getSecond()[firstLf.getIndex()]][reducedClasses.getSecond()[secondLf
								.getIndex()]] = LoopAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
					}
				}
				if (thirdLf != null) {
					endsLf[reducedClasses.getSecond()[thirdLf
							.getIndex()]] = StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN;
				} else if (secondLf != null) {
					endsLf[reducedClasses.getSecond()[secondLf
							.getIndex()]] = StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN;
				} else if (firstLf != null) {
					endsLf[reducedClasses.getSecond()[firstLf
							.getIndex()]] = StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN;
				}
			}
		}
		XEventClass[] arr = XEventClassUtils.toArray(classes);
		return new AlphaPlusAbstractionImpl<>(aca,
				LoopAbstractionFactory.constructLengthTwoLoopAbstraction(arr, ltl,
						LoopAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN),
				reducedClasses,
				DirectlyFollowsAbstractionFactory.constructDirectlyFollowsAbstraction(reducedClasses.getFirst(), dfaLf,
						DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN),
				StartEndActivityFactory.constructStartActivityAbstraction(reducedClasses.getFirst(), startsLf,
						StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN),
				StartEndActivityFactory.constructEndActivityAbstraction(reducedClasses.getFirst(), endsLf,
						StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN),
				LoopAbstractionFactory.constructLengthTwoLoopAbstraction(reducedClasses.getFirst(), ltlLf,
						LoopAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN));

	}

	public static AlphaPlusPlusAbstraction<XEventClass> createAlphaPlusPlusAbstraction(XLog log,
			XEventClassifier classifier) {
		AlphaPlusAbstraction<XEventClass> apa = createAlphaPlusAbstraction(log, classifier);
		CausalPrecedenceAbstraction<XEventClass> cpa = CausalAbstractionFactory
				.constructAlphaPlusPlusCausalPrecedenceAbstraction(apa.getLengthOneLoopFreeCausalRelationAbstraction(),
						apa.getLengthOneLoopFreeUnrelatedAbstraction());
		CausalSuccessionAbstraction<XEventClass> csa = CausalAbstractionFactory
				.constructAlphaPlusPlusCausalSuccessionAbstraction(apa.getLengthOneLoopFreeCausalRelationAbstraction(),
						apa.getLengthOneLoopFreeUnrelatedAbstraction());

		return new AlphaPlusPlusAbstractionImpl<XEventClass>(apa, cpa, csa,
				LongTermFollowsAbstractionFactory.constructAlphaPlusPlusLengthOneLoopFreeLongTermFollowsAbstraction(log,
						XEventClasses.deriveEventClasses(classifier, log),
						apa.getLengthOneLoopFreeDirectlyFollowsAbstraction(), cpa, csa,
						apa.getLengthOneLoopAbstraction()));
	}

	public static AlphaSharpAbstraction<XEventClass> createAlphaSharpAbstraction(XLog log,
			XEventClassifier classifier) {
		return new AlphaSharpAbstractionImpl<>(createAlphaPlusAbstraction(log, classifier));
	}
	
	// NEW FOR ROBUST
	public static AlphaRobustAbstraction<XEventClass> createAlphaRobustAbstraction(XLog log,
			XEventClassifier classifier, AlphaRobustMinerParameters parameters) {
		XEventClasses classes = XEventClasses.deriveEventClasses(classifier, log);
		double[][] dfa = new double[classes.size()][classes.size()]; // directly follows (count)
		double[] starts = new double[classes.size()]; // start activity
		double[] ends = new double[classes.size()]; // end activity
		double[] lol = new double[classes.size()]; // length one loop
		double[] ac = new double[classes.size()]; // activity count
		for (XTrace trace : log) {
			if (!trace.isEmpty()) {
				starts[classes.getClassOf(trace.get(0))
						.getIndex()] += StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN;
				ends[classes.getClassOf(trace.get(trace.size() - 1))
						.getIndex()] += StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN;
				for (int i = 0; i < trace.size() - 1; i++) {
					XEventClass from = classes.getClassOf(trace.get(i));
					XEventClass to = classes.getClassOf(trace.get(i + 1));
					dfa[from.getIndex()][to.getIndex()] += DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
					if (from.equals(to)) {
						lol[from.getIndex()] = LoopAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
					}
					ac[from.getIndex()] += DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
					if (i == trace.size() - 2) { // count final activity as well
						ac[to.getIndex()] += DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN;
					}
				}
			}
		}
		XEventClass[] arr = XEventClassUtils.toArray(classes);
		return new AlphaRobustAbstractionImpl<>(arr,
				DirectlyFollowsAbstractionFactory.constructDirectlyFollowsAbstraction(arr, dfa,
						DirectlyFollowsAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN),
				StartEndActivityFactory.constructStartActivityAbstraction(arr, starts,
						StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN),
				StartEndActivityFactory.constructEndActivityAbstraction(arr, ends,
						StartEndActivityFactory.DEFAULT_THRESHOLD_BOOLEAN),
				LoopAbstractionFactory.constructLengthOneLoopAbstraction(arr, lol,
						LoopAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN),
				ActivityCountAbstractionFactory.constructActivityCountAbstraction(arr, ac,
						ActivityCountAbstractionFactory.DEFAULT_THRESHOLD_BOOLEAN),
				parameters.getCausalThreshold(),
				parameters.getNoiseThresholdLeastFreq(),
				parameters.getNoiseThresholdMostFreq());
	}
	// END NEW

}
