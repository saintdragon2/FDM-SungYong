package FDM;

import java.util.ArrayList;

public class Node {
	
	private int id;
	protected String nodeType;
	protected double initialValue;
	
	private double[] coord = new double[2];
	private double dx, dy;
	
	protected double diffusionCoeff;
	
	protected Node northNode, southNode, eastNode, westNode;
	
	protected ArrayList<Double> values;
	
	protected double last;
	
//	protected double[] values;
//	public double present, next;
	
	public Node(int id, String nodeType, double dx, double dy){
		this.id = id;
		this.nodeType = nodeType;
		this.dx = dx;
		this.dy = dy;
		this.values = new ArrayList<Double>();
	}
	
	public void setCoord(double x, double y){
		this.coord[0] = x;
		this.coord[1] = y;
	}
	
	public double[] getCoord(){
		return this.coord;
	}
	
	public double getX(){
		return this.coord[0];
	}
	
	public double getY(){
		return this.coord[1];
	}
	
	public String getType(){
		return this.nodeType.toUpperCase();
	}
	
	public void setDiffusionCoef(double coeff){
		this.diffusionCoeff = coeff;
	}
	
	public double getDiffusionCoeff(){
		return this.diffusionCoeff;
	}
	
	public void setInitialValue(double value){
		this.initialValue = value;
		if( values.size() < 1 ){
//			present = value;
			values.add(this.initialValue);
		}
	}
	
	public Node[] getNeighbors(){
		Node[] neighbors = new Node[4];
		
		neighbors[0] = northNode;
		neighbors[1] = southNode;
		neighbors[2] = eastNode;
		neighbors[3] = westNode;
		
		return neighbors;
	}
	
	public double getInitialValue(){
		return this.initialValue;
	}
	
	public void add(double value){
		this.values.add(value);
	}
	
//	public void update(){
////		this.values.add(this.present);
//	}
	
	public int solvedTime(){
		return this.values.size();
	}
	
//	public double distance(Node other){
//		double dX = other.getX() - this.getX();
//		double dY = other.getY() - this.getY();
//		
//		return Math.sqrt(dX*dX + dY*dY);
//	}
	
	public boolean isBoundary(){
		if(this.isEssentialBoundary() || this.isNaturalBoundary() || this.nodeType.toUpperCase().startsWith("B")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean isEssentialBoundary(){
		if(this.nodeType.toUpperCase().startsWith("E")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean isDomain(){
		return this.nodeType.toUpperCase().startsWith("D");
	}
	
	public boolean isNaturalBoundary(){
		if(this.nodeType.toUpperCase().startsWith("N")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean isEmptyNode(){
		if(this.nodeType.toUpperCase().startsWith("X")){
			return true;
		}
		else{
			return false;
		}
	}
	
//	public double previous(){
//		return this.values.get( this.values.size() - 1 );
//	}
	
	public double values(int iter){
		return this.values.get(iter);
	}
	
	public void initializeValues(){
//		double initialValue = this.values(0);
		this.values.clear();
		this.values.add(this.initialValue);
//		this.setInitialValue(this.initialValue);
	}
	
	public double getValue(int t){
		return this.values(t);
	}
	
	public ArrayList<Double> getValues(){
		return this.values;
	}
	
	public int getID(){
		return this.id;
	}
	
	public String getNodeType(){
		return this.nodeType;
	}
	
	public double getDx(){
		return this.dx;
	}
	
	public double getDy(){
		return this.dy;
	}
	
	public double distanceFromANode(Node node){
		double otherX = node.getX();
		double otherY = node.getY();
		
		double distX = otherX - this.getX();
		double distY = otherY - this.getY();
		
		double distance = Math.sqrt( distX*distX + distY*distY  );
		
		return distance;
	}
	
	protected double fourierNumber(double diffusionCoefficient, double deltaTime){
		double fourierNumber = diffusionCoefficient * deltaTime/ (dx * dy);
		
		return fourierNumber;
	}
	
	public void setNeighborNodes(Node northNode, Node southNode, Node eastNode, Node westNode){
		this.northNode = northNode;
		this.southNode = southNode;
		this.eastNode = eastNode;
		this.westNode = westNode;
	}
	
	public double last(){
		return this.last;
	}
	
	public void preSolve(){
		this.last = this.values.get( this.values.size()-1 );
	}
	
	public void solve( double deltaTime){
		double fourierNumber = this.fourierNumber( this.diffusionCoeff, deltaTime);
		
		if (fourierNumber > 0.25) {
			System.err.println("Fourier Number is Too big. It must be less than 0.25 : " +fourierNumber + "\t D = " + this.diffusionCoeff + "\t Delta Time = " + deltaTime);
			System.exit(1);
			
			return;
		}

		if ( this.isBoundary() ) { //E:Essential Boundary
//			this.next = this.present();
			this.values.add(this.last);
		}
		else{
			double north = northNode.last;
			double south = southNode.last;
			double west = westNode.last;
			double east = eastNode.last;
			this.values.add( fourierNumber*(north+south+west+east) + (1-4*fourierNumber)*this.last );	
		}
	}
	
	public boolean hasSameHistory(ArrayList<Double> history){
		
		
		if( this.getValues().size() != history.size() ){
			System.err.println("Different length of history.");
			System.out.println(this.getValues().size() + "\t" + history.size());
			return false;
		}
		
		for(int i=0; i< history.size(); i++){
			double myValue = Double.parseDouble(String.format("%.13f", this.values(i) ));
			double othersValue = Double.parseDouble(String.format("%.13f", history.get(i) ) );
			
			if( othersValue != myValue){
				System.err.println("Different value at " + i + "th time.");
				System.err.println(history.get(i) +"\t" + this.getValue(i) );
				return false;
			}
		}
		
		return true;
	}
	
	
	public boolean hasSameHistory(Node otherNode){
		return this.hasSameHistory(otherNode.getValues());
	}
	
	@Override
	public String toString(){
		return this.id + "\t" + this.coord[0] + "\t" + this.coord[1];
	}
}
