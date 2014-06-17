import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import FDM.Fdm;
import FDM.Node;


public class ReadFile {
	private boolean  isFinished = false;
	
	private Fdm fdm = null;
	private double dt = Double.NaN; 
	
	private ArrayList<Node> nodes;
	
	private String selectedFile;
	
	private int rows, cols;
	
	public ReadFile(String fileName){
		
		selectedFile = fileName;
		this.nodes = new ArrayList<Node>();
		
		try{
			this.read(selectedFile);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		Node[] nodesArr = this.list2Array(this.nodes);
		this.fdm = new Fdm( nodesArr, this.rows, this.cols);
		this.fdm.setDt(this.dt);
		
		for(Node node : this.fdm.getNodes() ){
			this.setNeighbors(node);
		}
	}
	
	public void read(String fileName) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName)));
		
		double diffCoeff = Double.NaN;
		double dx = Double.NaN;
		double dy = Double.NaN;
		this.dt = Double.NaN;
		
		String line;
		while((line = br.readLine() ) != null ){
			if( line.startsWith("diffusion coeff")){
				String[] infos = line.split(":");
				diffCoeff = Double.parseDouble( infos[1].trim() );
			}
			
			if( line.startsWith("dx")){
				String[] infos = line.split(":");
				dx = Double.parseDouble(infos[1].trim());
			}
			
			if( line.startsWith("dy")){
				String[] infos = line.split(":");
				dy = Double.parseDouble(infos[1].trim());
			}
			
			if( line.startsWith("dt")){
				String[] infos = line.split(":");
				this.dt = Double.parseDouble(infos[1].trim());
			}
			
			if( line.startsWith("Node Type")){
				line = this.readNodeType(br, diffCoeff, dx, dy);
			}
			
			if( line.startsWith("Initial Condition")){
				line = this.readInitCondition(br);
			}
		}
		
		
		
	}
	
	private Node[] list2Array(ArrayList<Node> list){
		Node[] nodes = new Node[ this.nodes.size() ];
		
		for(int i=0; i< nodes.length; i++){
			nodes[i] = this.nodes.get(i);
		}
		
		return nodes;
	}
	

	private void setNeighbors(Node node) {
		int northId = node.getID() - this.cols;
		int southId = node.getID() + this.cols;
		int westId = node.getID() - 1;
		int eastId = node.getID() + 1;
		
		Node northNode = this.fdm.getNodebyIdx(northId);
		Node southNode = this.fdm.getNodebyIdx(southId);
		Node westNode = this.fdm.getNodebyIdx(westId);
		Node eastNode = this.fdm.getNodebyIdx(eastId);
		
		node.setNeighborNodes(northNode, southNode, eastNode, westNode);
	}

	private String readInitCondition(BufferedReader br) throws IOException {
		String line;
		
		int idx = 0; 
		while((line = br.readLine())!= null && !this.isTitle(line)){
			String[] infos = line.split("\\s+");
			for(String info : infos){
				double value = Double.parseDouble(info.trim());
				this.nodes.get(idx).setInitialValue(value);
				idx++;
			}
		}
		return line;
	}

	private String readNodeType(BufferedReader br, double diffCoeff, double dx, double dy) throws IOException {
		
		String line;
		
		int idx = 1;
		
		double x = 0.0;
		double y = 0.0;
		
		while((line = br.readLine())!= null && !this.isTitle(line)){
			String[] infos = line.trim().split("\\s+");
			
			this.cols = infos.length;
			
			for(String info : infos){
				Node node = new Node(idx, info, dx, dy);
				
				node.setCoord(x, y);
				node.setDiffusionCoef(diffCoeff);
				nodes.add(node);
				
				idx++;
				
				x += dx;
			}
			
			x = 0.0;
			y += dy;
			
			this.rows++;
		}
		
		return line;
	}
	
	public Fdm getFdm(){
		return this.fdm;
	}

	private boolean isTitle(String line) {
		String[] titles = {
				"Node Type",
				"Initial Condition",
				"node id",
				"Result",
				"EOF"
		};
		
		for( String title : titles){
			if(line.startsWith(title)){
				return true;
			}
		}
		
		return false;
	}
}
