package it.units.malelab.learningstl.BuildingBlocks.SignalBuilders;

import eu.quanticol.moonlight.signal.Signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UnsupervisedSignalBuilder implements SignalBuilder<Signal<double[]>[]> {

    private final int windowSize = 200;
    private final Map<String, double[]> varsBounds = new HashMap<>();
    private double[] temporalBounds;
    public static final Map<String, Integer> fromVarToIdx = new HashMap<>();

    public List<Signal<double[]>[]> parseSignals(String fileName) throws IOException {
        List<Signal<double[]>[]> signals = new ArrayList<>();
        BufferedReader reader = this.createReaderFromFile(fileName);
        String[] header = reader.readLine().split(",");
        int i = 0;
        for (String var : header) {
            if (var.equals("Vehicle_ID") || var.equals("Global_Time")) {
                continue;
            }
            this.varsBounds.put(var, new double[]{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY});
            fromVarToIdx.put(var, i++);
        }
        String[] line;
        while (true) {
            try {
                line = reader.readLine().split(",");
            }
            catch (NullPointerException | IOException e) {
                break;
            }
            for (int idx=2; idx < header.length; ++idx) {
                double val;
                if (line[idx].equals("inf")) {
                    val = Double.MAX_VALUE;
                }
                else {
                    val = Double.parseDouble(line[idx]);
                }
                double[] temp = this.varsBounds.get(header[idx]);
                temp[0] = Math.min(val, temp[0]);
                temp[1] = Math.max(val, temp[1]);
            }
        }
        reader.close();
        reader = this.createReaderFromFile(fileName);
        reader.readLine();
        this.temporalBounds = new double[]{0, 99};
        int vehicleIdx = 1;
        boolean isFinished = false;
        List<double[]> trajectory = new ArrayList<>();
        List<Long> times = new ArrayList<>();
        while (!isFinished) {
            while (true) {
                try {
                    line = reader.readLine().split(",");
                }
                catch (NullPointerException | IOException e) {
                    isFinished = true;
                    break;
                }
                double[] trajectoryRecord = new double[header.length - 2];
                for (int idx=2; idx < header.length; ++idx) {
                    double val;
                    if (line[idx].equals("inf")) {
                        val = Double.MAX_VALUE;
                    }
                    else {
                        val = (Double.parseDouble(line[idx]) - this.varsBounds.get(header[idx])[0]) / (this.varsBounds.get(header[idx])[1] - this.varsBounds.get(header[idx])[0]);
                    }
                    trajectoryRecord[idx - 2] = val;
                }
                if (vehicleIdx != Integer.parseInt(line[0])) {
                    createSignalAndUpdateWithSlidingWindow(trajectory, times, signals);
                    vehicleIdx = Integer.parseInt(line[0]);
                    trajectory.clear();
                    trajectory.add(trajectoryRecord);
                    times.add(Long.parseLong(line[1]));
                    break;
                }
                trajectory.add(trajectoryRecord);
                times.add(Long.parseLong(line[1].trim()));
            }
        }
        createSignalAndUpdateWithSlidingWindow(trajectory, times, signals);
        trajectory.clear();
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

    private void createSignalAndUpdateWithSlidingWindow(List<double[]> trajectory, List<Long> times,
                                              List<Signal<double[]>[]> signals) {
        if (times.size() == 0) {
            return;
        }
        Signal<?>[] innerSignal = new Signal<?>[(trajectory.size() * 2) / this.windowSize];
        int length = times.size();
        int j = 0;
        int i;
        int t = 0;
        double time = 0.0;
        while (j < length) {
            Signal<double[]> currSignal = new Signal<>();
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
        signals.add((Signal<double[]>[]) innerSignal);
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
