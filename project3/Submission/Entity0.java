public class Entity0 extends Entity {
    // This is the number of entitys in the network
    private static final int NUMENTITIES = NetworkSimulator.NUMENTITIES;

    // This is the to store the Distance Table for entity 0
    private static int[][] distanceTable0;

    // This is to store the mincost for entity 0
    private static int[] currentCost;

    // This is to store the original cost to entity0 direct neighbors
    private static int[] originalCost;

    // This is to store the path entity0 took for the minimum cost
    private static int[] through;

    // This is to store packets sent
    private static Packet outPTo1;
    private static Packet outPTo2;
    private static Packet outPTo3;

    // This is a flag for if entity0's distance vector has changed
    // Any value cahnge would flip the boolean value
    private boolean isChanged = false;

    // THis is a flag when link cost has changed
    private boolean linkCostChanged = false;

    // Perform any necessary initialization in the constructor
    public Entity0() {
        System.out.println();
        System.out.println("________________________________________________________________________");
        System.out.println("Entity0() is called at time: " + NetworkSimulator.time);
        System.out.println("Initializing the distance table: ");
        // initialize the distance table for entity 0
        distanceTable0 = new int[NUMENTITIES][NUMENTITIES];

        distanceTable0[0][0] = 0;
        distanceTable0[0][1] = 999;
        distanceTable0[0][2] = 999;
        distanceTable0[0][3] = 999;

        distanceTable0[1][0] = 1;
        distanceTable0[1][1] = 999;
        distanceTable0[1][2] = 999;
        distanceTable0[1][3] = 999;

        distanceTable0[2][0] = 3;
        distanceTable0[2][1] = 999;
        distanceTable0[2][2] = 999;
        distanceTable0[2][3] = 999;

        distanceTable0[3][0] = 7;
        distanceTable0[3][1] = 999;
        distanceTable0[3][2] = 999;
        distanceTable0[3][3] = 999;

        // debug print
        printDT();

        // record the current cost for entiity 0
        currentCost = new int[NUMENTITIES];
        currentCost[0] = distanceTable0[0][0];
        currentCost[1] = distanceTable0[1][0];
        currentCost[2] = distanceTable0[2][0];
        currentCost[3] = distanceTable0[3][0];

        // record the original cost to its direct neighbors
        originalCost = new int[NUMENTITIES];
        originalCost[0] = 0;
        originalCost[1] = 1;
        originalCost[2] = 3;
        originalCost[3] = 7;

        // recod the path
        through = new int[NUMENTITIES];
        through[0] = 0;
        through[1] = 1;
        through[2] = 2;
        through[3] = 3;

        // send out packets to entity 0's neighbor
        outPTo1 = new Packet(0, 1, currentCost);
        outPTo2 = new Packet(0, 2, currentCost);
        outPTo3 = new Packet(0, 3, currentCost);
        NetworkSimulator.toLayer2(outPTo1);
        printMessage(1);
        NetworkSimulator.toLayer2(outPTo2);
        printMessage(2);
        NetworkSimulator.toLayer2(outPTo3);
        printMessage(3);
    }

    private void printMessage(int neighbor) {
        // output message
        System.out.println();
        System.out.print("Sending packet: [" + currentCost[0]);
        System.out.print(", " + currentCost[1]);
        System.out.print(", " + currentCost[2]);
        System.out.print(", " + currentCost[3] + "]");
        System.out.print(" to Entity0's direct Neighbor: Entity" + neighbor);
    }

    // Handle updates when a packet is received. You will need to call
    // NetworkSimulator.toLayer2() with new packets based upon what you
    // send to update. Be careful to construct the source and destination of
    // the packet correctly. Read the warning in NetworkSimulator.java for more
    // details.
    public void update(Packet p) {
        // System.out.println();
        // System.out.println("________________________________________________________________________");
        System.out.println("Entity0 update() is called at time: " + NetworkSimulator.time);

        // open the packet
        int source = p.getSource();
        int dest = p.getDest();
        System.out.println("Packet received at: Entity" + dest);
        System.out.println("Sender: Entity" + source);
        System.out.print("Packet Content: [");
        System.out.print(p.getMincost(0) + ", ");
        System.out.print(p.getMincost(1) + ", ");
        System.out.print(p.getMincost(2) + ", ");
        System.out.print(p.getMincost(3) + "]\n");
        System.out.print("Distance Vector for Entity0: [");
        System.out.print(currentCost[0] + ", ");
        System.out.print(currentCost[1] + ", ");
        System.out.print(currentCost[2] + ", ");
        System.out.print(currentCost[3] + "]\n");
        System.out.println("Distance Table at Entity0 before update: ");
        printDT();

        // check the source and destnation of the packet
        if (dest == 0) {
            // update the distanceTable base of packet received
            if (source == 1) {
                // update neighbor 1's distance vector
                linkCostChanged = updateDT(1, p);

                if (linkCostChanged) {
                    isChanged = calculateDTLinkChanged();
                } else {
                    // recalculate the distanceTable base on the changes
                    isChanged = calculateDT();
                }

            } else if (source == 2) {
                // update neighbor 2's distance vector
                linkCostChanged = updateDT(2, p);

                if (linkCostChanged) {
                    isChanged = calculateDTLinkChanged();
                } else {
                    // recalculate the distanceTable base on the changes
                    isChanged = calculateDT();
                }

            } else if (source == 3) {
                // update neighbor 3's distance vector
                linkCostChanged = updateDT(3, p);

                if (linkCostChanged) {
                    isChanged = calculateDTLinkChanged();
                } else {
                    // recalculate the distanceTable base on the changes
                    isChanged = calculateDT();
                }

            } else {
                System.out.println("Invalid source number!");
            }
        }

        // send updates to neighbors if distance vector for entity0 has changed
        if (isChanged) {
            System.out.println();
            System.out.println("After recalculation, Entity0's Distance Vector has Changed:");

            // debug print
            for (int i = 0; i < NUMENTITIES; i++) {
                System.out
                        .println("Distance to Entity " + i + ": " + currentCost[i] + " -----> " + distanceTable0[i][0]);
            }

            // repack the packet
            currentCost[0] = distanceTable0[0][0];
            currentCost[1] = distanceTable0[1][0];
            currentCost[2] = distanceTable0[2][0];
            currentCost[3] = distanceTable0[3][0];

            // send out packets to entity 0's neighbor
            outPTo1 = new Packet(0, 1, currentCost);
            outPTo2 = new Packet(0, 2, currentCost);
            outPTo3 = new Packet(0, 3, currentCost);
            NetworkSimulator.toLayer2(outPTo1);
            printMessage(1);
            NetworkSimulator.toLayer2(outPTo2);
            printMessage(2);
            NetworkSimulator.toLayer2(outPTo3);
            printMessage(3);

            // isChanged = false;
        } else {
            System.out.println();
            System.out.print("After recalculation, No change to Entity0's Distance Vector");
        }
    }

    // This is a private helper method to update the distanceTable
    private boolean updateDT(int index, Packet p) {
        boolean linkChanged = false;
        for (int i = 0; i < NUMENTITIES; i++) {
            if (distanceTable0[i][index] < p.getMincost(i)) {
                linkChanged = true;
            }
        }
        distanceTable0[0][index] = p.getMincost(0);
        distanceTable0[1][index] = p.getMincost(1);
        distanceTable0[2][index] = p.getMincost(2);
        distanceTable0[3][index] = p.getMincost(3);
        System.out.println();
        System.out.println("Distance Table at Entity0 after update: ");

        // debug print
        printDT();
        return linkChanged;
    }

    // This is a private helper method to calculate the new minimum cost for entity2
    private boolean calculateDT() {
        boolean smaller = false;
        int[] temp = new int[NUMENTITIES];
        temp[0] = 0;
        temp[1] = minToDest(1);
        temp[2] = minToDest(2);
        temp[3] = minToDest(3);

        // check is the mincost is changed
        for (int i = 1; i < NUMENTITIES; i++) {
            if (temp[i] < distanceTable0[i][0]) {
                distanceTable0[i][0] = temp[i];
                smaller = true;
            }
        }
        return smaller;
    }

    private int minToDest(int destination) {
        int minCost = 10000;
        // // dxy = min(c(x,v) + dvy)
        for (int i = 1; i < NUMENTITIES; i++) {
            int dist = originalCost[i] + distanceTable0[destination][i];
            if (dist < minCost) {
                minCost = dist;
                through[destination] = i;
            }
        }
        return minCost;
    }

    public void linkCostChangeHandler(int whichLink, int newCost) {
        System.out.println("Entity0 linkCostChageHandler() is called at time: " + NetworkSimulator.time);
        System.out.print("Distance Vector for Entity0: [");
        System.out.print(currentCost[0] + ", ");
        System.out.print(currentCost[1] + ", ");
        System.out.print(currentCost[2] + ", ");
        System.out.print(currentCost[3] + "]\n");
        System.out.println("Distance Table at Entity0: ");
        printDT();
        System.out.println();
        System.out.print("Entity0's direct cost to neighbors before change: [");
        System.out.print(originalCost[0] + ", ");
        System.out.print(originalCost[1] + ", ");
        System.out.print(originalCost[2] + ", ");
        System.out.print(originalCost[3] + "]\n");

        if (whichLink == 1) {
            if (currentCost[whichLink] == newCost) {
                // do nothing since the cost is the same
            } else {
                linkCostChanged = true;
                // distanceTable0[whichLink][0] = newCost;
                originalCost[whichLink] = newCost;
                System.out.println("The link cost form 0 to 1 has changed to " + newCost + ".");
                System.out.print("Entity0's direct cost to neighbors after change: [");
                System.out.print(originalCost[0] + ", ");
                System.out.print(originalCost[1] + ", ");
                System.out.print(originalCost[2] + ", ");
                System.out.print(originalCost[3] + "]\n");

                isChanged = calculateDTLinkChanged();
                if (isChanged) {
                    System.out.println();
                    System.out.println("After recalculation, Entity0's Distance Vector has Changed:");

                    // debug print
                    for (int i = 0; i < NUMENTITIES; i++) {
                        System.out.println(
                                "Distance to Entity " + i + ": " + currentCost[i] + " -----> " + distanceTable0[i][0]);
                    }

                    // repack the packet
                    currentCost[0] = distanceTable0[0][0];
                    currentCost[1] = distanceTable0[1][0];
                    currentCost[2] = distanceTable0[2][0];
                    currentCost[3] = distanceTable0[3][0];

                    // send out packets to entity 0's neighbor
                    outPTo1 = new Packet(0, 1, currentCost);
                    outPTo2 = new Packet(0, 2, currentCost);
                    outPTo3 = new Packet(0, 3, currentCost);
                    NetworkSimulator.toLayer2(outPTo1);
                    printMessage(1);
                    NetworkSimulator.toLayer2(outPTo2);
                    printMessage(2);
                    NetworkSimulator.toLayer2(outPTo3);
                    printMessage(3);

                } else {
                    System.out.println();
                    System.out.print("After recalculation, No change to Entity0's Distance Vector");
                }
            }
        }
    }

    // This is a private helper method to calculate the new minimum cost for entity2
    private boolean calculateDTLinkChanged() {
        boolean changed = false;
        int[] temp = new int[NUMENTITIES];
        temp[0] = 0;
        temp[1] = minToDest(1);
        temp[2] = minToDest(2);
        temp[3] = minToDest(3);

        // check is the mincost is changed
        for (int i = 1; i < NUMENTITIES; i++) {
            if (distanceTable0[i][0] == temp[i]) {
                continue;
            } else {
                distanceTable0[i][0] = temp[i];
                changed = true;
            }
        }

        linkCostChanged = false;
        return changed;
    }

    public void printDT() {
        System.out.println();
        System.out.println("           via");
        System.out.println(" D0 |   1   2   3");
        System.out.println("----+------------");
        for (int i = 1; i < NetworkSimulator.NUMENTITIES; i++) {
            System.out.print("   " + i + "|");
            for (int j = 1; j < NetworkSimulator.NUMENTITIES; j++) {
                if (distanceTable0[i][j] < 10) {
                    System.out.print("   ");
                } else if (distanceTable0[i][j] < 100) {
                    System.out.print("  ");
                } else {
                    System.out.print(" ");
                }

                System.out.print(distanceTable0[i][j]);
            }
            System.out.println();
        }
    }
}
