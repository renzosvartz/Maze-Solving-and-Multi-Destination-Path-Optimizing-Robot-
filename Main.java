package src;

public class Main 
{
    public enum ORIENTATION 
    {
        NORTH, EAST, SOUTH, WEST
    }

    public static void main(String[] args) 
    {
        //Djikstras_Solver djikstras_solver = new Djikstras_Solver(); 
        //djikstras_solver.main();

        //WIPDS djikstras_solver = new WIPDS(); 
        //djikstras_solver.main();

        //WIPDS2 djikstras_solver = new WIPDS2(); 
        //djikstras_solver.main();

        WIPDS3 djikstras_solver = new WIPDS3(); 
        djikstras_solver.main();

        //orange end points
        //other center points
        //add heuristic
        //other algorithms to solve
        //make id -> id
        //any id to any id
        //clean up Custom_Vertex
        //best multi id path
        //walls?

        //center points
        //corner hallways work right?
        //lesson learned: edge case testing

    }

}
