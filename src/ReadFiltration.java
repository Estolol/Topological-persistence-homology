import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.Vector;



class Simplex {
	float val;
	int dim;
	TreeSet<Integer> vert;

	Simplex(Scanner sc){
		val = sc.nextFloat();
		dim = sc.nextInt();
		vert = new TreeSet<Integer>();
		for (int i=0; i<=dim; i++)
			vert.add(sc.nextInt());
	}

	public String toString(){
		return "{val="+val+"; dim="+dim+"; "+vert+"}\n";
	}

}

public class ReadFiltration {

	static Vector<Simplex> readFiltration (String filename) throws FileNotFoundException {
		Vector<Simplex> F = new Vector<Simplex>();
		Scanner sc = new Scanner(new File(filename));
		sc.useLocale(Locale.US);
		while (sc.hasNext())
			F.add(new Simplex(sc));
		sc.close();
		return F;
	}

	public static void main(String[] args) throws FileNotFoundException {
		String input = "input.txt";
		String output = "output.txt";
			
		DenseMatrix matrix = new DenseMatrix(readFiltration(input));
		//SparseMatrix matrix = new SparseMatrix(readFiltration(input));
		matrix.sortVect();
		matrix.initMatrix();
		matrix.reduction();
		matrix.barcode(output);
		//System.out.println(matrix.F);
		//System.out.println(Arrays.deepToString(matrix.matrix));
	}
}
