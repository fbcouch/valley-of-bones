package com.ahsgaming.valleyofbones;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

/**
 * Created by jami on 7/12/14.
 */
public class Main {
    public static void main(String[] args) {
        int port = 54556;
        String name = "Server";
        boolean pub = false;

        for(int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                for (char c: args[i].substring(1).toCharArray()) {
                    switch (c) {
                        case 'p':
                            try {
                                port = Integer.parseInt(args[i + 1]);
                            } catch (Exception e) {
                                System.out.println("Port required with -p");
                            }
                            break;
                        case 'P':
                            pub = true;
                            break;
                        case 'n':
                            if (i + 1 < args.length) {
                                name = args[i + 1];
                            } else {
                                System.out.println("Name required with -n");
                            }
                            break;
                        case 'h':
                            System.out.println("Valley of Bones CLI Server");
                            System.out.println("\n\t-p=PORT\tname of the server");
                            System.out.println("\t-n=NAME\tname of the server");
                            System.out.println("\t-P\tpublic server");
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Unrecognized flag -" + c);
                    }
                }
            }
        }
        HeadlessApplicationConfiguration cfg = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new VOBServer(name, port, pub), cfg);
    }
}
