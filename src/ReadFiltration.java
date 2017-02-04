import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ReadFiltration {

	// provided code that turns an input filtration into a vector of simplices
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
		String input = args[0];
		String output = "output.txt";
		boolean dense = true; // switch between dense or sparse representations of matrices
		if(dense){
			DenseMatrix matrix = new DenseMatrix(readFiltration(input));
			matrix.sortVect();
			matrix.initMatrix();
			//matrix.reduction();
			//matrix.barcode(output);
			//System.out.println(matrix.F);
			System.out.println(matrix.toString());
			//System.out.println(Arrays.deepToString(matrix.matrix));
		}
		else{
			/* SparseMatrix matrix = new SparseMatrix(readFiltration(input));
			matrix.sortVect();
			matrix.initMatrix();
			matrix.reduction();
			matrix.barcode(output);
			//System.out.println(matrix.F);
			System.out.println(Arrays.deepToString(matrix.matrix));
			*/
		}
	}
}
