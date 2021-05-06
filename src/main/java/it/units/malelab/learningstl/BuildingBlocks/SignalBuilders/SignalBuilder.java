package it.units.malelab.learningstl.BuildingBlocks.SignalBuilders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


public interface SignalBuilder<T> {

    default BufferedReader createReaderFromFile(String fileName) throws IOException {
        Path path = Paths.get(".", fileName);
        InputStream in = Files.newInputStream(path);
        return new BufferedReader(new InputStreamReader(in));
    }

    List<T> parseSignals(String fileName) throws IOException;

    Map<String, double[]> getVarsBounds();

    double[] getTemporalBounds();

    default double[] readVectorFromFile(String s) throws IOException {return null;}
}
