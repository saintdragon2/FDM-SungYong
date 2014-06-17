import FDM.Fdm;
import FDM.Node;



public class Main {

	public static void main(String[] args) {
		System.out.println("Finite Difference Method - SungYong 2014 Summer");
		
		ReadFile readFile = new ReadFile("input/test.txt");
		
		Fdm fdm = readFile.getFdm();
		
		int solveTime = 3;
		fdm.solve(solveTime, true);
		
		for(int t=0; t<solveTime-1; t++){
			System.out.println("time : " + t*fdm.getDt() );
			for(int i=0; i<fdm.getRow(); i++){
				for(int j=0; j<fdm.getColummn(); j++){
					System.out.print(fdm.getNodes()[i*fdm.getColummn()+j].getValue(t)+ "\t");
				}
				System.out.println();
			}
			System.out.println();
		}
		
	}

}
