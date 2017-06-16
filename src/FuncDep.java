import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Liangyan Ding
 */
class FuncDep {

    ArrayList<String> lhs = new ArrayList<>();
    ArrayList<String> rhs = new ArrayList<>();
    ArrayList<String> lur = new ArrayList<>();

    FuncDep(String FD){

        boolean isRHS = false;

        Scanner sc = new Scanner(FD);
        while(sc.hasNext()){

            String token = sc.next();

            if( token.equals("->")){
                isRHS = true;
                token = sc.next();
            }

            if(isRHS){
                lur.add(token);
                rhs.add(token);
            }else{
                lur.add(token);
                lhs.add(token);
            }
        }
    }

    ArrayList<String> lhs(){
        return lhs;
    }

    ArrayList<String> rhs(){
        return rhs;
    }

    ArrayList<String> lur(){
        return lur;
    }

    ArrayList<String> amr(ArrayList<String> relation, ArrayList<FuncDep> FDList){

        ArrayList<String> amr = new ArrayList<>();

        boolean equals = false;

        for(int i=0;i<relation.size();i++){
            for(int j=0;j<this.closure(FDList).size();j++){
                if(relation.get(i).equals(this.closure(FDList).get(j))){
                    equals = true;
                }
            }

            if(!equals){
                amr.add(relation.get(i));
            }

            equals = false;

        }

        return amr;
    }

    ArrayList<String> closure(ArrayList<FuncDep> FDList){

        // store the closure of given FD
        ArrayList<String> closure = new ArrayList<>();

        boolean isInSet = false;// for checking a single element is in set
        boolean isSubset = true;// for checking a set is a subset of another set
        boolean alreadyInSet = false;

        // add all element in the FD to the closure
        for(int i=0;i<this.rhs().size();i++){
            closure.add(this.rhs().get(i));
        }

        for(int i=0;i<FDList.size();i++) {// iterate through all the FD

            // check if the lhs of a FD is a subset of the closure
            for(int j=0;j<FDList.get(i).lhs().size();j++){
                for(int k=0;k<closure.size();k++){
                    if(closure.get(k).equals(FDList.get(i).lhs().get(j))){
                        isInSet = true;
                    }
                }
                // if one of the element is not in the set then it is not a subset to the closure
                if( ! isInSet){
                    isSubset = false;
                    break;
                }
                // reset isInSet to false for checking the next element in the FD
                isInSet = false;
            }

            if(isSubset){
                // check if the rhs is already in the closure
                for(int a=0;a<FDList.get(i).rhs().size();a++){
                    for(int b=0;b<closure.size();b++){
                        if(closure.get(b).equals(FDList.get(i).rhs.get(a))){
                            alreadyInSet = true;
                        }
                    }
                    // if not add it to the closure
                    if( ! alreadyInSet){
                        closure.add(FDList.get(i).rhs.get(a));
                    }
                    // reset
                    alreadyInSet = false;
                }
            }
            // reset
            isSubset = true;

        }

        return closure;
    }

    ArrayList<String> lhsAndClosure(ArrayList<FuncDep> FDList){

        // store the closure of given FD
        ArrayList<String> closure = new ArrayList<>();

        boolean isInSet = false;// for checking a single element is in set
        boolean isSubset = true;// for checking a set is a subset of another set
        boolean alreadyInSet = false;

        // add all element in the FD to the closure
        for(int i=0;i<this.lur().size();i++){
            closure.add(this.lur().get(i));
        }

        for(int i=0;i<FDList.size();i++) {// iterate through all the FD

            // check if the lhs of a FD is a subset of the closure
            for(int j=0;j<FDList.get(i).lhs().size();j++){
                for(int k=0;k<closure.size();k++){
                    if(closure.get(k).equals(FDList.get(i).lhs().get(j))){
                        isInSet = true;
                    }
                }
                // if one of the element is not in the set then it is not a subset to the closure
                if( ! isInSet){
                    isSubset = false;
                    break;
                }
                // reset isInSet to false for checking the next element in the FD
                isInSet = false;
            }

            if(isSubset){
                // check if the rhs is already in the closure
                for(int a=0;a<FDList.get(i).rhs().size();a++){
                    for(int b=0;b<closure.size();b++){
                        if(closure.get(b).equals(FDList.get(i).rhs.get(a))){
                            alreadyInSet = true;
                        }
                    }
                    // if not add it to the closure
                    if( ! alreadyInSet){
                        closure.add(FDList.get(i).rhs.get(a));
                    }
                    // reset
                    alreadyInSet = false;
                }
            }
            // reset
            isSubset = true;

        }
        return closure;
    }

    boolean isViolationTo(ArrayList<String> relation, ArrayList<FuncDep> FDList){

        boolean firstCondition = true;
        boolean secondCondition = false;
        boolean isInSet = false;

        // check if no element in the FD's closure is in the subset of the relation
        for(int i=0;i<this.rhs().size();i++){
            for(int j=0;j<relation.size();j++){
                if(this.rhs.get(i).equals(relation.get(j))){
                    isInSet = true;
                    break;
                }
            }
        }
        // if no element is in subset, it CANNOT be BCNF violation
        if( ! isInSet){
            return false;
        }

        isInSet = false;
        // check if FD's LHS is a subset of the relation
        for(int i=0;i<this.lhs().size();i++){
            for(int j=0;j<relation.size();j++){
                if(this.lhs.get(i).equals(relation.get(j))){
                    isInSet = true;
                }
            }

            if( ! isInSet){
                firstCondition = false;
                break;
            }

            isInSet = false;
        }

        isInSet = false;
        // check if A is not a subset of the [lhs U closure]
        for(int i=0;i<relation.size();i++){
            for(int j=0;j<this.lhsAndClosure(FDList).size();j++){
                if(this.lhsAndClosure(FDList).get(j).equals(relation.get(i))){
                    //
                    isInSet = true;
                }
            }

            if( ! isInSet){
                secondCondition = true;
                break;
            }

            isInSet = false;
        }
        // if both condition are true then FD is a BCNF violation with respect to relation
        if(firstCondition && secondCondition){
            System.out.println("BCNF violation: " + this.lhs() + "->" + this.rhs());
            return true;
        }

        return false;
    }
}