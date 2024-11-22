package edu.uiowa.cs.warp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code ReliabilityAnalysis} class provides methods to analyze and compute the end-to-end reliability
 * of message transmissions in flows for the WARP system. It calculates the number of transmission attempts
 * required per link in a flow to achieve a specified end-to-end reliability, given the minimum packet reception rate.
 *
 * <p>
 * The end-to-end reliability for each flow from source to sink is computed iteratively using the following process:
 * </p>
 * <ol>
 *   <li>The source node of the flow has an initial message reception probability of 1.0 at time zero.
 *       All other nodes have an initial probability of 0.0.</li>
 *   <li>For each time slot, and for each link (source node to sink node) in the flow, the probability that
 *       the sink node has received the message is updated using:
 *       <br>{@code NewSinkNodeState = (1 - M) * PrevSinkNodeState + M * PrevSourceNodeState}
 *       <br>where {@code M} is the minimum packet reception rate on that link.</li>
 * </ol>
 * <p>
 * This class provides methods to compute the number of transmission attempts required per link to meet the specified
 * end-to-end reliability, either by specifying a desired end-to-end reliability and minimum packet reception rate,
 * or by specifying a fixed number of faults per link.
 * </p>
 *
 * @author sgoddard
 * @version 1.0
 */
public class ReliabilityAnalysis {
    /** The desired end-to-end reliability for the flow. */
    private Double e2e = 0.99;

    /** The minimum packet reception rate (probability) for each link in the flow. */
    private Double minPacketReceptionRate = 0.9;

    /** The fixed number of faults allowed per link in the flow. */
    private Integer numFaults = 0;
    private Boolean numFaultModel = false;
    
    /**
     * Constructs a {@code ReliabilityAnalysis} object with the specified end-to-end reliability and minimum packet reception rate.
     *
     * @param e2e the desired end-to-end reliability (between 0.0 and 1.0)
     * @param minPacketReceptionRate the minimum packet reception rate for each link (between 0.0 and 1.0)
     */
    public ReliabilityAnalysis(Double e2e, Double minPacketReceptionRate) {
        this.e2e = e2e;
        this.minPacketReceptionRate = minPacketReceptionRate;
    }

    /**
     * Constructs a {@code ReliabilityAnalysis} object with a specified fixed number of faults per link.
     *
     * @param numFaults the fixed number of faults allowed per link in the flow
     */
    public ReliabilityAnalysis(Integer numFaults) {
        this.numFaults = numFaults;
        numFaultModel = true;
    }

    /**
     * Calculates the number of transmission attempts required per link in the given flow,
     * as well as the total transmission cost to meet the specified end-to-end reliability.
     * The calculation method depends on whether a fixed number of faults per link is specified.
     *
     * @param flow the {@code Flow} object representing the sequence of nodes
     * @return an {@code ArrayList<Integer>} where each element represents the number of transmission attempts for each link,
     *         and the last element represents the total transmission cost for the flow
     */
    public ArrayList<Integer> numTxPerLinkAndTotalTxCost(Flow flow) {
        return numFaultModel ? getFixedTxPerLinkAndTotalTxCost(flow) : numTxAttemptsPerLinkAndTotalTxAttempts(flow);
    }

    /**
     * Verifies whether the calculated reliabilities meet the specified end-to-end reliability requirements.
     *
     * @return {@code true} if the reliabilities meet the requirements; {@code false} otherwise
     */
    public Boolean verifyReliabilities() {
        // TODO: Implement this method
        return true;
    }

