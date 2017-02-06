import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.*;

public class SparseMatrix {
	Vector<Simplex> F;
	ArrayList<LinkedList<Integer>> matrix; // an arraylist of columns, each represented by a linked of the positions where the matrix is evaluated to 1.
	HashMap<Integer,Integer> pivots; // mapping a line number to the column of the pivot on that line
	HashMap<HashSet<Integer>,Integer> reverse = new HashMap<HashSet<Integer>,Integer>();
	// mapping a simplex (seen as a set of integers) to its position in F (post-sort)

	public SparseMatrix(Vector<Simplex> F){
		this.F = F;
		this.pivots = new HashMap<Integer,Integer>();
	}

	// sorting the simplices by increasing values
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

	// computing the boundary matrix of the filtration
	public void initMatrix(){ // time complexity O(m * d²) where d is the largest dimension in F, thus O(m * (ln m)²), where m is the size of F.
		int size = F.size();
		matrix = new ArrayList<LinkedList<Integer>>();
		Simplex simp;
		HashSet<Integer> vertices;
		HashSet<Integer> copy;

		for(int k = 0; k<size; k++){ // k is the index of the current simplex
			simp = F.get(k);
			ArrayList<Integer> column = new ArrayList<Integer>(); // for now, column is an array list, this makes sorting faster
			if(simp.dim == 0){ // for a simple point, there is no need to fill the column
				matrix.add(new LinkedList<Integer>());
				continue;
			}

			vertices = simp.vert;
			copy = new HashSet<Integer>(vertices); // copy to iterate over vertices
			for(int vertex:copy){ // loop over copy.size() elements, which is O(ln m)
				vertices.remove(vertex);
				column.add(reverse.get(vertices));	// amortized time complexity of finding the index for vertices is vertices.size(), again O(ln m)
				vertices.add(vertex);
			}

			column.sort(new Comparator<Integer>(){
				public int compare(Integer s1, Integer s2)
			    {
			    	return Integer.compare(s1,s2);
			    }
			}); // sorting each column in time O((ln m) * (ln ln m)), this doesn't change the complexity
			LinkedList<Integer> columnFinal = new LinkedList<Integer>(column); // convert the arraylist to a linkedlist
			matrix.add(columnFinal);
		}
	}

	// auxiliary function which reduces one column
	public LinkedList<Integer> reduce(LinkedList<Integer> column){ // time complexity O(m * (ln m))
		int lastOne = column.isEmpty() ? (-1) : column.getLast();
		LinkedList<Integer> newColumn;
		LinkedList<Integer> currentPivot;
		int left, right;
		while(!column.isEmpty() && pivots.containsKey(lastOne)){
			// reduce the column until either it is empty or we found a new pivot. O(m) iterations since lastOne decreases
			newColumn = new LinkedList<Integer>();
			currentPivot = new LinkedList<Integer>(matrix.get(pivots.get(lastOne)));
			while(!column.isEmpty() && !currentPivot.isEmpty()){
				/* Adding two columns in Z/2Z can be done by computing the symmetric difference of the sets of ones in each column.
					 Algorithmically, this is the same as the merge part of a merge sort, expect for the fact that we drop duplicates entirely.
					 The operation in linear in the sum of the sizes of the columns. Since a column has length O(ln m), then the addition takes O(ln m). */
				left = column.pollFirst();
				right = currentPivot.pollFirst();
				if(left == right){
					continue;
				}
				if(left<right){
					newColumn.addLast(left);
					currentPivot.addFirst(right); // put it back for reconsideration next loop iteration
				}
				else{
					newColumn.addLast(right);
					column.addFirst(left); // put it back for reconsideration next loop iteration
				}
			}
			// once of the columns is empty, we empty the other into the new one
			while(!currentPivot.isEmpty()){
				newColumn.add(currentPivot.pollFirst());
			}
			while(!column.isEmpty()){
				newColumn.add(column.pollFirst());
			}
			column = newColumn;
			lastOne = column.isEmpty() ? (-1) : column.getLast();
		}
		return column;
	}

	// gaussian elemination on the matrix
	public void elimination(){ // time complexity O(m² * (ln m))
		int size = F.size();

		for(int i=0;i<size;i++){ // iterate over columns
			LinkedList<Integer> column = matrix.get(i);
			int lastOne = column.isEmpty() ? (-1) : column.getLast();
			if(!pivots.containsKey(lastOne)){ // if we found a new pivot we add it to the map
				pivots.put(lastOne, i);
			}
			else{ // otherwise we reduce the column
				LinkedList<Integer> newPivot = reduce(column);
				if(!newPivot.isEmpty()){ // if we found a new pivot we add it to the map
					pivots.put(newPivot.getLast(), i);
				}
				matrix.set(i, newPivot); // we update the column to its reduced form
			}
		}
	}



	// compute the barcode and write it to a file
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
