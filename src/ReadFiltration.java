import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

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
		boolean dense = true;
		if(dense){
			DenseMatrix matrix = new DenseMatrix(readFiltration(input));
			matrix.sortVect();
			matrix.initMatrix();
			//matrix.reduction();
			//matrix.barcode(output);
			//System.out.println(matrix.F);
			System.out.println(Arrays.deepToString(matrix.matrix));
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
