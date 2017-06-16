import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Stack;

/**
 * @author Liangyan Ding
 */
public class BCNFDecomp {

    public static void main(String args[]) throws FileNotFoundException {

        // R - A relation
        ArrayList<String> relation = new ArrayList<>();

        // L - A list of functional dependencies.
        ArrayList<FuncDep> FDList = new ArrayList<>();

        // S - a relation stack
        Stack<ArrayList<String>> relationStack = new Stack<>();

        // DB - a list of relations in BCNF, for output
        ArrayList<ArrayList<String>> decomposed = new ArrayList<>();


        // Read the file
        // -------------------------------------------------------------------
        Scanner scanner = new Scanner(new File("src/Test/test1.txt"));
        int i = 0;
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            Scanner sc = new Scanner(line);
            if(i == 0) {// the first line is the Relation
                while (sc.hasNext()) {
                    // keep scan until next line
                    String token = sc.next();
                    // convert string to char
                    char charToken = token.charAt(0);
                    // convert to uppercase
                    char upperCased = Character.toUpperCase(charToken);
                    String upperCasedToken = Character.toString(upperCased);
                    // add to original relation
                    relation.add(upperCasedToken);
                }
                // push original relation onto the stack
                relationStack.push(relation);
            }else{
                // insert functional dependency into FDList
                FuncDep FD = new FuncDep(line);
                FDList.add(FD);
            }
            i++;
        }

        // get rid of trivial FDs
        // -------------------------------------------------------
        boolean isInSet = false;
        boolean isSubset = true;
        // check if FD's RHS is a subset of the LHS
        for(int a=0;a<FDList.size();a++) {
            for (int b = 0; b < FDList.get(a).rhs().size(); b++) {
                for (int c = 0; c < FDList.get(a).lhs().size(); c++) {
                    if (FDList.get(a).rhs().get(b).equals(FDList.get(a).lhs().get(c))) {
                        isInSet = true;
                    }
                }
                if ( ! isInSet) {
                    isSubset = false;
                }
                isInSet = false;
            }
            if(isSubset){
                FDList.remove(a);
            }
            isSubset = true;
        }

        // printout all the non-trivial FDs
        System.out.println("Non-trivial functional dependencies: ");
        for(int d=0;d<FDList.size();d++){
            System.out.println(FDList.get(d).lhs() + "->" + FDList.get(d).rhs());
        }
        System.out.println();

        // print out all the closures of the FDs
        System.out.println("The closures for the FDs are");
        for(int e=0;e<FDList.size();e++){
            System.out.println(FDList.get(e).lhs() + "+ = " + FDList.get(e).closure(FDList));
        }
        System.out.println();


        // BCNF decomposition algorithm
        // ----------------------------------------------------------
        while( ! relationStack.isEmpty()){

            ArrayList<String> checkRelation = relationStack.pop();
            boolean violation = false;
            FuncDep checkFD = null;

            ListIterator<FuncDep> FDListIterator = FDList.listIterator();
            while(FDListIterator.hasNext() && ! violation){
                checkFD = FDListIterator.next();

                if(checkFD.isViolationTo(checkRelation, FDList)) {
                    violation = true;
                }
            }

            if( ! violation){
                decomposed.add(checkRelation);
            }else{

                relationStack.push(checkFD.lhsAndClosure(FDList));
                relationStack.push(checkFD.amr(checkRelation, FDList));
            }
        }


        // output the result
        // ----------------------------------------------------
        System.out.println();
        System.out.println("Result relations after BCNF decomposition: ");
        for(int k=0;k<decomposed.size();k++){
            System.out.println(decomposed.get(k));
        }
    }
}