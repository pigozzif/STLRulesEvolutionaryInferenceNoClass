package it.units.malelab.learningstl;

import java.io.IOException;


public class SuperMain {

    public static void main(String[] args) throws IOException {
        String[] newArgs = new String[args.length];
        if (args.length - 1 >= 0) System.arraycopy(args, 1, newArgs, 1, args.length - 1);
        for (int seed=0; seed < 10; ++seed) {
            newArgs[0] = "seed=" + seed;
            Main.main(newArgs);
        }
    }

}
