# Mining Road Traffic Rules with Signal Temporal Logic and Grammatical Evolution

This is the official repository for the Applied Sciences paper "Mining Road Traffic Rules with Signal Temporal Logic and Grammatical Evolution", hosting all the code necessary to replicate the experiments. This work is mostly based on Federico Pigozzi's master's thesis and was carried on at the Machine Learning Laboratory (MaLeLab) at the Department of Engineering and Architecture, University of Trieste (Italy).

## Scope
By running
```
mkdir output
java -cp libs/JGEA.jar:libs/moonlight.jar:libs/jblas-1.2.4.jar:target/STLRulesEvolutionaryInferenceNoClass.jar it.units.malelab.learningstl.Main {args}
```
where `{args}` is a placeholder for the arguments you must provide (see below), you will launch a grammar-based evolutionary optimization of the formula structure and parameters for Signal Temporal Logic rules of a real traffic dataset. At the same time, a number of evolution metadata will be saved inside the `output` folder. The project has been tested with Java `14.0.2`.

### Warning
On more recent Linux versions, dynamic libraries for jblas may not link properly. In this case, run the following commands:
```
unzip dlibs.zip
```
to unzip a directory containing the necessary .so files, then
```
sudo mv dlibs/libgfortran.so.3.0.0 /usr/local/lib/
sudo mv dlibs/libquadmath.so.0.0.0 /usr/local/lib/
sudo ldconfig
```
to move the .so files in the appropriate place and configure them.

## Structure
* `src` contains all the source code for the project;
* `libs` contains the .jar files for the dependencies (see below);
* `grammars` contains the .bnf files with the grammars;
* `data` contains a sample .csv data file with one vehicle trajectory (for space issues);
* `target` contains the main .jar file;
* `dlibs.zip` is an emergency kit for the aforementioned warning.

## Dependencies
The project relies on:
* [JGEA](https://github.com/ericmedvet/jgea), for the evolutionary optimization;
* [MoonLight](https://github.com/MoonLightSuite/MoonLight), for the monitoring of signal-temporal logic formulae.

The corresponding jars have already been included in the directory `libs`. See `pom.xml` for more details on dependencies.

## Usage
This is a table of possible command-line arguments:

Argument       | Type                                         | Optional (yes/no) | Default
---------------|----------------------------------------------|-------------------|-------------------------
seed           | integer                                      | no                | -
grammar        | string                                       | no                | -
input          | string                                       | yes               | -
output         | string                                       | yes               | ./output/
threads        | integer                                      | yes               | # available cores on CPU

where {...} denotes a finite and discrete set of possible choices for the corresponding argument. The description for each argument is as follows:
* seed: the random seed for the experiment;
* grammar: a (relative) path to the .bnf file for the grammar;
* input: a (relative) path to the .csv file containing the data;
* output: a (relative) path to the directory to save output files into;
* threads: the number of threads to perform evolution with. Defaults to the number of available cores on the current CPU. Parallelization is taken care by JGEA and implements a distributed fitness assessment.

## Bibliography
Please cite as
```
@article{pigozzi2021mining,
  title={Mining road traffic rules with signal temporal logic and grammar-based genetic programming},
  author={Pigozzi, Federico and Medvet, Eric and Nenzi, Laura},
  journal={Applied Sciences},
  volume={11},
  number={22},
  pages={10573},
  year={2021},
  publisher={MDPI}
}
```
