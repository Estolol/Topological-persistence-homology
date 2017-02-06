import java.util.*;

// provided code of a class to represent a simplex
class Simplex {
	float val;
	int dim;
	HashSet<Integer> vert;

	Simplex(Scanner sc){
		val = sc.nextFloat();
		dim = sc.nextInt();
		vert = new HashSet<Integer>(); // we use HashSet instead of TreeSet for constant time add and remove
		for (int i=0; i<=dim; i++)
			vert.add(sc.nextInt()+1);
	}

	public String toString(){
		return "{val="+val+"; dim="+dim+"; "+vert+"}\n";
	}

}
