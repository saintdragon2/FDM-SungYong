package FDM;

import java.util.ArrayList;

public class Fdm {
	
	protected Node[] nodes;
	protected int rows, columns;
	
	private double dt; // delta Time
	
	private double centerX, centerY, width, height;
	
	protected String type;
	
	private boolean isSolved = false;
	private double solvedYears[] = null;  
	
	protected String problemType = "";
	
	protected Node[] trackingNodes;
	
	
	public Fdm(Node[] nodes, int rows, int columns){
		this.nodes = nodes;
		
		this.rows = rows;
		this.columns = columns;
		
		this.setNodesGeometry();
	}
	
	public void setProblemType(String problemType){
		this.problemType = problemType;
	}
	
	public void setTrackingNodes(Node[] trackingNodes){
		this.trackingNodes = trackingNodes;
	}
	
	public Node[] getTrackingNodes(){
		return this.trackingNodes;
	}
	
	private double minDiffusionCoeff(){
		double min = Double.POSITIVE_INFINITY;
		
		for( Node node: this.nodes){
			if(node.getDiffusionCoeff() < min){
				min = node.getDiffusionCoeff();
			}
		}

		return min;
	}
	
	private double dt(double fourierNumber, double minDiffCoeff){
		return fourierNumber *  nodes[0].getDx() * nodes[0].getDy() / minDiffCoeff;
	}
	
	public void solve(int iteration, boolean showProcess) {
		
		this.solvedYears = new double[iteration];
		
		double time = 0.0;
		
		for(int i=0; i< iteration; i++){
//			double dt = 0.045;//this.dt(fourierNumber, minDiffCoeff);
			
			
			for(Node node: this.nodes){
				node.preSolve();
			}
			
			for( Node node : this.nodes){
				node.solve(dt);
			}
			time += dt; 
			this.solvedYears[i]= time;
			
			if( showProcess ){
				System.out.println((i*1.0)/iteration * 100 +"%");
			}
		}
		
		
		this.isSolved = true;
		System.out.println("Solved for " + time + " later.");
	}
	
	private void setNodesGeometry(){
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		
		Node maxXNode = null;
		Node minXNode = null;
		Node maxYNode = null;
		Node minYNode = null;
		
		int i=0; 
		for( Node node : this.nodes){
			if( node.getX() < minX){
				minXNode = node;
				minX = node.getX() - node.getDx() /2.0;
			}
			if( node.getY() < minY){
				minYNode = node;
				minY = node.getY() - node.getDy() / 2.0;
			}
			if( node.getX() > maxX){
				maxXNode = node;
				maxX = node.getX() + node.getDx() / 2.0;
			}
			if( node.getY() > maxY ){
				maxYNode = node;
				maxY = node.getY() + node.getDy() / 2.0;
			}
			i++;
		}
		
		this.centerX = (maxX + minX)/2.0;
		this.centerY = (maxY + minY)/2.0;
		
		this.width = maxX - minX;
		this.height = maxY - minY;
	}
	
	public double getCenterX(){
		return this.centerX;
	}
	
	public double getCenterY(){
		return this.centerY;
	}
	
	public double getWidth(){
		return this.width;
	}
	
	public double getHeight(){
		return this.height;
	}
	
	public Node[] getNodes(){
		return this.nodes;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean isSolved(){
		return this.isSolved;
	}
	
	public int getRow(){
		return this.rows;
	}
	
	public double getDt(){
		return this.dt;
	}
	
	public int getColummn(){
		return this.columns;
	}
	
	public double[] getSolvedTimes(){
		return this.solvedYears;
	}
	
	public Node[] getBoundaryNodes(){
		ArrayList<Node> boundaryNodes = new ArrayList<Node>();
		
		for(Node node : this.nodes){
			if( node.isBoundary() ){
				boundaryNodes.add(node);
			}
		}
		
		return (Node[]) boundaryNodes.toArray(new Node[0]);
	}
	
	public Node[] getDomainNodes(){
		ArrayList<Node> domainNodes = new ArrayList<Node>();
		
		for(Node node : this.nodes){
			if( node.isDomain() ){
				domainNodes.add(node);
			}
		}
		
		return (Node[]) domainNodes.toArray(new Node[0]);
	}
	
	public Node getNodebyIdx(int idx){
		for(Node node : this.nodes){
			if( node.getID() == idx ){
				return node;
			}
		}
		return null;
	}
		
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("type: " + this.type + "\n");
		
		str.append("dx: " + this.nodes[0].getDx() + "\n");
		str.append("dy: " + this.nodes[0].getDy() + "\n");
		
		str.append("tracking node: ");
		if( this.trackingNodes != null){
			for(Node node : this.trackingNodes){
				if( node != null ){
					str.append(node.getID() + ", ");
				}
				else{
					str.append("Not existing node");
				}
			}
		}
		str.append("\n");
		
		str.append("Node Type --[E:Essential Boundary]--[N:Natural Boundary]--[D:Domain]--\n");
		
		for(int i=0; i< rows; i++){
			for( int j=0; j<columns; j++){
				str.append(this.nodes[i*columns+j].getNodeType() + "\t");
			}
			str.append("\n");
		}
		
		str.append("Initial Contidion\n");
		for( int i=0; i<rows; i++){
			for( int j=0; j<columns; j++){
				str.append(this.nodes[i*columns+j].getInitialValue()+"\t");
			}
			str.append("\n");
		}
		
		str.append("diffusion coeff.\n");
		for(int i=0; i<rows; i++){
			for( int j=0; j< columns; j++){
				Node node = this.nodes[i*columns+j];
				
				if( node.isDomain() ){
					str.append(node.getDiffusionCoeff() + "\t");
				}
				else{
					str.append("-\t");
				}
			}
			str.append("\n");
		}
		
		str.append("node id\n");
		for(int i=0; i< rows; i++){
			for( int j=0; j< columns; j++){
				Node node = this.nodes[i*columns+j];
				
				str.append(node.getID() + "\t");
			}
			str.append("\n");
		}
		
		return str.toString();
	}

	public void setDt(double dt) {
		this.dt = dt;
	}
}
