import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.*;

public class SparseMatrix {
	Vector<Simplex> F;
	ArrayList<LinkedList<Integer>> matrix; // on stocke les coordonnées où il y a des 1
	Hashtable<Integer,Integer> pivots;
	HashMap<FastHashSet<Integer>,Integer> reverse = new HashMap<FastHashSet<Integer>,Integer>();

	public SparseMatrix(Vector<Simplex> F){
		this.F=F;
		this.pivots = new Hashtable<Integer,Integer>();
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

	public LinkedList<Integer> reduce(LinkedList<Integer> column){
		int lastOne = column.isEmpty() ? (-1) : column.getLast();
		LinkedList<Integer> newColumn;
		LinkedList<Integer> currentPivot;
		int left, right;
		while(!column.isEmpty() && pivots.containsKey(lastOne)){
			newColumn = new LinkedList<Integer>();
			currentPivot = new LinkedList<Integer>(matrix.get(pivots.get(lastOne)));
			while(!column.isEmpty() && !currentPivot.isEmpty()){
				left = column.pollFirst();
				right = currentPivot.pollFirst();
				if(left==right){
					continue;
				}
				if(left<right){
					newColumn.addLast(left);
					currentPivot.addFirst(right);
				}
				else{
					newColumn.addLast(right);
					column.addFirst(left);
				}
			}
			while(!currentPivot.isEmpty()){
				newColumn.add(currentPivot.pollFirst());
			}
			while(!column.isEmpty()){
				newColumn.add(column.pollFirst());
			}
			column=newColumn;
			lastOne=column.isEmpty() ? (-1) : column.getLast();
		}
		return column;
	}

	public void reduction(){
		int size = F.size();

		for(int i=0;i<size;i++){
			LinkedList<Integer> temp = matrix.get(i);
			int lastOne = temp.isEmpty() ? (-1) : temp.getLast();
			if(!pivots.containsKey(lastOne)){
				pivots.put(lastOne, i);
			}
			else{
				LinkedList<Integer> newPivot = reduce(temp);
				if(!newPivot.isEmpty()){
					pivots.put(newPivot.getLast(), i);
				}
				matrix.set(i, newPivot);
			}
		}
	}




	public void barcode(String output){
		try {
			PrintWriter writer = new PrintWriter(output , "UTF-8");
			for(int i=0;i<matrix.size();i++){
				if(matrix.get(i).isEmpty()){
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
