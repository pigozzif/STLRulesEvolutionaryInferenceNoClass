package it.units.malelab.learningstl.BuildingBlocks.SignalBuilders;

import eu.quanticol.moonlight.signal.Signal;

import java.io.*;
import java.util.*;


public class SupervisedSignalBuilder implements SignalBuilder<Signal<Map<String, Double>>> {

    private final Map<String, double[]> varsBounds = new HashMap<>();
    private double[] temporalBounds;

    public double[] readVectorFromFile(String filePath) throws IOException {
        BufferedReader reader = this.createReaderFromFile(filePath);
        String[] line;
        try {
            line = reader.readLine().split(",");
        }
        catch (NullPointerException | IOException e) {  // TODO: might pretend that NullPointerException is not there
            return new double[0];
        }
        double[] out = new double[line.length];
        for (int i=0; i < line.length; ++i) out[i] = Double.parseDouble(line[i]);
        reader.close();
        return out;
    }

    public List<Signal<Map<String, Double>>> parseSignals(String path) throws IOException {
        List<Signal<Map<String, Double>>> signals = new ArrayList<>();
        double[] times = this.readVectorFromFile(path + "/times.csv");
        for (int i=0; i < times.length; ++i) {
            times[i] = i;
        }
        this.temporalBounds = new double[]{times[0], times[times.length - 1] / 2};
        BufferedReader reader = this.createReaderFromFile(path + "/data.csv");
        String[] header = reader.readLine().split(",");
        for (String var : header) {
            this.varsBounds.put(var, new double[]{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY});
        }
        this.computeVarsBounds(path, header);
        double[] varsData;
        //boolean[] dummy = new boolean[0];
        int numVars = header.length;
        //String[] boolNames = boolIndexes.stream().map(i -> header[i]).toArray(String[]::new);
        //String[] doubleNames = doubleIndexes.stream().map(i -> header[i]).toArray(String[]::new);
        Signal<Map<String, Double>> currSignal = new Signal<>();
        while (true) {
            try {
                varsData = Arrays.stream(reader.readLine().split(",")).mapToDouble(Double::parseDouble).toArray();
            }
            catch (NullPointerException | IOException e) {
                break;
            }
            int k = 0;
            for (int i=0; i < varsData.length; i+=numVars) {
                Map<String, Double> trajectoryRecord = new HashMap<>();
                for (int j=0; j < numVars; ++j) {
                    double[] bounds = this.varsBounds.get(header[j]);
                    trajectoryRecord.put(header[j], (varsData[i + j] - bounds[0]) / (bounds[1] - bounds[0]));
                    //currData[j] = varsData[i + j];
                }
                currSignal.add(times[k++], trajectoryRecord);//new TrajectoryRecord(dummy, boolNames, currData, doubleNames));
            }
            signals.add(currSignal);
            currSignal = new Signal<>();
        }
        reader.close();
        return signals;
    }

    private void computeVarsBounds(String path, String[] header) throws IOException {
        BufferedReader reader = this.createReaderFromFile(path + "/data.csv");
        reader.readLine();
        double[] varsData;
        while (true) {
            try {
                varsData = Arrays.stream(reader.readLine().split(",")).mapToDouble(Double::parseDouble).toArray();
            }
            catch (NullPointerException | IOException e) {
                break;
            }
            for (int i=0; i < varsData.length; i+=header.length) {
                for (int j=0; j < header.length; ++j) {
                    double[] temp = this.varsBounds.get(header[j]);
                    temp[0] = Math.min(varsData[i + j], temp[0]);
                    temp[1] = Math.max(varsData[i + j], temp[1]);
                }
            }
        }
        reader.close();
    }

    public Map<String, double[]> getVarsBounds() {
        return this.varsBounds;
    }

    public double[] getTemporalBounds() {
        return this.temporalBounds;
    }

}
