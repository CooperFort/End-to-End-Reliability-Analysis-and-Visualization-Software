package edu.uiowa.cs.warp;

import java.util.ArrayList;

/**
 * ReliabilityAnalysis analyzes the end-to-end reliability of messages transmitted in flows for the
 * WARP system.
 * <p>
 * 
 * Let M represent the Minimum Packet Reception Rate on an edge in a flow. The end-to-end
 * reliability for each flow, flow:src->sink, is computed iteratively as follows:<br>
 * (1)The flow:src node has an initial probability of 1.0 when it is released. All other initial
 * probabilities are 0.0. (That is, the reset of the nodes in the flow have an initial probability
 * value of 0.0.) <br>
 * (2) each src->sink pair probability is computed as NewSinkNodeState = (1-M)*PrevSnkNodeState +
 * M*PrevSrcNodeState <br>
 * This value represents the probability that the message as been received by the node SinkNode.
 * Thus, the NewSinkNodeState probability will increase each time a push or pull is executed with
 * SinkNode as a listener.
 * <p>
 * 
 * The last probability state value for any node is the reliability of the message reaching that
 * node, and the end-to-end reliability of a flow is the value of the last Flow:SinkNode
 * probability.
 * <p>
 * 
 * CS2820 Fall 2024 Project: Implement this class to compute the probabilities that comprise the
 * ReliablityMatrix, which is the core of the reliability visualization that is requested in Warp.
 * <p>
 * 
 * To do this, you will need to retrieve the program source, parse the instructions for each node,
 * in each time slot, to extract the src and snk nodes in the instruction and then apply the message
 * success probability equation defined in the project assignment.
 * <p>
 * 
 * I recommend using the getInstructionParameters method of the WarpDSL class to extract the src and
 * snk nodes from the instruction string in a program schedule time slot.
 * 
 * @author sgoddard
 * @version 1.8 Fall 2024
 *
 */

public class ReliabilityAnalysis {
    private double e2e;
    private double minPacketReceptionRate;
    private int numFaults;

    // Constructor with e2e and minPacketReceptionRate
    public ReliabilityAnalysis(Double e2e, Double minPacketReceptionRate) {
        this.e2e = (e2e != null) ? e2e : 0.99;
        this.minPacketReceptionRate = (minPacketReceptionRate != null) ? minPacketReceptionRate : 0.9;
    }

    // Constructor with numFaults
    public ReliabilityAnalysis(Integer numFaults) {
        this.numFaults = (numFaults != null) ? numFaults : 0;
    }

    /**
     * Calculates transmissions per link and total transmission cost.
     * @param flow The flow to analyze.
     * @return ArrayList containing transmission results.
     */
    public ArrayList<Integer> numTxPerLinkAndTotalTxCost(Flow flow) {
        ArrayList<Integer> txResults = new ArrayList<>();

        if (numFaults > 0) {
            txResults = getFixedTxPerLinkAndTotalTxCost(flow);
        } else {
            txResults = numTxAttemptsPerLinkAndTotalTxAttempts(flow, e2e, minPacketReceptionRate, false);
        }

        return txResults;
    }

    private ArrayList<Integer> getFixedTxPerLinkAndTotalTxCost(Flow flow) {
        int nNodesInFlow = flow.nodes.size();
        int numEdgesInFlow = nNodesInFlow - 1;
        int maxFaultsInFlow = numEdgesInFlow * numFaults;
        ArrayList<Integer> txPerLink = new ArrayList<>();

        for (int i = 0; i < nNodesInFlow; i++) {
            txPerLink.add(numFaults + 1);
        }

        txPerLink.add(numEdgesInFlow + maxFaultsInFlow);
        return txPerLink;
    }

    private ArrayList<Integer> numTxAttemptsPerLinkAndTotalTxAttempts(Flow flow, Double e2e, Double M, boolean optimizationRequested) {
        int nNodesInFlow = flow.nodes.size();
        int nHops = nNodesInFlow - 1;
        ArrayList<Integer> nPushesArrayList = new ArrayList<>();
        for (int i = 0; i <= nNodesInFlow; i++) {
            nPushesArrayList.add(0);
        }

        double minLinkReliabilityNeeded = Math.max(e2e, Math.pow(e2e, 1.0 / nHops));
        ArrayList<Double> currentReliabilityRow = new ArrayList<>(nNodesInFlow);
        for (int i = 0; i < nNodesInFlow; i++) {
            currentReliabilityRow.add(0.0);
        }
        currentReliabilityRow.set(0, 1.0);

        int timeSlot = 0;
        double e2eReliabilityState = 0.0;

        while (e2eReliabilityState < e2e) {
            ArrayList<Double> prevReliabilityRow = new ArrayList<>(currentReliabilityRow);

            for (int nodeIndex = 0; nodeIndex < nHops; nodeIndex++) {
                double prevSrcNodeState = prevReliabilityRow.get(nodeIndex);
                double prevSnkNodeState = prevReliabilityRow.get(nodeIndex + 1);
                double nextSnkState;

                if (prevSnkNodeState < minLinkReliabilityNeeded && prevSrcNodeState > 0) {
                    nextSnkState = (1 - M) * prevSnkNodeState + M * prevSrcNodeState;
                    nPushesArrayList.set(nodeIndex, nPushesArrayList.get(nodeIndex) + 1);
                } else {
                    nextSnkState = prevSnkNodeState;
                }

                currentReliabilityRow.set(nodeIndex + 1, nextSnkState);
            }

            e2eReliabilityState = currentReliabilityRow.get(nHops);
            timeSlot++;
        }

        nPushesArrayList.set(nNodesInFlow, timeSlot);
        return nPushesArrayList;
    }
}

