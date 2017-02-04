import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.*;

public class DenseMatrix {
	Vector<Simplex> F;
	int[][] matrix;
	HashMap<Integer,int[]> pivots = new HashMap<Integer,int[]>();
	HashMap<HashSet<Integer>,Integer> reverse = new HashMap<HashSet<Integer>,Integer>(); // from the list of vertices, find the index in F (post-sort)

	public DenseMatrix(Vector<Simplex> F){
		this.F=F;
	}

	// on trie le vecteur pour avoir un ordre, on trie selon les temps croissants
	// pas besoin de trier suivant une autre composante, on peut se contenter de garder le vecteur trié
	// vu que l'ordre arbitraire obtenu sera conserve quand on construira la matrice

	public void sortVect(){
		int length = F.size();
		F.sort(new Comparator<Simplex>(){
			public int compare(Simplex s1, Simplex s2)
		    {
		    	return Float.compare(s1.val,s2.val);
		    }
		});
		int size = F.size();
		for(int k = 0; k<size; k++){ // k est l'indice du simplexe qu'on examine en ce moment
			reverse.put(F.get(k).vert,k);
		}
	}

	// on remplit la matrice (matrice dense pour le moment)

	public void initMatrix(){
		int size = F.size();
		matrix = new int[size][size]; //matrice qu'on veut remplir
		Simplex simp;
		HashSet<Integer> vertices;
		HashSet<Integer> copy;

		for(int k = 0; k<size; k++){ // k est l'indice du simplexe qu'on examine en ce moment
			simp = F.get(k);
			if(simp.dim==0){ // pour un point
				continue;
			}

			vertices = simp.vert;
			copy = new HashSet<Integer>(vertices)	; // copy to iterate over vertices
			for(int vertex:copy){
				vertices.remove(vertex);
				matrix[k][reverse.get(vertices)] = 1;
				vertices.add(vertex);
			}
		}
	}

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


	public void reduction(){
		// on va stocker les pivots dans pivots, les clefs seront l'emplacement du 1 le plus bas
		int size = F.size();

		for(int i = 0; i<size; i++){
			int[] temp = matrix[i];
			int lastOne = getLastOne(temp);
			if(!pivots.containsKey(lastOne)){
				pivots.put(lastOne,temp);
			}
			else{
				int[] newPivot = reduce(temp);
				if(!containsOnlyZeros(newPivot)){
					pivots.put(getLastOne(newPivot), newPivot);
				}
				matrix[i] = newPivot;
			}
		}
	}

	public int getLastOne(int[] temp){
		int lastOne = -1;
		for(int j = 0; j<temp.length ; j++){
			if(temp[j]!=0){
				lastOne = j;
			}
		}
		return lastOne;
	}

	public boolean containsOnlyZeros(int[] list){
		for(int i=0; i<list.length; i++){
			if(list[i]!=0) return false;
		}
		return true;
	}

	public int[] reduce(int[] temp){ // a modifier pour qe ca marche a coup sur (j'ai l'impression qu'il faudra des double)
		int[] temp2 = temp;
		if(containsOnlyZeros(temp)){
			return temp;
		}
		else{
			int lastOne = getLastOne(temp);
			if(!pivots.containsKey(lastOne)){
				return temp;
			}
			else{
				int[] pivot = pivots.get(lastOne);
				for(int k=0; k<pivot.length;k++){
					temp2[k] = Math.abs(temp[k]);
					temp2[k] = Math.abs(temp2[k] - pivot[k]);
				}
				return reduce(temp2);
			}
		}
	}



	public void barcode(String output){
		try {
			PrintWriter writer = new PrintWriter(output , "UTF-8");
			for(int i=0; i<matrix.length;i++){
				if(containsOnlyZeros(matrix[i])){
					if(pivots.containsKey(i)){
						int indice = searchColumn(pivots.get(i));
						writer.println(F.get(i).dim+" "+F.get(i).val+" "+F.get(indice).val);
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

	public int searchColumn(int[] column){
		for(int i=0;i<matrix.length;i++){
			if(matrix[i].equals(column)){
				return i;
			}
		}
		return -1;
	}

}
