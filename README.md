# STLRulesEvolutionaryInferenceNoClass

This is the official repository for the Applied Sciences paper "Mining Road Traffic Rules with Signal Temporal Logic and Grammatical Evolution", hosting all the code necessary to replicate the experiments. This work is mostly based on Federico Pigozzi's master's thesis and was carried on at the Machine Learning Laboratory (MaLeLab) at the Department of Engineering and Architecture, University of Trieste (Italy).

## Scope

## Structure
* `src` contains all the source code for the project;
* `libs` contains the .jar files for the dependencies (see below);
* `target` contains the main .jar file.

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
input          | string                                       | yes               | ./data/traffic.csv
output         | string                                       | yes               | ./output/
threads        | integer                                      | yes               | # available cores on CPU

The description for each argument is as follows:
* seed: the random seed for the experiment;
* grammar: a (relative) path to the .bnf file for the grammar;
* input: a (relative) path to the .csv file containing the traffic data;
* output: a (relative) path to the directory to save output files into;
* threads: the number of threads to perform evolution with. Defaults to the number of available cores on the current CPU. Parallelization is taken care by JGEA and implements a distributed fitness assessment.

## Bibliography
TODO, if accepted