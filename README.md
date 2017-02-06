# Topological Persistence (Project for [INF563 Topological Data Analysis](https://moodle.polytechnique.fr/enrol/index.php?id=2650) @ Ecole polytechnique)

[Link](http://www.enseignement.polytechnique.fr/informatique/INF563/TD5/index.html) to the project description.

## File structure

**src** contains the *.java* source files.  
**input** contains the input files (some provided, some generated, some written) with extension *.in*.  
**results** contains some of the results obtained by running the algorithm on the inputs. These are *.out* files. Of note are filtered (see below) versions of the ouputs on some of the large datasets provided with the assignment.  
**generate_d_sphere_ball.py** is a python script that generates input files for the d-sphere and the d-ball.  
**filter_barcode.py** is a python script that creates a filtered version of an output file file by limiting it to barcodes that are longer than a specified float.

## Code structure

**Simplex** is the provided class for parsing and storing simplices.  
**ReadFiltration** is the provided class for reading inputs. It contains the main function.  
**DenseMatrix** is the first implementation of the algorithm. It relies on dense matrices and thus lacks in performances, it will fail for large inputs.  
**SparseMatrix** is the second, more reliable implementation, based on sparse matrices.  

## Usage

**Compiling**
~~~~
javac -d bin src/*.java
~~~~

**Running** (for instance on *input/filtration_A.in*)
~~~~
java -cp bin ReadFiltration filtration_A
~~~~
## Performances

**filtration_A** *12M*
~~~~
Barcode construction on filtration_A using Sparse Matrices took 96 seconds.
Initialisation: 5 seconds.
Reduction: 91 seconds.
Barcode extraction: 0 seconds.
~~~~

**filtration_B** *2,6M*
~~~~
Barcode construction on filtration_B using Sparse Matrices took 5 seconds.
Initialisation: 1 seconds.
Reduction: 4 seconds.
Barcode extraction: 0 seconds.
~~~~

**filtration_C** *4,1M*
~~~~
Barcode construction on filtration_C using Sparse Matrices took 15 seconds.
Initialisation: 4 seconds.
Reduction: 10 seconds.
Barcode extraction: 0 seconds.
~~~~

**filtration_D** *65M*
~~~~
Barcode construction on filtration_D using Sparse Matrices took 668 seconds.
Initialisation: 218 seconds.
Reduction: 449 seconds.
Barcode extraction: 0 seconds.
~~~~
