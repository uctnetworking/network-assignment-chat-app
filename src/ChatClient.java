import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient
{
    final static int serverPort = 60000;

    public static void main(String args[]) throws UnknownHostException, IOException
    {
        Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket socket = new Socket("192.168.0.109", serverPort);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner in = new Scanner(socket.getInputStream());

        System.out.println(in.nextLine()); // This is a request from the server for the client's name
        out.println(scn.nextLine());
        String serverResponse = in.nextLine();
        while(!serverResponse.equals(ProtocolResponses.NAME_SUCCESS))
        {
            System.out.println(serverResponse); // Asks the client to enter another name
            out.println(scn.nextLine());
            serverResponse = in.nextLine();
        }
        System.out.println(in.nextLine()); // This is a thanks from the server for a correct name

        // These threads must send and receive messages
        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    // read the message to deliver.
                    String msg = scn.nextLine();
                    out.println(msg);
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    // read the message sent to this client
                    String msg =  in.nextLine();
                    System.out.println(msg);
                    if(msg.equalsIgnoreCase(ProtocolResponses.REQUEST_LOGOUT))
                    {
                        System.exit(0);
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }
}
