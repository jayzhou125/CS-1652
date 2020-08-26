public class Entity0 extends Entity {
    // This is the number of entitys in the network
    private static final int NUMENTITIES = NetworkSimulator.NUMENTITIES;

    // This is the number of nerghbors entity 0 have
    private static final int NUMNEIGHBORS = 3;

    // This is the to store the Distance Table for entity 0
    private static int[][] distanceTable0;

    // This is to store the mincost for entity 0
    private static int[] currentCost;

    // This is to store packets sent
    private static Packet outPTo1;
    private static Packet outPTo2;
    private static Packet outPTo3;

    // This is a flag for if entity0's distance vector has changed
    // Any value cahnge would flip the boolean value
    private boolean isChanged = false;

    // Perform any necessary initialization in the constructor
    public Entity0() {
        System.out.println();
        System.out.println("Entity0() is called");
        System.out.println("Initializing the distance table: ");
        // initialize the distance table for entity 0
        distanceTable0 = new int[NUMENTITIES][NUMNEIGHBORS + 1];

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

        // send out packets to entity 0's neighbor
        outPTo1 = new Packet(0, 1, currentCost);
        outPTo2 = new Packet(0, 2, currentCost);
        outPTo3 = new Packet(0, 3, currentCost);
        NetworkSimulator.toLayer2(outPTo1);
        NetworkSimulator.toLayer2(outPTo2);
        NetworkSimulator.toLayer2(outPTo3);
    }

    // Handle updates when a packet is received. You will need to call
    // NetworkSimulator.toLayer2() with new packets based upon what you
    // send to update. Be careful to construct the source and destination of
    // the packet correctly. Read the warning in NetworkSimulator.java for more
    // details.
    public void update(Packet p) {

        System.out.println("Entity0 update() is called");
        // open the packet
        int source = p.getSource();
        int dest = p.getDest();
        System.out.println("Packet received at " + dest + ", " + "packet is from: " + source);

        // check the source and destnation of the packet
        if (dest == 0) {
            // update the distanceTable base of packet received
            if (source == 1) {
                // update neighbor 1's distance vector
                updateDT(1, p);
                // recalculate the distanceTable base on the changes
                isChanged = calculateDT();

            } else if (source == 2) {
                // update neighbor 2's distance vector
                updateDT(2, p);
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

        // send updates to neighbors if distance vector for entity0 has changed
        if (isChanged) {
            System.out.println();
            System.out.println("After recalculation, the Distance Table has Changed:");
            // debug print
            printDT();
            currentCost[0] = distanceTable0[0][0];
            currentCost[1] = distanceTable0[1][0];
            currentCost[2] = distanceTable0[2][0];
            currentCost[3] = distanceTable0[3][0];

            // send out packets to entity 0's neighbor
            outPTo1 = new Packet(0, 1, currentCost);
            outPTo2 = new Packet(0, 2, currentCost);
            outPTo3 = new Packet(0, 3, currentCost);
            NetworkSimulator.toLayer2(outPTo1);
            NetworkSimulator.toLayer2(outPTo2);
            NetworkSimulator.toLayer2(outPTo3);

            isChanged = false;
        } else {
            System.out.println();
            System.out.println("No change to the Distance Table");
        }
    }

    // This is a private helper method to update the distanceTable
    private void updateDT(int index, Packet p) {
        distanceTable0[0][index] = p.getMincost(0);
        distanceTable0[1][index] = p.getMincost(1);
        distanceTable0[2][index] = p.getMincost(2);
        distanceTable0[3][index] = p.getMincost(3);
        System.out.println("Updating the Distance Table: ");
        // debug print
        printDT();
    }

    // This is a private helper method to calculate the new minimum cost for entity2
    private boolean calculateDT() {
        int[] temp = new int[NUMENTITIES];
        temp[0] = 0;
        temp[1] = minToDest(1);
        temp[2] = minToDest(2);
        temp[3] = minToDest(3);

        // check is the mincost is changed
        for (int i = 1; i < NUMENTITIES; i++) {
            if (temp[i] < distanceTable0[i][0]) {
                distanceTable0[i][0] = temp[i];
                isChanged = true;
            }
        }
        // // System.out.println("this is after calculating the min cost:");
        // // // debug print
        // // printDT();
        return isChanged;
    }

    private int minToDest(int destination) {
        int minCost = 10000;
        // // dxy = min(c(x,v) + dvy)
        for (int i = 1; i < NUMNEIGHBORS + 1; i++) {
            int dist = distanceTable0[i][0] + Math.min(distanceTable0[destination][i], distanceTable0[i][destination]);
            // int disNeighborToDest = Math.min(distanceTable0[destination][i],
            // distanceTable0[i][destination]);
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
        System.out.println(" D0 |   0   1   2   3");
        System.out.println("----+----------------");
        for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++) {
            System.out.print("   " + i + "|");
            for (int j = 0; j < NUMNEIGHBORS + 1; j++) {
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
