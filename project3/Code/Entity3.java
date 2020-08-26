public class Entity3 extends Entity {
    // This is the number of entitys in the network
    private static final int NUMENTITIES = NetworkSimulator.NUMENTITIES;

    // This is the to store the Distance Table for entity 0
    private static int[][] distanceTable3;

    // This is to store the mincost for entity 0
    private static int[] currentCost;

    // This is to store the original cost to entity0 direct neighbors
    private static int[] originalCost;

    // This is to store the path entity0 took for the minimum cost
    private static int[] through;

    // This is to store packets received and sent
    private static Packet outPTo0;
    private static Packet outPTo2;

    // This is a flag for if entity0's distance vector has changed
    // Any value cahnge would flip the boolean value
    private boolean isChanged = false;

    // THis is a flag when link cost has changed
    private boolean linkCostChanged = false;

    // Perform any necessary initialization in the constructor
    public Entity3() {
        System.out.println();
        System.out.println("________________________________________________________________________");
        System.out.println("Entity3() is called at time: " + NetworkSimulator.time);
        System.out.println("Initializing the distance table: ");
        // initialize the distance table for entity 0
        distanceTable3 = new int[NUMENTITIES][NUMENTITIES];

        distanceTable3[0][0] = 999;
        distanceTable3[0][1] = 999;
        distanceTable3[0][2] = 999;
        distanceTable3[0][3] = 7;

        distanceTable3[1][0] = 999;
        distanceTable3[1][1] = 999;
        distanceTable3[1][2] = 999;
        distanceTable3[1][3] = 999;

        distanceTable3[2][0] = 999;
        distanceTable3[2][1] = 999;
        distanceTable3[2][2] = 999;
        distanceTable3[2][3] = 2;

        distanceTable3[3][0] = 999;
        distanceTable3[3][1] = 999;
        distanceTable3[3][2] = 999;
        distanceTable3[3][3] = 0;

        // debug print
        printDT();

        // record the current cost for entity 1
        currentCost = new int[NUMENTITIES];
        currentCost[0] = distanceTable3[0][3];
        currentCost[1] = distanceTable3[1][3];
        currentCost[2] = distanceTable3[2][3];
        currentCost[3] = distanceTable3[3][3];

        // record the original cost to its direct neighbors
        originalCost = new int[NUMENTITIES];
        originalCost[0] = 7;
        originalCost[1] = 999;
        originalCost[2] = 2;
        originalCost[3] = 0;

        // record the path
        through = new int[NUMENTITIES];
        through[0] = 0;
        through[1] = 1;
        through[2] = 2;
        through[3] = 3;

        // send out packets to entity 1's neighbor
        outPTo0 = new Packet(3, 0, currentCost);
        outPTo2 = new Packet(3, 2, currentCost);
        NetworkSimulator.toLayer2(outPTo0);
        printMessage(0);
        NetworkSimulator.toLayer2(outPTo2);
        printMessage(2);
    }

    private void printMessage(int neighbor) {
        // output message
        System.out.println();
        System.out.print("Sending packet: [" + currentCost[0]);
        System.out.print(", " + currentCost[1]);
        System.out.print(", " + currentCost[2]);
        System.out.print(", " + currentCost[3] + "]");
        System.out.print(" to Entity3's direct Neighbor: Entity" + neighbor);

    }

    // Handle updates when a packet is received. Students will need to call
    // NetworkSimulator.toLayer2() with new packets based upon what they
    // send to update. Be careful to construct the source and destination of
    // the packet correctly. Read the warning in NetworkSimulator.java for more
    // details.
    public void update(Packet p) {
        System.out.println("Entity3 update() is called at time: " + NetworkSimulator.time);
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
        System.out.print("Distance Vector for Entity3: [");
        System.out.print(currentCost[0] + ", ");
        System.out.print(currentCost[1] + ", ");
        System.out.print(currentCost[2] + ", ");
        System.out.print(currentCost[3] + "]\n");
        System.out.println("Distance Table at Entity3 before update: ");
        printDT();

        // check the source and destnation of the packet
        if (dest == 3) {
            if (source == 0) {
                // update neighbor 1's distance vector
                linkCostChanged = updateDT(0, p);

                if (linkCostChanged) {
                    isChanged = calculateDTLinkChanged();
                } else {
                    // recalculate the distanceTable base on the changes
                    isChanged = calculateDT();
                }

            } else if (source == 2) {
                // update neighbor 1's distance vector
                linkCostChanged = updateDT(2, p);

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
        if (isChanged)

        {
            System.out.println();
            System.out.println("After recalculation, Entity3's Distance Table has Changed:");

            // debug print
            for (int i = 0; i < NUMENTITIES; i++) {
                System.out
                        .println("Distance to Entity " + i + ": " + currentCost[i] + " -----> " + distanceTable3[i][3]);
            }

            // repack the packet
            currentCost[0] = distanceTable3[0][3];
            currentCost[1] = distanceTable3[1][3];
            currentCost[2] = distanceTable3[2][3];
            currentCost[3] = distanceTable3[3][3];

            // send out packets to entity 1's neighbor
            outPTo0 = new Packet(3, 0, currentCost);
            outPTo2 = new Packet(3, 2, currentCost);
            NetworkSimulator.toLayer2(outPTo0);
            printMessage(0);
            NetworkSimulator.toLayer2(outPTo2);
            printMessage(2);

        } else {
            System.out.println();
            System.out.print("After recalculation, No change to Entity3's Distance Table");
        }
    }

    // This is private helper method to update the distanceTable
    private boolean updateDT(int index, Packet p) {

        boolean linkChanged = false;
        for (int i = 0; i < NUMENTITIES; i++) {
            if (distanceTable3[i][index] < p.getMincost(i)) {
                linkChanged = true;
            }
        }
        distanceTable3[0][index] = p.getMincost(0);
        distanceTable3[1][index] = p.getMincost(1);
        distanceTable3[2][index] = p.getMincost(2);
        distanceTable3[3][index] = p.getMincost(3);
        System.out.println();
        System.out.println("Distance Table at Entity3 after update: ");

        // debug print
        printDT();
        return linkChanged;
    }

    // This is a private helper method to calculate the new minimum cost for entity0
    private boolean calculateDTLinkChanged() {
        boolean changed = false;
        int[] temp = new int[NUMENTITIES];
        temp[0] = minToDest(0);
        temp[1] = minToDest(1);
        temp[2] = minToDest(2);
        temp[3] = 0;

        // check is the mincost is changed
        for (int i = 0; i < NUMENTITIES; i++) {
            if (i == 3) {
                continue;
            }
            if (distanceTable3[i][3] == temp[i]) {
                continue;
            } else {
                distanceTable3[i][3] = temp[i];
                changed = true;
            }

        }
        linkCostChanged = false;
        return changed;
    }

    // This is a private helper method to calculate the new minimum cost for entity1
    private boolean calculateDT() {
        boolean smaller = false;
        int[] temp = new int[NUMENTITIES];
        temp[0] = minToDest(0);
        temp[1] = minToDest(1);
        temp[2] = minToDest(2);
        temp[3] = 0;

        // // check is the mincost is changed
        for (int i = 0; i < NUMENTITIES; i++) {
            if (i == 3) {
                continue;
            }
            if (temp[i] < distanceTable3[i][3]) {
                distanceTable3[i][3] = temp[i];
                smaller = true;
            }
        }
        return smaller;
    }

    private int minToDest(int destination) {
        int minCost = 10000;
        // dxy = min(c(x,v) + dvy)
        for (int i = 0; i < NUMENTITIES; i++) {
            if (i == 3) {
                continue;
            }
            int dist = originalCost[i] + distanceTable3[destination][i];
            if (dist < minCost) {
                minCost = dist;
                through[destination] = i;
            }
        }
        return minCost;
    }

    public void linkCostChangeHandler(int whichLink, int newCost) {
        // Entity3's link cost is not going to change
    }

    public void printDT() {
        System.out.println("         via");
        System.out.println(" D3 |   0   2");
        System.out.println("----+--------");
        for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++) {
            if (i == 3) {
                continue;
            }

            System.out.print("   " + i + "|");
            for (int j = 0; j < NetworkSimulator.NUMENTITIES; j += 2) {

                if (distanceTable3[i][j] < 10) {
                    System.out.print("   ");
                } else if (distanceTable[i][j] < 100) {
                    System.out.print("  ");
                } else {
                    System.out.print(" ");
                }

                System.out.print(distanceTable3[i][j]);
            }
            System.out.println();
        }
    }
}
