import com.dev.julien.factorcon.FactorioServer;
import com.dev.julien.factorcon.Packet;

import java.util.Scanner;

public class Main {

    public static void main(String[] Args) {
        try {
            FactorioServer server = new FactorioServer("127.0.0.1", 34198);
            server.authenticate("testpass");

            Scanner scan = new Scanner(System.in);

            while (server.isConnected()) {
                System.out.print("FactoRCON> ");

                String command = scan.nextLine();
                Packet packet = new Packet(command);
                if (!server.isConnected()) break;

                if (!command.equals("quit")) {
                    String response = server.sendAndRecieveBody(packet);
                    System.out.println(response);
                } else {
                    server.send(packet);
                    server.disconnect();
                    System.out.println("Quit command sent to server.");
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Could not connect to server.");
            e.printStackTrace();
        }
        System.out.println("shutting down.");
    }


}
