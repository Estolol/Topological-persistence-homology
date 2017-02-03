import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.*;

public class DenseMatrix {
	Vector<Simplex> F;
	int[][] matrix;
	Hashtable<Integer,int[]> pivots;
	
	public DenseMatrix(Vector<Simplex> F){
		this.F=F;
		this.pivots = new Hashtable<Integer,int[]>();
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
		// TODO youpi
	}
	
	// on remplit la matrice (matrice dense pour le moment)
	
	public void initMatrix(){
		//Vector<Simplex> vect = (Vector<Simplex>) F.clone();
		//ArrayList<TreeSet<Integer>> vectIndices = new ArrayList<TreeSet<Integer>>();
		int size = F.size();
		/*for(int i=0;i<size;i++){
			vectIndices.add(F.get(i).vert);
		}*/
		matrix = new int[size][size]; //matrice qu'on veut remplir
		
		for(int k = 0; k<size; k++){ // k est l'indice du simplexe qu'on examine en ce moment
			Simplex simp = F.get(k);
			if(simp.dim==0){ // pour un point
				int degree = 0;
				int j = k+1;
				int value = simp.vert.first().intValue();
				while(j<size && degree < 2){ // on regarde tous les segments (pas la peine de regarder les surfaces)
					Simplex tempSimp = F.get(j);
					if(tempSimp.dim==1){ // s'il s'agit bien d'un segment (et pas d'un point par exemple, même si on pourrait se passer de cette vérification en fait)
						if(tempSimp.vert.contains(value)){ // on regarde s'il contient le simplexe qu'on examine
							matrix[j][k]=1; // si oui, on met un 1 dans la matrice
						}
					}
					j++; // on passe au simplexe suivant (on compare toujours au même simplexe par contre)
					degree = tempSimp.dim;
				}
			}
			
			else{ // pour des dimensions superieures l'idee reste la meme
				//TreeSet<Integer> values = vectIndices.get(k);
				TreeSet<Integer> values = F.get(k).vert;
				int number = values.size();
				ArrayList<Integer> value = new ArrayList<Integer>();
				int indice = 0;
				while(!values.isEmpty()){
					int firstElement = values.first();
					value.add(firstElement);
					values.remove(firstElement);
				} // on a rempli une ArrayList avec les valeurs du simplexe qu'on examine
				int degree = simp.dim, degreefinal = simp.dim + 2;
				int j = k + 1;
				while(j<size && degree<degreefinal){ // on ne regarde que les elements de la dimension juste superieure
					Simplex tempSimp = F.get(j);
					if(tempSimp.dim==degreefinal-1){
						if(tempSimp.vert.containsAll(value)){ // si le simplexe contient tous les elements du simplexe que l'on examine
							matrix[j][k]=1; // on peut mettre un 1
						}
					}
					
					j++;
					degree=simp.dim;
				}
			}
		}
		//TODO hourra
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
		//TODO genial
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
		//TODO super
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
