package it.units.malelab.learningstl.BuildingBlocks.SignalBuilders;

import eu.quanticol.moonlight.signal.Signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UnsupervisedSignalBuilder implements SignalBuilder<Signal<Map<String, Double>>[]> {

    private final int windowSize = 200;
    private final Map<String, double[]> varsBounds = new HashMap<>();
    private double[] temporalBounds;

    // TODO: maybe fix Long Method
    // TODO: does not work at all with latest refactor, BEWARE!
    public List<Signal<Map<String, Double>>[]> parseSignals(String fileName) throws IOException {
        List<Signal<Map<String, Double>>[]> signals = new ArrayList<>();
        BufferedReader reader = this.createReaderFromFile(fileName);
        String[] header = reader.readLine().split(",");
        for (String var : header) {
            this.varsBounds.put(var, new double[]{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY});
        }
        this.temporalBounds = new double[]{0, 99};
        int vehicleIdx = 1;
        boolean isFinished = false;
        String[] line = new String[header.length + 1];
        List<Map<String, Double>> trajectory = new ArrayList<>();
        List<Long> times = new ArrayList<>();
        //String[] boolNames = boolIndexes.stream().map(i -> header[i]).toArray(String[]::new);
        //String[] doubleNames = doubleIndexes.stream().map(i -> header[i]).toArray(String[]::new);
        //boolean[] boolVars = new boolean[boolIndexes.size()];
        //double[] doubleVars = new double[doubleIndexes.size()];
        while (!isFinished) {
            while (true) {
                try {
                    line = reader.readLine().split(",");
                }
                catch (NullPointerException | IOException e) {
                    isFinished = true;
                    break;
                }
                //for (int idx=0; idx < boolIndexes.size(); ++idx) {
                //    boolVars[idx] = line[boolIndexes.get(idx)].equals("1");
                //}
                Map<String, Double> trajectoryRecord = new HashMap<>();
                for (int idx=0; idx < header.length; ++idx) {
                    double val = Double.parseDouble(line[idx]);
                    double[] temp = this.varsBounds.get(header[idx]);
                    temp[0] = Math.min(val, temp[0]);
                    temp[1] = Math.max(val, temp[1]);
                    //doubleVars[idx] = val;
                    trajectoryRecord.put(header[idx], val);
                }
                if (vehicleIdx != Integer.parseInt(line[14])) {
                    break;
                }
                trajectory.add(trajectoryRecord);//new TrajectoryRecord(boolVars, boolNames, doubleVars, doubleNames));
                times.add(Long.parseLong(line[15].trim()));
            }
            //if (line[14].equals("3")) {
            //    break;
            //}
            createSignalAndUpdateWithSlidingWindow(trajectory, times, signals);
            vehicleIdx = Integer.parseInt(line[14]);
            // TODO: THIS LINE IS STILL FAULTY!
            //trajectory.add(trajectoryRecord);//new TrajectoryRecord(boolVars, boolNames, doubleVars, doubleNames));
            times.add(Long.parseLong(line[15]));
        }
        reader.close();
        return signals;
    }

    @Override
    public Map<String, double[]> getVarsBounds() {
        return this.varsBounds;
    }

    @Override
    public double[] getTemporalBounds() {
        return this.temporalBounds;
    }

    private void createSignalAndUpdateWithSlidingWindow(List<Map<String, Double>> trajectory, List<Long> times,
                                              List<Signal<Map<String, Double>>[]> signals) {
        if (times.size() == 0) { // TODO: maybe more complete check
            return;
        }
        Signal<?>[] innerSignal = new Signal<?>[(trajectory.size() * 2) / this.windowSize];
        int length = times.size();
        int j = 0;
        int i;
        int t = 0;
        double time = 0.0;
        while (j < length) {
            Signal<Map<String, Double>> currSignal = new Signal<>();
            for (i = 0; i < this.windowSize && j < length; ++i, ++j) {
                currSignal.add(time, trajectory.get(j));
                ++time;
            }
            currSignal.endAt(time);
            ++time;
            innerSignal[t] = currSignal;
            j -= this.windowSize / 2;
            if (currSignal.size() != this.windowSize) {
                break;
            }
            ++t;
        }
        signals.add((Signal<Map<String, Double>>[]) innerSignal);
        trajectory.clear();
        times.clear();
    }

    private static void createSignalAndUpdate(List<Map<String, Double>> trajectory, List<Long> times,
                                              List<Signal<Map<String, Double>>> signals) {
        if (times.size() == 0) { // TODO: maybe more complete check
            return;
        }
        Signal<Map<String, Double>> currSignal = new Signal<>();
        int length = times.size();
        double time = 0.0;
        for (int i=0; i < length; ++i) {
            currSignal.add(time, trajectory.get(i));
            ++time;
        }
        currSignal.endAt(time);
        ++time;
        signals.add(currSignal);
        trajectory.clear();
        times.clear();
    }

}
