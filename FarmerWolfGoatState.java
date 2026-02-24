import java.util.Set;
import java.util.HashSet;

import search.AbstractState;
import search.State;

/* This class implements the AbstractState abstract class,
 * representing the domain of the farmer-wolf-goat-cabbage problem.
 */
public class FarmerWolfGoatState extends AbstractState {
	
    private FarmerWolfGoatSide farmer = FarmerWolfGoatSide.EAST;
    private FarmerWolfGoatSide wolf = FarmerWolfGoatSide.EAST;
    private FarmerWolfGoatSide goat = FarmerWolfGoatSide.EAST;
    private FarmerWolfGoatSide cabbage = FarmerWolfGoatSide.EAST;
    

    /**
     * Constructs a new state. Everyone is on the east side.
     */
    public FarmerWolfGoatState() {
    }

    /**
     * Constructs a child for the given @param parent,
     * based on the specified positions.
     * @param parent the parent state
     */
    public FarmerWolfGoatState(FarmerWolfGoatState parent, 
    		FarmerWolfGoatSide farmer, FarmerWolfGoatSide wolf, FarmerWolfGoatSide goat, FarmerWolfGoatSide cabbage) {
        super(parent);
        
        this.farmer = farmer;
        this.wolf = wolf;
        this.goat = goat;
        this.cabbage = cabbage;
    }
    
   

    /**
     * Returns the list of states reachable from the current state.
     */
    public Iterable<State> getPossibleMoves() {
        Set<State> moves = new HashSet<State>();
        // Move wolf
        if (farmer==wolf)
            new FarmerWolfGoatState(this,farmer.getOpposite(),
                                         wolf.getOpposite(),
                                         goat,
                                         cabbage).addIfSafe(moves);
        // Move goat
        if (farmer==goat)
            new FarmerWolfGoatState(this,farmer.getOpposite(),
                                         wolf,
                                         goat.getOpposite(),
                                         cabbage).addIfSafe(moves);
        // Move cabbage
        if (farmer==cabbage)
            new FarmerWolfGoatState(this,farmer.getOpposite(),
                                         wolf,
                                         goat,
                                         cabbage.getOpposite()).addIfSafe(moves);
        // Move only the farmer
        new FarmerWolfGoatState(this,farmer.getOpposite(),
                                     wolf,
                                     goat,
                                     cabbage).addIfSafe(moves);

        return moves;
    }
    
    private final void addIfSafe(Set<State> moves) {
        boolean unsafe = (farmer != wolf && farmer != goat) ||
                         (farmer != goat && farmer != cabbage);
        if (!unsafe)
            moves.add(this);
    }

    /**
     * Determines whether the given state is a solution.
     * @return true if and only if the state is a goal state
     */
    public boolean isSolution() {
        return farmer==FarmerWolfGoatSide.WEST && 
               wolf==FarmerWolfGoatSide.WEST &&
               goat==FarmerWolfGoatSide.WEST && 
               cabbage==FarmerWolfGoatSide.WEST;
    }



    /**
     * The heuristic equals the number of items on the EAST side.
     */
    public double getHeuristic() {
        int sum = 0;
        if (farmer  == FarmerWolfGoatSide.EAST) sum++;
        if (wolf    == FarmerWolfGoatSide.EAST) sum++;
        if (cabbage == FarmerWolfGoatSide.EAST) sum++;
        if (goat    == FarmerWolfGoatSide.EAST) sum++;
        return sum;
    }
    /**
     * As usual, compares two FWGS objects by content.
     * @returns true if the given object is of the correct type
     *          and is equal to this instance.
     */
    public boolean equals(Object o) {
        if (o==null || !(o instanceof FarmerWolfGoatState))
            return false;
        FarmerWolfGoatState fwgs = (FarmerWolfGoatState)o;
        return farmer  == fwgs.farmer && 
               wolf    == fwgs.wolf && 
               cabbage == fwgs.cabbage &&
               goat    == fwgs.goat;
    }
    /**
     * hashCode, a unique identifier for instances.
     * Two instances are equal if and only if their states match.
     */
    public int hashCode() {
        return (farmer  == FarmerWolfGoatSide.EAST ? 1 : 0)+
               (wolf    == FarmerWolfGoatSide.EAST ? 2 : 0)+
               (cabbage == FarmerWolfGoatSide.EAST ? 4 : 0)+
               (goat    == FarmerWolfGoatSide.EAST ? 8 : 0);
    }
    /**
     * Prints the state.
     */
    public String toString() {
        return (farmer  == FarmerWolfGoatSide.EAST ? "F" : " ")+
               (wolf    == FarmerWolfGoatSide.EAST ? "W" : " ")+
               (cabbage == FarmerWolfGoatSide.EAST ? "C" : " ")+
               (goat    == FarmerWolfGoatSide.EAST ? "G" : " ")+
               " | ~~~~~ | "+
               (farmer  == FarmerWolfGoatSide.WEST ? "F" : " ")+
               (wolf    == FarmerWolfGoatSide.WEST ? "W" : " ")+
               (cabbage == FarmerWolfGoatSide.WEST ? "C" : " ")+
               (goat    == FarmerWolfGoatSide.WEST ? "G" : " ")+
               " (heuristic: "+getHeuristic()+")"+"\n";
    }

}