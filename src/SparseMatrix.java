import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.*;

public class SparseMatrix {
	Vector<Simplex> F;
	ArrayList<LinkedList<Integer>> matrix; // on stocke les coordonnées où il y a des 1
	Hashtable<Integer,LinkedList<Integer>> pivots;
	HashMap<FastHashSet<Integer>,Integer> reverse = new HashMap<FastHashSet<Integer>,Integer>();
	
	public SparseMatrix(Vector<Simplex> F){
		this.F=F;
		this.pivots = new Hashtable<Integer,LinkedList<Integer>>();
	}
	
	// on trie le vecteur pour avoir un ordre, on trie selon les temps croissants
		// pas besoin de trier suivant une autre composante, on peut se contenter de garder le vecteur trié
		// vu que l'ordre arbitraire obtenu sera conserve quand on construira la matrice
		
	public void sortVect(){
		int length = F.size();
		F.sort(new Comparator<Simplex>(){
			public int compare(Simplex s1, Simplex s2)
		    {
		    	float v1 = s1.val , v2 = s2.val;
		    	if(v1<v2) return -1;
		    	if(v1 == v2) return 0;
		    	if(v1>v2) return 1;
		    	return 0;
		    }
		});
		int size = F.size();
		for(int k = 0; k<size; k++){ // k est l'indice du simplexe qu'on examine en ce moment
			reverse.put(F.get(k).vert,k);
		}
		// TODO youpi
	}

	
	public void initMatrix(){ // time complexity n * d where d is the largest dimension in F, thus n * (ln n), where n is the size of F.
		int size = F.size();
		matrix = new ArrayList<LinkedList<Integer>>(); //matrice qu'on veut remplir
		Simplex simp;
		FastHashSet<Integer> vertices;
		FastHashSet<Integer> copy;

		for(int k = 0; k<size; k++){ // k est l'indice du simplexe qu'on examine en ce moment
			simp = F.get(k);
			ArrayList<Integer> column = new ArrayList<Integer>();
			if(simp.dim==0){ // for a simple point, there is no need to fill the column
				matrix.add(new LinkedList<Integer>());
				continue;
			}

			vertices = simp.vert;
			copy = new FastHashSet<Integer>(vertices); // copy to iterate over vertices
			for(int vertex:copy){
				vertices.remove(vertex);
				column.add(reverse.get(vertices));	// amortized time complexity of get on a FastHashSet is constant
				vertices.add(vertex);
			}
			
			column.sort(new Comparator<Integer>(){
				public int compare(Integer s1, Integer s2)
			    {
			    	return Integer.compare(s1,s2);
			    }
			});
			LinkedList<Integer> columnFinal = new LinkedList<Integer>(column);
			matrix.add(columnFinal);
		}
	}
	
	public void reduction(){
		int size = F.size();
		
		for(int i=0;i<size;i++){
			LinkedList<Integer> temp = matrix.get(i);
			int lastOne = getLastOne(temp);
			if(!pivots.containsKey(lastOne)){
				pivots.put(lastOne, temp);
			}
			else{
				LinkedList<Integer> newPivot = reduce(temp);
				if(!newPivot.isEmpty()){
					pivots.put(getLastOne(newPivot), newPivot);
				}
				matrix.set(i, newPivot);
			}
		}
	}
	
	public int getLastOne(LinkedList<Integer> temp){
		if(!temp.isEmpty()) return (int) temp.get(temp.size()-1);
		else return -1;
	}
	
	public int[] toList(LinkedList<Integer> temp){
		int size = F.size();
		int[] result = new int[size];
		for(int i = 0; i<temp.size();i++){
			int index = (int) temp.get(i);
			result[index]=1;
		}
		return result;
	}
	
	public LinkedList<Integer> toArray(int[] temp){
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(int i=0; i<temp.length;i++){
			if(temp[i]!=0){
				result.add(i);
			}
		}
		return result;
	}
	
	public LinkedList<Integer> normalize(LinkedList<Integer> temp){
		if(temp.isEmpty()) return temp;
		int lastOne = getLastOne(temp);
		for(int i=0; i<temp.size();i++){
			temp.get(i)[1] = Math.abs(temp.get(i)[1] / temp.get(temp.size()-1)[1]);
		}
		return temp;
	}
	
	public LinkedList<Integer> reduce(LinkedList<Integer> column){
		LinkedList<Integer> copy = column;
		int lastOne = getLastOne(column);
		while(!copy.isEmpty() && pivots.containsKey(lastOne)){
			LinkedList<Integer> newPivot = column;
			LinkedList<Integer> currentPivot = pivots.get(lastOne);
			for(int k=0;k<currentPivot.size();k++){
				int value = currentPivot.get(k);
				if(newPivot.remove((Integer) value));
				else{
					
				}
			}
		}
		return column;
		if(column.isEmpty()) return column;
		int lastOne = getLastOne(column);
		
		if(!pivots.containsKey(lastOne)){
			return column;
		}
		else{
			LinkedList<Integer> newPivot = column;
			Integer currentPivot = toList(pivots.get(lastOne));
			for(int k=0;k<lastOne;k++){
				newPivot[k] = newPivot[k] / newPivot[lastOne] - currentPivot[k];
			}
			newPivot[lastOne] = 0;
			LinkedList<Integer> newPivotArray = normalize(toArray(newPivot));
			return reduce(newPivotArray);
		}
	}
	
	public void barcode(String output){
		try {
			PrintWriter writer = new PrintWriter(output , "UTF-8");
			for(int i=0;i<matrix.size();i++){
				if(matrix.get(i).isEmpty()){
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
	
	public int searchColumn(LinkedList<Integer> column){
		for(int i=0;i<matrix.size();i++){
			if(matrix.get(i).equals(column)){
				return i;
			}
		}
		return -1;
	}
}