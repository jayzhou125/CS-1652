public class Entity3 extends Entity {
    // This is the number of entitys in the network
    private static final int NUMENTITIES = NetworkSimulator.NUMENTITIES;

    // This is the number of nerghbors entity 0 have
    private static final int NUMNEIGHBORS = 2;

    // This is the to store the Distance Table for entity 0
    private static int[][] distanceTable3;

    // This is to store the mincost for entity 0
    private static int[] currentCost;

    // This is to store packets received and sent
    private static Packet outPTo0;
    private static Packet outPTo2;

    // This is a flag for if entity0's distance vector has changed
    // Any value cahnge would flip the boolean value
    private boolean isChanged = false;

    // Perform any necessary initialization in the constructor
    public Entity3() {
        System.out.println();
        System.out.println("Entity3() is called");
        System.out.println("Initializing the distance table: ");
        // initialize the distance table for entity 0
        distanceTable3 = new int[NUMENTITIES][NUMNEIGHBORS + 1];

        distanceTable3[0][0] = 999;
        distanceTable3[0][1] = 999;
        distanceTable3[0][2] = 7;
        // distanceTable3[0][3] = 7;

        distanceTable3[1][0] = 999;
        distanceTable3[1][1] = 999;
        distanceTable3[1][2] = 999;
        // distanceTable3[1][3] = 999;

        distanceTable3[2][0] = 999;
        distanceTable3[2][1] = 999;
        distanceTable3[2][2] = 2;
        // distanceTable3[2][3] = 2;

        distanceTable3[3][0] = 999;
        distanceTable3[3][1] = 999;
        distanceTable3[3][2] = 0;
        // distanceTable3[3][3] = 999;

        // debug print
        printDT();

        // record the current cost for entity 1
        currentCost = new int[NUMENTITIES];
        currentCost[0] = distanceTable3[0][2];
        currentCost[1] = distanceTable3[1][2];
        currentCost[2] = distanceTable3[2][2];
        currentCost[3] = distanceTable3[3][2];

        // send out packets to entity 1's neighbor
        outPTo0 = new Packet(3, 0, currentCost);
        outPTo2 = new Packet(3, 2, currentCost);
        NetworkSimulator.toLayer2(outPTo0);
        NetworkSimulator.toLayer2(outPTo2);
    }

    // Handle updates when a packet is received. Students will need to call
    // NetworkSimulator.toLayer2() with new packets based upon what they
    // send to update. Be careful to construct the source and destination of
    // the packet correctly. Read the warning in NetworkSimulator.java for more
    // details.
    public void update(Packet p) {
        System.out.println();
        System.out.println("Entity3 update() is called");
        System.out.println("Initializing the distance table: ");
        // open the packet
        int source = p.getSource();
        int dest = p.getDest();
        System.out.println("Packet received at " + dest + ", " + "packet is from: " + source);

        // check the source and destnation of the packet
        if (dest == 3) {
            // update the distanceTable base of packet received
            if (source == 0) {
                // update neighbor 1's distance vector
                updateDT(0, p);
                // recalculate the distanceTable base on the changes
                isChanged = calculateDT();

            } else if (source == 2) {
                // update neighbor 2's distance vector
                updateDT(1, p);
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

            currentCost[0] = distanceTable3[0][2];
            currentCost[1] = distanceTable3[1][2];
            currentCost[2] = distanceTable3[2][2];
            currentCost[3] = distanceTable3[3][2];

            // send out packets to entity 1's neighbor
            outPTo0 = new Packet(3, 0, currentCost);
            outPTo2 = new Packet(3, 2, currentCost);
            NetworkSimulator.toLayer2(outPTo0);
            NetworkSimulator.toLayer2(outPTo2);

            isChanged = false;
        } else {
            System.out.println();
            System.out.println("No change to the Distance Table");
        }
    }

    // This is a private helper method to calculate the new minimum cost for entity1
    private boolean calculateDT() {
        int[] temp = new int[NUMENTITIES];
        temp[0] = minToDest(0);
        temp[1] = minToDest(1);
        temp[2] = minToDest(2);
        temp[3] = 0;

        // // check is the mincost is changed
        for (int i = 0; i < NUMENTITIES; i++) {
            if (i == 2) {
                continue;
            }
            if (temp[i] < distanceTable3[i][2]) {
                distanceTable3[i][2] = temp[i];
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
        // // dxy = min(c(x,v) + dvy)
        int route0 = 0;
        int route2 = 0;
        if (destination == 0) {
            route0 = distanceTable3[destination][2] + distanceTable3[destination][0];
            route2 = distanceTable3[2][2] + Math.min(distanceTable3[destination][1], distanceTable3[2][destination]);
            minCost = Math.min(route0, route2);
        } else if (destination == 1) {
            route0 = distanceTable3[0][2] + distanceTable3[destination][0];
            route2 = distanceTable3[2][2] + distanceTable3[destination][1];
            minCost = Math.min(route0, route2);
        } else if (destination == 2) {
            route0 = distanceTable3[0][2]
                    + Math.min(distanceTable3[destination][0], distanceTable3[0][destination - 1]);
            route2 = distanceTable3[2][2] + distanceTable3[destination][1];
            minCost = Math.min(route0, route2);
        }
        return minCost;
    }

    // This is private helper method to update the distanceTable
    private void updateDT(int index, Packet p) {
        distanceTable3[0][index] = p.getMincost(0);
        distanceTable3[1][index] = p.getMincost(1);
        distanceTable3[2][index] = p.getMincost(2);
        distanceTable3[3][index] = p.getMincost(3);
        System.out.println("Updating the Distance Table: ");
        // debug print
        printDT();
    }

    public void linkCostChangeHandler(int whichLink, int newCost) {
    }

    public void printDT() {
        System.out.println();
        System.out.println("           via");
        System.out.println(" D3 |   0   2   3");
        System.out.println("----+------------");
        for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++) {
            // if (i == 3) {
            // continue;
            // }

            System.out.print("   " + i + "|");
            for (int j = 0; j < NUMNEIGHBORS + 1; j++) {

                if (distanceTable3[i][j] < 10) {
                    System.out.print("   ");
                } else if (distanceTable3[i][j] < 100) {
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