    /**
     * Retrieves the reliability table containing the computed reliability values for each node in the flows over time.
     *
     * @param program the {@code Program} containing the nodes to calculate reliability values for
     * @return a {@code ReliabilityTable} representing the reliability values
     */
    public ReliabilityTable getReliabilities(Program program) {
        // Retrieve the minimum packet reception rate (M)
        double M = minPacketReceptionRate;

        // Initialize the ReliabilityTable to store the reliability values over time
        ReliabilityTable table = new ReliabilityTable();

        // Convert the program to a WorkLoad to get flows and nodes
        WorkLoad workLoad = program.toWorkLoad();
        ArrayList<String> flowsInOrder = workLoad.getFlowNamesInPriorityOrder();

        // Map to store nodes in each flow
        Map<String, List<String>> nodesInFlows = new HashMap<>();
        for (String flow : flowsInOrder) {
            nodesInFlows.put(flow, Arrays.asList(workLoad.getNodesInFlow(flow)));
        }

        // Initialize the defaultRow with initial reliability values
        ReliabilityRow defaultRow = new ReliabilityRow();
        // Map to store the starting index of each flow in the defaultRow
        Map<String, Integer> flowStartIndexes = new HashMap<>();

        // Build the defaultRow and flowStartIndexes
        int overallIndex = 0;
        for (Map.Entry<String, List<String>> entry : nodesInFlows.entrySet()) {
            String flowName = entry.getKey();
            List<String> nodesInFlow = entry.getValue();

            // The source node starts with reliability 1.0
            defaultRow.add(1.0);
            flowStartIndexes.put(flowName, overallIndex++);

            // For the rest of the nodes in the flow, set initial reliability to 0.0
            for (int i = 1; i < nodesInFlow.size(); i++) {
                defaultRow.add(0.0);
                overallIndex++;
            }
        }

        // Create an instance of WarpDSL to parse instruction parameters
        WarpDSL dsl = new WarpDSL();
        // Get the schedule from the program
        ProgramSchedule schedule = program.getSchedule();

        // Get the number of time slots and nodes
        int numRows = schedule.getNumRows();
        int numColumns = schedule.getNumColumns();

        // Iterate over each time slot
        for (int timeSlot = 0; timeSlot < numRows; timeSlot++) {
            // Get the previous reliability row or use defaultRow if first time slot
            ReliabilityRow prevRow = (timeSlot > 0) ? table.get(timeSlot - 1) : defaultRow;
            // Clone the previous row to update it for the current time slot
            ReliabilityRow currRow = new ReliabilityRow(prevRow);

            // Iterate over each node (column) in the schedule
            for (int nodeColumn = 0; nodeColumn < numColumns; nodeColumn++) {
                // Get the instruction parameters for the instruction at this time slot and node
                var parameters = dsl.getInstructionParameters(schedule.get(timeSlot, nodeColumn));

                // Iterate over the parameters
                for (var parameter : parameters) {
                    String instructionName = parameter.getName(); // e.g., "push", "pull", "wait", etc.
                    if (instructionName.equals("push") || instructionName.equals("pull")) {
                        String flowName = parameter.getFlow();
                        if (!nodesInFlows.containsKey(flowName)) {
                            continue; // Skip if the flow is not recognized
                        }
                        List<String> nodesInFlow = nodesInFlows.get(flowName);

                        int flowStartIndex = flowStartIndexes.get(flowName);
                        int srcNodeFlowIndex = nodesInFlow.indexOf(parameter.getSrc());
                        int snkNodeFlowIndex = nodesInFlow.indexOf(parameter.getSnk());
                        if (srcNodeFlowIndex == -1 || snkNodeFlowIndex == -1) {
                            continue; // Skip if source or sink node is not found
                        }

                        int srcNodeIndex = flowStartIndex + srcNodeFlowIndex;
                        int snkNodeIndex = flowStartIndex + snkNodeFlowIndex;

                        // Get the previous reliability values
                        double prevSrcReliability = prevRow.get(srcNodeIndex);
                        double prevSnkReliability = prevRow.get(snkNodeIndex);

                        // Handle the case when a new packet is injected at the source node
                        if (instructionName.equals("push") && srcNodeFlowIndex == 0) {
                            // Reset the source node's reliability to 1.0 (new packet injected)
                            prevSrcReliability = 1.0;
                            currRow.set(srcNodeIndex, 1.0);

                            // Reset sink nodes' reliability to 0.0 for this flow (starting new transmission)
                            for (int k = 1; k < nodesInFlow.size(); k++) {
                                int nodeIndexInRow = flowStartIndex + k;
                                currRow.set(nodeIndexInRow, 0.0);
                            }
                            // Since sink node reliability is reset, set prevSnkReliability to 0.0
                            prevSnkReliability = 0.0;
                        }

                        // Update the sink node's reliability using the formula
                        double newSnkReliability = (1 - M) * prevSnkReliability + M * prevSrcReliability;
                        currRow.set(snkNodeIndex, newSnkReliability);
                    }
                }
            }
            // Add the updated row to the table
            table.add(currRow);
        }

        return table;
    }

    /**
     * Calculates the number of transmission attempts per link and the total transmission cost for a flow,
     * assuming a fixed number of faults per link.
     *
     * @param flow the {@code Flow} for which the transmission costs are calculated
     * @return an {@code ArrayList<Integer>} containing the number of transmission attempts per link,
     *         and the total transmission cost as the last element
     */
    private ArrayList<Integer> getFixedTxPerLinkAndTotalTxCost(Flow flow) {
        var nodesInFlow = flow.nodes;
        var nNodesInFlow = nodesInFlow.size();
        ArrayList<Integer> txArrayList = new ArrayList<>();

        // Each node will have at most numFaults + 1 transmissions.
        for (int i = 0; i < nNodesInFlow; i++) {
            txArrayList.add(numFaults + 1);
        }

        // Compute the maximum number of transmission attempts
        var numEdgesInFlow = nNodesInFlow - 1;
        var maxFaultsInFlow = numEdgesInFlow * numFaults;
        txArrayList.add(numEdgesInFlow + maxFaultsInFlow);

        return txArrayList;
    }

