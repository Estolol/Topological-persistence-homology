import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.*;

public class DenseMatrix {
	Vector<Simplex> F;
	int[][] matrix;
	HashMap<Integer,Integer> pivots = new HashMap<Integer,Integer>();
	HashMap<HashSet<Integer>,Integer> reverse = new HashMap<HashSet<Integer>,Integer>(); // from a set of vertices, find the index in F (post-sort)
	boolean[] isZeroes; // store whether matrix[i] is all zeroes or not
	int[] lastOne; // store the largest j such that matrix[i][j] isn't zero

	public DenseMatrix(Vector<Simplex> F){
		this.F=F;
		isZeroes = new boolean[F.size()];
		lastOne = new int[F.size()];
	}

	// we sort the input vector by increasing value

	public void sortVect(){
		int length = F.size();
		F.sort(new Comparator<Simplex>(){
			public int compare(Simplex s1, Simplex s2)
		    {
		    	return Float.compare(s1.val,s2.val);
		    }
		}); // we compare Simplices using Float.compare on the values of the filtration
		int size = F.size();
		for(int k = 0; k<size; k++){ // k is the index of the current simplex
			reverse.put(F.get(k).vert,k);
		}
	}

	// filling the matrix

	public void initMatrix(){ // time complexity n * d where d is the largest dimension in F, thus n * (ln n), where n is the size of F.
		int size = F.size();
		matrix = new int[size][size];
		Simplex simp;
		HashSet<Integer> vertices;
		HashSet<Integer> copy;
		int index;
		for(int k = 0; k<size; k++){ // k is the index of the current simplex
			simp = F.get(k);
			isZeroes[k] = true;
			lastOne[k] = (-1);
			if(simp.dim!=0){ // ignore points
				vertices = simp.vert;
				copy = new HashSet<Integer>(vertices); // copy to iterate over vertices
				for(int vertex:copy){
					vertices.remove(vertex);
					index = reverse.get(vertices);
					matrix[k][index] = 1;
					vertices.add(vertex);
					isZeroes[k] = false;
					lastOne[k] = (index>lastOne[k]) ? index : lastOne[k];
				}
			}
		}
	}

	// pretty printing the matrix
	@Override
	public String toString(){
		String out = "";
		for(int i=0; i<F.size();i++){
			for(int j=0; j<F.size()-1;j++){
				out += matrix[j][i]+" ";
			}
			out += matrix[F.size()-1][i]+"\n";
		}
		return out;
	}


	public void reduce(int index){
		int current;
		boolean empty;
		int last;
		while(!isZeroes[index] && pivots.containsKey(lastOne[index])){
			current = pivots.get(lastOne[index]);
			empty = true;
			last = (-1);
			for(int j=0; j<=lastOne[index]; j++){
				matrix[index][j] = (matrix[index][j] == matrix[current][j]) ? 0 : 1;
				if(matrix[index][j]==1){
					empty = false;
					last = j;
				}
			}
			isZeroes[index] = empty;
			lastOne[index] = last;
		}
	}

	public void reduction(){
		int size = F.size();
		for(int i = 0; i<size; i++){
			if(!pivots.containsKey(lastOne[i])){
				pivots.put(lastOne[i],i);
			}
			else{
				reduce(i);
				if(!isZeroes[i]){
					pivots.put(lastOne[i], i);
				}
			}
		}
	}


	public void barcode(String output){
		try {
			PrintWriter writer = new PrintWriter(output , "UTF-8");
			for(int i=0; i<matrix.length;i++){
				if(isZeroes[i]){
					if(pivots.containsKey(i)){
						writer.println(F.get(i).dim+" "+F.get(i).val+" "+F.get(pivots.get(i)).val);
					}
					else{
						writer.println(F.get(i).dim+" "+F.get(i).val+" inf");
					}
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
