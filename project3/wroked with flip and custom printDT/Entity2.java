public class Entity2 extends Entity {
    // This is the number of entitys in the network
    private static final int NUMENTITIES = NetworkSimulator.NUMENTITIES;

    // This is the number of nerghbors entity 0 have
    private static final int NUMNEIGHBORS = 3;

    // This is the to store the Distance Table for entity 0
    private static int[][] distanceTable2;

    // This is to store the mincost for entity 0
    private static int[] currentCost;

    // This is to store packets received and sent
    private static Packet outPTo0;
    private static Packet outPTo1;
    private static Packet outPTo3;

    // This is a flag for if entity0's distance vector has changed
    // Any value cahnge would flip the boolean value
    private boolean isChanged = false;

    // Perform any necessary initialization in the constructor
    public Entity2() {
        System.out.println();
        System.out.println("Entity2() is called");
        System.out.println("Initializing the distance table: ");
        // initialize the distance table for entity 0
        distanceTable2 = new int[NUMENTITIES][NUMNEIGHBORS + 1];

        distanceTable2[0][0] = 999;
        distanceTable2[0][1] = 999;
        distanceTable2[0][2] = 3;
        distanceTable2[0][3] = 999;

        distanceTable2[1][0] = 999;
        distanceTable2[1][1] = 999;
        distanceTable2[1][2] = 1;
        distanceTable2[1][3] = 999;

        distanceTable2[2][0] = 999;
        distanceTable2[2][1] = 999;
        distanceTable2[2][2] = 0;
        distanceTable2[2][3] = 999;

        distanceTable2[3][0] = 999;
        distanceTable2[3][1] = 999;
        distanceTable2[3][2] = 2;
        distanceTable2[3][3] = 999;

        // debug print
        printDT();

        // record the current cost for entity 2
        currentCost = new int[NUMENTITIES];
        currentCost[0] = distanceTable2[0][2];
        currentCost[1] = distanceTable2[1][2];
        currentCost[2] = distanceTable2[2][2];
        currentCost[3] = distanceTable2[3][2];

        // send out packets to entity 2's neighbor
        outPTo0 = new Packet(2, 0, currentCost);
        outPTo1 = new Packet(2, 1, currentCost);
        outPTo3 = new Packet(2, 3, currentCost);
        NetworkSimulator.toLayer2(outPTo0);
        NetworkSimulator.toLayer2(outPTo1);
        NetworkSimulator.toLayer2(outPTo3);
    }

    // Handle updates when a packet is received. Students will need to call
    // NetworkSimulator.toLayer2() with new packets based upon what they
    // send to update. Be careful to construct the source and destination of
    // the packet correctly. Read the warning in NetworkSimulator.java for more
    // details.
    public void update(Packet p) {

        System.out.println("Entity2 update() is called");
        // open the packet
        int source = p.getSource();
        int dest = p.getDest();
        System.out.println("Packet received at " + dest + ", " + "packet is from: " + source);

        // check the source and destnation of the packet
        if (dest == 2) {
            // update the distanceTable base of packet received
            if (source == 0) {
                // update neighbor 1's distance vector
                updateDT(0, p);
                // recalculate the distanceTable base on the changes
                isChanged = calculateDT();

            } else if (source == 1) {
                // update neighbor 2's distance vector
                updateDT(1, p);
                // recalculate the distanceTable base on the changes
                isChanged = calculateDT();

            } else if (source == 3) {
                // update neighbor 3's distance vector
                updateDT(3, p);
                // recalculate the distanceTable base on the changes
                isChanged = calculateDT();

            } else {
                System.out.println("Invalid source number!");
            }
        }

        // send updates to neighbors if distanceTable has changed
        if (isChanged) {
            System.out.println("After recalculation, the Distance Table has Changed:");
            // debug print
            printDT();

            currentCost[0] = distanceTable2[0][2];
            currentCost[1] = distanceTable2[1][2];
            currentCost[2] = distanceTable2[2][2];
            currentCost[3] = distanceTable2[3][2];

            // send out packets to entity 2's neighbor
            outPTo0 = new Packet(2, 0, currentCost);
            outPTo1 = new Packet(2, 1, currentCost);
            outPTo3 = new Packet(2, 3, currentCost);
            NetworkSimulator.toLayer2(outPTo0);
            NetworkSimulator.toLayer2(outPTo1);
            NetworkSimulator.toLayer2(outPTo3);

            isChanged = false;
        } else {
            System.out.println();
            System.out.println("No change to the Distance Table");
        }
    }

    // This is private helper method to update the distanceTable
    private void updateDT(int index, Packet p) {
        distanceTable2[0][index] = p.getMincost(0);
        distanceTable2[1][index] = p.getMincost(1);
        distanceTable2[2][index] = p.getMincost(2);
        distanceTable2[3][index] = p.getMincost(3);
        System.out.println("Updating the Distance Table: ");
        // debug print
        printDT();
    }

    // This is a private helper method to calculate the new minimum cost for entity0
    private boolean calculateDT() {
        int[] temp = new int[NUMENTITIES];
        temp[0] = minToDest(0);
        temp[1] = minToDest(1);
        temp[2] = 0;
        temp[3] = minToDest(3);

        // check is the mincost is changed
        for (int i = 0; i < NUMENTITIES; i++) {
            if (i == 2) {
                continue;
            }

            if (temp[i] < distanceTable2[i][2]) {
                distanceTable2[i][2] = temp[i];
                isChanged = true;
            }
        }
        // System.out.println("this is after calculating the min cost:");
        // // debug print
        // printDT();
        return isChanged;
    }

    private int minToDest(int destination) {
        int minCost = 10000;
        // dxy = min(c(x,v) + dvy)
        for (int i = 0; i < NUMNEIGHBORS + 1; i++) {
            if (i == 2) {
                continue;
            }
            int dist = distanceTable2[i][2] + Math.min(distanceTable2[destination][i], distanceTable2[i][destination]);
            // int disNeighborToDest = Math.min(distanceTable2[destination][i],
            // distanceTable2[i][destination]);
            if (dist < minCost) {
                minCost = dist;
            }
        }
        return minCost;
    }

    public void linkCostChangeHandler(int whichLink, int newCost) {
    }

    public void printDT() {
        System.out.println();
        System.out.println("           via");
        System.out.println(" D2 |   0   1   2   3");
        System.out.println("----+----------------");
        for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++) {
            // if (i == 2) {
            // continue;
            // }

            System.out.print("   " + i + "|");
            for (int j = 0; j < NUMNEIGHBORS + 1; j++) {
                // if (j == 2) {
                // continue;
                // }

                if (distanceTable2[i][j] < 10) {
                    System.out.print("   ");
                } else if (distanceTable2[i][j] < 100) {
                    System.out.print("  ");
                } else {
                    System.out.print(" ");
                }

                System.out.print(distanceTable2[i][j]);
            }
            System.out.println();
        }
    }
}