    /**
     * Calculates the number of transmission attempts required per link in the given flow to meet the specified end-to-end reliability,
     * based on the minimum packet reception rate. It uses an iterative process to simulate message propagation through the flow.
     *
     * @param flow the {@code Flow} being analyzed
     * @return an {@code ArrayList<Integer>} containing the number of transmission attempts per link,
     *         and the total transmission cost as the last element
     */
    private ArrayList<Integer> numTxAttemptsPerLinkAndTotalTxAttempts(Flow flow) {
        // Retrieve the list of nodes in the flow and the total number of nodes
        var nodesInFlow = flow.nodes;
        var nNodesInFlow = nodesInFlow.size();

        // Initialize a list to track the number of transmission attempts (nPushes) for each link in the flow
        // The last element will be used to store the total transmission cost
        var nPushes = new ArrayList<Integer>(Collections.nCopies(nNodesInFlow + 1, 0));

        // Calculate the number of hops (links) in the flow
        var nHops = nNodesInFlow - 1;

        // Calculate the minimum reliability needed per link to meet the end-to-end reliability requirement
        Double minLinkReliabilityNeeded = Math.max(e2e, Math.pow(e2e, (1.0 / (double) nHops)));
        // Use Math.max to handle cases where e2e == 1.0, avoiding potential rounding errors

        // Initialize the reliability window, which tracks the reliability state of each node over time
        var reliabilityWindow = new ReliabilityTable();

        // Initialize the first row of the reliability window with 0.0 for all nodes
        var newReliabilityRow = new ReliabilityRow(nNodesInFlow, 0.0);
        reliabilityWindow.add(newReliabilityRow);

        // Set the initial reliability state: the source node has a probability of 1.0 at time zero
        ReliabilityRow currentReliabilityRow = new ReliabilityRow(newReliabilityRow);
        currentReliabilityRow.set(0, 1.0);  // P(packet at source node) = 1.0

        // Initialize the end-to-end reliability state; initially, the sink node has not received the packet
        Double e2eReliabilityState = currentReliabilityRow.get(nNodesInFlow - 1);

        // Iteratively compute the reliability state until the end-to-end reliability requirement is met
        while (e2eReliabilityState < e2e) {
            // Clone the current reliability row to use as the previous state in this iteration
            var prevReliabilityRow = new ReliabilityRow(currentReliabilityRow);

            // For each link in the flow, update the reliability state of the sink node
            for (int nodeIndex = 0; nodeIndex < (nNodesInFlow - 1); nodeIndex++) {
                var flowSrcNodeIndex = nodeIndex;
                var flowSnkNodeIndex = nodeIndex + 1;

                var prevSrcNodeState = prevReliabilityRow.get(flowSrcNodeIndex);
                var prevSnkNodeState = prevReliabilityRow.get(flowSnkNodeIndex);
                Double nextSnkState;

                // Determine if a transmission ("push") is needed on this link
                // A push is needed if the sink node's reliability is below the minimum required,
                // and the source node has a non-zero probability of having the packet
                if ((prevSnkNodeState < minLinkReliabilityNeeded) && prevSrcNodeState > 0) {
                    // Update the sink node's reliability state using the message success probability equation
                    nextSnkState = ((1.0 - minPacketReceptionRate) * prevSnkNodeState) + (minPacketReceptionRate * prevSrcNodeState);
                    // Increment the number of pushes (transmissions) for this link
                    nPushes.set(nodeIndex, nPushes.get(nodeIndex) + 1);
                } else {
                    // If no push is needed, the sink node's reliability state remains the same
                    nextSnkState = prevSnkNodeState;
                }

                // Update the current reliability row with the new sink node state
                currentReliabilityRow.set(flowSnkNodeIndex, nextSnkState);

                // Ensure the source node's reliability state is carried forward if it has increased
                if (currentReliabilityRow.get(flowSrcNodeIndex) < prevReliabilityRow.get(flowSrcNodeIndex)) {
                    currentReliabilityRow.set(flowSrcNodeIndex, prevReliabilityRow.get(flowSrcNodeIndex));
                }
            }

            // Update the end-to-end reliability state based on the sink node's reliability
            e2eReliabilityState = currentReliabilityRow.get(nNodesInFlow - 1);

            // Add the current reliability row to the reliability window
            reliabilityWindow.add(new ReliabilityRow(currentReliabilityRow));
        }

        // The total transmission cost is the number of iterations (time slots) in the reliability window
        nPushes.set(nNodesInFlow, reliabilityWindow.size());

        return nPushes;
    }
}