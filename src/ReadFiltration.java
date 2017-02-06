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
		String base = args[0];
		boolean dense = false; // switch between dense or sparse representations of matrices
		long startTime = System.currentTimeMillis();

		if(dense){
			DenseMatrix matrix = new DenseMatrix(readFiltration("input/"+base+".in"));
			matrix.sortVect();
			matrix.initMatrix();
			long initTime = System.currentTimeMillis();
			matrix.elimination();
			long reductionTime = System.currentTimeMillis();
			matrix.barcode("results/"+base+".out");
			long endTime = System.currentTimeMillis();
			System.out.println("Barcode construction on "+base+" using Dense Matrices took " + ((float)(endTime - startTime)/1000) + " seconds.");
			System.out.println("Initialisation: " + ((float)(initTime - startTime)/1000) + " seconds.");
			System.out.println("Reduction: " + ((float)(reductionTime - initTime)/1000) + " seconds.");
			System.out.println("Barcode extraction: " + ((float)(endTime - reductionTime)/1000) + " seconds.");
		}
		else{
			SparseMatrix matrix = new SparseMatrix(readFiltration("input/"+base+".in"));
			matrix.sortVect();
			matrix.initMatrix();
			long initTime = System.currentTimeMillis();
			matrix.elimination();
			long reductionTime = System.currentTimeMillis();
			matrix.barcode("results/"+base+".out");
			long endTime = System.currentTimeMillis();
			System.out.println("Barcode construction on "+base+" using Sparse Matrices took " + ((endTime - startTime)/1000) + " seconds.");
			System.out.println("Initialisation: " + ((initTime - startTime)/1000) + " seconds.");
			System.out.println("Reduction: " + ((reductionTime - initTime)/1000) + " seconds.");
			System.out.println("Barcode extraction: " + ((endTime - reductionTime)/1000) + " seconds.");
		}
	}
}
