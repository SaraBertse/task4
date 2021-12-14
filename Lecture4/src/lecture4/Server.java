package lecture4;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Base64;

public class Server {

    public static void main(String[] args) {
        SSLServerSocketFactory ssf; // = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

        try {
            KeyStore ks = null;
            ks = KeyStore.getInstance("JKS", "SUN");
            //ks = KeyStore.getInstance("jks");
            InputStream is = null;
            //is = new FileInputStream(new File("C:/Program Files/Java/jdk1.8.0_271/jre/lib/security/cacerts"));
            // "C:/Users/sarab/cert.pfx"
            is = new FileInputStream(new File("C:\\Users\\HP\\Documents\\skolan\\kurser\\id1212_natverksprog\\task4\\cert.pfx"));
            char[] pwd = "qwerty".toCharArray();
            ks.load(is, pwd);

            SSLContext ctx = SSLContext.getInstance("TLS");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, pwd);
            ctx.init(kmf.getKeyManagers(), null, null);
            //ctx.init(null, null, null);
            ssf = ctx.getServerSocketFactory();

            System.out.println("Supported:");
            //   for(int i = 0; i < ssf.getSupportedCipherSuites().length; i++)
            //      System.out.println(ssf.getSupportedCipherSuites()[i]);
            SSLServerSocket ss = null;
            ss = (SSLServerSocket) ssf.createServerSocket(443);
            //String[] cipher = {"SSL_DH_anon_WITH_RC4_128_MD5"};
            String[] cipher = {"TLS_RSA_WITH_AES_128_CBC_SHA"};
            //ss.setEnabledCipherSuites(cipher);
            //System.out.println("Choosen:");
            // for(int i = 0; i < ss.getEnabledCipherSuites().length; i++)
            //     System.out.println(ss.getEnabledCipherSuites()[i]);
            SSLSocket socket = (SSLSocket) ss.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String row = null;
            /* System.out.println("----------received HTTPS request----------");
            while( ((row=reader.readLine()) != null) || (row=="\\r\\n"))  {
                System.out.println(row);
                System.out.println("stuck spinning");
                if(!(row.trim().equals(""))){ 
                    break;
                }
            }*/

            reader.close();

            SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            HttpsURLConnection.setDefaultSSLSocketFactory(sf);
            SSLSocket mailSocket = null;
            String host = "webmail.kth.se";
            mailSocket = (SSLSocket) sf.createSocket(host, 993);

            //String host = "localhost";
            try {
                mailSocket.startHandshake();
            } catch (IOException e) {
                System.out.println("*************" + e.getMessage());
            }
            BufferedReader mailReader = null;
            PrintWriter mailWriter = null;
            try {

                mailReader = new BufferedReader(new InputStreamReader(mailSocket.getInputStream()));
                mailWriter = new PrintWriter(mailSocket.getOutputStream());
            } catch (MalformedURLException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            //bertse@ug.kth.se
            String str;
            str = mailReader.readLine();
            System.out.println(str);
            mailWriter.println("a001 login bertse tempPWForTask4");

            //  mailWriter.println("Host: " + host);
            //  mailWriter.println("");
            //  mailWriter.flush();
            //  System.out.println("HTTP-request sent");
            mailWriter.flush();

            // while( (str=mailReader.readLine()) != null)
            //    System.out.println(str);
            str = mailReader.readLine();
            System.out.println(str);

            mailWriter.println("a002 select inbox");
            mailWriter.flush();
            //mailWriter.println("a003 fetch 1 full");
            // mailWriter.flush();
            // str=mailReader.readLine();
            // System.out.println(str);
            System.out.println("mail reader is ready 1? " + mailReader.ready());
            //while(mailReader.ready())
            while (!(str = mailReader.readLine()).contains("a002")) {
                //str = mailReader.readLine();
                System.out.println(str);
            }

            // fetches and prints a002 OK
            System.out.println(str);

            mailWriter.println("a003 fetch 1 body[header]");
            mailWriter.flush();
            /* str=mailReader.readLine();
             System.out.println(str);
             str=mailReader.readLine();
             System.out.println(str);*/

            System.out.println("mail reader is ready 2? " + mailReader.ready());
            while (!(str = mailReader.readLine()).contains("a003")) {
                System.out.println(str);
            }

            // fetches and prints a003 OK
            System.out.println(str);
            //mailWriter.flush();
            mailWriter.close();
            mailReader.close();
            mailSocket.close();
            
            System.out.println("\n\n### PART II ###\n");
            // ####### PART 2 #######
            String host2 = "smtp.kth.se";
            int port2 = 587;
            Socket socket2 = new Socket(host2, port2);
            
            /*try {
                socket2.startHandshake();
            } catch (IOException e) {
                System.out.println("*************" + e.getMessage());
            }*/
            
            BufferedReader streamReader = null;
            PrintWriter streamWriter = null;
            try {
                streamReader = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
                streamWriter = new PrintWriter(socket2.getOutputStream());
            } catch (MalformedURLException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            
            // fetch 220 OK
            str = streamReader.readLine();
            System.out.println(str);
            
            streamWriter.println("EHLO www.kth.se");
            streamWriter.flush();
            
            
            // prints "250-smtp-3.sys.kth.se"
            str = streamReader.readLine();
            System.out.println(str);
            
            while (!(str).contains("250 DSN")) {
                str = streamReader.readLine();
                System.out.println(str);
            }
            
            streamWriter.println("STARTTLS");
            streamWriter.flush();
            
            // prints "220 2.0.0 Ready to start TLS"
            str = streamReader.readLine();
            System.out.println(str);
            
            
            //SocketFactory basicSocketFactory = SocketFactory.getDefault();
            //Socket s = basicSocketFactory.createSocket(host, port);
            
            //SSLSocketFactory tlsSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            //socket2 = sf.createSocket(socket2, host2, port2, true);

            //SSLSocket secureSocket = null;
            //secureSocket = (SSLSocket) sf.createSocket(host2, port2);
            
            SSLSocket sslSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(socket2, host2, port2, true);
            
            /*try {
                socket2.startHandshake();
            } catch (IOException e) {
                System.out.println("*************" + e.getMessage());
            }
            */

            
            try {
                streamReader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
                streamWriter = new PrintWriter(sslSocket.getOutputStream());
            } catch (MalformedURLException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            
            
            
            String msg = "EHLO www.kth.se";
            
            // EHLO again to start encrypted line of comm
            streamWriter.println(msg);
            streamWriter.flush();
            
            str = streamReader.readLine();
            System.out.println(str);
            //(str = streamReader.readLine()).contains("250")
            while (!(str).contains("250 DSN")) {
                str = streamReader.readLine();
                System.out.println(str);
            }
            
            streamWriter.println("AUTH LOGIN");
            streamWriter.flush();
            
            String usrn = "bertse"; 
            String encodedUsrn = Base64.getEncoder().encodeToString(usrn.getBytes());
            
            String passw = "tempPWForTask4";
            String encodedPassw = Base64.getEncoder().encodeToString(passw.getBytes());
            
            // prints "334 VXNlcm5hbWU6"
            str = streamReader.readLine();
            System.out.println(str);
            
            streamWriter.println(encodedUsrn);
            streamWriter.flush();
            
            // prints "334 UGFzc3dvcmQ6"
            str = streamReader.readLine();
            System.out.println(str);
            
            streamWriter.println(encodedPassw);
            streamWriter.flush();
            
            // prints "235 2.7.0 Authentication successful"
            str = streamReader.readLine();
            System.out.println(str);
            
            streamWriter.println("MAIL FROM:<bertse@kth.se>");
            streamWriter.flush();
            
            // prints "250 OK"
            str = streamReader.readLine();
            System.out.println(str);
            
            streamWriter.println("RCPT TO:<dwyer@kth.se>");
            streamWriter.flush();
            
            // prints "250 OK"
            str = streamReader.readLine();
            System.out.println(str);
            
            streamWriter.println("DATA");
            streamWriter.flush();
            
            // prints "354 End data with <CR><LF>.<CR><LF>"
            str = streamReader.readLine();
            System.out.println(str);
            
            
            streamWriter.println("Date: Tue, 14 Dec 2021 05:33:29 -0700\n" +
                                 "From: SamLogic <bertse@kth.se>\n" +
                                    "Subject: The Next Meeting\n" +
                                    "To: dwyer@kth.se\n" +
                                    "\n" +
                                    "Hi John,\n" +
                                    "The next meeting will be on Friday.\n" +
                                    "/Anna.\n" +
                                    ".");
            streamWriter.flush();
            streamWriter.println(".");
            streamWriter.flush();

            // prints "250 2.0.0 Ok: queued as 3C0F7E28"
            str = streamReader.readLine();
            System.out.println(str);
            
            System.out.println("the end");
            
            streamWriter.close();
            streamReader.close();
            socket2.close();
            
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
